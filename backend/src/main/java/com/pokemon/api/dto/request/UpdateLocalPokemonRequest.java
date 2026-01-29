package com.pokemon.api.dto.request;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.Map;

public record UpdateLocalPokemonRequest(
        @Size(min = 1, max = 100, message = "Name must be between 1 and 100 characters")
        String name,

        @Positive(message = "Weight must be positive")
        Integer weight,

        Map<String, String> attributes
) {}
