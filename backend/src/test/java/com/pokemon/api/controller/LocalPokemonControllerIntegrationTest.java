package com.pokemon.api.controller;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.pokemon.api.repository.LocalPokemonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@WireMockTest(httpPort = 8089)
class LocalPokemonControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LocalPokemonRepository repository;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("pokeapi.base-url", () -> "http://localhost:8089");
        registry.add("spring.cache.type", () -> "none");
    }

    @BeforeEach
    void setUp() {
        repository.deleteAll();
        setupWireMockStubs();
    }

    @Test
    void copyPokemon_shouldCreateLocalCopy() throws Exception {
        mockMvc.perform(post("/api/v1/local-pokemon/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "attributes": {
                                        "name_jp": "フシギダネ",
                                        "name_es": "Bulbasaur"
                                    }
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("bulbasaur"))
                .andExpect(jsonPath("$.attributes.name_jp").value("フシギダネ"))
                .andExpect(jsonPath("$.attributes.name_es").value("Bulbasaur"));
    }

    @Test
    void copyPokemon_whenAlreadyExists_shouldReturn400() throws Exception {
        mockMvc.perform(post("/api/v1/local-pokemon/1"))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/local-pokemon/1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("Pokemon already exists locally with id: 1"));
    }

    @Test
    void getLocalPokemon_shouldReturnStoredPokemon() throws Exception {
        mockMvc.perform(post("/api/v1/local-pokemon/1"))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/v1/local-pokemon/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("bulbasaur"));
    }

    @Test
    void getLocalPokemon_whenNotFound_shouldReturn404() throws Exception {
        mockMvc.perform(get("/api/v1/local-pokemon/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Pokemon not found with id: 999"));
    }

    @Test
    void updatePokemon_shouldUpdateFields() throws Exception {
        mockMvc.perform(post("/api/v1/local-pokemon/1"))
                .andExpect(status().isCreated());

        mockMvc.perform(put("/api/v1/local-pokemon/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "Bulby",
                                    "weight": 100,
                                    "attributes": {
                                        "nickname": "My Bulbasaur"
                                    }
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Bulby"))
                .andExpect(jsonPath("$.weight").value(100))
                .andExpect(jsonPath("$.attributes.nickname").value("My Bulbasaur"));
    }

    @Test
    void updatePokemon_whenNotFound_shouldReturn404() throws Exception {
        mockMvc.perform(put("/api/v1/local-pokemon/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "test"}
                                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("NOT_FOUND"));
    }

    @Test
    void updatePokemon_withInvalidWeight_shouldReturn400() throws Exception {
        mockMvc.perform(post("/api/v1/local-pokemon/1"))
                .andExpect(status().isCreated());

        mockMvc.perform(put("/api/v1/local-pokemon/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"weight": -1}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    void listLocalPokemon_shouldReturnAllStored() throws Exception {
        mockMvc.perform(post("/api/v1/local-pokemon/1"))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/v1/local-pokemon"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    private void setupWireMockStubs() {
        stubFor(WireMock.get(urlPathEqualTo("/pokemon/1"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("pokemon-1.json")));

        stubFor(WireMock.get(urlPathEqualTo("/pokemon-species/1"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("species-1.json")));

        stubFor(WireMock.get(urlPathEqualTo("/evolution-chain/1"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("evolution-chain-1.json")));
    }
}
