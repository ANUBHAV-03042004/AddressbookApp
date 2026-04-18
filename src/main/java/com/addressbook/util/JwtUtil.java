package com.addressbook.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    private static final Logger log = LoggerFactory.getLogger(JwtUtil.class);

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration-ms}")
    private long jwtExpirationMs;

    // FIX: decode the key once at startup instead of on every sign/verify call
    private SecretKey cachedKey;

    @PostConstruct
    public void init() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        this.cachedKey = Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(String username) {
        Date now    = new Date();
        Date expiry = new Date(now.getTime() + jwtExpirationMs);
        return Jwts.builder()
                .subject(username)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(cachedKey)
                .compact();
    }

    public String getUsernameFromToken(String token) {
        return parseClaims(token).getSubject();
    }

    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (ExpiredJwtException e)    { log.warn("JWT expired: {}",           e.getMessage()); }
          catch (UnsupportedJwtException e){ log.warn("Unsupported JWT: {}",        e.getMessage()); }
          catch (MalformedJwtException e)  { log.warn("Malformed JWT: {}",          e.getMessage()); }
          catch (SecurityException e)      { log.warn("JWT signature invalid: {}",  e.getMessage()); }
          catch (IllegalArgumentException e){ log.warn("JWT claims empty: {}",      e.getMessage()); }
        return false;
    }

    public long getExpirationMs() { return jwtExpirationMs; }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(cachedKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
