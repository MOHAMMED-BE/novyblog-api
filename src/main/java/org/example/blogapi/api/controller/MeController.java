package org.example.blogapi.api.controller;

import lombok.RequiredArgsConstructor;
import org.example.blogapi.api.dto.request.MyArticleCriteria;
import org.example.blogapi.api.dto.response.ArticleDto;
import org.example.blogapi.domain.entity.User;
import org.example.blogapi.api.dto.auth.MeResponse;
import org.example.blogapi.service.ArticleService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MeController {

    private final ArticleService articleService;

    @GetMapping("/me")
    public MeResponse me(@AuthenticationPrincipal User user) {
        return new MeResponse(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getRoles().stream().map(Enum::name).toList()
        );
    }

    @GetMapping("/me/articles")
    public Page<ArticleDto> myArticles(
            @AuthenticationPrincipal User user,
            MyArticleCriteria criteria,
            Pageable pageable
    ) {
        return articleService.findMyArticles(user.getId(), criteria, pageable);
    }
}
