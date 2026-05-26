package com.bajajfinserv.health.qualifier1.controller;

import com.bajajfinserv.health.qualifier1.dto.BfhlRequest;
import com.bajajfinserv.health.qualifier1.dto.BfhlResponse;
import com.bajajfinserv.health.qualifier1.service.BfhlServiceInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST controller exposing the /bfhl endpoint as required by the contest.
 */
@RestController
public class BfhlController {

    private static final Logger log = LoggerFactory.getLogger(BfhlController.class);

    private final BfhlServiceInterface bfhlService;

    public BfhlController(BfhlServiceInterface bfhlService) {
        this.bfhlService = bfhlService;
    }

    /**
     * POST /bfhl
     * Accepts an array of strings and returns categorised output.
     * This is the primary contest endpoint.
     */
    @PostMapping("/bfhl")
    public ResponseEntity<BfhlResponse> process(@RequestBody BfhlRequest request) {
        log.info("POST /bfhl called with {} items",
                request.getData() == null ? 0 : request.getData().size());
        BfhlResponse response = bfhlService.process(request);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /bfhl
     * Returns a simple info message.
     * Browsers hitting /bfhl will see this instead of 405.
     */
    @GetMapping("/bfhl")
    public ResponseEntity<Map<String, String>> info() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "endpoint", "POST /bfhl",
                "description", "Send POST request with JSON body: {\"data\":[...]}"
        ));
    }

    /**
     * GET /health
     * Root-level health check for Railway and evaluators.
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "UP"));
    }

    /**
     * GET /bfhl/health
     * Secondary health check path (kept for Railway healthcheckPath compatibility).
     */
    @GetMapping("/bfhl/health")
    public ResponseEntity<Map<String, String>> bfhlHealth() {
        return ResponseEntity.ok(Map.of("status", "UP"));
    }
}
