//package com.armorcode.capstone.config;
//
//
//import org.apache.http.client.HttpClient;
//import org.apache.http.impl.client.CloseableHttpClient;
//import org.apache.http.impl.client.HttpClientBuilder;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
//import org.springframework.web.client.RestTemplate;
//
//@Configuration
//public class RestTemplateConfig {
//
//    @Bean
//    public RestTemplate restTemplate() {
//        HttpClient httpClient = HttpClientBuilder.create().build();
//        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
//        requestFactory.setHttpClient(httpClient);
//        return new RestTemplate(requestFactory);
//    }
//
//
//
//
//}
