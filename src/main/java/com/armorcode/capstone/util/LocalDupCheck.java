package com.armorcode.capstone.util;

import com.armorcode.capstone.entity.Findings;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Component
public class LocalDupCheck {

    public Map<String , Pair<Findings,Integer>> localDuDupCheck(Map<String, Pair<Findings, Integer>> store){

        Map<String,Pair<Findings,Integer>> result = new HashMap<>();

        Iterator<Map.Entry<String, Pair<Findings, Integer>>> iterator = store.entrySet().iterator();

        while(iterator.hasNext()){
            Map.Entry<String, Pair<Findings, Integer>> entry = iterator.next();
            String hash = entry.getKey();
            Pair<Findings, Integer> findWithID = entry.getValue();
//            System.out.println(hash);
            if(!result.containsKey(hash)){
//                System.out.println(findWithID);
                result.put(hash,findWithID);
            }

        }

        return result;
    }
}
