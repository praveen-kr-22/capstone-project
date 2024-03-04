package com.armorcode.capstone.service;


import com.armorcode.capstone.entity.Ticket;
import com.armorcode.capstone.repository.TicketRepo;
import com.armorcode.capstone.rest.TicketController;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
public class SqlTicketServices {

    @Autowired
    TicketRepo ticketRepo;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public int getTotalPages(Page<Ticket> page) {
        return page.getTotalPages();
    }
    public Page<Ticket> getAllTickets(Pageable pageable,int orgID){
        return ticketRepo.findByorgID(pageable,orgID);
    }

    public void saveModifyTicket(Ticket ticket){
        ticketRepo.save(ticket);
    }

    public Ticket getTicketsFromFindingID(String id){
       return ticketRepo.findByfindingsID(id);
    }

    public void saveNewTicketOnSql(String key,int orgID){


        String url = "https://capstoneproject22.atlassian.net/rest/api/2/issue/" + key;
        String username = "kumarp222909@gmail.com";
        String password = "ATATT3xFfGF0yGWfT52T41Hvu52b5oISqZdrknsG7JJB-ttz0rFYjwnbBLhXZedextbCh21fiDc9eQCl-Shq_21MzsfnF-Y1iSNpoqv6cVoaJx70LuaVVpinBlf7gBqDjmgPrPLclEg02Lm-Y2zbMxshK18uGs5TYRSvIWq_byqb5ly18QaRngA=6A08E6A1";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBasicAuth(username,password);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<JsonNode> response = restTemplate.exchange(url, HttpMethod.GET,entity, JsonNode.class);
        if (response.getStatusCode().is2xxSuccessful()) {
            Ticket ticket = getTicket(Objects.requireNonNull(response.getBody()));
            ticket.setOrgID(orgID);
            ticket.setUpdatedAt(new Date());
            ticketRepo.save(ticket);
//            System.out.println("Ticket Save");
        } else {
            System.out.println(STR."Failed to create Jira ticket. Status code: \{response.getStatusCode()}");
        }

    }

    private Ticket getTicket(JsonNode body) {
        Ticket ticket = new Ticket();

        String id = String.valueOf(body.get("id")).replace("\"", "");
        String key = String.valueOf(body.get("key")).replace("\"", "");
        String issueType = String.valueOf(body.get("fields").get("issuetype").get("name")).replace("\"", "");
        String title = String.valueOf(body.get("fields").get("summary")).replace("\"", "");
        String priority = String.valueOf(body.get("fields").get("priority").get("name")).replace("\"", "");
        String ticketStatus = String.valueOf(body.get("fields").get("status").get("statusCategory").get("name")).replace("\"", "");
        String desc = String.valueOf(body.get("fields").get("description"));

        int lastWhitespaceIndex = desc.lastIndexOf(" ");

        String findingID = desc.substring(lastWhitespaceIndex + 1).replace("\"", "");

        ticket.setId(id);
        ticket.setKey(key);
        ticket.setIssueType(issueType);
        ticket.setTitle(title);
        ticket.setPriority(priority);
        ticket.setTicketStatus(ticketStatus);
        ticket.setFindingsID(findingID);
        return ticket;
    }

}


