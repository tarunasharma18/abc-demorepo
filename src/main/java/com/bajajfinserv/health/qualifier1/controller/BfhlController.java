package com.bajajfinserv.health.qualifier1.controller;

import com.bajajfinserv.health.qualifier1.dto.BfhlRequest;
import com.bajajfinserv.health.qualifier1.dto.BfhlResponse;
import com.bajajfinserv.health.qualifier1.service.BfhlServiceInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller exposing the /bfhl endpoint as required by the contest.
 */
@RestController
@RequestMapping("/bfhl")
public class BfhlController {

    private static final Logger log = LoggerFactory.getLogger(BfhlController.class);

    private final BfhlServiceInterface bfhlService;

    public BfhlController(BfhlServiceInterface bfhlService) {
        this.bfhlService = bfhlService;
    }

    /**
     * POST /bfhl
     * Accepts an array of strings and returns categorised output.
     */
    @PostMapping
    public ResponseEntity<BfhlResponse> process(@RequestBody BfhlRequest request) {
        log.info("POST /bfhl called with {} items", request.getData() == null ? 0 : request.getData().size());
        BfhlResponse response = bfhlService.process(request);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /health
     * Simple liveness probe for Railway / Render health checks.
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("OK");
    }
}
