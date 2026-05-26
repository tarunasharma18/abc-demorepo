package com.bajajfinserv.health.qualifier1.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Response DTO from the /generateWebhook/JAVA endpoint.
 * Contains the webhook URL and access token needed for the second call.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class GenerateWebhookResponse {

    @JsonProperty("webhook")
    private String webhook;

    @JsonProperty("accessToken")
    private String accessToken;

    public GenerateWebhookResponse() {}

    public String getWebhook() {
        return webhook;
    }

    public void setWebhook(String webhook) {
        this.webhook = webhook;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    @Override
    public String toString() {
        return "GenerateWebhookResponse{webhook='" + webhook
                + "', accessToken='" + (accessToken != null ? "[PRESENT]" : "null") + "'}";
    }
}
