package org.example.blogapi.domain.spec;

import org.example.blogapi.domain.entity.Article;
import org.example.blogapi.domain.enums.ArticleStatus;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;

import java.util.ArrayList;
import java.util.List;


public final class ArticleSpecifications {

    private ArticleSpecifications() {
    }

    public static Specification<Article> withFilters(
            Long id,
            ArticleStatus status,
            String slug,
            String name,
            String keywords,
            String categoryName
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // id (exact)
            if (id != null) {
                predicates.add(cb.equal(root.get("id"), id));
            }

            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }

            // slug (exact, ignore case)
            if (hasText(slug)) {
                predicates.add(cb.equal(cb.lower(root.get("slug")), slug.trim().toLowerCase()));
            }

            // name -> title (contains, ignore case)
            if (hasText(name)) {
                String pattern = "%" + name.trim().toLowerCase() + "%";
                predicates.add(cb.like(cb.lower(root.get("title")), pattern));
            }

            // categoryName (exact, ignore case) via join
            if (hasText(categoryName)) {
                var categoryJoin = root.join("category", JoinType.LEFT);
                predicates.add(cb.equal(cb.lower(categoryJoin.get("name")), categoryName.trim().toLowerCase()));
            }

            // keywords param: "book,code,app" => match ANY (OR) in stored keywords string
            if (hasText(keywords)) {
                String[] parts = keywords.split(",");
                List<Predicate> keywordOr = new ArrayList<>();

                for (String part : parts) {
                    String kw = part == null ? "" : part.trim().toLowerCase();
                    if (!kw.isEmpty()) {
                        // Simple contains match. (Trade-off: "book" matches "notebook")
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

    private static boolean hasText(String s) {
        return s != null && !s.trim().isEmpty();
    }
}
