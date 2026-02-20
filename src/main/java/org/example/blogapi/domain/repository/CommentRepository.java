package org.example.blogapi.domain.repository;

import org.example.blogapi.domain.entity.Comment;
import org.example.blogapi.domain.enums.CommentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    // Public listing: only visible + enabled comments
    Page<Comment> findByArticleIdAndEnabledTrueAndStatus(
            Long articleId,
            CommentStatus status,
            Pageable pageable
    );
}
