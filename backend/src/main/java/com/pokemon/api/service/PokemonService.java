package com.pokemon.api.service;

import com.pokemon.api.dto.external.PokeApiEvolutionChainResponse;
import com.pokemon.api.dto.external.PokeApiListResponse;
import com.pokemon.api.dto.external.PokeApiPokemonResponse;
import com.pokemon.api.dto.external.PokeApiSpeciesResponse;
import com.pokemon.api.dto.response.PokemonDetailResponse;
import com.pokemon.api.dto.response.PokemonListResponse;
import com.pokemon.api.dto.response.PokemonSummaryDto;
import com.pokemon.api.mapper.PokemonMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Service for retrieving Pokemon data from PokeAPI.
 * Orchestrates calls to PokeAPI client and maps responses to DTOs.
 */
@Service
@RequiredArgsConstructor
public class PokemonService {

    private static final Pattern ID_PATTERN = Pattern.compile("/pokemon/(\\d+)/");
    private static final Pattern CHAIN_ID_PATTERN = Pattern.compile("/evolution-chain/(\\d+)/");

    private final PokeApiClient pokeApiClient;
    private final PokemonMapper mapper;

    public PokemonListResponse listPokemon(int page, int size) {
        int offset = page * size;
        PokeApiListResponse apiResponse = pokeApiClient.getPokemonList(size, offset);

        List<PokemonSummaryDto> summaries = apiResponse.results().stream()
                .map(result -> {
                    int id = extractIdFromUrl(result.url());
                    PokeApiPokemonResponse pokemon = pokeApiClient.getPokemon(id);
                    return mapper.toSummary(pokemon);
                })
                .toList();

        return new PokemonListResponse(summaries, page, size, apiResponse.count());
    }

    public PokemonDetailResponse getPokemonDetail(int id) {
        PokeApiPokemonResponse pokemon = pokeApiClient.getPokemon(id);
        PokeApiSpeciesResponse species = pokeApiClient.getSpecies(id);

        int chainId = extractChainIdFromUrl(species.evolutionChain().url());
        PokeApiEvolutionChainResponse chain = pokeApiClient.getEvolutionChain(chainId);

        return mapper.toDetailResponse(pokemon, species, chain);
    }

    private int extractIdFromUrl(String url) {
        Matcher matcher = ID_PATTERN.matcher(url);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }
        throw new IllegalArgumentException("Could not extract ID from URL: " + url);
    }

    private int extractChainIdFromUrl(String url) {
        Matcher matcher = CHAIN_ID_PATTERN.matcher(url);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }
        throw new IllegalArgumentException("Could not extract chain ID from URL: " + url);
    }
}
