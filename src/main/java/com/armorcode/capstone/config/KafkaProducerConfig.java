package com.armorcode.capstone.config;

import com.armorcode.capstone.entity.Findings;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {

    @Bean
    public ProducerFactory<String, Findings> producerFactory() {
        Map<String, Object> config = new HashMap<>();

        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class); // JsonSerializer for sending POJOs

        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public KafkaTemplate<String, Findings> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}




//@Configuration
//public class KafkaConfig {
//
//    @Value("${spring.kafka.producer.bootstrap-servers}")
//    private String bootstrapServers;
//    @Bean
//    public ProducerFactory<String, Findings> producerFactory() {
//        Map<String, Object> configProps = new HashMap<>();
//        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
//        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
//        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
//        // You can add more producer configuration properties as needed
//        return new DefaultKafkaProducerFactory<>(configProps);}
//    @Bean
//    public Map<String, Object> producerConfigs() {
//        // Configure producer properties here
//        Map<String, Object> properties = new HashMap<>();
//        properties.put("bootstrap.servers", bootstrapServers);
//        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
//        // Use JsonSerializer for the value
//        properties.put("value.serializer", JsonSerializer.class.getName());
//        return properties;
//    }
//    @Bean
//    public KafkaTemplate<String, Findings> kafkaTemplate() {
//        // Configure KafkaTemplate here
//        return new KafkaTemplate<>(producerFactory());
//    }
//}






