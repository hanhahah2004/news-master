package com.news.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.news.common.page.PageResult;
import com.news.common.result.Result;
import com.news.dto.ArticleDTO;
import com.news.entity.Article;
import com.news.security.UserPrincipal;
import com.news.service.ArticleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/articles")
@RequiredArgsConstructor
public class ArticleController {
    private final ArticleService articleService;

    @GetMapping
    public ResponseEntity<?> getArticlePage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<Article> page = new Page<>(pageNum, pageSize);
        IPage<Article> result = articleService.getArticlePage(page);
        return ResponseEntity.ok(Result.success(PageResult.of(result.getTotal(), pageNum, pageSize, result.getRecords())));
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<?> getArticlesByCategory(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<Article> page = new Page<>(pageNum, pageSize);
        IPage<Article> result = articleService.getArticlePageByCategory(page, categoryId);
        return ResponseEntity.ok(Result.success(PageResult.of(result.getTotal(), pageNum, pageSize, result.getRecords())));
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchArticles(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<Article> page = new Page<>(pageNum, pageSize);
        IPage<Article> result = articleService.searchArticles(page, keyword);
        return ResponseEntity.ok(Result.success(PageResult.of(result.getTotal(), pageNum, pageSize, result.getRecords())));
    }

    @GetMapping("/hot")
    public ResponseEntity<?> getHotArticles(@RequestParam(defaultValue = "10") Integer limit) {
        List<Article> articles = articleService.getHotArticles(limit);
        return ResponseEntity.ok(Result.success(articles));
    }

    @GetMapping("/top")
    public ResponseEntity<?> getTopArticles() {
        List<Article> articles = articleService.getTopArticles();
        return ResponseEntity.ok(Result.success(articles));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getArticleDetail(@PathVariable Long id) {
        Article article = articleService.getArticleDetail(id);
        articleService.incrementViewCount(id);
        return ResponseEntity.ok(Result.success(article));
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> createArticle(
            @Valid @RequestBody ArticleDTO articleDTO,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        articleService.createArticle(articleDTO, userPrincipal.getUserId());
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> updateArticle(
            @PathVariable Long id,
            @Valid @RequestBody ArticleDTO articleDTO,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        articleDTO.setId(id);
        articleService.updateArticle(articleDTO);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> deleteArticle(@PathVariable Long id) {
        articleService.deleteArticle(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/publish")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> publishArticle(@PathVariable Long id) {
        articleService.publishArticle(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/unpublish")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> unpublishArticle(@PathVariable Long id) {
        articleService.unpublishArticle(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<?> likeArticle(@PathVariable Long id) {
        articleService.likeArticle(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/author/{authorId}")
    public ResponseEntity<?> getArticlesByAuthor(
            @PathVariable Long authorId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<Article> page = new Page<>(pageNum, pageSize);
        IPage<Article> result = articleService.getArticlesByAuthor(page, authorId);
        return ResponseEntity.ok(Result.success(PageResult.of(result.getTotal(), pageNum, pageSize, result.getRecords())));
    }
}
