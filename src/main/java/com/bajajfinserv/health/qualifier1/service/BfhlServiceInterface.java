package com.bajajfinserv.health.qualifier1.service;

import com.bajajfinserv.health.qualifier1.dto.BfhlRequest;
import com.bajajfinserv.health.qualifier1.dto.BfhlResponse;

/**
 * Interface for the BFHL data processing service.
 */
public interface BfhlServiceInterface {

    /**
     * Processes the input array and returns categorised output.
     *
     * @param request the incoming request containing the data array
     * @return BfhlResponse with categorised numbers, alphabets, special chars, etc.
     */
    BfhlResponse process(BfhlRequest request);
}
