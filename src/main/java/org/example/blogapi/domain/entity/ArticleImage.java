package org.example.blogapi.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "article_images",
        indexes = {
                @Index(name = "idx_article_images_article", columnList = "article_id"),
                @Index(name = "idx_article_images_position", columnList = "article_id,position")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArticleImage extends TraceableEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "article_id", nullable = false, foreignKey = @ForeignKey(name = "fk_image_article"))
    private Article article;

    /**
     * Image path/URL.
     */
    @Column(nullable = false, length = 800)
    private String url;

    @Column(length = 200)
    private String altText;

    /**
     * Order of images in the article gallery.
     */
    @Column(nullable = false)
    @Builder.Default
    private int position = 0;
}
