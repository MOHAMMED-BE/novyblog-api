package org.example.blogapi.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.blogapi.api.dto.request.ArticleUpsertRequest;
import org.example.blogapi.api.dto.response.ArticleDto;
import org.example.blogapi.domain.enums.ArticleStatus;
import org.example.blogapi.service.ArticleService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/articles")
@Slf4j
public class ArticleController {

    private final ArticleService articleService;

    @GetMapping
    public ResponseEntity<org.springframework.data.domain.Page<ArticleDto>> findAll(
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String slug,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String keywords,
            @RequestParam(required = false) String categoryName,
            @RequestParam(required = false) ArticleStatus status,
            Pageable pageable
    ) {
        return ResponseEntity.ok(articleService.findAll(id, status, slug, name, keywords, categoryName, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ArticleDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(articleService.findById(id));
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<ArticleDto> findBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(articleService.findBySlug(slug));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ArticleDto> create(@Valid @ModelAttribute ArticleUpsertRequest req) {
        ArticleDto created = articleService.create(req);
        URI location = URI.create("/api/articles/" + created.id());
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ArticleDto> update(@PathVariable Long id, @Valid @ModelAttribute ArticleUpsertRequest req) {
        ArticleDto updated = articleService.update(id, req);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        articleService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
