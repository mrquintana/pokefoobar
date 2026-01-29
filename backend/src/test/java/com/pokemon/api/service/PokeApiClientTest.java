package com.pokemon.api.service;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.pokemon.api.dto.external.PokeApiEvolutionChainResponse;
import com.pokemon.api.dto.external.PokeApiListResponse;
import com.pokemon.api.dto.external.PokeApiPokemonResponse;
import com.pokemon.api.dto.external.PokeApiSpeciesResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

@WireMockTest
class PokeApiClientTest {

    private PokeApiClient pokeApiClient;

    @BeforeEach
    void setUp(WireMockRuntimeInfo wmRuntimeInfo) {
        RestClient restClient = RestClient.builder()
                .baseUrl(wmRuntimeInfo.getHttpBaseUrl())
                .build();
        pokeApiClient = new PokeApiClient(restClient);
    }

    @Test
    void getPokemonList_shouldReturnPaginatedResults() {
        stubFor(get(urlPathEqualTo("/pokemon"))
                .withQueryParam("limit", equalTo("20"))
                .withQueryParam("offset", equalTo("0"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("pokemon-list.json")));

        PokeApiListResponse response = pokeApiClient.getPokemonList(20, 0);

        assertThat(response.count()).isEqualTo(1302);
        assertThat(response.results()).hasSize(3);
        assertThat(response.results().get(0).name()).isEqualTo("bulbasaur");
    }

    @Test
    void getPokemon_shouldReturnPokemonDetails() {
        stubFor(get(urlPathEqualTo("/pokemon/1"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("pokemon-1.json")));

        PokeApiPokemonResponse response = pokeApiClient.getPokemon(1);

        assertThat(response.id()).isEqualTo(1);
        assertThat(response.name()).isEqualTo("bulbasaur");
        assertThat(response.weight()).isEqualTo(69);
        assertThat(response.types()).hasSize(2);
        assertThat(response.abilities()).hasSize(2);
    }

    @Test
    void getSpecies_shouldReturnSpeciesWithDescription() {
        stubFor(get(urlPathEqualTo("/pokemon-species/1"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("species-1.json")));

        PokeApiSpeciesResponse response = pokeApiClient.getSpecies(1);

        assertThat(response.id()).isEqualTo(1);
        assertThat(response.name()).isEqualTo("bulbasaur");
        assertThat(response.flavorTextEntries()).isNotEmpty();
        assertThat(response.evolutionChain().url()).contains("evolution-chain/1");
    }

    @Test
    void getEvolutionChain_shouldReturnEvolutionTree() {
        stubFor(get(urlPathEqualTo("/evolution-chain/1"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("evolution-chain-1.json")));

        PokeApiEvolutionChainResponse response = pokeApiClient.getEvolutionChain(1);

        assertThat(response.id()).isEqualTo(1);
        assertThat(response.chain().species().name()).isEqualTo("bulbasaur");
        assertThat(response.chain().evolvesTo()).hasSize(1);
        assertThat(response.chain().evolvesTo().get(0).species().name()).isEqualTo("ivysaur");
    }
}
