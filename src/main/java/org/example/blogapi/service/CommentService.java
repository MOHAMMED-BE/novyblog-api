package org.example.blogapi.service;

import org.example.blogapi.api.dto.request.CommentCreateRequest;
import org.example.blogapi.api.dto.request.CommentUpdateRequest;
import org.example.blogapi.api.dto.response.CommentDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CommentService {

    List<CommentDto> findByArticle(Long articleId, Pageable pageable);

    CommentDto findById(Long id);

    CommentDto create(Long articleId, CommentCreateRequest req);

    CommentDto update(Long id, CommentUpdateRequest req);

    void delete(Long id);
}
