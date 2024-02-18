package com.armorcode.capstone.rest;

import com.armorcode.capstone.entity.Findings;
import com.armorcode.capstone.service.IssuesServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/findings")
public class FindingsController {

    @Autowired
    IssuesServices issuesServices;


    @PostMapping("/save")
    public String getIssuesFromDependabot(Pageable pageable){

        List<Findings> allFindings = new ArrayList<>();

        List<Findings> dependabotFindings = issuesServices.getDependabotIssues(pageable);

        List<Findings> codeScanFindings = issuesServices.getCodeScanIssues(pageable);

        List<Findings> secretScanFindings = issuesServices.getSecretScanIssues(pageable);

        allFindings.addAll(dependabotFindings);
        allFindings.addAll(codeScanFindings);
        if(secretScanFindings != null)
            allFindings.addAll(secretScanFindings);


        for(Findings find : allFindings){
            issuesServices.saveAllFindings(find);
        }

        int dataSize = allFindings.size();
        String message = dataSize + " New Findings";
        return message;
    }

    @GetMapping("")
    public ResponseEntity<Page<Findings>> getAllFinging(Pageable pageable){
        Page<Findings> findings = issuesServices.getAllFindings(pageable);
        return ResponseEntity.ok().body(findings);
    }

    @GetMapping("/allfindings")
    public Iterable<Findings> getAllFinding(){
        return issuesServices.getAllFindings();
    }


    @DeleteMapping("/deleteAll")
    public void deleteAllFinding(){
        issuesServices.deletingAllFindings();
    }

}
