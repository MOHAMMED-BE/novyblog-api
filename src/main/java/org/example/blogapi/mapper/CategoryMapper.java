package org.example.blogapi.mapper;


import org.example.blogapi.api.dto.request.CategoryDto;
import org.example.blogapi.domain.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CategoryMapper {
    Category toEntity(CategoryDto dto);

    CategoryDto toDto(Category entity);

    List<Category> toEntity(List<CategoryDto> dtos);

    List<CategoryDto> toDto(List<Category> entities);
}

