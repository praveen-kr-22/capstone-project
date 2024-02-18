package com.armorcode.capstone.rest;


import com.armorcode.capstone.entity.Findings;
import com.armorcode.capstone.service.IssuesServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@RestController
@RequestMapping("/dashboard")
public class DashBoardController {

    @Autowired
    IssuesServices issuesServices;

    @GetMapping("/allinfo")
    public Map<String, Map<String, int[]>> getAllDashBoardInformation(){
        Iterable<Findings> allFindings = issuesServices.getAllFindings();

        Map<String,int[]> findingsStatusInfo = getFindingsStatusInfo(allFindings);
        Map<String ,int[]> findingsAge = getNewFindingsAgeInfo(allFindings);
        Map<String,int[]> findingsByApp = getFindingsInfoByApp(allFindings);
        Map<String, int[]> findingsOverTime = getFindingsOverTime(allFindings);


        Map<String ,Map<String, int[]>> info = new HashMap<>();

        info.put("status",findingsStatusInfo);
        info.put("findingsAge",findingsAge);
        info.put("findingsApp",findingsByApp);
        info.put("findingsOverTime",findingsOverTime);

        return info;
    }

    private Map<String, int[]> getFindingsOverTime(Iterable<Findings> allFindings) {

        Map<String ,int[]> result = new HashMap<>();

        result.put("Aug 2023",new int[1]);
        result.put("Sep 2023",new int[1]);
        result.put("Oct 2023",new int[1]);
        result.put("Nov 2023",new int[1]);
        result.put("Dec 2023",new int[1]);
        result.put("Jan 2024",new int[1]);
        result.put("Feb 2024",new int[1]);

        for(Findings find : allFindings){
            Date createdAt = find.getCreatedAt();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(createdAt);

            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;

            if(year == 2023 && month == 8){
                int[] val = result.get("Aug 2023");
                val[0]++;
                result.put("Aug 2023",val);
            } else if(year == 2023 && month == 9){
                int[] val = result.get("Sep 2023");
                val[0]++;
                result.put("Sep 2023",val);
            } else if(year == 2023 && month == 10){
                int[] val = result.get("Oct 2023");
                val[0]++;
                result.put("Oct 2023",val);
            }else if(year == 2023 && month == 11){
                int[] val = result.get("Nov 2023");
                val[0]++;
                result.put("Nov 2023",val);
            }else if(year == 2023 && month == 12){
                int[] val = result.get("Dec 2023");
                val[0]++;
                result.put("Dec 2023",val);
            } else if(year == 2024 && month == 1){
                int[] val = result.get("Jan 2024");
                val[0]++;
                result.put("Jan 2024",val);
            }else if(year == 2024 && month == 2){
                int[] val = result.get("Feb 2024");
                val[0]++;
                result.put("Feb 2024",val);
            }

        }

        return result;
    }

    private Map<String,int[]> getFindingsInfoByApp(Iterable<Findings> allFindings) {

        Map<String ,int[]> result = new HashMap<>();

        for(Findings find : allFindings){
            String productName = find.getProductName();
            String securityLevel = find.getSecurityLevel();

            int[] temp;
            if(result.containsKey(productName)){
                temp = result.get(productName);
            }else{
                temp = new int[2];
            }
            if(Objects.equals(securityLevel, "Critical")){
                temp[0]++;
            } else if (Objects.equals(securityLevel, "High")) {
                temp[1]++;
            }
            result.put(productName,temp);
        }

        return result;
    }


