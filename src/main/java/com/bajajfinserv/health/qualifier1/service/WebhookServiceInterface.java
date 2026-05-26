package com.bajajfinserv.health.qualifier1.service;

import com.bajajfinserv.health.qualifier1.dto.GenerateWebhookResponse;

/**
 * Interface for the webhook service layer.
 * Defines the contract for generating the webhook and submitting the final query.
 */
public interface WebhookServiceInterface {

    /**
     * Calls the generateWebhook endpoint and returns the parsed response
     * containing the webhook URL and access token.
     *
     * @param name   student full name
     * @param regNo  student registration number
     * @param email  student email address
     * @return GenerateWebhookResponse with webhook and accessToken
     */
    GenerateWebhookResponse generateWebhook(String name, String regNo, String email);

    /**
     * Submits the final SQL query to the provided webhook URL
     * using the given Bearer access token.
     *
     * @param webhookUrl  the webhook URL received from step 1
     * @param accessToken the Bearer token received from step 1
     * @param finalQuery  the SQL query string to submit
     */
    void submitFinalQuery(String webhookUrl, String accessToken, String finalQuery);
}
