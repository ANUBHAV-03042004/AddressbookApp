package com.addressbook.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    private static final String SECURITY_SCHEME = "BearerAuth";

    @Value("${app.server-url:http://localhost:5000}")
    private String serverUrl;

    @Bean
    public OpenAPI addressBookOpenAPI() {
        return new OpenAPI()
                .servers(List.of(new Server().url(serverUrl).description("Active Server")))
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME,
                                new SecurityScheme()
                                        .name(SECURITY_SCHEME)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Paste your JWT (without 'Bearer ' prefix)")
                        ))
                .info(new Info()
                        .title("Address Book REST API")
                        .description("Spring Boot REST API for managing Address Books and Contacts")
                        .version("v1.0.0")
                        .contact(new Contact().name("Anubhav").email("anubhav@example.com"))
                        .license(new License().name("Apache 2.0").url("https://springdoc.org")));
    }
}
