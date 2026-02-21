package org.example.blogapi.api.dto.request;

import lombok.*;
import org.example.blogapi.domain.enums.ArticleStatus;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArticleCriteria {

    private Long id;
    private ArticleStatus status;
    private String slug;
    private String name;
    private String keywords;
    private String categoryName;
    private Long authorId;
}