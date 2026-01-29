package com.pokemon.api.dto.external;

import java.util.List;

public record PokeApiListResponse(
        int count,
        String next,
        String previous,
        List<NamedApiResource> results
) {}
