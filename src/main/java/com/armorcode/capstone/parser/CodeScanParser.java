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
public class CodeScanParser {

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

    public List<Findings> parseCodeScanFinding(String body,Iterable<Findings> oldFindings) {

        ObjectMapper objectMapper = new ObjectMapper();


        Map<String,Long> oldFindingStoreHash = getHashWithID(oldFindings);
        Map<String , Pair<Findings,Integer>> newFindingStoreHash = new HashMap<>();


        try{
            Object[] issues = objectMapper.readValue(body, Object[].class);
            StringBuilder extractedField = new StringBuilder();

            for(Object issue : issues){
                if(issue instanceof Map){

                    Map<String,Object> data = getDataFromResponse(issue);
                    Map<String ,Object> result = makeNewFinding(data);

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
        Object rule = ((Map<?, ?>) issue).get("rule");
        Object most_recent_instance = ((Map<?,?>) issue).get("most_recent_instance");
        Object location = ((Map<?,?>) most_recent_instance).get("location");
        String url = (String) ((Map<?,?>) issue).get("url");


        String repoName = getRepoName.getRepoName(url);
//        System.out.println(repoName);


        Integer number = (Integer) ((Map<?, ?>) issue).get("number");
        String securityLevel = (String) ((Map<?, ?>) rule).get("security_severity_level");
        String status = (String) ((Map<?, ?>) issue).get("state");
        String summary = (String) ((Map<?, ?>) rule).get("description");
        String pathNameOfIssue = (String) ((Map<?,?>) location).get("path");
        String startColOfIssue = (String) Integer.toString((int) ((Map<?,?>) location).get("start_column"));
        String endColOfIssue = (String) Integer.toString((int) ((Map<?,?>) location).get("end_column"));
        String startLineOfIssue = (String) Integer.toString((int) ((Map<?,?>) location).get("start_line"));
        String endLineOfIssue = (String) Integer.toString((int) ((Map<?,?>) location).get("end_line"));

        String ACStatus = getMappingOfFinding.getMappingForStatus(status);
        String ACSecurityLevel = getMappingOfFinding.getMappingForSeverity(securityLevel);

        String description = STR."pathNameOfIssue: \{pathNameOfIssue}   \nstartLine: \{startLineOfIssue}  \nendLine: \{endLineOfIssue}  \nstartColumn: \{startColOfIssue}  \nendColumn: \{endColOfIssue}  \n";





        Map<String,Object> data = new HashMap<>();


        data.put("number",number);
        data.put("securityLevel",ACSecurityLevel);
        data.put("status",ACStatus);
        data.put("summary",summary);
        data.put("description",description);
        data.put("pathNameOfIssue",pathNameOfIssue);
        data.put("startColOfIssue",startColOfIssue);
        data.put("endColOfIssue",endColOfIssue);
        data.put("startLineOfIssue",startLineOfIssue);
        data.put("endLineOfIssue",endLineOfIssue);
        data.put("repoName",repoName);
        return data;
    }




    private Map<String, Object> makeNewFinding(Map<String ,Object> data){

        String pathNameOfIssue = (String) data.get("pathNameOfIssue");
        String startColOfIssue = (String) data.get("startColOfIssue");
        String endColOfIssue = (String) data.get("endColOfIssue");
        String startLineOfIssue = (String) data.get("startLineOfIssue");
        String endLineOfIssue = (String) data.get("endLineOfIssue");
        String repoName = (String) data.get("repoName");
        String hashString = sha256Hashing.hashing( pathNameOfIssue + startColOfIssue + endColOfIssue + startLineOfIssue + endLineOfIssue + repoName);

        Integer ID = generateUniqueID.getUniqueID();


        Findings find = new Findings();


        find.setId(ID);
        find.setDescription((String) data.get("description"));
        find.setStatus((String) data.get("status"));
        find.setSummary((String) data.get("summary"));
        find.setCveScore(0.0);
        find.setProductName("Demo App 1");
        find.setSecurityLevel((String) data.get("securityLevel"));
        find.setToolName("CS");
        find.setPathNameOfIssue((String) data.get("pathNameOfIssue"));
        find.setStartColOfIssue((String) data.get("startColOfIssue"));
        find.setEndColOfIssue((String) data.get("endColOfIssue"));
        find.setStartLineOfIssue((String) data.get("startLineOfIssue"));
        find.setEndLineOfIssue((String) data.get("endLineOfIssue"));
        find.setRepoName((String) data.get("repoName"));
        find.setCreatedAt(new Date());


        Map<String,Object> result = new HashMap<>();

        result.put("finding",find);
        result.put("hashString",hashString);
        result.put("ID",ID);

        return result;
    }

    private Map<String, Long> getHashWithID(Iterable<Findings> findings) {

        Map<String,Long> storeHash = new HashMap<>();
        for(Findings find : findings){
            String pathNameOfIssue = find.getPathNameOfIssue();
            String startColOfIssue = find.getStartColOfIssue();
            String endColOfIssue = find.getEndColOfIssue();
            String startLineOfIssue = find.getStartLineOfIssue();
            String endLineOfIssue = find.getEndLineOfIssue();
            String repoName = find.getRepoName();
            long id = find.getId();
            String hashString = sha256Hashing.hashing( pathNameOfIssue + startColOfIssue + endColOfIssue + startLineOfIssue + endLineOfIssue + repoName);
            storeHash.put(hashString,id);
        }


        return storeHash;
    }




}
