package com.armorcode.capstone.service;


import com.armorcode.capstone.entity.Findings;
import com.armorcode.capstone.parser.CodeScanParser;
import com.armorcode.capstone.parser.DependabotParser;
import com.armorcode.capstone.parser.SecretScanParser;
import com.armorcode.capstone.repository.IssuesRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.data.domain.Pageable;

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


    public Page<Findings> getAllFindings(Pageable pageable){
        Page<Findings> findings = issuesRepo.findAll(pageable);
        return findings;
    }

    public Iterable<Findings> getAllFindings(){
        return  issuesRepo.findAll();
    }

    public void saveAllFindings(Findings findings){
        issuesRepo.save(findings);
    }

    public void deletingAllFindings(){
        issuesRepo.deleteAll();
    }


    public List<Findings> getDependabotIssues(Pageable pageable) {
        RestTemplate restTemplate = new RestTemplate();

        String url = "http://localhost:8080/issues/dependabot";

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        String resBody =  response.getBody();

        Iterable<Findings> oldFindings = getAllFindings();

        List<Findings> findings =  dependabotParser.parseDependabotFinding(resBody,oldFindings);
        return findings;
    }

    public List<Findings> getCodeScanIssues(org.springframework.data.domain.Pageable pageable) {
        RestTemplate restTemplate = new RestTemplate();

        String url = "http://localhost:8080/issues/codescan";

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        String resBody =  response.getBody();

        Iterable<Findings> oldFindings = getAllFindings();

        List<Findings> findings = codeScanParser.parseCodeScanFinding(resBody,oldFindings);
        return findings;
    }

    public List<Findings> getSecretScanIssues(org.springframework.data.domain.Pageable pageable) {
        RestTemplate restTemplate = new RestTemplate();

        String url = "http://localhost:8080/issues/secretscan";

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        String resBody =  response.getBody();

        Iterable<Findings> oldFindings = getAllFindings();

        List<Findings> findings = secretScanParser.parseSecretScanFinding(resBody,oldFindings);
        return findings;
    }


}
