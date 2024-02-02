package com.armorcode.capstone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.client.RestTemplate;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.net.URISyntaxException;


@SpringBootApplication
public class CapstoneApplication {

	public static void getProduct() {
		String apiUrl = "https://api.github.com/repos/praveen-kr-22/natours/code-scanning/alerts";
		String githubToken = "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";

		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization:", "Token " + githubToken);
		headers.setContentType(MediaType.APPLICATION_JSON);


		RequestEntity<Void> requestEntity = new RequestEntity<>(headers, null, null);
		System.out.println(requestEntity);
//		RestTemplate restTemplate = new RestTemplate();
//		ResponseEntity<String> responseEntity = restTemplate.exchange(requestEntity, String.class);
//		System.out.println("responseEntity.getBody()");
//		String responseBody = responseEntity.getBody();
//		System.out.println("GitHub API Response:\n" + responseBody);
	}


	public static void main(String[] args) {
		SpringApplication.run(CapstoneApplication.class, args);
		System.out.println("Ok");

//		getProduct();

	}

}



//
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.boot.web.client.RestTemplateBuilder;
//import org.springframework.context.annotation.Bean;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.MediaType;
//import org.springframework.http.RequestEntity;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.client.RestTemplate;
//
//import java.net.URI;
//
//@SpringBootApplication
//public class CapstoneApplication {
//
//	public static void main(String[] args) {
//		SpringApplication.run(GitHubApiFetcherApplication.class, args);
//	}
//
//	@Bean
//	public CommandLineRunner run(RestTemplate restTemplate) {
//		return args -> {
//			String apiUrl = "https://api.github.com/repos/praveen-kr-22/natours/code-scanning/alerts";
//			String githubToken = "YOUR_GITHUB_TOKEN";
//
//			HttpHeaders headers = new HttpHeaders();
//			headers.set("Authorization", "Token " + githubToken);
//			headers.setContentType(MediaType.APPLICATION_JSON);
//
//			RequestEntity<Void> requestEntity = new RequestEntity<>(headers, null, null);
//
//			ResponseEntity<String> responseEntity = restTemplate.exchange(requestEntity, String.class);
//
//			String responseBody = responseEntity.getBody();
//			System.out.println("GitHub API Response:\n" + responseBody);
//		};
//	}
//
//	@Bean
//	public RestTemplate restTemplate(RestTemplateBuilder builder) {
//		return builder.build();
//	}
//}
//
