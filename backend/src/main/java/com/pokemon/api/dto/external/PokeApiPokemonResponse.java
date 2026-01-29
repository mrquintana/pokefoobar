package com.pokemon.api.dto.external;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record PokeApiPokemonResponse(
        int id,
        String name,
        int weight,
        PokemonSprites sprites,
        List<PokemonType> types,
        List<PokemonAbility> abilities,
        NamedApiResource species
) {
    public record PokemonSprites(
            @JsonProperty("front_default") String frontDefault
    ) {}

    public record PokemonType(
            int slot,
            NamedApiResource type
    ) {}

    public record PokemonAbility(
            int slot,
            @JsonProperty("is_hidden") boolean isHidden,
            NamedApiResource ability
    ) {}
}
