package com.pokemon.api.repository;

import com.pokemon.api.entity.LocalPokemon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocalPokemonRepository extends JpaRepository<LocalPokemon, Integer> {
}
