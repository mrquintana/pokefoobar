package com.pokemon.api.dto.external;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record PokeApiEvolutionChainResponse(
        int id,
        ChainLink chain
) {
    public record ChainLink(
            NamedApiResource species,
            @JsonProperty("evolves_to") List<ChainLink> evolvesTo
    ) {}
}
