package com.pokemon.api.service;

import com.pokemon.api.dto.request.UpdateLocalPokemonRequest;
import com.pokemon.api.dto.response.LocalPokemonResponse;
import com.pokemon.api.dto.response.PokemonDetailResponse;
import com.pokemon.api.entity.LocalPokemon;
import com.pokemon.api.exception.InvalidPokemonDataException;
import com.pokemon.api.exception.PokemonNotFoundException;
import com.pokemon.api.mapper.PokemonMapper;
import com.pokemon.api.repository.LocalPokemonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LocalPokemonServiceTest {

    @Mock
    private LocalPokemonRepository repository;

    @Mock
    private PokemonService pokemonService;

    private LocalPokemonService localPokemonService;

    @BeforeEach
    void setUp() {
        PokemonMapper mapper = new PokemonMapper();
        localPokemonService = new LocalPokemonService(repository, pokemonService, mapper);
    }

    @Test
    void copyFromPokeApi_shouldCreateLocalPokemon() {
        PokemonDetailResponse detail = createMockDetail();
        LocalPokemon savedEntity = createMockEntity();

        when(repository.existsById(1)).thenReturn(false);
        when(pokemonService.getPokemonDetail(1)).thenReturn(detail);
        when(repository.save(any(LocalPokemon.class))).thenReturn(savedEntity);

        LocalPokemonResponse result = localPokemonService.copyFromPokeApi(1, null);

        assertThat(result.id()).isEqualTo(1);
        assertThat(result.name()).isEqualTo("bulbasaur");
        verify(repository).save(any(LocalPokemon.class));
    }

    @Test
    void copyFromPokeApi_withAttributes_shouldAddAttributes() {
        PokemonDetailResponse detail = createMockDetail();
        LocalPokemon savedEntity = createMockEntity();

        when(repository.existsById(1)).thenReturn(false);
        when(pokemonService.getPokemonDetail(1)).thenReturn(detail);
        when(repository.save(any(LocalPokemon.class))).thenAnswer(inv -> {
            LocalPokemon entity = inv.getArgument(0);
            entity.setCreatedAt(LocalDateTime.now());
            entity.setUpdatedAt(LocalDateTime.now());
            return entity;
        });

        Map<String, String> attrs = Map.of("name_jp", "フシギダネ", "name_es", "Bulbasaur");
        LocalPokemonResponse result = localPokemonService.copyFromPokeApi(1, attrs);

        assertThat(result.attributes()).containsKey("name_jp");
        assertThat(result.attributes()).containsKey("name_es");
    }

    @Test
    void copyFromPokeApi_whenAlreadyExists_shouldThrowException() {
        when(repository.existsById(1)).thenReturn(true);

        assertThatThrownBy(() -> localPokemonService.copyFromPokeApi(1, null))
                .isInstanceOf(InvalidPokemonDataException.class)
                .hasMessageContaining("already exists");
    }

    @Test
    void update_shouldUpdateExistingPokemon() {
        LocalPokemon entity = createMockEntity();
        when(repository.findById(1)).thenReturn(Optional.of(entity));
        when(repository.save(any())).thenReturn(entity);

        UpdateLocalPokemonRequest request = new UpdateLocalPokemonRequest("Bulby", 100, null);
        LocalPokemonResponse result = localPokemonService.update(1, request);

        assertThat(result.name()).isEqualTo("Bulby");
        assertThat(result.weight()).isEqualTo(100);
    }

    @Test
    void update_whenNotFound_shouldThrow404() {
        when(repository.findById(999)).thenReturn(Optional.empty());

        UpdateLocalPokemonRequest request = new UpdateLocalPokemonRequest("test", null, null);

        assertThatThrownBy(() -> localPokemonService.update(999, request))
                .isInstanceOf(PokemonNotFoundException.class);
    }

    @Test
    void getById_shouldReturnPokemon() {
        LocalPokemon entity = createMockEntity();
        when(repository.findById(1)).thenReturn(Optional.of(entity));

        LocalPokemonResponse result = localPokemonService.getById(1);

        assertThat(result.id()).isEqualTo(1);
        assertThat(result.name()).isEqualTo("bulbasaur");
    }

    @Test
    void getById_whenNotFound_shouldThrow404() {
        when(repository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> localPokemonService.getById(999))
                .isInstanceOf(PokemonNotFoundException.class);
    }

    @Test
    void findAll_shouldReturnAllLocalPokemon() {
        List<LocalPokemon> entities = List.of(createMockEntity());
        when(repository.findAll()).thenReturn(entities);

        List<LocalPokemonResponse> result = localPokemonService.findAll();

        assertThat(result).hasSize(1);
    }

    private PokemonDetailResponse createMockDetail() {
        return new PokemonDetailResponse(
                1,
                "bulbasaur",
                "https://example.com/1.png",
                List.of("grass", "poison"),
                69,
                List.of("overgrow"),
                "A strange seed was planted on its back.",
                List.of()
        );
    }

    private LocalPokemon createMockEntity() {
        LocalPokemon entity = new LocalPokemon();
        entity.setId(1);
        entity.setName("bulbasaur");
        entity.setImageUrl("https://example.com/1.png");
        entity.setTypes("[\"grass\",\"poison\"]");
        entity.setWeight(69);
        entity.setAbilities("[\"overgrow\"]");
        entity.setDescription("A strange seed.");
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        return entity;
    }
}
