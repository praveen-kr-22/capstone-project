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


    public Map<String,List<Findings>> parseDependabotFinding(String body,Iterable<Findings> oldFindings,int orgID) {

        ObjectMapper objectMapper = new ObjectMapper();


        List<Map<String,Object>> oldFindingStoreHash = getHashWithID(oldFindings);
        List<Map<String , Object>> newFindingStoreHash = new ArrayList<>();

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
                    find.setOrgID(orgID);


                    Map<String,Object> temp = new HashMap<>();


                    temp.put("hashString",hashString);
                    temp.put("finding",find);
                    temp.put("status",find.getStatus());

                    newFindingStoreHash.add(temp);

                }
            }

        }catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        List<Map<String, Object>> newFindingHashWithoutLocalDup = localDuDupCheck.localDuDupCheck(newFindingStoreHash);


        return globalFindingCheck.getFindingWithoutDup(oldFindingStoreHash, newFindingHashWithoutLocalDup);
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
        int number = (int) data.get("number");
        String url = (String) data.get("url");
        String ecosystem = (String) data.get("ecosystem");
        String name = (String) data.get("name");
        String summary = (String) data.get("summary");
        String stringToBeHashed = ecosystem + name + summary;
        String hashString = sha256Hashing.hashing(stringToBeHashed);

        Integer ID = generateUniqueID.getUniqueID();
        String repoName = getRepoName.getRepoName(url);

        Findings find = new Findings();

        find.setNumber(number);
        find.setId(ID);
        find.setDescription((String) data.get("description"));
        find.setStatus((String) data.get("status"));
        find.setSummary(summary);
        find.setCveScore((Double) data.get("cveScore"));
        find.setSecurityLevel((String) data.get("securityLevel"));
        find.setToolName("dependabot");
        find.setEcoSystem(ecosystem);
        find.setName(name);
        find.setRepoName(repoName);
        find.setCreatedAt(new Date());
        find.setProductName("Demo App 2");
        find.setUpdatedAt(new Date());

        Map<String,Object> result = new HashMap<>();

        result.put("finding",find);
        result.put("hashString",hashString);
        result.put("ID",ID);

        return result;
    }

    private List<Map<String, Object>> getHashWithID(Iterable<Findings> findings) {

        List<Map<String,Object>> storeHash = new ArrayList<>();
        for(Findings find : findings){
            String name = find.getName();
            String ecoSystem = find.getEcoSystem();
            String summary = find.getSummary();
            long id = find.getId();
            String status = find.getStatus();
            String hashString = sha256Hashing.hashing( ecoSystem + name + summary);

            Map<String,Object> temp = new HashMap<>();

            temp.put("hashString",hashString);
            temp.put("finding",find);
            temp.put("status",status);

            storeHash.add(temp);
        }

        return storeHash;
    }



}
