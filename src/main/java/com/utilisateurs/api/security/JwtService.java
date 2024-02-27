package com.utilisateurs.api.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;


@Service
public class JwtService {
    private static final String SECRET_KEY = "IfQVJagWSJLQKkhaeCKnJvVm4Pvkn95AmUReqoLbY5q6ffpn6UTwxEC8wU/sjMcx";

    public String extractUserName(String accessToken){
        return extractClaims(accessToken, Claims::getSubject);
    }

    public String generateToken(UserDetails userDetails){
        return generateToken(new HashMap<>(),userDetails);
    }

    public String generateToken(Map<String, Objects> extraClaims,
                                UserDetails userDetails){

        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();

    }

    public boolean isTokenValid(String accessToken , UserDetails userDetails){
        final String username = extractUserName(accessToken);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(accessToken);
    }

    public boolean isTokenExpired(String accessToken){
        return extractExpiration(accessToken).before(new Date());
    }

    private Date extractExpiration(String accessToken) {
        return extractClaims(accessToken,Claims::getExpiration);
    }

    public <T> T extractClaims(String accessToken, Function<Claims, T> claimsResolver){
        final Claims claims = extractAllClaims(accessToken);
        return claimsResolver.apply(claims);

    }

    public Claims extractAllClaims(String accessToken){
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(accessToken)
                .getBody();
    }

    private Key getSignInKey() {
        byte[] KeyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(KeyBytes);

    }
}


