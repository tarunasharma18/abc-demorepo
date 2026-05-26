package com.bajajfinserv.health.qualifier1.service;

import com.bajajfinserv.health.qualifier1.dto.BfhlRequest;
import com.bajajfinserv.health.qualifier1.dto.BfhlResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Processes the input data array and categorises elements into:
 * - even numbers, odd numbers, alphabets (uppercase), special characters
 * - sum of all numbers
 * - concat_string: all alphabetical chars reversed, in alternating caps
 */
@Service
public class BfhlService implements BfhlServiceInterface {

    private static final Logger log = LoggerFactory.getLogger(BfhlService.class);

    @Value("${student.name}")
    private String studentName;

    @Value("${student.regNo}")
    private String studentRegNo;

    @Value("${student.email}")
    private String studentEmail;

    @Override
    public BfhlResponse process(BfhlRequest request) {
        BfhlResponse response = new BfhlResponse();

        try {
            List<String> data = request.getData();
            if (data == null) {
                data = Collections.emptyList();
            }

            List<String> evenNumbers     = new ArrayList<>();
            List<String> oddNumbers      = new ArrayList<>();
            List<String> alphabets       = new ArrayList<>();
            List<String> specialChars    = new ArrayList<>();
            long         numericSum      = 0;
            StringBuilder allAlphaChars = new StringBuilder();

            for (String item : data) {
                if (item == null || item.isEmpty()) continue;

                if (isNumeric(item)) {
                    long val = Long.parseLong(item);
                    numericSum += val;
                    if (val % 2 == 0) {
                        evenNumbers.add(item);
                    } else {
                        oddNumbers.add(item);
                    }
                } else if (isAlphabetic(item)) {
                    // Whole token is alphabetic — uppercase it and collect chars
                    alphabets.add(item.toUpperCase());
                    allAlphaChars.append(item);
                } else {
                    // Mixed or special character token
                    boolean hasAlpha   = false;
                    boolean hasSpecial = false;

                    for (char c : item.toCharArray()) {
                        if (Character.isLetter(c)) {
                            hasAlpha = true;
                            allAlphaChars.append(c);
                        } else if (!Character.isDigit(c)) {
                            hasSpecial = true;
                        }
                    }

                    if (hasAlpha) {
                        alphabets.add(item.toUpperCase());
                    }
                    if (hasSpecial) {
                        specialChars.add(item);
                    }
                }
            }

            // concat_string: reverse all collected alpha chars, then alternating caps
            String reversed = allAlphaChars.reverse().toString();
            String concatString = alternatingCaps(reversed);

            response.setSuccess(true);
            response.setUserId(buildUserId());
            response.setEmail(studentEmail);
            response.setRollNumber(studentRegNo);
            response.setEvenNumbers(evenNumbers);
            response.setOddNumbers(oddNumbers);
            response.setAlphabets(alphabets);
            response.setSpecialCharacters(specialChars);
            response.setSum(String.valueOf(numericSum));
            response.setConcatString(concatString);

        } catch (Exception e) {
            log.error("Error processing BFHL request: {}", e.getMessage(), e);
            response.setSuccess(false);
            response.setUserId(buildUserId());
            response.setEmail(studentEmail);
            response.setRollNumber(studentRegNo);
            response.setEvenNumbers(Collections.emptyList());
            response.setOddNumbers(Collections.emptyList());
            response.setAlphabets(Collections.emptyList());
            response.setSpecialCharacters(Collections.emptyList());
            response.setSum("0");
            response.setConcatString("");
        }

        return response;
    }

    /**
     * Builds user_id in format: fullname_ddmmyyyy (lowercase, spaces replaced with _).
     * DOB is extracted from regNo: last 8 digits = ddmmyyyy.
     * Format: tarun_asharma_ddmmyyyy
     */
    private String buildUserId() {
        // name: "Tarun Asharma" -> "tarun_asharma"
        String namePart = studentName.trim().toLowerCase().replace(" ", "_");
        // regNo: "0827AL231133" — no DOB embedded, use regNo as-is for uniqueness
        // Per contest spec: user_id = {full_name_ddmmyyyy}
        // We use regNo as the date suffix since no DOB is provided separately
        return namePart + "_" + studentRegNo.toLowerCase();
    }

    /**
     * Returns true if the entire string represents a valid integer (possibly negative).
     */
    private boolean isNumeric(String s) {
        if (s == null || s.isEmpty()) return false;
        int start = s.charAt(0) == '-' ? 1 : 0;
        if (start == s.length()) return false;
        for (int i = start; i < s.length(); i++) {
            if (!Character.isDigit(s.charAt(i))) return false;
        }
        return true;
    }

    /**
     * Returns true if every character in the string is a letter.
     */
    private boolean isAlphabetic(String s) {
        for (char c : s.toCharArray()) {
            if (!Character.isLetter(c)) return false;
        }
        return true;
    }

    /**
     * Applies alternating caps starting with uppercase at index 0.
     * E.g. "eoD" -> "EoD"
     */
    private String alternatingCaps(String s) {
        StringBuilder sb = new StringBuilder(s.length());
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            sb.append(i % 2 == 0 ? Character.toUpperCase(c) : Character.toLowerCase(c));
        }
        return sb.toString();
    }
}
