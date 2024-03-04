package com.armorcode.capstone.service;

import com.armorcode.capstone.entity.Findings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {

    @Autowired
    private KafkaTemplate<String, Findings> kafkaTemplate;

    public void sendNewFinding(Findings message) {
        kafkaTemplate.send("new-findings", message);
    }

    public void sendUpdateFinding(Findings message){
        kafkaTemplate.send("updated_finding",message);
    }

}
