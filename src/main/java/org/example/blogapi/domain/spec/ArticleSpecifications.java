package org.example.blogapi.domain.spec;

import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.example.blogapi.api.dto.request.ArticleCriteria;
import org.example.blogapi.api.dto.request.MyArticleCriteria;
import org.example.blogapi.domain.entity.Article;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public final class ArticleSpecifications {

    private ArticleSpecifications() {
    }

    public static Specification<Article> withFilters(ArticleCriteria c) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (c == null) {
                return cb.conjunction();
            }

            if (c.getId() != null) {
                predicates.add(cb.equal(root.get("id"), c.getId()));
            }

            if (c.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), c.getStatus()));
            }

            if (hasText(c.getSlug())) {
                predicates.add(cb.equal(cb.lower(root.get("slug")), c.getSlug().trim().toLowerCase()));
            }

            if (hasText(c.getName())) {
                String pattern = "%" + c.getName().trim().toLowerCase() + "%";
                predicates.add(cb.like(cb.lower(root.get("title")), pattern));
            }

            if (hasText(c.getCategoryName())) {
                var categoryJoin = root.join("category", JoinType.LEFT);
                predicates.add(cb.equal(cb.lower(categoryJoin.get("name")), c.getCategoryName().trim().toLowerCase()));
            }

            if (c.getAuthorId() != null) {
                var authorJoin = root.join("author", JoinType.INNER);
                predicates.add(cb.equal(authorJoin.get("id"), c.getAuthorId()));
            }

            if (hasText(c.getKeywords())) {
                String[] parts = c.getKeywords().split(",");
                List<Predicate> keywordOr = new ArrayList<>();

                for (String part : parts) {
                    String kw = part == null ? "" : part.trim().toLowerCase();
                    if (!kw.isEmpty()) {
                        keywordOr.add(cb.like(cb.lower(root.get("keywords")), "%" + kw + "%"));
                    }
                }

                if (!keywordOr.isEmpty()) {
                    predicates.add(cb.or(keywordOr.toArray(new Predicate[0])));
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<Article> withFilters(MyArticleCriteria c) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (c == null) {
                return cb.conjunction();
            }

            if (c.getId() != null) {
                predicates.add(cb.equal(root.get("id"), c.getId()));
            }

            if (c.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), c.getStatus()));
            }

            if (hasText(c.getName())) {
                String pattern = "%" + c.getName().trim().toLowerCase() + "%";
                predicates.add(cb.like(cb.lower(root.get("title")), pattern));
            }

            if (hasText(c.getCategoryName())) {
                var categoryJoin = root.join("category", JoinType.LEFT);
                predicates.add(cb.equal(cb.lower(categoryJoin.get("name")), c.getCategoryName().trim().toLowerCase()));
            }

            if (hasText(c.getKeywords())) {
                String[] parts = c.getKeywords().split(",");
                List<Predicate> keywordOr = new ArrayList<>();

                for (String part : parts) {
                    String kw = part == null ? "" : part.trim().toLowerCase();
                    if (!kw.isEmpty()) {
                        keywordOr.add(cb.like(cb.lower(root.get("keywords")), "%" + kw + "%"));
                    }
                }

                if (!keywordOr.isEmpty()) {
                    predicates.add(cb.or(keywordOr.toArray(new Predicate[0])));
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    // Enforce author from token
    public static Specification<Article> withAuthorId(Long authorId) {
        return (root, query, cb) -> {
            if (authorId == null) return cb.conjunction();
            var authorJoin = root.join("author", JoinType.INNER);
            return cb.equal(authorJoin.get("id"), authorId);
        };
    }

    private static boolean hasText(String s) {
        return s != null && !s.trim().isEmpty();
    }
}