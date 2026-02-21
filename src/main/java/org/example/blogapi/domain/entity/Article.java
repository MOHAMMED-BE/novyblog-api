package org.example.blogapi.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.blogapi.domain.enums.ArticleStatus;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "articles",
        uniqueConstraints = {
                @UniqueConstraint(name = "uniq_article_slug", columnNames = {"slug"})
        },
        indexes = {
                @Index(name = "idx_article_status_publishedAt", columnList = "status,publishedAt"),
                @Index(name = "idx_article_author", columnList = "author_id"),
                @Index(name = "idx_article_category", columnList = "category_id"),
                @Index(name = "idx_article_createdAt", columnList = "createdAt")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Article extends TraceableEntity {

    @Column(nullable = false, length = 250)
    private String title;

    @Column(nullable = false, length = 250)
    private String slug;

    @Column(length = 600)
    private String excerpt;

    @Lob
    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String content;

    @Column(length = 500)
    private String keywords;

    @Column(length = 800)
    private String thumbnailUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private ArticleStatus status = ArticleStatus.DRAFT;

    private LocalDateTime publishedAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_id", nullable = false, foreignKey = @ForeignKey(name = "fk_article_author"))
    private User author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", foreignKey = @ForeignKey(name = "fk_article_category"))
    private Category category;
}
