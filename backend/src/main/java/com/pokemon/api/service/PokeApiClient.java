package com.pokemon.api.service;

import com.pokemon.api.dto.external.PokeApiEvolutionChainResponse;
import com.pokemon.api.dto.external.PokeApiListResponse;
import com.pokemon.api.dto.external.PokeApiPokemonResponse;
import com.pokemon.api.dto.external.PokeApiSpeciesResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class PokeApiClient {

    private final RestClient pokeApiRestClient;

    @Cacheable(value = "pokemon-list", key = "#limit + '-' + #offset")
    public PokeApiListResponse getPokemonList(int limit, int offset) {
        return pokeApiRestClient.get()
                .uri("/pokemon?limit={limit}&offset={offset}", limit, offset)
                .retrieve()
                .body(PokeApiListResponse.class);
    }

    @Cacheable(value = "pokemon-detail", key = "#id")
    public PokeApiPokemonResponse getPokemon(int id) {
        return pokeApiRestClient.get()
                .uri("/pokemon/{id}", id)
                .retrieve()
                .body(PokeApiPokemonResponse.class);
    }

    @Cacheable(value = "pokemon-species", key = "#id")
    public PokeApiSpeciesResponse getSpecies(int id) {
        return pokeApiRestClient.get()
                .uri("/pokemon-species/{id}", id)
                .retrieve()
                .body(PokeApiSpeciesResponse.class);
    }

    @Cacheable(value = "evolution-chain", key = "#id")
    public PokeApiEvolutionChainResponse getEvolutionChain(int id) {
        return pokeApiRestClient.get()
                .uri("/evolution-chain/{id}", id)
                .retrieve()
                .body(PokeApiEvolutionChainResponse.class);
    }
}
