package com.pokemon.api.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pokemon.api.dto.external.PokeApiEvolutionChainResponse;
import com.pokemon.api.dto.external.PokeApiPokemonResponse;
import com.pokemon.api.dto.external.PokeApiSpeciesResponse;
import com.pokemon.api.dto.response.EvolutionDto;
import com.pokemon.api.dto.response.LocalPokemonResponse;
import com.pokemon.api.dto.response.PokemonDetailResponse;
import com.pokemon.api.dto.response.PokemonSummaryDto;
import com.pokemon.api.entity.LocalPokemon;
import com.pokemon.api.entity.PokemonAttribute;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class PokemonMapper {

    private static final Pattern ID_PATTERN = Pattern.compile("/pokemon-species/(\\d+)/");
    private final ObjectMapper objectMapper = new ObjectMapper();

    public PokemonSummaryDto toSummary(PokeApiPokemonResponse pokemon) {
        return new PokemonSummaryDto(
                pokemon.id(),
                pokemon.name(),
                pokemon.sprites().frontDefault(),
                extractTypes(pokemon),
                pokemon.weight(),
                extractAbilities(pokemon)
        );
    }

    public PokemonDetailResponse toDetailResponse(
            PokeApiPokemonResponse pokemon,
            PokeApiSpeciesResponse species,
            PokeApiEvolutionChainResponse evolutionChain) {
        return new PokemonDetailResponse(
                pokemon.id(),
                pokemon.name(),
                pokemon.sprites().frontDefault(),
                extractTypes(pokemon),
                pokemon.weight(),
                extractAbilities(pokemon),
                extractDescription(species),
                extractEvolutions(evolutionChain)
        );
    }

    public LocalPokemon toLocalEntity(PokemonDetailResponse detail) {
        LocalPokemon entity = new LocalPokemon();
        entity.setId(detail.id());
        entity.setName(detail.name());
        entity.setImageUrl(detail.imageUrl());
        entity.setTypes(toJson(detail.types()));
        entity.setWeight(detail.weight());
        entity.setAbilities(toJson(detail.abilities()));
        entity.setDescription(detail.description());
        return entity;
    }

    public LocalPokemonResponse toLocalResponse(LocalPokemon entity) {
        return new LocalPokemonResponse(
                entity.getId(),
                entity.getName(),
                entity.getImageUrl(),
                fromJson(entity.getTypes()),
                entity.getWeight(),
                fromJson(entity.getAbilities()),
                entity.getDescription(),
                entity.getAttributes().stream()
                        .collect(Collectors.toMap(PokemonAttribute::getKey, PokemonAttribute::getValue)),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    private List<String> extractTypes(PokeApiPokemonResponse pokemon) {
        return pokemon.types().stream()
                .map(t -> t.type().name())
                .toList();
    }

    private List<String> extractAbilities(PokeApiPokemonResponse pokemon) {
        return pokemon.abilities().stream()
                .map(a -> a.ability().name())
                .toList();
    }

    private String extractDescription(PokeApiSpeciesResponse species) {
        return species.flavorTextEntries().stream()
                .filter(entry -> "en".equals(entry.language().name()))
                .findFirst()
                .map(entry -> entry.flavorText().replace("\n", " ").replace("\f", " "))
                .orElse("");
    }

    private List<EvolutionDto> extractEvolutions(PokeApiEvolutionChainResponse chain) {
        List<EvolutionDto> evolutions = new ArrayList<>();
        collectEvolutions(chain.chain(), evolutions);
        return evolutions;
    }

    private void collectEvolutions(PokeApiEvolutionChainResponse.ChainLink link, List<EvolutionDto> evolutions) {
        int id = extractIdFromSpeciesUrl(link.species().url());
        String imageUrl = String.format(
                "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/%d.png", id);
        evolutions.add(new EvolutionDto(id, link.species().name(), imageUrl));

        for (PokeApiEvolutionChainResponse.ChainLink next : link.evolvesTo()) {
            collectEvolutions(next, evolutions);
        }
    }

    private int extractIdFromSpeciesUrl(String url) {
        Matcher matcher = ID_PATTERN.matcher(url);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }
        return 0;
    }

    private String toJson(List<String> list) {
        try {
            return objectMapper.writeValueAsString(list);
        } catch (JsonProcessingException e) {
            return "[]";
        }
    }

    private List<String> fromJson(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (Exception e) {
            return List.of();
        }
    }
}
