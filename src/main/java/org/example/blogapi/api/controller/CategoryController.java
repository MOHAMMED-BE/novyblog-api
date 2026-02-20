package org.example.blogapi.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.blogapi.api.dto.request.CategoryDto;
import org.example.blogapi.service.CategoryService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/categories")
@Slf4j
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<CategoryDto>> findAll(Pageable pageable) {
        log.info("REST request to get all Categories - page {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());
        return ResponseEntity.ok(categoryService.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryDto> findById(@PathVariable Long id) {
        log.info("REST request to get Category {}", id);
        return ResponseEntity.ok(categoryService.findById(id));
    }

    @PostMapping
    public ResponseEntity<CategoryDto> create(@Valid @RequestBody CategoryDto dto) {
        CategoryDto created = categoryService.create(dto);

        URI location = URI.create("categories/" + created.getId());

        return ResponseEntity
                .created(location)
                .body(created);
    }

    /**
     * PUT /api/categories/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<CategoryDto> update(@PathVariable Long id, @Valid @RequestBody CategoryDto dto) {
        CategoryDto updated = categoryService.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    /**
     * DELETE /api/categories/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
