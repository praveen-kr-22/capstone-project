package com.armorcode.capstone.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GetMappingOfFinding {
    public String getMappingForSeverity(String severity){

        return switch (severity){
            case "critical" -> "Critical";
            case "high" -> "High";
            case "medium" -> "Medium";
            case "low" -> "Low";
            case "waring" -> "Info";
            default -> "False Positive";
        };
    }



    public String getMappingForStatus(String status){
        return switch (status){
            case "open" -> "Open";
            case "fixed","dismissed" -> "Mitigated";
            default -> "False Positive";
        };
    }

}
