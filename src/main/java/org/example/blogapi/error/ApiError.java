package org.example.blogapi.error;

import java.time.Instant;

public record ApiError(
        Instant timestamp,
        int status,
        String error,
        String message
) {
    public static ApiError unauthorized(String message) {
        return new ApiError(Instant.now(), 401, "UNAUTHORIZED", message);
    }

    public static ApiError forbidden(String message) {
        return new ApiError(Instant.now(), 403, "FORBIDDEN", message);
    }

    public static ApiError badRequest(String message) {
        return new ApiError(Instant.now(), 400, "BAD_REQUEST", message);
    }

    public static ApiError notFound(String message) {
        return new ApiError(Instant.now(), 404, "NOT_FOUND", message);
    }

    public static ApiError conflict(String message) {
        return new ApiError(Instant.now(), 409, "CONFLICT", message);
    }
}
