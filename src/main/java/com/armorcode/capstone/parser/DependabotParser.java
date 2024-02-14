package com.armorcode.capstone.parser;

import com.armorcode.capstone.entity.Findings;
import com.armorcode.capstone.util.GenerateUniqueID;
import com.armorcode.capstone.util.SHA256Hashing;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class DependabotParser {

    @Autowired
    GenerateUniqueID generateUniqueID;

    @Autowired
    SHA256Hashing sha256Hashing;


    public List<Findings> parseDependabotFinding(String body,Iterable<Findings> oldFindings) {

        ObjectMapper objectMapper = new ObjectMapper();

        List<Findings> findings = new ArrayList<>();
        Map<String,Long> storeHash = new HashMap<>();
        for(Findings find : oldFindings){
            String name = find.getName();
            String ecoSystem = find.getEcoSystem();
            String summary = find.getSummary();
            long id = find.getId();
            String hashString = sha256Hashing.hashing( ecoSystem + name + summary);
            storeHash.put(hashString,id);
        }

        try{
            Object[] issues = objectMapper.readValue(body, Object[].class);
            StringBuilder extractedField = new StringBuilder();

            for(Object issue : issues){
                if(issue instanceof Map){

                    Map<String,Object> data = getDataFromResponse(issue);
                    Map<String,Object> result = makeNewFinding(data);

                    Findings find = (Findings) result.get("finding");
                    String hashString = (String) result.get("hashString");

                    boolean isDup = false;

                    if(storeHash.containsKey(hashString)){
                        isDup = true;
                    }

                    if(!isDup){
                        findings.add(find);
                    }

                }
            }

        }catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        System.out.println(findings.size());
        return findings;
    }


    private Map<String,Object> getDataFromResponse(Object issue){
        Object security_advisory = ((Map<?, ?>) issue).get("security_advisory");
        Object dependency = ((Map<?, ?>) issue).get("dependency");
        Object packages = ((Map<?, ?>) dependency).get("package");
        Object cvss = ((Map<?, ?>) security_advisory).get("cvss");

        String ecosystem = (String) ((Map<?, ?>) packages).get("ecosystem");
        String name = (String) ((Map<?, ?>) packages).get("name");
        Integer number = (Integer) ((Map<?, ?>) issue).get("number");
        String securityLevel = (String) ((Map<?, ?>) security_advisory).get("severity");
        String status = (String) ((Map<?, ?>) issue).get("state");
        String summary = (String) ((Map<?, ?>) security_advisory).get("summary");
        String description = (String) ((Map<?, ?>) security_advisory).get("description");
        double cveScore = (double) ((Map<?, ?>) cvss).get("score");

        Map<String,Object> data = new HashMap<>();

        data.put("ecosystem",ecosystem);
        data.put("name",name);
        data.put("number",number);
        data.put("securityLevel",securityLevel);
        data.put("status",status);
        data.put("summary",summary);
        data.put("description",description);
        data.put("cveScore",cveScore);
        return data;
    }



    private Map<String,Object> makeNewFinding(Map<String ,Object> data){
        String ecosystem = (String) data.get("ecosystem");
        String name = (String) data.get("name");
        String summary = (String) data.get("summary");
        String stringToBeHashed = ecosystem + name + summary;
        String hashString = sha256Hashing.hashing(stringToBeHashed);

        Integer ID = generateUniqueID.getUniqueID();


        Findings find = new Findings();


        find.setId(ID);
        find.setDescription((String) data.get("description"));
        find.setStatus((String) data.get("status"));
        find.setSummary(summary);
        find.setCveScore((Double) data.get("cveScore"));
        find.setSecurityLevel((String) data.get("securityLevel"));
        find.setToolName("DB");
        find.setEcoSystem(ecosystem);
        find.setName(name);

        Map<String ,Object> result = new HashMap<>();

        result.put("finding",find);
        result.put("hashString",hashString);

        return result;
    }





}
