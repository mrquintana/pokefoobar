package com.pokemon.api.exception;

public class InvalidPokemonDataException extends RuntimeException {

    public InvalidPokemonDataException(String message) {
        super(message);
    }
}
