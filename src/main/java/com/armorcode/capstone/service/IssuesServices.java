package com.armorcode.capstone.service;


import com.armorcode.capstone.entity.Findings;
import com.armorcode.capstone.parser.CodeScanParser;
import com.armorcode.capstone.parser.DependabotParser;
import com.armorcode.capstone.parser.SecretScanParser;
import com.armorcode.capstone.repository.IssuesRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;


@Service
public class IssuesServices {

    @Autowired
    IssuesRepo issuesRepo;
    @Autowired
    DependabotParser dependabotParser;
    @Autowired
    SecretScanParser secretScanParser;

    @Autowired
    CodeScanParser codeScanParser;


    public Iterable<Findings> getAllFindings(){
        Iterable<Findings> findings = issuesRepo.findAll();
        return findings;
    }

    public void saveAllFindings(Findings findings){
        issuesRepo.save(findings);
    }

    public void deletingAllFindings(){
        issuesRepo.deleteAll();
    }


    public List<Findings> getDependabotIssues() {
        RestTemplate restTemplate = new RestTemplate();

        String url = "http://localhost:8080/issues/dependabot";

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        String resBody =  response.getBody();

        Iterable<Findings> oldFindings = getAllFindings();

        List<Findings> findings =  dependabotParser.parseDependabotFinding(resBody,oldFindings);
        return findings;
    }

    public List<Findings> getCodeScanIssues() {
        RestTemplate restTemplate = new RestTemplate();

        String url = "http://localhost:8080/issues/codescan";

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        String resBody =  response.getBody();

        Iterable<Findings> oldFindings = getAllFindings();

        List<Findings> findings = codeScanParser.parseCodeScanFinding(resBody,oldFindings);
        return findings;
    }

    public List<Findings> getSecretScanIssues() {
        RestTemplate restTemplate = new RestTemplate();

        String url = "http://localhost:8080/issues/secretscan";

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        String resBody =  response.getBody();

        List<Findings> findings = secretScanParser.parseSecretScanFinding(resBody);
        return findings;
    }


}
