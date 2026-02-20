package org.example.blogapi.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.blogapi.domain.enums.CommentStatus;

@Entity
@Table(
        name = "comments",
        indexes = {
                @Index(name = "idx_comments_article_createdAt", columnList = "article_id,createdAt"),
                @Index(name = "idx_comments_user", columnList = "user_id"),
                @Index(name = "idx_comments_status", columnList = "status")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment extends TraceableEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "article_id", nullable = false, foreignKey = @ForeignKey(name = "fk_comment_article"))
    private Article article;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_comment_user"))
    private User user;

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private CommentStatus status = CommentStatus.VISIBLE;
}
