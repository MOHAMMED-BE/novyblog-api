package org.example.blogapi.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.blogapi.api.dto.request.CommentCreateRequest;
import org.example.blogapi.api.dto.request.CommentUpdateRequest;
import org.example.blogapi.api.dto.response.CommentDto;
import org.example.blogapi.service.CommentService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api")
public class CommentController {

    private final CommentService commentService;

    // ✅ List comments for an article (public: VISIBLE + enabled only)
    @GetMapping("/articles/{articleId}/comments")
    public ResponseEntity<List<CommentDto>> findByArticle(
            @PathVariable Long articleId,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable
    ) {
        return ResponseEntity.ok(commentService.findByArticle(articleId, pageable));
    }

    // ✅ Create comment for an article
    @PostMapping("/articles/{articleId}/comments")
    public ResponseEntity<CommentDto> create(
            @PathVariable Long articleId,
            @Valid @RequestBody CommentCreateRequest req
    ) {
        CommentDto created = commentService.create(articleId, req);
        URI location = URI.create("/api/comments/" + created.id());
        return ResponseEntity.created(location).body(created);
    }

    // ✅ Read single comment
    @GetMapping("/comments/{id}")
    public ResponseEntity<CommentDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(commentService.findById(id));
    }

    // ✅ Update comment (owner or admin)
    @PutMapping("/comments/{id}")
    public ResponseEntity<CommentDto> update(
            @PathVariable Long id,
            @Valid @RequestBody CommentUpdateRequest req
    ) {
        return ResponseEntity.ok(commentService.update(id, req));
    }

    // ✅ Delete comment (soft delete) (owner or admin)
    @DeleteMapping("/comments/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        commentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
