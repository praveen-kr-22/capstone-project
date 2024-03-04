package com.armorcode.capstone.util;

import com.armorcode.capstone.entity.Findings;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class LocalDupCheck {

    public List<Map<String , Object>> localDuDupCheck(List<Map<String, Object>> store){

        Set<String> storeHash = new HashSet<>();

        List<Map<String, Object>> uniqueList = new ArrayList<>();

        for (Map<String, Object> map : store) {
            String hashstring = (String) map.get("hashString");

            if (hashstring != null && !storeHash.contains(hashstring)) {
                storeHash.add(hashstring);
                uniqueList.add(map);
            }
        }
        return uniqueList;
    }
}



// newFindingStoreHash.put("hashString",hashString);
//                    newFindingStoreHash.put("finding",find);
//                    newFindingStoreHash.put("status",find.getStatus());