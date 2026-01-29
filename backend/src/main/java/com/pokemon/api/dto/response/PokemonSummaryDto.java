package com.pokemon.api.dto.response;

import java.util.List;

public record PokemonSummaryDto(
        Integer id,
        String name,
        String imageUrl,
        List<String> types,
        Integer weight,
        List<String> abilities
) {}
