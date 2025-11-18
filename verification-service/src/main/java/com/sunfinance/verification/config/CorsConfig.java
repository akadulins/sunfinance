package com.sunfinance.verification.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        
        // Разрешить запросы с localhost:3000 и localhost:3001
        config.setAllowedOrigins(Arrays.asList(
            "http://localhost:3000",
            "http://localhost:3001"
        ));
        
        // Разрешить все HTTP методы
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        
        // Разрешить все заголовки
        config.setAllowedHeaders(Arrays.asList("*"));
        
        // Разрешить credentials (cookies, authorization headers)
        config.setAllowCredentials(true);
        
        // Максимальное время кеширования preflight запроса (1 час)
        config.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        
        return new CorsFilter(source);
    }
}