package org.example.blogapi.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "categories",
        uniqueConstraints = {
                @UniqueConstraint(name = "uniq_category_slug", columnNames = {"slug"})
        },
        indexes = {
                @Index(name = "idx_category_name", columnList = "name")
        }
)
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Category extends TraceableEntity {

    @Column(nullable = false, length = 120)
    private String name;

    /**
     * Used for SEO URLs: /category/{slug}
     */
    @Column(nullable = false, length = 160)
    private String slug;

    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    @Builder.Default
    private boolean enabled = true;
}
