package com.bajajfinserv.health.qualifier1;

import com.bajajfinserv.health.qualifier1.client.HttpClient;
import com.bajajfinserv.health.qualifier1.dto.FinalQueryRequest;
import com.bajajfinserv.health.qualifier1.dto.GenerateWebhookRequest;
import com.bajajfinserv.health.qualifier1.dto.GenerateWebhookResponse;
import com.bajajfinserv.health.qualifier1.service.WebhookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for WebhookService.
 *
 * Mocks the HttpClient interface (not RestTemplate directly) so these tests
 * work cleanly on Java 17+ / Java 23 without Byte Buddy instrumentation issues.
 */
@ExtendWith(MockitoExtension.class)
class WebhookServiceTest {

    // HttpClient is our own interface — Mockito can always mock interfaces
    @Mock
    private HttpClient httpClient;

    private WebhookService webhookService;

    @BeforeEach
    void setUp() {
        webhookService = new WebhookService(httpClient);
        ReflectionTestUtils.setField(webhookService, "apiBaseUrl",
                "https://bfhldevapigw.healthrx.co.in/hiring");
    }

    // -----------------------------------------------------------------------
    // generateWebhook — happy path
    // -----------------------------------------------------------------------

    @Test
    void generateWebhook_returnsValidResponse() {
        GenerateWebhookResponse mockResponse = new GenerateWebhookResponse();
        mockResponse.setWebhook("https://example.com/webhook");
        mockResponse.setAccessToken("test-token-abc123");

        when(httpClient.exchange(anyString(), eq(HttpMethod.POST),
                any(HttpEntity.class), eq(GenerateWebhookResponse.class)))
                .thenReturn(new ResponseEntity<>(mockResponse, HttpStatus.OK));

        GenerateWebhookResponse result =
                webhookService.generateWebhook("John Doe", "REG12347", "john@example.com");

        assertNotNull(result);
        assertEquals("https://example.com/webhook", result.getWebhook());
        assertEquals("test-token-abc123", result.getAccessToken());
    }

    // -----------------------------------------------------------------------
    // generateWebhook — validation failures
    // -----------------------------------------------------------------------

    @Test
    void generateWebhook_throwsWhenWebhookMissing() {
        GenerateWebhookResponse mockResponse = new GenerateWebhookResponse();
        mockResponse.setWebhook(null);
        mockResponse.setAccessToken("token");

        when(httpClient.exchange(anyString(), eq(HttpMethod.POST),
                any(HttpEntity.class), eq(GenerateWebhookResponse.class)))
                .thenReturn(new ResponseEntity<>(mockResponse, HttpStatus.OK));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> webhookService.generateWebhook("John Doe", "REG12347", "john@example.com"));

        assertTrue(ex.getMessage().contains("Webhook URL is missing"));
    }

    @Test
    void generateWebhook_throwsWhenAccessTokenMissing() {
        GenerateWebhookResponse mockResponse = new GenerateWebhookResponse();
        mockResponse.setWebhook("https://example.com/webhook");
        mockResponse.setAccessToken(null);

        when(httpClient.exchange(anyString(), eq(HttpMethod.POST),
                any(HttpEntity.class), eq(GenerateWebhookResponse.class)))
                .thenReturn(new ResponseEntity<>(mockResponse, HttpStatus.OK));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> webhookService.generateWebhook("John Doe", "REG12347", "john@example.com"));

        assertTrue(ex.getMessage().contains("Access token is missing"));
    }

    @Test
    void generateWebhook_throwsWhenResponseBodyIsNull() {
        when(httpClient.exchange(anyString(), eq(HttpMethod.POST),
                any(HttpEntity.class), eq(GenerateWebhookResponse.class)))
                .thenReturn(new ResponseEntity<>(null, HttpStatus.OK));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> webhookService.generateWebhook("John Doe", "REG12347", "john@example.com"));

        assertTrue(ex.getMessage().contains("null response body"));
    }

    // -----------------------------------------------------------------------
    // submitFinalQuery — happy path
    // -----------------------------------------------------------------------

    @Test
    void submitFinalQuery_successOnFirstAttempt() {
        when(httpClient.exchange(anyString(), eq(HttpMethod.POST),
                any(HttpEntity.class), eq(String.class)))
                .thenReturn(new ResponseEntity<>("{\"status\":\"ok\"}", HttpStatus.OK));

        assertDoesNotThrow(() ->
                webhookService.submitFinalQuery(
                        "https://example.com/webhook", "test-token", "SELECT 1"));

        verify(httpClient, times(1))
                .exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class));
    }

    @Test
    void submitFinalQuery_sendsCorrectAuthorizationHeader() {
        when(httpClient.exchange(anyString(), eq(HttpMethod.POST),
                any(HttpEntity.class), eq(String.class)))
                .thenReturn(new ResponseEntity<>("ok", HttpStatus.OK));

        webhookService.submitFinalQuery(
                "https://example.com/webhook", "my-secret-token", "SELECT 1");

        @SuppressWarnings("unchecked")
        ArgumentCaptor<HttpEntity<FinalQueryRequest>> captor =
                ArgumentCaptor.forClass(HttpEntity.class);

        verify(httpClient).exchange(
                anyString(), eq(HttpMethod.POST), captor.capture(), eq(String.class));

        HttpEntity<FinalQueryRequest> captured = captor.getValue();
        assertTrue(captured.getHeaders().containsKey("Authorization"));
        assertEquals("Bearer my-secret-token",
                captured.getHeaders().getFirst("Authorization"));
    }

    // -----------------------------------------------------------------------
    // DTO tests — no mocking needed
    // -----------------------------------------------------------------------

    @Test
    void generateWebhookRequest_gettersAndSetters() {
        GenerateWebhookRequest req = new GenerateWebhookRequest("Alice", "REG001", "alice@test.com");
        assertEquals("Alice", req.getName());
        assertEquals("REG001", req.getRegNo());
        assertEquals("alice@test.com", req.getEmail());

        req.setName("Bob");
        req.setRegNo("REG002");
        req.setEmail("bob@test.com");
        assertEquals("Bob", req.getName());
        assertEquals("REG002", req.getRegNo());
        assertEquals("bob@test.com", req.getEmail());
    }

    @Test
    void finalQueryRequest_gettersAndSetters() {
        FinalQueryRequest req = new FinalQueryRequest("SELECT * FROM TABLE");
        assertEquals("SELECT * FROM TABLE", req.getFinalQuery());

        req.setFinalQuery("SELECT 1");
        assertEquals("SELECT 1", req.getFinalQuery());
    }

    @Test
    void generateWebhookResponse_gettersAndSetters() {
        GenerateWebhookResponse resp = new GenerateWebhookResponse();
        resp.setWebhook("https://hook.example.com");
        resp.setAccessToken("token123");

        assertEquals("https://hook.example.com", resp.getWebhook());
        assertEquals("token123", resp.getAccessToken());
    }
}
