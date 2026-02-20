package org.example.blogapi.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.blogapi.domain.enums.ArticleStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @Column(nullable = false, length = 220)
    private String title;

    /**
     * Slug for dynamic routes: /articles/{slug}
     */
    @Column(nullable = false, length = 260)
    private String slug;

    /**
     * Optional short text for lists/cards.
     */
    @Column(length = 600)
    private String excerpt;

    /**
     * Main body (HTML/Markdown/Text). Use LONGTEXT in MySQL.
     */
    @Lob
    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String content;

    /**
     * SEO keywords, comma-separated
     * Example: "book,code,key1,key2"
     */
    @Column(length = 500)
    private String keywords;

    /**
     * Miniature image path/URL (CDN, S3, local storage URL, etc.)
     * This is the "thumbnail".
     */
    @Column(length = 800)
    private String thumbnailUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private ArticleStatus status = ArticleStatus.DRAFT;

    /**
     * Set when published.
     */
    private LocalDateTime publishedAt;

    /**
     * Admin can disable an article without deleting it.
     */
    @Column(nullable = false)
    @Builder.Default
    private boolean enabled = true;

    /**
     * AUTHOR who created the article.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_id", nullable = false, foreignKey = @ForeignKey(name = "fk_article_author"))
    private User author;

    /**
     * Optional category.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", foreignKey = @ForeignKey(name = "fk_article_category"))
    private Category category;

    /**
     * Optional multi-images attached to the article.
     */
    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("position ASC")
    @Builder.Default
    private List<ArticleImage> images = new ArrayList<>();
}
