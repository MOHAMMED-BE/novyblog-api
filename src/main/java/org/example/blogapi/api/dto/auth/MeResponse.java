package org.example.blogapi.api.dto.auth;

import java.util.List;

public record MeResponse(
        Long id,
        String email,
        String fullName,
        List<String> roles
) {
}
