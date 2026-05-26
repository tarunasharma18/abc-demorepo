package com.bajajfinserv.health.qualifier1.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Request DTO for submitting the final SQL query to the webhook URL.
 */
public class FinalQueryRequest {

    @JsonProperty("finalQuery")
    private String finalQuery;

    public FinalQueryRequest() {}

    public FinalQueryRequest(String finalQuery) {
        this.finalQuery = finalQuery;
    }

    public String getFinalQuery() {
        return finalQuery;
    }

    public void setFinalQuery(String finalQuery) {
        this.finalQuery = finalQuery;
    }

    @Override
    public String toString() {
        return "FinalQueryRequest{finalQuery='" + finalQuery + "'}";
    }
}
