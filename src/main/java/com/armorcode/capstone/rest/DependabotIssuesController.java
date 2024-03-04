package com.armorcode.capstone.rest;

import com.armorcode.capstone.entity.Findings;
import com.armorcode.capstone.service.FindingsServices;
import com.armorcode.capstone.service.KafkaProducerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;



import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;



import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;


@RestController
public class DependabotIssuesController {

    @Value("${github.url}")
    public static String apiUrl;

    @Value("${github.token}")
    public static String token;

    @Autowired
    FindingsServices findingsServices;

    @Autowired
    KafkaProducerService kafkaProducerService;

    @GetMapping("/issues/dependabot")
    private static List<Object> getIssues() {


        // Create headers with the authorization token and additional headers
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Accept", "application/vnd.github.v3+json");
        headers.add("X-GitHub-Api-Version", "2022-11-28");
        // Create an HttpEntity with headers
        HttpEntity<Void> httpEntity = new HttpEntity<>(headers);
        // Create a RestTemplate
        RestTemplate restTemplate = new RestTemplate();
        // Make the GET request and handle the response
        ResponseEntity<Object[]> responseEntity = restTemplate.exchange(
                apiUrl, HttpMethod.GET, httpEntity, Object[].class);
        List<Object> Issues = new ArrayList<>();
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            Issues.addAll(List.of(Objects.requireNonNull(responseEntity.getBody())));
            // Handle the response, e.g., add to a list or print
            for (Object issue : Issues) {
//                System.out.println("success");
            }
        } else {
            System.err.println("Error: " + responseEntity.getStatusCode());
        }
        return Issues;
    }


    public ResponseEntity<?> changeDependabotAlertStatus(){

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        // Prepare request body with updated status and dismissed reason
        String requestBody = "{\"state\": \"" + "dismissed" + "\", \"dismissed_reason\": \"" + "tolerable_risk" + "\"}";
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.PATCH, requestEntity, String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.status(HttpStatus.OK).body(STR."Dependabot alert status changed successfully.\{response.getBody()}");
        } else {
            return ResponseEntity.status(response.getStatusCode()).body(STR."Failed to change Dependabot alert status. Response: \{response.getBody()}");
        }

    }

    @GetMapping("/issues/dependabot/closed")
    public ResponseEntity<?> updateAlertStatus(@RequestParam long id) throws ClientProtocolException, IOException {

        Findings findings = findingsServices.getFindingByID(id);

        if(findings == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(STR."No Finding associated with Findings ID: \{id}");
        }

        int number = findings.getNumber();

        String apiUrl = STR."https://api.github.com/repos/praveen-kr-22/vulnerable-repo/dependabot/alerts/\{number}";
        try {
            HttpClient httpClient = HttpClients.createDefault();
            HttpPatch httpPatch = new HttpPatch(apiUrl);

            httpPatch.setHeader(HttpHeaders.ACCEPT, "application/vnd.github+json");
            httpPatch.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
            httpPatch.setHeader("X-GitHub-Api-Version", "2022-11-28");
            httpPatch.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");

            String requestBody = "{\"state\":\"dismissed\",\"dismissed_reason\":\"tolerable_risk\",\"dismissed_comment\":\"This alert is accurate but we use a sanitizer.\"}";
            httpPatch.setEntity(new StringEntity(requestBody));

            HttpResponse response = httpClient.execute(httpPatch);

            int statusCode = response.getStatusLine().getStatusCode();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()))) {
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                findings.setStatus("Mitigated");
                findings.setUpdatedAt(new Date());
                findingsServices.saveAllFindings(findings);
                kafkaProducerService.sendUpdateFinding(findings);
                return ResponseEntity.status(HttpStatus.OK).body(convertToJson(result.toString()));
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while processing the request");
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("I/O error occurred while processing the request");
        }
    }


    private String convertToJson(String data) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(data);
        } catch (IOException e) {
            e.printStackTrace();
            return "{\"error\": \"An error occurred while converting to JSON\"}";
        }
    }



}