    private Map<String,int[]> getFindingsStatusInfo(Iterable<Findings> allFindings){
        int[] criticalByStatus = new int[4];
        int[] highByStatus = new int[4];
        int[] mediumByStatus = new int[4];
        int[] lowByStatus = new int[4];

        for(Findings find : allFindings){
            String securityLevel = find.getSecurityLevel();
            String status = find.getStatus();
            if(Objects.equals(securityLevel, "Critical")){
                criticalByStatus[3]++;
                if (Objects.equals(status, "Open")) {
                    criticalByStatus[0]++;
                } else if (Objects.equals(status, "Mitigated")) {
                    criticalByStatus[1]++;
                } else if (Objects.equals(status, "False Positive")) {
                    criticalByStatus[2]++;
                }
            } else if (Objects.equals(securityLevel, "High")) {
                highByStatus[3]++;
                if (Objects.equals(status, "Open")) {
                    highByStatus[0]++;
                } else if (Objects.equals(status, "Mitigated")) {
                    highByStatus[1]++;
                } else if (Objects.equals(status, "False Positive")) {
                    highByStatus[2]++;
                }
            } else if (Objects.equals(securityLevel, "Medium")) {
                mediumByStatus[3]++;
                if (Objects.equals(status, "Open")) {
                    mediumByStatus[0]++;
                } else if (Objects.equals(status, "Mitigated")) {
                    mediumByStatus[1]++;
                } else if (Objects.equals(status, "False Positive")) {
                    mediumByStatus[2]++;
                }
            } else if (Objects.equals(securityLevel, "Low")) {
                lowByStatus[3]++;
                if (Objects.equals(status, "Open")) {
                    lowByStatus[0]++;
                } else if (Objects.equals(status, "Mitigated")) {
                    lowByStatus[1]++;
                } else if (Objects.equals(status, "False Positive")) {
                    lowByStatus[2]++;
                }
            }

        }

        Map<String,int[]> findingStatusInfo = new HashMap<>();

        findingStatusInfo.put("critical",criticalByStatus);
        findingStatusInfo.put("high",highByStatus);
        findingStatusInfo.put("medium",mediumByStatus);
        findingStatusInfo.put("low",lowByStatus);


        return findingStatusInfo;
    }

    private Map<String,int[]> getNewFindingsAgeInfo(Iterable<Findings> allFindings){
        int[] criticalByAge = new int[3];
        int[] highByAge = new int[3];
        int[] mediumByAge = new int[3];
        int[] lowByAge = new int[3];

        for(Findings find : allFindings){
            String securityLevel = find.getSecurityLevel();
            String status = find.getStatus();

            Date createdAt = find.getCreatedAt();

            String age = isDateOlderThan(createdAt);

            if(Objects.equals(securityLevel, "Critical")){
                if (Objects.equals(age, "24HoursOld")) {
                    criticalByAge[0]++;
                } else if (Objects.equals(age, "7DaysOld")) {
                    criticalByAge[1]++;
                } else if (Objects.equals(age, "1MonthOld")) {
                    criticalByAge[2]++;
                }
            } else if (Objects.equals(securityLevel, "High")) {
                if (Objects.equals(age, "24HoursOld")) {
                    highByAge[0]++;
                } else if (Objects.equals(age, "7DaysOld")) {
                    highByAge[1]++;
                } else if (Objects.equals(age, "1MonthOld")) {
                    highByAge[2]++;
                }
            } else if (Objects.equals(securityLevel, "Medium")) {
                if (Objects.equals(age, "24HoursOld")) {
                    mediumByAge[0]++;
                } else if (Objects.equals(age, "7DaysOld")) {
                    mediumByAge[1]++;
                } else if (Objects.equals(age, "1MonthOld")) {
                    mediumByAge[2]++;
                }
            } else if (Objects.equals(securityLevel, "Low")) {
                if (Objects.equals(age, "24HoursOld")) {
                    lowByAge[0]++;
                } else if (Objects.equals(age, "7DaysOld")) {
                    lowByAge[1]++;
                } else if (Objects.equals(age, "1MonthOld")) {
                    lowByAge[2]++;
                }
            }

        }

        Map<String,int[]> findingAgeInfo = new HashMap<>();

        findingAgeInfo.put("critical",criticalByAge);
        findingAgeInfo.put("high",highByAge);
        findingAgeInfo.put("medium",mediumByAge);
        findingAgeInfo.put("low",lowByAge);


        return findingAgeInfo;
    }



    private String isDateOlderThan(Date date) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime then = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        Duration difference = Duration.between(then, now);
        long hours = difference.toHours();

        if(hours <= 24){
            return "24HoursOld";
        }else if(hours <= 168){
            return "7DaysOld";
        } else if (hours <= 720) {
            return "1MonthOld";
        }else{
            return "none";
        }

    }
}
