package org.example.blogapi.service;

import org.example.blogapi.api.dto.request.ArticleCriteria;
import org.example.blogapi.api.dto.request.MyArticleCriteria;
import org.example.blogapi.api.dto.response.ArticleDto;
import org.example.blogapi.domain.enums.ArticleStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ArticleService {
    Page<ArticleDto> findAll(ArticleCriteria criteria, Pageable pageable);

    Page<ArticleDto> findMyArticles(Long authorId, MyArticleCriteria criteria, Pageable pageable);

    ArticleDto findById(Long id);

    ArticleDto findBySlug(String slug);

    ArticleDto create(org.example.blogapi.api.dto.request.ArticleUpsertRequest req);

    ArticleDto update(Long id, org.example.blogapi.api.dto.request.ArticleUpsertRequest req);

    void delete(Long id);
}
