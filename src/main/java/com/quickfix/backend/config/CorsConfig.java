
package com.quickfix.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
public class CorsConfig {

    @Value("${app.cors.allowed-origins:https://quick-fix-frontend-9hfu.vercel.app,http://localhost:5174,http://localhost:5173}")
    private String allowedOrigins;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        java.util.Set<String> originSet = new java.util.LinkedHashSet<>(Arrays.asList(
            "http://localhost:5173",
            "http://localhost:5174",
            "https://quick-fix-frontend-9hfu.vercel.app"
        ));

        if (allowedOrigins != null && !allowedOrigins.isBlank()) {
            Arrays.stream(allowedOrigins.split(","))
                    .map(String::trim)
                    .filter(origin -> !origin.isEmpty())
                    .forEach(originSet::add);
        }

        config.setAllowedOrigins(new java.util.ArrayList<>(originSet));

        config.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "PATCH",
            "DELETE", "OPTIONS"
        ));

        config.setAllowedHeaders(Arrays.asList("*"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source =
            new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", config);

        return source;
    }
}
