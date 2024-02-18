package com.armorcode.capstone.util;


import com.armorcode.capstone.entity.Findings;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Component
public class GlobalFindingCheck {

    public List<Findings> getFindingWithoutDup(Map<String,Long> oldFindingStoreHash, Map<String, Pair<Findings, Integer>> newFindingHashWithoutLocalDup) {

        List<Findings> findings = new ArrayList<>();

        Iterator<Map.Entry<String, Pair<Findings, Integer>>> iterator = newFindingHashWithoutLocalDup.entrySet().iterator();

        while(iterator.hasNext()){
            Map.Entry<String, Pair<Findings, Integer>> entry = iterator.next();

            String hashString = entry.getKey();
            Findings find = entry.getValue().getFirst();

            boolean isDup = false;

            if(oldFindingStoreHash.containsKey(hashString)){
                isDup = true;
            }

            if(!isDup){
                findings.add(find);
            }
        }

        return findings;

    }
}
