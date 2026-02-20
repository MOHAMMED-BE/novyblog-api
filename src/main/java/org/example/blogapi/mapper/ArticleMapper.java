package org.example.blogapi.mapper;

import org.example.blogapi.api.dto.response.ArticleDto;
import org.example.blogapi.domain.entity.Article;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ArticleMapper {

    @Mapping(target = "authorId", source = "author.id")
    @Mapping(target = "categoryId", source = "category.id")
    ArticleDto toDto(Article entity);
}
