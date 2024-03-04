package com.armorcode.capstone.util;


import com.armorcode.capstone.entity.Findings;
import com.armorcode.capstone.entity.Runbook;
import com.armorcode.capstone.service.RunbookService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.json.Json;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

@Component
public class NewFindingsRunbook {

    @Autowired
    RunbookService runbookService;

    public int isValidFindings(Findings findings){

        if(!Objects.equals(findings.getStatus(), "Open")){
            return 0;
        }

        List<Runbook> allRunbook = runbookService.getAllRunbooks();
//        System.out.println("allRunbook -> " + allRunbook);
//        System.out.println("allRunbook size -> " + allRunbook.size());

        if(allRunbook.isEmpty()){
            return 1;
        }
        List<Runbook> findingsRunbook = new ArrayList<>();

        for(Runbook runbook : allRunbook){
            if(Objects.equals(runbook.getInput(), "new finding")){
                findingsRunbook.add(runbook);
            }
        }

        if(findingsRunbook.isEmpty()){
            return 1;
        }

        List<JsonNode> filters = new ArrayList<>();
        int count = 0;
        for(Runbook runbook : findingsRunbook){
            boolean isValidFinding = isValidFindingForThisRunbook(findings,runbook);
            if(isValidFinding){
                count++;
            }
        }

        return count;
    }


    private boolean isValidFindingForThisRunbook(Findings findings, Runbook runbook){

        JsonNode filter = convertStringToObject(runbook.getTask());

        String toolNameFind = findings.getToolName();
        String securityLevelFind = findings.getSecurityLevel();
        String productNameFind = findings.getProductName();

        boolean isToolNameValid = true; // Set to true by default
        boolean isSecurityLevelValid = true; // Set to true by default
        boolean isProductNameValid = true; // Set to true by default

        assert filter != null;
        for (JsonNode node : filter.get("filter")) {
            String type = node.get("type").asText();
            JsonNode values = node.get("values");

            if (Objects.equals(type, "toolName")) {
                isToolNameValid = false; // Reset to false if type is present
                for (JsonNode valueNode : values) {
                    String toolName = valueNode.asText();
//                    System.out.println(toolName);
                    if (Objects.equals(toolNameFind, toolName)) {
                        isToolNameValid = true;
                        break; // Exit loop if a match is found
                    }
                }
            } else if (Objects.equals(type, "securityLevel")) {
                isSecurityLevelValid = false; // Reset to false if type is present
                for (JsonNode valueNode : values) {
                    String securityLevel = valueNode.asText();
//                    System.out.println(securityLevel);
                    if (Objects.equals(securityLevelFind, securityLevel)) {
                        isSecurityLevelValid = true;
                        break; // Exit loop if a match is found
                    }
                }
            } else if (Objects.equals(type, "productName")) {
                isProductNameValid = false; // Reset to false if type is present
                for (JsonNode valueNode : values) {
                    String productName = valueNode.asText();
//                    System.out.println(productName);
                    if (Objects.equals(productNameFind, productName)) {
                        isProductNameValid = true;
                        break; // Exit loop if a match is found
                    }
                }
            }
        }

        return (isProductNameValid && isToolNameValid && isSecurityLevelValid);
    }




    private JsonNode convertStringToObject(String jsonString){
        try {
            // Convert JSON string to JSON object
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readTree(jsonString);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }





}
