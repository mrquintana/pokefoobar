package com.pokemon.api.controller;

import com.pokemon.api.dto.request.CreateLocalPokemonRequest;
import com.pokemon.api.dto.request.UpdateLocalPokemonRequest;
import com.pokemon.api.dto.response.LocalPokemonResponse;
import com.pokemon.api.service.LocalPokemonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/local-pokemon")
@RequiredArgsConstructor
@Tag(name = "Local Pokemon", description = "Locally stored Pokemon data")
public class LocalPokemonController {

    private final LocalPokemonService localPokemonService;

    @PostMapping("/{pokemonId}")
    @Operation(summary = "Copy Pokemon from PokeAPI to local database")
    public ResponseEntity<LocalPokemonResponse> copyPokemon(
            @Parameter(description = "Pokemon ID from PokeAPI")
            @PathVariable int pokemonId,
            @RequestBody(required = false) CreateLocalPokemonRequest request) {
        Map<String, String> attrs = request != null ? request.attributes() : null;
        LocalPokemonResponse response = localPokemonService.copyFromPokeApi(pokemonId, attrs);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get locally stored Pokemon")
    public ResponseEntity<LocalPokemonResponse> getLocalPokemon(
            @Parameter(description = "Local Pokemon ID")
            @PathVariable int id) {
        return ResponseEntity.ok(localPokemonService.getById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update locally stored Pokemon")
    public ResponseEntity<LocalPokemonResponse> updatePokemon(
            @Parameter(description = "Local Pokemon ID")
            @PathVariable int id,
            @RequestBody @Valid UpdateLocalPokemonRequest request) {
        return ResponseEntity.ok(localPokemonService.update(id, request));
    }

    @GetMapping
    @Operation(summary = "List all locally stored Pokemon")
    public ResponseEntity<List<LocalPokemonResponse>> listLocalPokemon() {
        return ResponseEntity.ok(localPokemonService.findAll());
    }
}
