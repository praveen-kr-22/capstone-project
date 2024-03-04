package com.armorcode.capstone.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Ticket")
public class Ticket {

    @Id
    @Column(name = "ticket_id")
    private String id;

    @Column(name = "ticket_key")
    private String key;

    @Column(name = "finding_id")
    private String findingsID;

    @Column(name = "ticket_system_name")
    private String ticketSystemName = "Jira";

    @Column(name = "ticket_type")
    private String ticketType = "Findings";

    @Column(name = "issue_type")
    private String issueType = "Task";

    @Column(name = "title")
    private String title;

    @Column(name = "priority")
    private String priority = "N/A";

    @Column(name = "ticket_Status")
    private String ticketStatus;

    @Column(name = "org_id")
    private int orgID;

    @Column(name="updated_at")
    private Date updatedAt;
}
