package com.armorcode.capstone.rest;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;



import java.util.ArrayList;
import java.util.List;




@RestController
public class DependabotIssuesController {

    @GetMapping("/issues/dependabot")
    private static List<Object> getIssues() {

        String apiUrl = "https://api.github.com/repos/praveen-kr-22/vulnerable-repo/dependabot/alerts";
        String token = "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";
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
            Issues.addAll(List.of(responseEntity.getBody()));
            // Handle the response, e.g., add to a list or print
            for (Object issue : Issues) {
//                System.out.println("success");
            }
        } else {
            System.err.println("Error: " + responseEntity.getStatusCode());
        }
        return Issues;
    }

}
