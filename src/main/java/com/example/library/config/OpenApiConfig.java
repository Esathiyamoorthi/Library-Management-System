package com.example.library.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration    // "this class contains settings for the app"
public class OpenApiConfig {

    @Bean         // "create this object and keep it ready for Spring to use"
    public OpenAPI libraryOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Library Management System API")
                        .description("REST API for users, books and book issuing")
                        .version("1.0"));
    }
}