package com.pokemon.api.dto.request;

import java.util.Map;

public record CreateLocalPokemonRequest(
        Map<String, String> attributes
) {}
