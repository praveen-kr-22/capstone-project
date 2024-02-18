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


    public List<Findings> parseSecretScanFinding(String body,Iterable<Findings> oldFindings) {

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

        String status  = (String) ((Map<?, ?>) issue).get("state");
        String url = (String) ((Map<?, ?>) issue).get("url");
        String summary = (String) ((Map<?, ?>) issue).get("secret_type_display_name");
        String secret = (String) ((Map<?, ?>) issue).get("secret");

        String repoName = getRepoName.getRepoName(url);

        String ACStatus = getMappingOfFinding.getMappingForStatus(status);



        Map<String,Object> data = new HashMap<>();

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

        Integer ID = generateUniqueID.getUniqueID();

        String hashString = sha256Hashing.hashing(repoName + secret + summary);


        Findings find = new Findings();


        find.setId(ID);
        find.setRepoName(repoName);
        find.setSecretOfSecretScan(secret);
        find.setSummary(summary);
        find.setStatus(status);
        find.setToolName("SS");
        find.setDescription(STR."\{summary} \{secret}");
        find.setCreatedAt(new Date());

        Map<String ,Object> result = new HashMap<>();

        result.put("finding",find);
        result.put("hashString",hashString);
        result.put("ID",ID);

        return result;
    }

    private Map<String, Long> getHashWithID(Iterable<Findings> findings) {

        Map<String,Long> storeHash = new HashMap<>();

        for(Findings find : findings){
            String repoName = find.getRepoName();
            String secret = find.getSecretOfSecretScan();
            String summary = find.getSummary();
            long id = find.getId();
            String hashString = sha256Hashing.hashing(repoName + secret + summary);;
            storeHash.put(hashString,id);
        }

        return storeHash;
    }


}
