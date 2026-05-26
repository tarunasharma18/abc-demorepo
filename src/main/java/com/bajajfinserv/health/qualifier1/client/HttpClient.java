package com.bajajfinserv.health.qualifier1.client;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

/**
 * Thin interface over HTTP exchange — makes the service layer fully testable
 * without needing to instrument RestTemplate directly (which fails on Java 17+
 * with Mockito's inline mock maker).
 */
public interface HttpClient {

    <T> ResponseEntity<T> exchange(String url,
                                   HttpMethod method,
                                   HttpEntity<?> requestEntity,
                                   Class<T> responseType);
}
