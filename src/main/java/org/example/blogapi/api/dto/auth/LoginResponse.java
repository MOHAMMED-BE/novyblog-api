package org.example.blogapi.api.dto.auth;

import java.util.List;

public record LoginResponse(
        String accessToken,
        String tokenType,
        long expiresInSeconds,
        String email,
        String fullName,
        List<String> roles
) {
}
