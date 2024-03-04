package com.armorcode.capstone.rest;


import com.armorcode.capstone.dao.LoginRequest;
import com.armorcode.capstone.entity.Employee;
import com.armorcode.capstone.dao.UserData;
import com.armorcode.capstone.entity.Organization;
import com.armorcode.capstone.service.EmployeeService;
import com.armorcode.capstone.service.FeaturePrivilegeService;
import com.armorcode.capstone.service.OrganizationService;
import com.armorcode.capstone.util.GenerateSHA256Password;
import com.armorcode.capstone.util.Jwt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/v1/users")
public class AuthenticationController {

    @Autowired
    EmployeeService employeeService;

    @Autowired
    GenerateSHA256Password generateSHA256Password;

    @Autowired
    OrganizationService organizationService;

    @Autowired
    Jwt jwt;

    @Autowired
    FeaturePrivilegeService featurePrivilegeService;


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest){

        String email = loginRequest.getEmail();
        String password = loginRequest.getPassword();
        Employee employee = employeeService.getEmployee(email);

        if(employee == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Incorrect username or password.");
        }

        String currHashPassword = generateSHA256Password.hashPassword(password);
        String storePassword = employee.getPassword();

        if(!Objects.equals(storePassword, currHashPassword)){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Incorrect username or password.");
        }

        String JwtToken = jwt.generateToken(email);

        return ResponseEntity.status(HttpStatus.OK).body(JwtToken);
    }


    @GetMapping("/me")
    public ResponseEntity<?> getDetailsOfLogginUser(@RequestHeader("Authorization") String authorizationHeader) throws Exception {
        String token = authorizationHeader.replace("Bearer ", "");

        UserData user = jwt.getUserIdFromToken(token);

        if(user == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        Employee employee = employeeService.getEmployee(user.getEmail());

        if(employee == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        List<Object[]> featurePrivilege = featurePrivilegeService.findFeatureAndPrivilegeByEmail(employee.getEmail());
        user.setFeaturePrivilege(featurePrivilege);
        Organization organization = organizationService.getOrganizationDetail(employee.getOrgID());

        user.setOrgName(organization.getName());
        user.setRole(employee.getRole());

        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

}
