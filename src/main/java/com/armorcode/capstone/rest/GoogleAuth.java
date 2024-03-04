//package com.armorcode.capstone.rest;
//
//import com.armorcode.capstone.dao.LoginRequest;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.security.oauth2.core.user.OAuth2User;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
//import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
//import org.springframework.security.oauth2.core.OAuth2AccessToken;
//import org.springframework.security.oauth2.core.user.OAuth2User;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//public class GoogleAuth {
//
////    @GetMapping("/login")
////    public String login(){
////        return "Login successfully";
////    }
////
////
////    @GetMapping("/user")
////    public OAuth2User getUserInfo(@AuthenticationPrincipal OAuth2User oauth2User) {
////        // Retrieve user attributes from OAuth2User object
////        String email = oauth2User.getAttribute("email");
////        String name = oauth2User.getAttribute("name");
////        // Other attributes you want to retrieve
////
////        // Perform actions based on user data
////        return oauth2User;
////    }
////
////    @GetMapping("/logout")
////    public String logout(){
////        return "logout";
////    }
////
////    @GetMapping("access-denied")
////    public String accessdesied(){
////        return "access-denied";
////    }
//
//
////    @Autowired
////    private OAuth2AuthorizedClientService authorizedClientService;
////
////    @PostMapping("/login")
////    public ResponseEntity<String> login(@RequestBody LoginRequest request) {
////        System.out.println("called");
////        // Authenticate user with Google OAuth2
////        // You can use the OAuth2AuthorizedClientService to retrieve the authorized client
////        OAuth2AuthorizedClient authorizedClient = authorizedClientService.loadAuthorizedClient("google", request.getEmail());
////
////        if (authorizedClient == null) {
////            // The user is not authenticated with Google
////            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication failed");
////        }
////        System.out.println("called");
////        System.out.println(authorizedClient);
////
////        // Retrieve the access token from the authorized client
////        OAuth2AccessToken accessToken = authorizedClient.getAccessToken();
////        System.out.println(accessToken);
////
////        // Here you can return the access token or any other information as needed
////        return ResponseEntity.ok("Authentication successful. Access token: " + accessToken.getTokenValue());
////    }
//
//
//
//
//
//}
