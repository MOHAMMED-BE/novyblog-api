package org.example.blogapi.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.Keys;
import org.example.blogapi.config.JwtProperties;
import org.example.blogapi.domain.entity.User;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Service
public class JwtService {

    private final JwtProperties props;
    private final SecretKey key;

    public JwtService(JwtProperties props) {
        this.props = props;

        this.key = Keys.hmacShaKeyFor(props.secret().getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(User user) {
        Instant now = Instant.now();
        Instant exp = now.plus(props.accessTokenMinutes(), ChronoUnit.MINUTES);

        return Jwts.builder()
                .issuer(props.issuer())
                .subject(user.getEmail())
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .claim("roles", user.getRoles().stream().map(Enum::name).toList())
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }

    public Claims parseAndValidate(String token) {
        return Jwts.parser()
                .requireIssuer(props.issuer())
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extractSubject(String token) {
        return parseAndValidate(token).getSubject();
    }

    public boolean isValid(String token) {
        try {
            parseAndValidate(token);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }

    public long accessTokenExpiresInSeconds() {
        return props.accessTokenMinutes() * 60L;
    }
}
