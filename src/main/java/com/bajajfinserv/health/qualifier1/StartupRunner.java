package com.bajajfinserv.health.qualifier1;

import com.bajajfinserv.health.qualifier1.dto.GenerateWebhookResponse;
import com.bajajfinserv.health.qualifier1.service.WebhookServiceInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Runs automatically on application startup.
 * Orchestrates the two-step webhook process:
 *   1. Generate webhook and retrieve access token.
 *   2. Submit the final SQL query to the webhook.
 *
 * The web server continues running after this completes so Railway
 * can serve POST /bfhl and GET /bfhl/health.
 */
@Component
public class StartupRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(StartupRunner.class);

    /**
     * Final SQL query as a single line.
     * Finds the employee with the highest salary payment NOT made on the 1st of any month.
     */
    private static final String FINAL_QUERY =
            "SELECT p.AMOUNT AS SALARY, CONCAT(e.FIRST_NAME, ' ', e.LAST_NAME) AS NAME, " +
            "TIMESTAMPDIFF(YEAR, e.DOB, CURDATE()) AS AGE, d.DEPARTMENT_NAME " +
            "FROM PAYMENTS p " +
            "JOIN EMPLOYEE e ON p.EMP_ID = e.EMP_ID " +
            "JOIN DEPARTMENT d ON e.DEPARTMENT = d.DEPARTMENT_ID " +
            "WHERE DAY(p.PAYMENT_TIME) <> 1 " +
            "ORDER BY p.AMOUNT DESC " +
            "LIMIT 1";

    private final WebhookServiceInterface webhookService;

    @Value("${student.name}")
    private String studentName;

    @Value("${student.regNo}")
    private String studentRegNo;

    @Value("${student.email}")
    private String studentEmail;

    public StartupRunner(WebhookServiceInterface webhookService) {
        this.webhookService = webhookService;
    }

    @Override
    public void run(String... args) {
        log.info("=== Bajaj Finserv Health Qualifier 1 - Startup Execution ===");
        log.info("Student: {}, RegNo: {}, Email: {}", studentName, studentRegNo, studentEmail);

        try {
            // Step 1: Generate webhook and get access token
            log.info("--- Step 1: Generating webhook ---");
            GenerateWebhookResponse webhookResponse =
                    webhookService.generateWebhook(studentName, studentRegNo, studentEmail);

            String webhookUrl   = webhookResponse.getWebhook();
            String accessToken  = webhookResponse.getAccessToken();

            log.info("Webhook URL received: {}", webhookUrl);
            log.info("Access token received: [PRESENT, length={}]", accessToken.length());

            // Step 2: Submit the final SQL query
            log.info("--- Step 2: Submitting final SQL query ---");
            webhookService.submitFinalQuery(webhookUrl, accessToken, FINAL_QUERY);

            log.info("=== Startup execution completed successfully. Web server is running. ===");

        } catch (Exception e) {
            // Log the error but do NOT exit — keep the web server alive for /bfhl requests
            log.error("=== Startup webhook execution failed: {} ===", e.getMessage(), e);
            log.warn("Web server will continue running. POST /bfhl is still available.");
        }
    }
}
