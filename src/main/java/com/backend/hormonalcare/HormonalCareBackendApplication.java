package com.backend.hormonalcare;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
@EnableJpaAuditing
public class HormonalCareBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(HormonalCareBackendApplication.class, args);
    }

    @Configuration
    public static class Myconfiguration{
        @Bean
        public WebMvcConfigurer corsConfigurer(){
            return new WebMvcConfigurer() {
                @Override
                public void addCorsMappings(CorsRegistry registry) {
                    registry.addMapping("/**")
                            .allowedOriginPatterns("*") // ✅ permite cualquier origen con credenciales
                            .allowedMethods("HEAD", "GET", "PUT", "POST", "DELETE", "PATCH")
                            .allowedHeaders("*")
                            .allowCredentials(true);
                }
            };
        }
    }
}
