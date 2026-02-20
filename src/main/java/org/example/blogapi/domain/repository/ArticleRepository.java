package org.example.blogapi.domain.repository;

import org.example.blogapi.domain.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface ArticleRepository extends JpaRepository<Article, Long>, JpaSpecificationExecutor<Article> {

    boolean existsBySlugIgnoreCase(String slug);

    Optional<Article> findBySlugIgnoreCase(String slug);
}
