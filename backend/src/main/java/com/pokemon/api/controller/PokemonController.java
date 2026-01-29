package com.pokemon.api.controller;

import com.pokemon.api.dto.response.PokemonDetailResponse;
import com.pokemon.api.dto.response.PokemonListResponse;
import com.pokemon.api.service.PokemonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/pokemon")
@RequiredArgsConstructor
@Tag(name = "Pokemon", description = "Pokemon data from PokeAPI")
public class PokemonController {

    private final PokemonService pokemonService;

    @GetMapping
    @Operation(summary = "List Pokemon paginated")
    public ResponseEntity<PokemonListResponse> listPokemon(
            @Parameter(description = "Page number (0-based)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(pokemonService.listPokemon(page, size));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Pokemon details with evolutions")
    public ResponseEntity<PokemonDetailResponse> getPokemonDetail(
            @Parameter(description = "Pokemon ID")
            @PathVariable int id) {
        return ResponseEntity.ok(pokemonService.getPokemonDetail(id));
    }
}
