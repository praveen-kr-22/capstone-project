//package com.armorcode.capstone.config;
//
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
//
//import static org.springframework.security.config.Customizer.withDefaults;
//
//@Configuration
//@EnableWebSecurity
//public class OauthConfig {
//
////    @Bean
////    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
////                http
////                .authorizeHttpRequests(auth -> {
////                    auth.requestMatchers("/login").permitAll();
////                    auth.anyRequest().authenticated();
////                })
////                .oauth2Login(withDefaults());
////
////                if (http.getSharedObject(HttpSessionSecurityContextRepository.class) != null) {
////                    http.logout(logout ->
////                            logout
////                                    .logoutUrl("/logout")
////                                    .logoutSuccessUrl("/login")
////                                    .invalidateHttpSession(true)
////                                    .deleteCookies("JSESSIONID")
////                    );
////                }
////
////
////        return http.build();
////    }
//
//
//}