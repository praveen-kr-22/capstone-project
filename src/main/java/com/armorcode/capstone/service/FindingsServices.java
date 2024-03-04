package com.armorcode.capstone.service;


import com.armorcode.capstone.entity.Findings;
import com.armorcode.capstone.parser.CodeScanParser;
import com.armorcode.capstone.parser.DependabotParser;
import com.armorcode.capstone.parser.SecretScanParser;
import com.armorcode.capstone.repository.FindingsRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.Optional;


@Service
public class FindingsServices {

    @Value("${base.url}")
    private String baseURL;

    @Autowired
    FindingsRepo findingsRepo;
    @Autowired
    DependabotParser dependabotParser;
    @Autowired
    SecretScanParser secretScanParser;

    @Autowired
    CodeScanParser codeScanParser;


    public void updateFindingStatus(Findings newFind){
        Optional<Findings> existingFindingsOptional = findingsRepo.findById(newFind.getId());

        if (existingFindingsOptional.isPresent()) {
            Findings existingFinding = existingFindingsOptional.get();

            existingFinding.setStatus(newFind.getStatus());
            findingsRepo.save(existingFinding);
            System.out.println("Finding Status Update! in ES");
        } else {
            throw new IllegalArgumentException(STR."Product with ID \{newFind.getId()} not found");
        }

    }

    public Page<Findings> getAllFindings(Pageable pageable,int orgID){
        return findingsRepo.findByorgID(orgID,pageable);
    }

    public Findings getFindingByID(long id){
        return findingsRepo.findByid(id);
    }

    public Iterable<Findings> getAllFindings(int orgID){
        return findingsRepo.findByorgID(orgID);
    }

    public void saveAllFindings(Findings findings){
        findingsRepo.save(findings);
    }

    public void deletingAllFindings(){
        findingsRepo.deleteAll();
    }


    public Map<String,List<Findings>> getDependabotIssues(int orgID) {
        RestTemplate restTemplate = new RestTemplate();

        String url = STR."\{baseURL}/issues/dependabot";

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        String resBody =  response.getBody();

        Iterable<Findings> oldFindings = getAllFindings(orgID);

        return dependabotParser.parseDependabotFinding(resBody,oldFindings,orgID);
    }

    public Map<String,List<Findings>> getCodeScanIssues(int orgID) {
        RestTemplate restTemplate = new RestTemplate();

        String url = STR."\{baseURL}/issues/codescan";

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        String resBody =  response.getBody();

        Iterable<Findings> oldFindings = getAllFindings(orgID);

        return codeScanParser.parseCodeScanFinding(resBody,oldFindings,orgID);
    }

    public Map<String,List<Findings>> getSecretScanIssues(int orgID) {
        RestTemplate restTemplate = new RestTemplate();

        String url = STR."\{baseURL}/issues/secretscan";

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        String resBody =  response.getBody();

        Iterable<Findings> oldFindings = getAllFindings(orgID);

        return secretScanParser.parseSecretScanFinding(resBody,oldFindings,orgID);
    }


}
