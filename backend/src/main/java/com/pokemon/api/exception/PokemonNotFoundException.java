package com.pokemon.api.exception;

public class PokemonNotFoundException extends RuntimeException {

    public PokemonNotFoundException(int id) {
        super("Pokemon not found with id: " + id);
    }
}
