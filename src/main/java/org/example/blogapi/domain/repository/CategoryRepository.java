package org.example.blogapi.domain.repository;

import org.example.blogapi.domain.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsBySlugIgnoreCase(String slug);
}
