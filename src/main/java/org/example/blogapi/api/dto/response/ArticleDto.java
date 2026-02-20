package org.example.blogapi.api.dto.response;

import org.example.blogapi.domain.enums.ArticleStatus;

import java.time.LocalDateTime;

public record ArticleDto(
        Long id,
        String title,
        String slug,
        String excerpt,
        String content,
        String keywords,
        String thumbnailUrl,
        ArticleStatus status,
        LocalDateTime publishedAt,
        boolean enabled,
        Long authorId,
        Long categoryId
) {
}
