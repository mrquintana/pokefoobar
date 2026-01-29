package com.pokemon.api.controller;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@WireMockTest(httpPort = 8089)
class PokemonControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("pokeapi.base-url", () -> "http://localhost:8089");
        registry.add("spring.cache.type", () -> "none");
    }

    @Test
    void listPokemon_shouldReturnPaginatedList() throws Exception {
        stubFor(WireMock.get(urlPathEqualTo("/pokemon"))
                .withQueryParam("limit", equalTo("20"))
                .withQueryParam("offset", equalTo("0"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("pokemon-list.json")));

        stubFor(WireMock.get(urlPathEqualTo("/pokemon/1"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("pokemon-1.json")));

        stubFor(WireMock.get(urlPathEqualTo("/pokemon/2"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                {
                                  "id": 2,
                                  "name": "ivysaur",
                                  "weight": 130,
                                  "sprites": {"front_default": "https://example.com/2.png"},
                                  "types": [{"slot": 1, "type": {"name": "grass", "url": ""}}],
                                  "abilities": [{"slot": 1, "is_hidden": false, "ability": {"name": "overgrow", "url": ""}}],
                                  "species": {"name": "ivysaur", "url": "https://pokeapi.co/api/v2/pokemon-species/2/"}
                                }
                                """)));

        stubFor(WireMock.get(urlPathEqualTo("/pokemon/3"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                {
                                  "id": 3,
                                  "name": "venusaur",
                                  "weight": 1000,
                                  "sprites": {"front_default": "https://example.com/3.png"},
                                  "types": [{"slot": 1, "type": {"name": "grass", "url": ""}}],
                                  "abilities": [{"slot": 1, "is_hidden": false, "ability": {"name": "overgrow", "url": ""}}],
                                  "species": {"name": "venusaur", "url": "https://pokeapi.co/api/v2/pokemon-species/3/"}
                                }
                                """)));

        mockMvc.perform(get("/api/v1/pokemon"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCount").value(1302))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].name").value("bulbasaur"))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(20));
    }

    @Test
    void getPokemonDetail_shouldReturnCompleteDetails() throws Exception {
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

        mockMvc.perform(get("/api/v1/pokemon/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("bulbasaur"))
                .andExpect(jsonPath("$.types").isArray())
                .andExpect(jsonPath("$.types[0]").value("grass"))
                .andExpect(jsonPath("$.abilities").isArray())
                .andExpect(jsonPath("$.description").isNotEmpty())
                .andExpect(jsonPath("$.evolutions").isArray())
                .andExpect(jsonPath("$.evolutions[0].name").value("bulbasaur"))
                .andExpect(jsonPath("$.evolutions[1].name").value("ivysaur"))
                .andExpect(jsonPath("$.evolutions[2].name").value("venusaur"));
    }

    @Test
    void listPokemon_withCustomPagination_shouldReturnCorrectPage() throws Exception {
        stubFor(WireMock.get(urlPathEqualTo("/pokemon"))
                .withQueryParam("limit", equalTo("10"))
                .withQueryParam("offset", equalTo("10"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                {
                                  "count": 100,
                                  "next": null,
                                  "previous": null,
                                  "results": []
                                }
                                """)));

        mockMvc.perform(get("/api/v1/pokemon")
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(1))
                .andExpect(jsonPath("$.size").value(10));
    }
}
