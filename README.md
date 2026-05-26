# Bajaj Finserv Health — Qualifier 1 (Java)

## Overview

Spring Boot 3 + Java 17 + Maven application that runs automatically on startup and:

1. **Step 1** — Sends a `POST` request to `https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA` with student details to receive a `webhook` URL and `accessToken`.
2. **Step 2** — Sends a `POST` request to the received webhook URL with a Bearer token and the final SQL query as the request body.

No web server is started. The application executes the logic via `CommandLineRunner` and exits.

---

## Project Structure

```
qualifier1-bfh-java/
├── pom.xml
├── README.md
└── src/
    ├── main/
    │   ├── java/com/bajajfinserv/health/qualifier1/
    │   │   ├── Qualifier1Application.java       # Spring Boot entry point
    │   │   ├── StartupRunner.java               # CommandLineRunner — orchestrates steps 1 & 2
    │   │   ├── config/
    │   │   │   └── AppConfig.java               # RestTemplate bean
    │   │   ├── dto/
    │   │   │   ├── GenerateWebhookRequest.java  # Request DTO for step 1
    │   │   │   ├── GenerateWebhookResponse.java # Response DTO for step 1
    │   │   │   └── FinalQueryRequest.java       # Request DTO for step 2
    │   │   └── service/
    │   │       ├── WebhookServiceInterface.java # Service interface
    │   │       └── WebhookService.java          # Service implementation
    │   └── resources/
    │       └── application.properties
    └── test/
        └── java/com/bajajfinserv/health/qualifier1/
            └── WebhookServiceTest.java
```

---

## Prerequisites

- Java 17+
- Maven 3.8+ (or use the included Maven Wrapper)

---

## Configuration

Edit `src/main/resources/application.properties` to set your student details:

```properties
student.name=Tarun Asharma
student.regNo=0827AL231133
student.email=tarunasharma230709@acropolis.in
```

---

## Build

```bash
# Using Maven Wrapper (recommended)
./mvnw clean package -DskipTests

# Using system Maven
mvn clean package -DskipTests
```

Output JAR: `target/qualifier-1.0.0.jar`

---

## Run

```bash
java -jar target/qualifier-1.0.0.jar
```

The application will:
1. Call the generateWebhook endpoint
2. Extract the webhook URL and access token
3. Submit the final SQL query to the webhook
4. Log the result and exit

---

## Run Tests

```bash
./mvnw test
# or
mvn test
```

---

## Final SQL Query

```sql
SELECT p.AMOUNT AS SALARY,
       CONCAT(e.FIRST_NAME, ' ', e.LAST_NAME) AS NAME,
       TIMESTAMPDIFF(YEAR, e.DOB, CURDATE()) AS AGE,
       d.DEPARTMENT_NAME
FROM PAYMENTS p
JOIN EMPLOYEE e ON p.EMP_ID = e.EMP_ID
JOIN DEPARTMENT d ON e.DEPARTMENT = d.DEPARTMENT_ID
WHERE DAY(p.PAYMENT_TIME) <> 1
ORDER BY p.AMOUNT DESC
LIMIT 1;
```

**Logic:** Returns the employee with the highest salary payment that was NOT made on the 1st of any month, along with their name, age, and department.

---

## GitHub Submission Checklist

- [ ] Update `application.properties` with your real name, regNo, and email
- [ ] Build passes: `./mvnw clean package -DskipTests`
- [ ] JAR runs successfully: `java -jar target/qualifier-1.0.0.jar`
- [ ] Tests pass: `./mvnw test`
- [ ] Initialize git repository:

```bash
git init
git add .
git commit -m "Final Bajaj Finserv Health Qualifier 1 submission"
```

- [ ] Push to a **public** GitHub repository
- [ ] Submit the GitHub repo URL via the contest form
