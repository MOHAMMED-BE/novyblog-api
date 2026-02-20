package org.example.blogapi.api.advice;

import jakarta.servlet.http.HttpServletRequest;
import org.example.blogapi.error.ApiError;
import org.example.blogapi.service.exceptions.ArticleAlreadyExistsException;
import org.example.blogapi.service.exceptions.ArticleNotFoundException;
import org.example.blogapi.service.exceptions.CategoryAlreadyExistsException;
import org.example.blogapi.service.exceptions.CategoryNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // ---------------------------
    // 400 - Validation
    // ---------------------------
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String msg = ex.getBindingResult().getFieldErrors().stream()
                .map(this::formatFieldError)
                .collect(Collectors.joining("; "));
        return ResponseEntity.badRequest().body(new ApiError(
                Instant.now(),
                400,
                "BAD_REQUEST",
                msg
        ));
    }

    // ---------------------------
    // 401 - Authentication
    // ---------------------------
    @ExceptionHandler({
            BadCredentialsException.class,
            UsernameNotFoundException.class
    })
    public ResponseEntity<ApiError> handleUnauthorized(RuntimeException ex, HttpServletRequest request) {
        return ResponseEntity.status(401).body(ApiError.unauthorized("Invalid email or password"));
    }

    // ---------------------------
    // 403 - Account state / Authorization
    // ---------------------------
    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ApiError> handleDisabled(DisabledException ex, HttpServletRequest request) {
        return ResponseEntity.status(403).body(ApiError.forbidden("Account is disabled."));
    }

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<ApiError> handleLocked(LockedException ex, HttpServletRequest request) {
        return ResponseEntity.status(403).body(ApiError.forbidden("Account is locked."));
    }

    // ---------------------------
    // 409 - Business conflict
    // ---------------------------
    @ExceptionHandler(CategoryAlreadyExistsException.class)
    public ResponseEntity<ApiError> handleCategoryAlreadyExists(CategoryAlreadyExistsException ex, HttpServletRequest request) {
        return ResponseEntity.status(409).body(new ApiError(
                Instant.now(),
                409,
                "CONFLICT",
                ex.getMessage()
        ));
    }

    @ExceptionHandler(ArticleAlreadyExistsException.class)
    public ResponseEntity<ApiError> handleArticleAlreadyExists(ArticleAlreadyExistsException ex, HttpServletRequest request) {
        return ResponseEntity.status(409).body(new ApiError(
                Instant.now(),
                409,
                "CONFLICT",
                ex.getMessage()
        ));
    }

    // ---------------------------
    // 404 - Not found
    // ---------------------------
    @ExceptionHandler(CategoryNotFoundException.class)
    public ResponseEntity<ApiError> handleCategoryNotFound(CategoryNotFoundException ex, HttpServletRequest request) {
        return ResponseEntity.status(404).body(new ApiError(
                Instant.now(),
                404,
                "NOT_FOUND",
                ex.getMessage()
        ));
    }

    @ExceptionHandler(ArticleNotFoundException.class)
    public ResponseEntity<ApiError> handleArticleNotFound(
            ArticleNotFoundException ex,
            HttpServletRequest request
    ) {
        return ResponseEntity.status(404).body(ApiError.notFound(ex.getMessage()));
    }

    // ---------------------------
    // 400 - Illegal arguments
    // ---------------------------
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest request) {
        return ResponseEntity.badRequest().body(ApiError.badRequest(ex.getMessage()));
    }

    private String formatFieldError(FieldError fe) {
        return fe.getField() + ": " + fe.getDefaultMessage();
    }
}
