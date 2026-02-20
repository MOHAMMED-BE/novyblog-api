package org.example.blogapi.api.dto.response;

import org.example.blogapi.domain.enums.CommentStatus;

import java.time.LocalDateTime;

public record CommentDto(
        Long id,
        Long articleId,
        Long userId,
        String userFullName,
        String content,
        CommentStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
