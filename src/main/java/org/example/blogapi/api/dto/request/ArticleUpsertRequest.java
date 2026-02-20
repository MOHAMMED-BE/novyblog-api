package org.example.blogapi.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.example.blogapi.domain.enums.ArticleStatus;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ArticleUpsertRequest {

    @NotBlank
    @Size(min = 2, max = 220)
    private String title;

    @Size(max = 600)
    private String excerpt;

    @NotBlank
    private String content;

    @Size(max = 500)
    private String keywords;

    private ArticleStatus status = ArticleStatus.DRAFT;

    // Optional category
    private Long categoryId;

    private MultipartFile thumbnail;
}
