package com.armorcode.capstone.rest;

import com.armorcode.capstone.entity.Findings;
import com.armorcode.capstone.service.IssuesServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/findings")
public class FindingsController {

    @Autowired
    IssuesServices issuesServices;


    @PostMapping("/save")
    public List<Findings> getIssuesFromDependabot(){

        List<Findings> allFindings = new ArrayList<>();

        List<Findings> dependabotFindings = issuesServices.getDependabotIssues();

        List<Findings> codeScanFindings = issuesServices.getCodeScanIssues();

        List<Findings> secretScanFindings = issuesServices.getSecretScanIssues();

        allFindings.addAll(dependabotFindings);
        allFindings.addAll(codeScanFindings);
        if(secretScanFindings != null)
            allFindings.addAll(secretScanFindings);


        for(Findings find : allFindings){
            issuesServices.saveAllFindings(find);
        }

        return allFindings;
    }

    @GetMapping("")
    public Iterable<Findings> getAllFinging(){
        return issuesServices.getAllFindings();
    }

    @DeleteMapping("/deleteAll")
    public void deleteAllFinding(){
        issuesServices.deletingAllFindings();
    }

}
