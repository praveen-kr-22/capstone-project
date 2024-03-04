package com.armorcode.capstone.rest;

import com.armorcode.capstone.dao.UserData;
import com.armorcode.capstone.entity.Employee;
import com.armorcode.capstone.entity.Runbook;
import com.armorcode.capstone.service.EmployeeService;
import com.armorcode.capstone.service.FeaturePrivilegeService;
import com.armorcode.capstone.service.RunbookService;
import com.armorcode.capstone.util.Jwt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/runbook")
public class RunbookController {

    @Autowired
    private RunbookService runbookService;

    @Autowired
    Jwt jwt;

    @Autowired
    EmployeeService employeeService;

    @Autowired
    FeaturePrivilegeService featurePrivilegeService;

    @GetMapping
    public List<Runbook> getAllRunbooks() {
        return runbookService.getAllRunbooks();
    }

    @GetMapping("/{id}")
    public Optional<Runbook> getRunbookById(@PathVariable Long id) {
        return runbookService.getRunbookById(id);
    }

    @PostMapping
    public ResponseEntity<?> createRunbook(@RequestBody Runbook newRunbook,@RequestHeader("Authorization") String authorizationHeader) throws Exception {
        String token = authorizationHeader.replace("Bearer ", "");

        UserData user = jwt.getUserIdFromToken(token);

        if(user == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        Employee employee = employeeService.getEmployee(user.getEmail());

        if(employee == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("UNAUTHORIZED");
        }

        Long valid = featurePrivilegeService.findByEmployeeEmailAndFeatureNameAndPrivilegeName(user.getEmail(),"write","runbook");

        if(valid == 0){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("UNAUTHORIZED");
        }

        Runbook runbook = runbookService.createRunbook(newRunbook);
        return ResponseEntity.status(HttpStatus.OK).body(runbook);
    }

    @PutMapping("/{id}")
    public Runbook updateRunbook(@PathVariable Long id, @RequestBody Runbook newRunbook) {
        return runbookService.updateRunbook(id, newRunbook);
    }

    @DeleteMapping("/{id}")
    public void deleteRunbook(@PathVariable Long id) {
        runbookService.deleteRunbook(id);
    }
}
