package com.pokemon.api.service;

import com.pokemon.api.dto.external.*;
import com.pokemon.api.dto.response.PokemonDetailResponse;
import com.pokemon.api.dto.response.PokemonListResponse;
import com.pokemon.api.mapper.PokemonMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PokemonServiceTest {

    @Mock
    private PokeApiClient pokeApiClient;

    private PokemonService pokemonService;

    @BeforeEach
    void setUp() {
        PokemonMapper mapper = new PokemonMapper();
        pokemonService = new PokemonService(pokeApiClient, mapper);
    }

    @Test
    void listPokemon_shouldReturnPaginatedResults() {
        PokeApiListResponse listResponse = new PokeApiListResponse(
                100,
                "https://pokeapi.co/api/v2/pokemon?offset=20&limit=20",
                null,
                List.of(
                        new NamedApiResource("bulbasaur", "https://pokeapi.co/api/v2/pokemon/1/"),
                        new NamedApiResource("ivysaur", "https://pokeapi.co/api/v2/pokemon/2/")
                )
        );

        when(pokeApiClient.getPokemonList(20, 0)).thenReturn(listResponse);
        when(pokeApiClient.getPokemon(1)).thenReturn(createMockPokemon(1, "bulbasaur"));
        when(pokeApiClient.getPokemon(2)).thenReturn(createMockPokemon(2, "ivysaur"));

        PokemonListResponse result = pokemonService.listPokemon(0, 20);

        assertThat(result.totalCount()).isEqualTo(100);
        assertThat(result.content()).hasSize(2);
        assertThat(result.content().get(0).name()).isEqualTo("bulbasaur");
        assertThat(result.page()).isEqualTo(0);
        assertThat(result.size()).isEqualTo(20);
    }

    @Test
    void listPokemon_shouldCalculateCorrectOffset() {
        PokeApiListResponse listResponse = new PokeApiListResponse(100, null, null, List.of());
        when(pokeApiClient.getPokemonList(10, 20)).thenReturn(listResponse);

        PokemonListResponse result = pokemonService.listPokemon(2, 10);

        assertThat(result.page()).isEqualTo(2);
        assertThat(result.size()).isEqualTo(10);
    }

    @Test
    void getPokemonDetail_shouldReturnCompleteDetails() {
        PokeApiPokemonResponse pokemon = createMockPokemon(1, "bulbasaur");
        PokeApiSpeciesResponse species = createMockSpecies(1);
        PokeApiEvolutionChainResponse chain = createMockEvolutionChain();

        when(pokeApiClient.getPokemon(1)).thenReturn(pokemon);
        when(pokeApiClient.getSpecies(1)).thenReturn(species);
        when(pokeApiClient.getEvolutionChain(1)).thenReturn(chain);

        PokemonDetailResponse result = pokemonService.getPokemonDetail(1);

        assertThat(result.id()).isEqualTo(1);
        assertThat(result.name()).isEqualTo("bulbasaur");
        assertThat(result.types()).containsExactly("grass", "poison");
        assertThat(result.abilities()).containsExactly("overgrow");
        assertThat(result.description()).isEqualTo("A strange seed was planted on its back.");
        assertThat(result.evolutions()).hasSize(3);
    }

    @Test
    void getPokemonDetail_shouldExtractEvolutionChainCorrectly() {
        when(pokeApiClient.getPokemon(anyInt())).thenReturn(createMockPokemon(1, "bulbasaur"));
        when(pokeApiClient.getSpecies(anyInt())).thenReturn(createMockSpecies(1));
        when(pokeApiClient.getEvolutionChain(anyInt())).thenReturn(createMockEvolutionChain());

        PokemonDetailResponse result = pokemonService.getPokemonDetail(1);

        assertThat(result.evolutions()).extracting("name")
                .containsExactly("bulbasaur", "ivysaur", "venusaur");
    }

    private PokeApiPokemonResponse createMockPokemon(int id, String name) {
        return new PokeApiPokemonResponse(
                id,
                name,
                69,
                new PokeApiPokemonResponse.PokemonSprites(
                        "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/" + id + ".png"
                ),
                List.of(
                        new PokeApiPokemonResponse.PokemonType(1,
                                new NamedApiResource("grass", "https://pokeapi.co/api/v2/type/12/")),
                        new PokeApiPokemonResponse.PokemonType(2,
                                new NamedApiResource("poison", "https://pokeapi.co/api/v2/type/4/"))
                ),
                List.of(
                        new PokeApiPokemonResponse.PokemonAbility(1, false,
                                new NamedApiResource("overgrow", "https://pokeapi.co/api/v2/ability/65/"))
                ),
                new NamedApiResource("bulbasaur", "https://pokeapi.co/api/v2/pokemon-species/1/")
        );
    }

    private PokeApiSpeciesResponse createMockSpecies(int id) {
        return new PokeApiSpeciesResponse(
                id,
                "bulbasaur",
                List.of(
                        new PokeApiSpeciesResponse.FlavorTextEntry(
                                "A strange seed was planted on its back.",
                                new NamedApiResource("en", "https://pokeapi.co/api/v2/language/9/")
                        )
                ),
                new PokeApiSpeciesResponse.EvolutionChainRef("https://pokeapi.co/api/v2/evolution-chain/1/")
        );
    }

    private PokeApiEvolutionChainResponse createMockEvolutionChain() {
        return new PokeApiEvolutionChainResponse(
                1,
                new PokeApiEvolutionChainResponse.ChainLink(
                        new NamedApiResource("bulbasaur", "https://pokeapi.co/api/v2/pokemon-species/1/"),
                        List.of(
                                new PokeApiEvolutionChainResponse.ChainLink(
                                        new NamedApiResource("ivysaur", "https://pokeapi.co/api/v2/pokemon-species/2/"),
                                        List.of(
                                                new PokeApiEvolutionChainResponse.ChainLink(
                                                        new NamedApiResource("venusaur", "https://pokeapi.co/api/v2/pokemon-species/3/"),
                                                        List.of()
                                                )
                                        )
                                )
                        )
                )
        );
    }
}
