package com.bajajfinserv.health.qualifier1.service;

import com.bajajfinserv.health.qualifier1.client.HttpClient;
import com.bajajfinserv.health.qualifier1.dto.FinalQueryRequest;
import com.bajajfinserv.health.qualifier1.dto.GenerateWebhookRequest;
import com.bajajfinserv.health.qualifier1.dto.GenerateWebhookResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

/**
 * Service implementation that handles both API calls:
 * 1. POST to /generateWebhook/JAVA to obtain webhook URL and access token.
 * 2. POST to the received webhook URL with the final SQL query.
 */
@Service
public class WebhookService implements WebhookServiceInterface {

    private static final Logger log = LoggerFactory.getLogger(WebhookService.class);

    private final HttpClient httpClient;

    @Value("${api.base.url}")
    private String apiBaseUrl;

    public WebhookService(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    /**
     * Step 1: POST to /generateWebhook/JAVA and parse the response.
     */
    @Override
    public GenerateWebhookResponse generateWebhook(String name, String regNo, String email) {
        String url = apiBaseUrl + "/generateWebhook/JAVA";
        log.info("Calling generateWebhook endpoint: {}", url);

        GenerateWebhookRequest requestBody = new GenerateWebhookRequest(name, regNo, email);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<GenerateWebhookRequest> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<GenerateWebhookResponse> response = httpClient.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    GenerateWebhookResponse.class
            );

            GenerateWebhookResponse body = response.getBody();
            if (body == null) {
                throw new IllegalStateException("Received null response body from generateWebhook endpoint.");
            }
            if (body.getWebhook() == null || body.getWebhook().isBlank()) {
                throw new IllegalStateException("Webhook URL is missing in the response.");
            }
            if (body.getAccessToken() == null || body.getAccessToken().isBlank()) {
                throw new IllegalStateException("Access token is missing in the response.");
            }

            log.info("Successfully received webhook URL: {}", body.getWebhook());
            return body;

        } catch (HttpClientErrorException e) {
            log.error("Client error calling generateWebhook: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("Client error during generateWebhook call: " + e.getMessage(), e);
        } catch (HttpServerErrorException e) {
            log.error("Server error calling generateWebhook: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("Server error during generateWebhook call: " + e.getMessage(), e);
        } catch (ResourceAccessException e) {
            log.error("Network error calling generateWebhook: {}", e.getMessage());
            throw new RuntimeException("Network error during generateWebhook call: " + e.getMessage(), e);
        }
    }

    /**
     * Step 2: POST the final SQL query to the webhook URL with Bearer token auth.
     * Retries up to 4 times on failure as per contest requirements.
     */
    @Override
    public void submitFinalQuery(String webhookUrl, String accessToken, String finalQuery) {
        log.info("Submitting final query to webhook: {}", webhookUrl);

        FinalQueryRequest requestBody = new FinalQueryRequest(finalQuery);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);

        HttpEntity<FinalQueryRequest> entity = new HttpEntity<>(requestBody, headers);

        int maxAttempts = 4;
        int attempt = 0;
        Exception lastException = null;

        while (attempt < maxAttempts) {
            attempt++;
            log.info("Attempt {}/{} - Posting final query to webhook...", attempt, maxAttempts);

            try {
                ResponseEntity<String> response = httpClient.exchange(
                        webhookUrl,
                        HttpMethod.POST,
                        entity,
                        String.class
                );

                log.info("Webhook submission successful. HTTP Status: {}", response.getStatusCode());
                log.info("Response body: {}", response.getBody());
                return;

            } catch (HttpClientErrorException e) {
                log.error("Client error on attempt {}: {} - {}", attempt, e.getStatusCode(), e.getResponseBodyAsString());
                lastException = e;
                // 4xx errors are not retryable
                break;
            } catch (HttpServerErrorException e) {
                log.warn("Server error on attempt {}: {} - {}", attempt, e.getStatusCode(), e.getResponseBodyAsString());
                lastException = e;
            } catch (ResourceAccessException e) {
                log.warn("Network error on attempt {}: {}", attempt, e.getMessage());
                lastException = e;
            }

            if (attempt < maxAttempts) {
                log.info("Retrying in 2 seconds...");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Interrupted during retry wait.", ie);
                }
            }
        }

        throw new RuntimeException(
                "Failed to submit final query after " + maxAttempts + " attempts. Last error: "
                        + (lastException != null ? lastException.getMessage() : "unknown"),
                lastException
        );
    }
}
