package org.example.blogapi.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.blogapi.api.dto.request.CommentCreateRequest;
import org.example.blogapi.api.dto.request.CommentUpdateRequest;
import org.example.blogapi.api.dto.response.CommentDto;
import org.example.blogapi.domain.entity.Article;
import org.example.blogapi.domain.entity.Comment;
import org.example.blogapi.domain.entity.User;
import org.example.blogapi.domain.enums.CommentStatus;
import org.example.blogapi.domain.repository.ArticleRepository;
import org.example.blogapi.domain.repository.CommentRepository;
import org.example.blogapi.domain.repository.UserRepository;
import org.example.blogapi.mapper.CommentMapper;
import org.example.blogapi.service.CommentService;
import org.example.blogapi.service.exceptions.ArticleNotFoundException;
import org.example.blogapi.service.exceptions.CommentNotFoundException;
import org.example.blogapi.service.exceptions.ForbiddenOperationException;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;
    private final CommentMapper commentMapper;

    @Override
    @Transactional(readOnly = true)
    public List<CommentDto> findByArticle(Long articleId, Pageable pageable) {
        return commentRepository
                .findByArticleIdAndStatus(articleId, CommentStatus.VISIBLE, pageable)
                .map(commentMapper::toDto)
                .getContent();
    }

    @Override
    @Transactional(readOnly = true)
    public CommentDto findById(Long id) {
        Comment c = commentRepository.findById(id)
                .orElseThrow(() -> new CommentNotFoundException("Comment with id " + id + " not found"));
        return commentMapper.toDto(c);
    }

    @Override
    public CommentDto create(Long articleId, CommentCreateRequest req) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ArticleNotFoundException("Article with id " + articleId + " not found"));

        User current = getCurrentUserOrThrow();

        Comment entity = new Comment();
        entity.setArticle(article);
        entity.setUser(current);
        entity.setContent(req.content());
        entity.setStatus(CommentStatus.VISIBLE);

        Comment saved = commentRepository.save(entity);
        return commentMapper.toDto(saved);
    }

    @Override
    public CommentDto update(Long id, CommentUpdateRequest req) {
        Comment existing = commentRepository.findById(id)
                .orElseThrow(() -> new CommentNotFoundException("Comment with id " + id + " not found"));

        ensureOwnerOrAdmin(existing);

        if (existing.getStatus() == CommentStatus.DELETED) {
            throw new ForbiddenOperationException("Cannot update a deleted comment");
        }

        existing.setContent(req.content());

        Comment saved = commentRepository.save(existing);
        return commentMapper.toDto(saved);
    }

    @Override
    public void delete(Long id) {
        Comment existing = commentRepository.findById(id)
                .orElseThrow(() -> new CommentNotFoundException("Comment with id " + id + " not found"));

        ensureOwnerOrAdmin(existing);

        // Soft-delete
        existing.setStatus(CommentStatus.DELETED);

        commentRepository.save(existing);
    }

    private void ensureOwnerOrAdmin(Comment comment) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            throw new ForbiddenOperationException("Unauthenticated request");
        }

        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));

        User current = getCurrentUserOrThrow();
        boolean isOwner = comment.getUser() != null
                && comment.getUser().getId() != null
                && comment.getUser().getId().equals(current.getId());

        if (!isOwner && !isAdmin) {
            throw new ForbiddenOperationException("You are not allowed to modify this comment");
        }
    }

    private User getCurrentUserOrThrow() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            throw new ForbiddenOperationException("Unauthenticated request");
        }

        Object principal = auth.getPrincipal();

        if (principal instanceof User u) {
            return u;
        }

        if (principal instanceof UserDetails ud) {
            String email = ud.getUsername();
            return userRepository.findByEmailIgnoreCase(email)
                    .orElseThrow(() -> new ForbiddenOperationException("Authenticated user not found in DB: " + email));
        }

        if (principal instanceof String email) {
            return userRepository.findByEmailIgnoreCase(email)
                    .orElseThrow(() -> new ForbiddenOperationException("Authenticated user not found in DB: " + email));
        }

        throw new ForbiddenOperationException("Unsupported principal type: " + principal.getClass().getName());
    }
}
