package com.bajajfinserv.health.qualifier1.client;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * Production implementation of HttpClient backed by Spring's RestTemplate.
 */
@Component
public class RestTemplateHttpClient implements HttpClient {

    private final RestTemplate restTemplate;

    public RestTemplateHttpClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public <T> ResponseEntity<T> exchange(String url,
                                          HttpMethod method,
                                          HttpEntity<?> requestEntity,
                                          Class<T> responseType) {
        return restTemplate.exchange(url, method, requestEntity, responseType);
    }
}
