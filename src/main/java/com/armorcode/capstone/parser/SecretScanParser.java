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
public class SecretScanParser {

    @Autowired
    GenerateUniqueID generateUniqueID;

    @Autowired
    SHA256Hashing sha256Hashing;

    @Autowired
    GetRepoName getRepoName;

    @Autowired
    LocalDupCheck localDuDupCheck;

    @Autowired
    GlobalFindingCheck globalFindingCheck;

    @Autowired
    GetMappingOfFinding getMappingOfFinding;


    public Map<String,List<Findings>> parseSecretScanFinding(String body,Iterable<Findings> oldFindings,int orgID) {

        ObjectMapper objectMapper = new ObjectMapper();


        List<Map<String,Object>> oldFindingStoreHash = getHashWithID(oldFindings);
        List<Map<String,Object>> newFindingStoreHash = new ArrayList<>();

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

        List<Map<String,Object>> newFindingHashWithoutLocalDup = localDuDupCheck.localDuDupCheck(newFindingStoreHash);


        return globalFindingCheck.getFindingWithoutDup(oldFindingStoreHash,newFindingHashWithoutLocalDup);
    }




    private Map<String,Object> getDataFromResponse(Object issue){

        String status  = (String) ((Map<?, ?>) issue).get("state");
        String url = (String) ((Map<?, ?>) issue).get("url");
        String summary = (String) ((Map<?, ?>) issue).get("secret_type_display_name");
        String secret = (String) ((Map<?, ?>) issue).get("secret");
        Integer number = (Integer) ((Map<?, ?>) issue).get("number");

        String repoName = getRepoName.getRepoName(url);

        String ACStatus = getMappingOfFinding.getMappingForStatus(status);



        Map<String,Object> data = new HashMap<>();

        data.put("number",number);
        data.put("status",ACStatus);
        data.put("repoName",repoName);
        data.put("summary",summary);
        data.put("secret",secret);

        return data;
    }



    private Map<String,Object> makeNewFinding(Map<String ,Object> data){
        String status = (String) data.get("status");
        String summary = (String) data.get("summary");
        String secret = (String) data.get("secret");
        String repoName = (String) data.get("repoName");
        int number = (int) data.get("number");

        Integer ID = generateUniqueID.getUniqueID();

        String hashString = sha256Hashing.hashing(repoName + secret + summary);


        Findings find = new Findings();


        find.setNumber(number);
        find.setId(ID);
        find.setRepoName(repoName);
        find.setSecretOfSecretScan(secret);
        find.setSummary(summary);
        find.setStatus(status);
        find.setToolName("secret scan");
        find.setDescription(STR."\{summary} \{secret}");
        find.setCreatedAt(new Date());
        find.setUpdatedAt(new Date());

        Map<String ,Object> result = new HashMap<>();

        result.put("finding",find);
        result.put("hashString",hashString);
        result.put("ID",ID);

        return result;
    }

    private List<Map<String, Object>> getHashWithID(Iterable<Findings> findings) {

        List<Map<String, Object>> storeHash = new ArrayList<>();

        for(Findings find : findings){
            String repoName = find.getRepoName();
            String secret = find.getSecretOfSecretScan();
            String summary = find.getSummary();
            long id = find.getId();
            String status = find.getStatus();
            String hashString = sha256Hashing.hashing(repoName + secret + summary);

            Map<String,Object> temp = new HashMap<>();

            temp.put("hashString",hashString);
            temp.put("finding",find);
            temp.put("status",status);

            storeHash.add(temp);
        }

        return storeHash;
    }


}
