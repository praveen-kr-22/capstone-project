package com.armorcode.capstone.util;


import com.armorcode.capstone.entity.Findings;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class GlobalFindingCheck {

    public Map<String,List<Findings>> getFindingWithoutDup(List<Map<String,Object>> oldFindingStoreHash, List<Map<String,Object>> newFindingHashWithoutLocalDup) {

        Map<String,List<Findings>> result = new HashMap<>();
        List<Findings> newFindings = new ArrayList<>();
        List<Findings> updatedFindings = new ArrayList<>();

        for (Map<String, Object> newFinding : newFindingHashWithoutLocalDup) {
            String newHashString = (String) newFinding.get("hashString");
            String newStatus = (String) newFinding.get("status");
            Findings newFind = (Findings) newFinding.get("finding");

            boolean foundInOld = false;
            for (Map<String, Object> oldFinding : oldFindingStoreHash) {
                String oldHashString = (String) oldFinding.get("hashString");
                String oldStatus = (String) oldFinding.get("status");
                Findings oldFind = (Findings) oldFinding.get("finding");

                if (newHashString != null && newHashString.equals(oldHashString)) {
                    foundInOld = true;
                    if (newStatus != null && !newStatus.equals(oldStatus)) {
                        // Status changed, add to updatedFindings
                        Findings temp = newFind;
                        temp.setId(oldFind.getId());
                        updatedFindings.add(temp);
                    }
                    break;
                }
            }

            if (!foundInOld) {
                newFindings.add(newFind);
            }
        }

        result.put("newFindings",newFindings);
        result.put("updatedFindings",updatedFindings);

        return result;
    }
}
