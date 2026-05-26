package com.bajajfinserv.health.qualifier1.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Request DTO for the /generateWebhook/JAVA endpoint.
 */
public class GenerateWebhookRequest {

    @JsonProperty("name")
    private String name;

    @JsonProperty("regNo")
    private String regNo;

    @JsonProperty("email")
    private String email;

    public GenerateWebhookRequest() {}

    public GenerateWebhookRequest(String name, String regNo, String email) {
        this.name = name;
        this.regNo = regNo;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRegNo() {
        return regNo;
    }

    public void setRegNo(String regNo) {
        this.regNo = regNo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "GenerateWebhookRequest{name='" + name + "', regNo='" + regNo + "', email='" + email + "'}";
    }
}
