package com.pokemon.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI pokemonApiOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Pokemon API")
                        .description("REST API for Pokemon data with local storage capabilities")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Miguel Ramos")
                                .url("https://github.com/mrquintana")));
    }
}
