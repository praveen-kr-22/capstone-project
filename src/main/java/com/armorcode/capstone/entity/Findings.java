package com.armorcode.capstone.entity;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Document(indexName = "findings")
public class Findings {

    @Id
    private long id;

    private String toolName = "N/A";

    private String securityLevel = "N/A";

    private String status = "N/A";

    private String summary = "N/A";

    private String description = "N/A";

    private double cveScore = 0.0;

    private String productName = "Demo App";

    private String ecoSystem = "N/A";

    private String name = "N/A";

    private String pathNameOfIssue = "N/A";

    private String startColOfIssue = "N/A";

    private String endColOfIssue = "N/A";

    private String startLineOfIssue = "N/A";

    private String endLineOfIssue = "N/A";

    private String repoName = "N/A";
}
