package com.pokemon.api.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "local_pokemon")
@Getter
@Setter
public class LocalPokemon {

    @Id
    private Integer id;

    @Column(nullable = false)
    private String name;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(nullable = false)
    private String types;

    private Integer weight;

    @Column(columnDefinition = "TEXT")
    private String abilities;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "pokemon", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PokemonAttribute> attributes = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public void addAttribute(String key, String value) {
        PokemonAttribute attr = new PokemonAttribute();
        attr.setPokemon(this);
        attr.setKey(key);
        attr.setValue(value);
        attr.setType("STRING");
        attributes.add(attr);
    }
}
