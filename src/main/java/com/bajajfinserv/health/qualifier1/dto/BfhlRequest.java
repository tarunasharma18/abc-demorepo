package com.bajajfinserv.health.qualifier1.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Request DTO for POST /bfhl.
 */
public class BfhlRequest {

    @JsonProperty("data")
    private List<String> data;

    public BfhlRequest() {}

    public List<String> getData() {
        return data;
    }

    public void setData(List<String> data) {
        this.data = data;
    }
}
