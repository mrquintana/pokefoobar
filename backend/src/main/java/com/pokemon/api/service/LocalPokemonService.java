package com.pokemon.api.service;

import com.pokemon.api.dto.request.UpdateLocalPokemonRequest;
import com.pokemon.api.dto.response.LocalPokemonResponse;
import com.pokemon.api.dto.response.PokemonDetailResponse;
import com.pokemon.api.entity.LocalPokemon;
import com.pokemon.api.entity.PokemonAttribute;
import com.pokemon.api.exception.InvalidPokemonDataException;
import com.pokemon.api.exception.PokemonNotFoundException;
import com.pokemon.api.mapper.PokemonMapper;
import com.pokemon.api.repository.LocalPokemonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Service for managing locally stored Pokemon.
 * Handles copying from PokeAPI, CRUD operations, and attribute management.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LocalPokemonService {

    private final LocalPokemonRepository repository;
    private final PokemonService pokemonService;
    private final PokemonMapper mapper;

    @Transactional
    public LocalPokemonResponse copyFromPokeApi(int pokemonId, Map<String, String> attributes) {
        if (repository.existsById(pokemonId)) {
            throw new InvalidPokemonDataException("Pokemon already exists locally with id: " + pokemonId);
        }

        PokemonDetailResponse detail = pokemonService.getPokemonDetail(pokemonId);
        LocalPokemon entity = mapper.toLocalEntity(detail);

        if (attributes != null && !attributes.isEmpty()) {
            attributes.forEach(entity::addAttribute);
        }

        LocalPokemon saved = repository.save(entity);
        return mapper.toLocalResponse(saved);
    }

    @Transactional
    public LocalPokemonResponse update(int pokemonId, UpdateLocalPokemonRequest request) {
        LocalPokemon pokemon = repository.findById(pokemonId)
                .orElseThrow(() -> new PokemonNotFoundException(pokemonId));

        if (request.name() != null) {
            pokemon.setName(request.name());
        }
        if (request.weight() != null) {
            pokemon.setWeight(request.weight());
        }

        if (request.attributes() != null) {
            request.attributes().forEach((key, value) -> {
                pokemon.getAttributes().stream()
                        .filter(a -> a.getKey().equals(key))
                        .findFirst()
                        .ifPresentOrElse(
                                attr -> attr.setValue(value),
                                () -> pokemon.addAttribute(key, value)
                        );
            });
        }

        return mapper.toLocalResponse(repository.save(pokemon));
    }

    public LocalPokemonResponse getById(int id) {
        return repository.findById(id)
                .map(mapper::toLocalResponse)
                .orElseThrow(() -> new PokemonNotFoundException(id));
    }

    public List<LocalPokemonResponse> findAll() {
        return repository.findAll().stream()
                .map(mapper::toLocalResponse)
                .toList();
    }

    @Transactional
    public void delete(int id) {
        if (!repository.existsById(id)) {
            throw new PokemonNotFoundException(id);
        }
        repository.deleteById(id);
    }
}
