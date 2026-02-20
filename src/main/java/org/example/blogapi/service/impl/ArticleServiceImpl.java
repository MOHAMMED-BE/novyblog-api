package org.example.blogapi.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.blogapi.api.dto.request.ArticleUpsertRequest;
import org.example.blogapi.api.dto.response.ArticleDto;
import org.example.blogapi.domain.entity.Article;
import org.example.blogapi.domain.entity.Category;
import org.example.blogapi.domain.entity.User;
import org.example.blogapi.domain.enums.ArticleStatus;
import org.example.blogapi.domain.repository.ArticleRepository;
import org.example.blogapi.domain.repository.CategoryRepository;
import org.example.blogapi.domain.repository.UserRepository;
import org.example.blogapi.domain.spec.ArticleSpecifications;
import org.example.blogapi.mapper.ArticleMapper;
import org.example.blogapi.service.ArticleService;
import org.example.blogapi.service.exceptions.ArticleAlreadyExistsException;
import org.example.blogapi.service.exceptions.ArticleNotFoundException;
import org.example.blogapi.service.storage.FileUploader;
import org.example.blogapi.util.Slugify;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ArticleServiceImpl implements ArticleService {

    private static final String THUMB_DIR = "articles_thumbnail";

    private final ArticleRepository articleRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final ArticleMapper articleMapper;
    private final FileUploader fileUploader;

    /*
     public List<ArticleDto> findAll(Pageable pageable) {
        return articleRepository.findAll(pageable)
                .map(articleMapper::toDto)
                .getContent();
    }
    */

    @Transactional(readOnly = true)
    @Override
    public Page<ArticleDto> findAll(Long id, ArticleStatus status, String slug, String name, String keywords, String categoryName, Pageable pageable) {
        var spec = ArticleSpecifications.withFilters(id, status, slug, name, keywords, categoryName);
        return articleRepository.findAll(spec, pageable).map(articleMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public ArticleDto findById(Long id) {
        Article a = articleRepository.findById(id)
                .orElseThrow(() ->
                        new ArticleNotFoundException("Article with id " + id + " not found")
                );
        return articleMapper.toDto(a);
    }

    @Override
    @Transactional(readOnly = true)
    public ArticleDto findBySlug(String slug) {
        Article article = articleRepository.findBySlugIgnoreCase(slug)
                .orElseThrow(() ->
                        new ArticleNotFoundException("Article with slug '" + slug + "' not found")
                );

        return articleMapper.toDto(article);
    }

    @Override
    public ArticleDto create(ArticleUpsertRequest req) {
        Article entity = new Article();

        entity.setTitle(req.getTitle());
        entity.setExcerpt(req.getExcerpt());
        entity.setContent(req.getContent());
        entity.setKeywords(req.getKeywords());
        entity.setEnabled(req.getEnabled() != null ? req.getEnabled() : true);
        entity.setStatus(req.getStatus() != null ? req.getStatus() : ArticleStatus.DRAFT);

        // Slug from title
        String slug = Slugify.slugify(req.getTitle());
        if (slug == null) throw new RuntimeException("Slug could not be generated");
        if (articleRepository.existsBySlugIgnoreCase(slug)) {
            throw new ArticleAlreadyExistsException(
                    "Article with slug '" + slug + "' already exists"
            );
        }
        entity.setSlug(slug);

        // Category optional
        if (req.getCategoryId() != null) {
            Category cat = categoryRepository.findById(req.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category with id " + req.getCategoryId() + " not found"));
            entity.setCategory(cat);
        }

        // Author required (resolved from SecurityContext)
        entity.setAuthor(getCurrentUserOrThrow());

        // Upload thumbnail if provided
        if (req.getThumbnail() != null && !req.getThumbnail().isEmpty()) {
            String url = fileUploader.upload(req.getThumbnail(), THUMB_DIR);
            entity.setThumbnailUrl(url);
        }

        // Set publishedAt if status becomes PUBLISHED
        applyPublishedAt(entity);

        Article saved = articleRepository.save(entity);
        return articleMapper.toDto(saved);
    }

    @Override
    public ArticleDto update(Long id, ArticleUpsertRequest req) {
        Article existing = articleRepository.findById(id)
                .orElseThrow(() ->
                        new ArticleNotFoundException("Article with id " + id + " not found")
                );

        existing.setTitle(req.getTitle());
        existing.setExcerpt(req.getExcerpt());
        existing.setContent(req.getContent());
        existing.setKeywords(req.getKeywords());
        existing.setEnabled(req.getEnabled() != null ? req.getEnabled() : existing.isEnabled());
        existing.setStatus(req.getStatus() != null ? req.getStatus() : existing.getStatus());

        // Slug derived from title
        String newSlug = Slugify.slugify(req.getTitle());
        if (newSlug == null) throw new RuntimeException("Slug could not be generated");

        if (!newSlug.equalsIgnoreCase(existing.getSlug())
                && articleRepository.existsBySlugIgnoreCase(newSlug)) {
            // ✅ IMPORTANT: use the same domain exception as create()
            throw new ArticleAlreadyExistsException(
                    "Article with slug '" + newSlug + "' already exists"
            );
        }
        existing.setSlug(newSlug);

        // Category optional
        if (req.getCategoryId() == null) {
            existing.setCategory(null);
        } else {
            Category cat = categoryRepository.findById(req.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category with id " + req.getCategoryId() + " not found"));
            existing.setCategory(cat);
        }

        // Thumbnail optional update
        if (req.getThumbnail() != null && !req.getThumbnail().isEmpty()) {
            String url = fileUploader.upload(req.getThumbnail(), THUMB_DIR);
            existing.setThumbnailUrl(url);
        }

        applyPublishedAt(existing);

        Article saved = articleRepository.save(existing);
        return articleMapper.toDto(saved);
    }

    @Override
    public void delete(Long id) {
        Article existing = articleRepository.findById(id)
                .orElseThrow(() ->
                        new ArticleNotFoundException("Article with id " + id + " not found")
                );

        articleRepository.delete(existing);
    }

    private void applyPublishedAt(Article entity) {
        if (entity.getStatus() == ArticleStatus.PUBLISHED && entity.getPublishedAt() == null) {
            entity.setPublishedAt(LocalDateTime.now());
        }
    }

    private User getCurrentUserOrThrow() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            throw new RuntimeException("Unauthenticated request");
        }

        Object principal = auth.getPrincipal();

        if (principal instanceof User u) {
            return u;
        }

        if (principal instanceof UserDetails ud) {
            String email = ud.getUsername();
            return userRepository.findByEmailIgnoreCase(email)
                    .orElseThrow(() -> new RuntimeException("Authenticated user not found in DB: " + email));
        }

        if (principal instanceof String email) {
            return userRepository.findByEmailIgnoreCase(email)
                    .orElseThrow(() -> new RuntimeException("Authenticated user not found in DB: " + email));
        }

        throw new RuntimeException("Unsupported principal type: " + principal.getClass().getName());
    }
}
