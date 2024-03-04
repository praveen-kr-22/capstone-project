package com.armorcode.capstone.service;


import com.armorcode.capstone.entity.Findings;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

@Service
public class JiraTicketServices {

    String apiUrl = "https://capstoneproject22.atlassian.net/rest/api/2/issue/";
    String username = "kumarp222909@gmail.com";
    String password = "ATATT3xFfGF0yGWfT52T41Hvu52b5oISqZdrknsG7JJB-ttz0rFYjwnbBLhXZedextbCh21fiDc9eQCl-Shq_21MzsfnF-Y1iSNpoqv6cVoaJx70LuaVVpinBlf7gBqDjmgPrPLclEg02Lm-Y2zbMxshK18uGs5TYRSvIWq_byqb5ly18QaRngA=6A08E6A1";


    public String createNewJiraTicket(Findings findings) {

        String projectKey = "AR";
        String issueType = "Task";
        String summary = findings.getSummary();
        long id = findings.getId();
        summary = StringUtils.trimWhitespace(summary.replaceAll("[\\r\\n\\t]|[^a-zA-Z0-9\\s]", ""));
        String description = findings.getDescription();
        description = StringUtils.trimWhitespace(description.replaceAll("[\\r\\n\\t]|[^a-zA-Z0-9\\s]", ""));
        String severityLevel = findings.getSecurityLevel();
        String temp = STR." .This ticket is associated with findingID \{id}";
        description = description + temp;
//        String priority = switch (severityLevel) {
//            case "Critical" -> "Highest";
//            case "High" -> "High";
//            case "Medium" -> "Medium";
//            case "Low" -> "Low";
//            case null, default -> "Lowest";
//        };

        // Construct the request body
//        String requestBody = STR."{\"fields\": {\"project\": {\"key\": \"\{projectKey}\"},\"summary\": \"\{summary}\",\"description\": \"\{description}\",\"issuetype\": {\"name\": \"\{issueType}\"}}}";
        String requestBody = "{\"fields\": {\"project\": {\"key\": \"" + projectKey + "\"},\"summary\": \"" + summary + "\",\"description\": \"" + description + "\",\"issuetype\": {\"name\": \"" + issueType + "\"}}}";

        // Validate if required fields are present

        // Set up HTTP headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBasicAuth(username, password);

        // Set up the HTTP request entity
        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        // Create RestTemplate instance
        RestTemplate restTemplate = new RestTemplate();

        // Make the HTTP POST request
        ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, entity, String.class);

        // Handle the response
        if (response.getStatusCode().is2xxSuccessful()) {
            System.out.println(response.getBody());
            return response.getBody();
        } else {
            System.err.println(STR."Failed to create Jira ticket. Status code: \{response.getStatusCode()}");
            return STR."Failed to create Jira ticket. Status code: \{response.getStatusCode()}";
        }

    }

    public ResponseEntity<String> closeJiraTicket(String ticketID){

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBasicAuth(username,password);
        String requestBody = "{\"transition\": {\"id\": \"31\"}}";

        HttpEntity<String> entity = new HttpEntity<>(requestBody,headers);

        String url = STR."https://capstoneproject22.atlassian.net/rest/api/3/issue/\{ticketID}/transitions";


        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> response = restTemplate.exchange(url,HttpMethod.POST,entity,String.class);

        if (response.getStatusCode() != HttpStatus.NO_CONTENT) {
            throw  new RuntimeException("Failed to close Jira ticket");
        }else{
            return ResponseEntity.ok(STR."Ticket \{ticketID} closed successfully");
        }
    }

    public ResponseEntity<String> updateJiraTicketStatus2(String issueKey) {
        try {
            String currentStatus = getCurrentStatus(issueKey);

            while (!currentStatus.equals("Done")) {
                String nextStatus = getNextStatus(currentStatus);

                if (nextStatus != null) {
                    int transitionId = getTransitionId(issueKey, nextStatus);

                    HttpHeaders headers = new HttpHeaders();
                    headers.setBasicAuth(username, password);
                    headers.setContentType(MediaType.APPLICATION_JSON);

                    String requestBody = STR."{ \"transition\": { \"id\": \{transitionId} } }";

                    HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

                    String apiUrl = STR."https://capstoneproject22.atlassian.net/rest/api/2/issue/\{issueKey}/transitions";
                    RestTemplate restTemplate = new RestTemplate();
                    ResponseEntity<JsonNode> response = restTemplate.exchange(apiUrl, HttpMethod.POST, requestEntity, JsonNode.class);

                    if (response.getStatusCode().is2xxSuccessful()) {
                        System.out.println("Jira ticket status updated successfully!");
                    } else {
                        System.err.println(STR."Failed to update Jira ticket status. Response: \{response.getBody()}");
                        break;
                    }

                    currentStatus = getCurrentStatus(issueKey);
                } else {
                    System.out.println("No next status. Exiting loop.");
                    break;
                }
            }
        } catch (HttpClientErrorException e) {
            System.err.println(STR."Error communicating with Jira API. Response: \{e.getResponseBodyAsString()}");
//            System.out.println(e);
        } catch (Exception e) {
            System.err.println(STR."An unexpected error occurred: \{e.getMessage()}");
        }

        return ResponseEntity.status(HttpStatus.OK).body("Status changed");
    }


    private int getTransitionId(String issueKey, String transitionName) {
        String apiUrl = STR."https://capstoneproject22.atlassian.net//rest/api/2/issue/\{issueKey}/transitions";
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(username, password);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<JsonNode> response = restTemplate.exchange(apiUrl, HttpMethod.GET, requestEntity, JsonNode.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            JsonNode transitions = Objects.requireNonNull(response.getBody()).get("transitions");

            for (JsonNode transition : transitions) {
                String name = transition.get("to").get("name").asText();
                if (name.equalsIgnoreCase(transitionName)) {
                    return transition.get("id").asInt();
                }
            }
            throw new RuntimeException(STR."Transition not found: \{transitionName}");
        } else {
            throw new RuntimeException(STR."Failed to retrieve transitions. Response: \{response.getBody()}");
        }
    }



    private String getNextStatus(String currentStatus) {
        return switch (currentStatus) {
            case "TO DO" -> "IN PROCESS";
            case "IN PROCESS" -> "DEV DONE";
            case "DEV DONE" -> "QA-DONE";
            case "QA-DONE" -> "DONE";
            case "DONE" -> null;
            default -> throw new RuntimeException("Unexpected current status: " + currentStatus);
        };
    }

    private String getCurrentStatus(String issueKey) {
        String apiUrl = STR."https://capstoneproject22.atlassian.net/rest/api/2/issue/\{issueKey}";
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(username, password);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<JsonNode> response = restTemplate.exchange(apiUrl, HttpMethod.GET, requestEntity, JsonNode.class);
//        System.out.println(response);
        if (response.getStatusCode().is2xxSuccessful()) {
            return Objects.requireNonNull(response.getBody()).get("fields").get("status").get("name").asText().toUpperCase();
        } else {
            throw new RuntimeException(STR."Failed to retrieve current status. Response: \{response.getBody()}");
        }
    }


}












