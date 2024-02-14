package com.armorcode.capstone.parser;

import com.armorcode.capstone.entity.Findings;
import com.armorcode.capstone.util.GenerateUniqueID;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class SecretScanParser {

    @Autowired
    GenerateUniqueID generateUniqueID;
    public List<Findings> parseSecretScanFinding(String body) {

        ObjectMapper objectMapper = new ObjectMapper();

        List<Findings> findings = new ArrayList<>();

        try{
            Object[] issues = objectMapper.readValue(body, Object[].class);
            StringBuilder extractedField = new StringBuilder();

            for(Object issue : issues){
                if(issue instanceof Map){

                    Integer number = (Integer) ((Map<?, ?>) issue).get("number");
                    String status = (String) ((Map<?, ?>) issue).get("state");
                    Integer cveScore = (int) (Math.random() * 10.0);
                    String productName = "Demo App";

                    Integer ID = generateUniqueID.getUniqueID();
                    Findings find = new Findings();
                    find.setId(ID);
                    find.setDescription("N/A");
                    find.setStatus(status);
                    find.setSummary("N/A");
                    find.setCveScore(0.0);
                    find.setProductName(productName);
                    find.setSecurityLevel("N/A");
                    find.setToolName("SS");

                    findings.add(find);
                }
            }

        }catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return findings;
    }
}
