package com.armorcode.capstone.service;

import com.armorcode.capstone.entity.Findings;
import com.armorcode.capstone.entity.Ticket;
import com.armorcode.capstone.repository.TicketRepo;
import com.armorcode.capstone.util.NewFindingsRunbook;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.List;


@Service
public class KafkaConsumerService {

    @Value("${base.url}")
    private String baseURL;


    @Autowired
    SqlTicketServices sqlTicketServices;

    @Autowired
    JiraTicketServices jiraTicketServices;

    @Autowired
    NewFindingsRunbook newFindingsRunbook;

    @KafkaListener(topics = "new-findings", groupId = "findings")
    public void listen(Findings message) {
//        System.out.println(message);

        int numberOfTicketCreate = newFindingsRunbook.isValidFindings(message);
//        System.out.println(numberOfTicketCreate);

        for(int i = 0;i<numberOfTicketCreate;i++){
            RestTemplate restTemplate = new RestTemplate();

            String url = STR."\{baseURL}/ticket/new";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Findings> requestEntity = new HttpEntity<>(message, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                String responseBody = response.getBody();
//            System.out.println("Response body: " + responseBody);

            } else {
                System.err.println(STR."Failed to send POST request. Status code: \{response.getStatusCode()}");
            }
        }
    }


    @KafkaListener(topics = "updated_finding", groupId = "findings")
    private void listenUpdateFinding(Findings message) {
        String id = String.valueOf(message.getId());
        int orgID = message.getOrgID();
        Ticket ticket = sqlTicketServices.getTicketsFromFindingID(id);

        if(ticket == null){
            return;
        }

        String ticketID = ticket.getId();

        ResponseEntity<String> response = jiraTicketServices.updateJiraTicketStatus2(ticketID);

//        System.out.println(ticket);
        if(response.getStatusCode().is2xxSuccessful()){
            ticket.setTicketStatus("Done");
            ticket.setUpdatedAt(new Date());
            sqlTicketServices.saveModifyTicket(ticket);
            System.out.println("Save Updated Ticket");
        }else {
            System.out.println(STR."Failed to save updated ticket. Status code: \{response.getStatusCode()}");
        }

    }
}
