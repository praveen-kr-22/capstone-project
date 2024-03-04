package com.armorcode.capstone.rest;

import com.armorcode.capstone.dao.UserData;
import com.armorcode.capstone.entity.Employee;
import com.armorcode.capstone.entity.Findings;
import com.armorcode.capstone.entity.Ticket;
import com.armorcode.capstone.service.*;
import com.armorcode.capstone.util.Jwt;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;


@RestController
@RequestMapping("/ticket")
public class TicketController {

    @Autowired
    JiraTicketServices jiraTicketServices;

    @Autowired
    SqlTicketServices sqlTicketServices;

    @Autowired
    EmployeeService employeeService;

    @Autowired
    Jwt jwt;

    @Autowired
    FeaturePrivilegeService featurePrivilegeService;

    @PostMapping("/new")
    public String createNewTicketWithJira(@RequestBody Findings findings) throws InterruptedException {
        int orgID = findings.getOrgID();
        Gson gson = new Gson();
        String res = jiraTicketServices.createNewJiraTicket(findings);
        JsonObject jsonObject = gson.fromJson(res, JsonObject.class);
        String key = String.valueOf(jsonObject.get("key"));
        key = key.replace("\"", "");
        sqlTicketServices.saveNewTicketOnSql(key,orgID);
        return res;
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllTickets(@RequestParam(defaultValue = "0") int pageNumber,
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

        int orgID = employee.getOrgID();

        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("updatedAt").descending());
        Page<Ticket> page = sqlTicketServices.getAllTickets(pageable,orgID);
        Map<String,Object> result = new HashMap<>();
        result.put("TotalPage",page.getTotalPages());
        result.put("Tickets",page.getContent());
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }


    @PostMapping("/close-ticket")
    public ResponseEntity<String> closeTicket(@RequestParam String ticketId,@RequestHeader("Authorization") String authorizationHeader) throws Exception {

        String token = authorizationHeader.replace("Bearer ", "");

        UserData user = jwt.getUserIdFromToken(token);

        if(user == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        Employee employee = employeeService.getEmployee(user.getEmail());

        if(employee == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }


        Long isValidPrivilage = featurePrivilegeService.findByEmployeeEmailAndFeatureNameAndPrivilegeName(user.getEmail(), "close","finding");

        if(!Objects.equals(employee.getRole(), "Admin") && isValidPrivilage == 0){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("UNAUTHORIZED");
        }

        jiraTicketServices.closeJiraTicket(ticketId);
        return ResponseEntity.ok(STR."Ticket \{ticketId} closed successfully");
    }

}
