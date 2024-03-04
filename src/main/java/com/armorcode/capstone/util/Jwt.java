package com.armorcode.capstone.util;

import com.armorcode.capstone.dao.UserData;
import com.fasterxml.jackson.core.JsonFactory;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;

import javax.crypto.SecretKey;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.Collections;
import java.util.Date;

@Component
public class Jwt {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration.ms}")
    private static long expirationMs;

    private String CLIENT_ID = "758783642705-l3kc64333crcmni8r09pptr76ijfa64m.apps.googleusercontent.com";

    private static final String SECRET_KEY = "oeRaYY7Wo24sDqKSX3IM9ASGmdGPmkTd9jo1QTy4b7P9Ze5_9hKolVX8xNrQDcNRfVEdTZNOuOyqEGhXEbdJI-ZQ19k_o9MI0y3eZN2lp9jow55FfXMiINEdt1XR85VipRLSOkT6kSpzs2x-jbLDiz9iFVzkd81YKxMgPA7VfZeQUm4n-mOmnWMaVX30zGFU4L3oPBctYKkl4dYfqYWqRNfrgPJVi5DGFjywgxx0ASEiJHtV72paI3fDR2XwlSkyhhmY-ICjCRmsJN4fX1pdoL8a18-aQrvyu4j0Os6dVPYIoPvvY0SAZtWYKHfM15g7A3HD4cVREf9cUsprCRK93w";


    public String generateToken(String username) {

        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + 3600000);

        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(SECRET_KEY);
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(signingKey,signatureAlgorithm)
                .compact();


    }


//    public String getUserIdFromToken(String token) {
//        try {
//            // Parse and validate the token
//            Jws<Claims> claimsJws = Jwts.parserBuilder()
//                    .setSigningKey(DatatypeConverter.parseBase64Binary(SECRET_KEY))
//                    .build()
//                    .parseClaimsJws(token);
//
//            // Extract the user ID from the subject claim
//            String userId = claimsJws.getBody().getSubject();
//
//            // Validate token expiration
//            if (claimsJws.getBody().getExpiration().before(new Date())) {
//                throw new ExpiredJwtException(null, null, "Token has expired");
//            }
//            return userId;
//        } catch (ExpiredJwtException e) {
//            System.err.println("Token has expired: " + e.getMessage());
//            return null;
//        } catch (Exception e) {
//            // Token validation failed or other error occurred
//            System.err.println("Error validating or parsing token: " + e.getMessage());
//            return null;
//        }
//    }

    public UserData getUserIdFromToken(String token) throws Exception {
        HttpTransport httpTransport = new NetHttpTransport();
        GsonFactory jsonFactory = GsonFactory.getDefaultInstance();
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(httpTransport, jsonFactory)
                .setAudience(Collections.singletonList(CLIENT_ID))
                .build();

        GoogleIdToken idToken = verifier.verify(token);
        if (idToken != null) {
            UserData user = new UserData();

            GoogleIdToken.Payload payload = idToken.getPayload();
            String email = payload.getEmail();
            String photoUrl = (String) payload.get("picture");
            String givenName = (String) payload.get("given_name");
            String familyName = (String) payload.get("family_name");

            user.setFirstName(givenName);
            user.setEmail(email);
            user.setLastName(familyName);
            user.setPhoto(photoUrl);
            return user;
        } else {
            System.out.println("Invalid credential");
            return null;
        }
    }
}
