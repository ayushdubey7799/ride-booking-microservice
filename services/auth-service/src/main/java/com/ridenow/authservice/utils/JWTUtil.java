package com.ridenow.authservice.utils;

import com.ridenow.authservice.domain.UserAuthEntity;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@Component
public class JWTUtil {

    @Value("${app.jwtSecret}")
    private String SECRET_KEY;

    private Key getSigningKey() {
        byte[] keyBytes = Base64.getDecoder().decode(SECRET_KEY);
        return new SecretKeySpec(keyBytes, SignatureAlgorithm.HS512.getJcaName());
    }

    public String generateToken(UserAuthEntity userAuth, List<String> roles, long expiryMinutes){

        return Jwts
                .builder()
                .subject(userAuth.getId().toString())
                .claim("roles",roles)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiryMinutes * 60 * 1000))
                .signWith(getSigningKey(),SignatureAlgorithm.HS512)
                .compact();

    }

    public String validateAndExtractUsername(String token) {
        try{
            return Jwts
                    .parser()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        }
        catch (JwtException j){
            return null;
        }
    }
}
