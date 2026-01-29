package com.pokemon.api.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "pokemon_attribute",
        uniqueConstraints = @UniqueConstraint(columnNames = {"pokemon_id", "attribute_key"}))
@Getter
@Setter
public class PokemonAttribute {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pokemon_id", nullable = false)
    private LocalPokemon pokemon;

    @Column(name = "attribute_key", nullable = false)
    private String key;

    @Column(name = "attribute_value", columnDefinition = "TEXT")
    private String value;

    @Column(name = "attribute_type")
    private String type;
}
