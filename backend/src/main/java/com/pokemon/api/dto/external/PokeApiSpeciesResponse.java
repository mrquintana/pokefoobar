package com.pokemon.api.dto.external;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record PokeApiSpeciesResponse(
        int id,
        String name,
        @JsonProperty("flavor_text_entries") List<FlavorTextEntry> flavorTextEntries,
        @JsonProperty("evolution_chain") EvolutionChainRef evolutionChain
) {
    public record FlavorTextEntry(
            @JsonProperty("flavor_text") String flavorText,
            NamedApiResource language
    ) {}

    public record EvolutionChainRef(
            String url
    ) {}
}
