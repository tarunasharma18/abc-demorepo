package com.bajajfinserv.health.qualifier1.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Application configuration — provides shared beans.
 */
@Configuration
public class AppConfig {

    /**
     * RestTemplate bean used for all outbound HTTP calls.
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
