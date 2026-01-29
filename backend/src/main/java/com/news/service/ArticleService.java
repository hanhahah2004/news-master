package com.news.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.news.dto.ArticleDTO;
import com.news.dto.ArticleQueryDTO;
import com.news.entity.Article;

import java.util.List;

public interface ArticleService extends IService<Article> {
    IPage<Article> getArticlePage(Page<Article> page);

    IPage<Article> getArticlePageByCategory(Page<Article> page, Long categoryId);

    IPage<Article> searchArticles(Page<Article> page, String keyword);

    Article getArticleDetail(Long id);

    void createArticle(ArticleDTO articleDTO, Long authorId);

    void updateArticle(ArticleDTO articleDTO);

    void deleteArticle(Long id);

    void publishArticle(Long id);

    void unpublishArticle(Long id);

    void incrementViewCount(Long id);

    void likeArticle(Long id);

    IPage<Article> getArticlesByAuthor(Page<Article> page, Long authorId);

    List<Article> getHotArticles(Integer limit);

    List<Article> getTopArticles();
}
