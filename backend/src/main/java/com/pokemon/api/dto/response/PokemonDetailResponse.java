package com.pokemon.api.dto.response;

import java.util.List;

public record PokemonDetailResponse(
        Integer id,
        String name,
        String imageUrl,
        List<String> types,
        Integer weight,
        List<String> abilities,
        String description,
        List<EvolutionDto> evolutions
) {}
