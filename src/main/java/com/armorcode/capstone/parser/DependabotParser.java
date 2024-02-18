package com.armorcode.capstone.parser;

import com.armorcode.capstone.entity.Findings;
import com.armorcode.capstone.util.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class DependabotParser {

    @Autowired
    GenerateUniqueID generateUniqueID;

    @Autowired
    SHA256Hashing sha256Hashing;

    @Autowired
    GetRepoName getRepoName;

    @Autowired
    GlobalFindingCheck globalFindingCheck;

    @Autowired
    LocalDupCheck localDuDupCheck;

    @Autowired
    GetMappingOfFinding getMappingOfFinding;


    public List<Findings> parseDependabotFinding(String body,Iterable<Findings> oldFindings) {

        ObjectMapper objectMapper = new ObjectMapper();


        Map<String,Long> oldFindingStoreHash = getHashWithID(oldFindings);
        Map<String , Pair<Findings,Integer>> newFindingStoreHash = new HashMap<>();

        try{
            Object[] issues = objectMapper.readValue(body, Object[].class);
            StringBuilder extractedField = new StringBuilder();

            for(Object issue : issues){
                if(issue instanceof Map){

                    Map<String,Object> data = getDataFromResponse(issue);
                    Map<String,Object> result = makeNewFinding(data);

                    Findings find = (Findings) result.get("finding");
                    String hashString = (String) result.get("hashString");

                    Integer ID = (Integer) result.get("ID");


                    Pair<Findings,Integer> temp = Pair.of(find,ID);


                    newFindingStoreHash.put(hashString,temp);

                }
            }

        }catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        Map<String, Pair<Findings, Integer>> newFindingHashWithoutLocalDup = localDuDupCheck.localDuDupCheck(newFindingStoreHash);

        List<Findings> findings = globalFindingCheck.getFindingWithoutDup(oldFindingStoreHash,newFindingHashWithoutLocalDup);

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
        String url = (String) ((Map<?, ?>) issue).get("url");

        String ACStatus = getMappingOfFinding.getMappingForStatus(status);
        String ACSecurityLevel = getMappingOfFinding.getMappingForSeverity(securityLevel);

        Map<String,Object> data = new HashMap<>();

        data.put("ecosystem",ecosystem);
        data.put("name",name);
        data.put("number",number);
        data.put("securityLevel",ACSecurityLevel);
        data.put("status",ACStatus);
        data.put("summary",summary);
        data.put("description",description);
        data.put("cveScore",cveScore);
        data.put("url",url);
        return data;
    }



    private Map<String,Object> makeNewFinding(Map<String ,Object> data){
        String url = (String) data.get("url");
        String ecosystem = (String) data.get("ecosystem");
        String name = (String) data.get("name");
        String summary = (String) data.get("summary");
        String stringToBeHashed = ecosystem + name + summary;
        String hashString = sha256Hashing.hashing(stringToBeHashed);

        Integer ID = generateUniqueID.getUniqueID();
        String repoName = getRepoName.getRepoName(url);

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
        find.setRepoName(repoName);
        find.setCreatedAt(new Date());
        find.setProductName("Demo App 2");

        Map<String,Object> result = new HashMap<>();

        result.put("finding",find);
        result.put("hashString",hashString);
        result.put("ID",ID);

        return result;
    }

    private Map<String, Long> getHashWithID(Iterable<Findings> findings) {

        Map<String,Long> storeHash = new HashMap<>();
        for(Findings find : findings){
            String name = find.getName();
            String ecoSystem = find.getEcoSystem();
            String summary = find.getSummary();
            long id = find.getId();
            String hashString = sha256Hashing.hashing( ecoSystem + name + summary);
            storeHash.put(hashString,id);
        }

        return storeHash;
    }



}
