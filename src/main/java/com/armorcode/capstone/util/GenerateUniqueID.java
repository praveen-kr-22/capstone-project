package com.armorcode.capstone.util;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class GenerateUniqueID {

    public Integer getUniqueID() {
        UUID uuid = UUID.randomUUID();
        long leastSignificantBits = uuid.getLeastSignificantBits();
        long positiveBits = leastSignificantBits & Long.MAX_VALUE;
        int generatedInteger = (int) (positiveBits % 900000 + 100000);
        return generatedInteger;
    }

}
