package com.pokemon.api.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public record LocalPokemonResponse(
        Integer id,
        String name,
        String imageUrl,
        List<String> types,
        Integer weight,
        List<String> abilities,
        String description,
        Map<String, String> attributes,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
