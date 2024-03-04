package com.armorcode.capstone.rest;

import com.armorcode.capstone.dao.UserData;
import com.armorcode.capstone.entity.Employee;
import com.armorcode.capstone.entity.Findings;
import com.armorcode.capstone.service.EmployeeService;
import com.armorcode.capstone.service.FeaturePrivilegeService;
import com.armorcode.capstone.service.FindingsServices;
import com.armorcode.capstone.service.KafkaProducerService;
import com.armorcode.capstone.util.Jwt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;

import java.util.*;

@RestController
@RequestMapping("/findings")
public class FindingsController {

    @Autowired
    FindingsServices findingsServices;

    @Autowired
    KafkaProducerService kafkaProducerService;

    @Autowired
    EmployeeService employeeService;

    @Autowired
    Jwt jwt;

    @Autowired
    FeaturePrivilegeService featurePrivilegeService;

    @GetMapping("/save")
    public ResponseEntity<?> getIssuesFromDependabot(@RequestHeader("Authorization") String authorizationHeader) throws Exception {


        String token = authorizationHeader.replace("Bearer ", "");

        UserData user = jwt.getUserIdFromToken(token);

        if(user == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        Employee employee = employeeService.getEmployee(user.getEmail());

        if(employee == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        String role = employee.getRole();

        if(Objects.equals(role, "User")){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access to the requested resource is not allowed.");
        }

        int orgID = employee.getOrgID();


        List<Findings> allNewFindings = new ArrayList<>();
        List<Findings> allUpdatedFindings = new ArrayList<>();

        Map<String,List<Findings>> dependabotFindings = findingsServices.getDependabotIssues(orgID);
        List<Findings> dependabotNewFindings = dependabotFindings.get("newFindings");
        List<Findings> dependabotUpdatedFindings = dependabotFindings.get("updatedFindings");

        Map<String,List<Findings>> codeScanFindings = findingsServices.getCodeScanIssues(orgID);
        List<Findings> codeScanNewFindings = codeScanFindings.get("newFindings");
        List<Findings> codeScanUpdatedFindings = codeScanFindings.get("updatedFindings");

        Map<String,List<Findings>> secretScanFindings = findingsServices.getSecretScanIssues(orgID);
        List<Findings> secretScanNewFindings = secretScanFindings.get("newFindings");
        List<Findings> secretScanUpdateFindings = secretScanFindings.get("updatedFindings");
        if(dependabotNewFindings != null)
            allNewFindings.addAll(dependabotNewFindings);
        if(dependabotUpdatedFindings != null){
            allUpdatedFindings.addAll(dependabotUpdatedFindings);
        }

        if(codeScanNewFindings != null){
            allNewFindings.addAll(codeScanNewFindings);
        }
        if(codeScanUpdatedFindings != null){
            allUpdatedFindings.addAll(codeScanUpdatedFindings);
        }

        if(secretScanNewFindings != null){
            allNewFindings.addAll(secretScanNewFindings);
        }
        if(secretScanUpdateFindings != null){
            allUpdatedFindings.addAll(secretScanUpdateFindings);
        }

        for(Findings find : allNewFindings){
            kafkaProducerService.sendNewFinding(find);
            findingsServices.saveAllFindings(find);
        }

        for(Findings find : allUpdatedFindings){
            kafkaProducerService.sendUpdateFinding(find);
            findingsServices.updateFindingStatus(find);
        }

        int dataSize = allNewFindings.size();
        return  ResponseEntity.status(HttpStatus.OK).body(STR."\{dataSize}New Findings");
    }

    @GetMapping("")
    public ResponseEntity<?> getAllFinging(@RequestParam(defaultValue = "0") int pageNumber,
                                                        @RequestParam(defaultValue = "15") int pageSize,
                                           @RequestHeader("Authorization") String authorizationHeader) throws Exception {

        String token = authorizationHeader.replace("Bearer ", "");

        UserData user = jwt.getUserIdFromToken(token);

        if(user == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        Employee employee = employeeService.getEmployee(user.getEmail());

        if(employee == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        int ordID = employee.getOrgID();

        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("updatedAt").descending());
        Page<Findings> page = findingsServices.getAllFindings(pageable,ordID);
        Map<String,Object> result = new HashMap<>();
        result.put("totalPage",page.getTotalPages());
        result.put("content",page.getContent());
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/allfindings")
    public ResponseEntity<?> getAllFinding(@RequestParam String employeeID){
        Employee employee = employeeService.getEmployee(employeeID);
        if(employee == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("The requested user could not be found.");
        }

        int ordID = employee.getOrgID();

        Iterable<Findings> findings = findingsServices.getAllFindings(ordID);

        return ResponseEntity.status(HttpStatus.OK).body(findings);
    }


    @DeleteMapping("/deleteAll")
    public void deleteAllFinding(){
        findingsServices.deletingAllFindings();
    }

}
