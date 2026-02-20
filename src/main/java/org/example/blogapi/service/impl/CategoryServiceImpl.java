// src/main/java/org/example/blogapi/service/impl/CategoryServiceImpl.java
package org.example.blogapi.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.blogapi.api.dto.request.CategoryDto;
import org.example.blogapi.domain.entity.Category;
import org.example.blogapi.domain.repository.CategoryRepository;
import org.example.blogapi.mapper.CategoryMapper;
import org.example.blogapi.service.CategoryService;
import org.example.blogapi.service.exceptions.CategoryAlreadyExistsException;
import org.example.blogapi.service.exceptions.CategoryNotFoundException;
import org.example.blogapi.util.Slugify;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDto> findAll(Pageable pageable) {
        return categoryRepository.findAll(pageable)
                .map(categoryMapper::toDto)
                .getContent();
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDto findById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Category with id " + id + " not found"));
        return categoryMapper.toDto(category);
    }

    @Override
    public CategoryDto create(CategoryDto dto) {
        Category entity = categoryMapper.toEntity(dto);

        // 🔹 ALWAYS generate slug from name
        String slug = Slugify.slugify(dto.getName());

        if (categoryRepository.existsBySlugIgnoreCase(slug)) {
            throw new CategoryAlreadyExistsException(
                    "Category with slug " + slug + " already exists"
            );
        }

        entity.setSlug(slug);

        Category saved = categoryRepository.save(entity);
        return categoryMapper.toDto(saved);
    }

    @Override
    public CategoryDto update(Long id, CategoryDto dto) {
        Category existing = categoryRepository.findById(id)
                .orElseThrow(() ->
                        new CategoryNotFoundException("Category with id " + id + " not found")
                );

        // 🔹 Slug is derived from name
        String newSlug = Slugify.slugify(dto.getName());

        if (!newSlug.equalsIgnoreCase(existing.getSlug())
                && categoryRepository.existsBySlugIgnoreCase(newSlug)) {
            throw new CategoryAlreadyExistsException(
                    "Category with slug " + newSlug + " already exists"
            );
        }

        existing.setName(dto.getName());
        existing.setSlug(newSlug);
        existing.setDescription(dto.getDescription());
        existing.setEnabled(dto.isEnabled());

        return categoryMapper.toDto(categoryRepository.save(existing));
    }


    @Override
    public void delete(Long id) {
        Category existing = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Category with id " + id + " not found"));
        categoryRepository.delete(existing);
    }
}
