package com.armorcode.capstone.rest;

import jakarta.annotation.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@RestController
@RequestMapping("/api")
public class DemoController {

    @GetMapping("/product")
    public String DemoProduct(){
        return "Product";
    }



    @PostMapping("/resources")
    public ResponseEntity<Resource> createResource(@RequestBody Resource resource) {
        // Create the resource
        Resource createdResource = resourceService.create(resource);

        // Return the created resource with a 201 (created) status code
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdResource);
    }

    @GetMapping("/resources/{id}")
    public ResponseEntity<Resource> getResource(@PathVariable("id") String id) {
        // Make a GET request to retrieve the resource from an external API
        RequestEntity<Void> request = RequestEntity
                .get(URI.create("https://api.example.com/resources/" + id))
                .accept(MediaType.APPLICATION_JSON)
                .build();
        ResponseEntity<Resource> response = restTemplate.exchange(request, Resource.class);

        // Return the resource
        return response;
    }


}
