package com.pokemon.api.dto.response;

import java.util.List;

public record PokemonListResponse(
        List<PokemonSummaryDto> content,
        int page,
        int size,
        int totalCount
) {}
