package com.news.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.news.common.exception.BusinessException;
import com.news.common.result.ResultCode;
import com.news.dto.ArticleDTO;
import com.news.entity.Article;
import com.news.mapper.ArticleMapper;
import com.news.mapper.CategoryMapper;
import com.news.service.ArticleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {
    private final ArticleMapper articleMapper;
    private final CategoryMapper categoryMapper;

    @Override
    public IPage<Article> getArticlePage(Page<Article> page) {
        return articleMapper.selectArticlePage(page);
    }

    @Override
    public IPage<Article> getArticlePageByCategory(Page<Article> page, Long categoryId) {
        return articleMapper.selectByCategoryId(page, categoryId);
    }

    @Override
    public IPage<Article> searchArticles(Page<Article> page, String keyword) {
        return articleMapper.searchArticles(page, keyword);
    }

    @Override
    public Article getArticleDetail(Long id) {
        Article article = articleMapper.findByIdWithDetail(id);
        if (article == null || article.getStatus() != 1) {
            throw new BusinessException(ResultCode.ARTICLE_NOT_FOUND);
        }
        return article;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createArticle(ArticleDTO articleDTO, Long authorId) {
        if (categoryMapper.selectById(articleDTO.getCategoryId()) == null) {
            throw new BusinessException(ResultCode.CATEGORY_NOT_FOUND);
        }
        Article article = new Article();
        article.setTitle(articleDTO.getTitle());
        article.setSummary(articleDTO.getSummary());
        article.setContent(articleDTO.getContent());
        article.setCoverImage(articleDTO.getCoverImage());
        article.setCategoryId(articleDTO.getCategoryId());
        article.setAuthorId(authorId);
        article.setStatus(articleDTO.getStatus() != null ? articleDTO.getStatus() : 0);
        article.setIsTop(articleDTO.getIsTop() != null ? articleDTO.getIsTop() : 0);
        if (articleDTO.getStatus() == 1) {
            article.setPublishedAt(LocalDateTime.now());
        }
        articleMapper.insert(article);
        log.info("文章创建成功: {}", article.getTitle());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateArticle(ArticleDTO articleDTO) {
        Article article = articleMapper.selectById(articleDTO.getId());
        if (article == null) {
            throw new BusinessException(ResultCode.ARTICLE_NOT_FOUND);
        }
        if (articleDTO.getCategoryId() != null && categoryMapper.selectById(articleDTO.getCategoryId()) == null) {
            throw new BusinessException(ResultCode.CATEGORY_NOT_FOUND);
        }
        article.setTitle(articleDTO.getTitle());
        article.setSummary(articleDTO.getSummary());
        article.setContent(articleDTO.getContent());
        article.setCoverImage(articleDTO.getCoverImage());
        if (articleDTO.getCategoryId() != null) {
            article.setCategoryId(articleDTO.getCategoryId());
        }
        if (articleDTO.getStatus() != null) {
            article.setStatus(articleDTO.getStatus());
            if (articleDTO.getStatus() == 1 && article.getPublishedAt() == null) {
                article.setPublishedAt(LocalDateTime.now());
            }
        }
        if (articleDTO.getIsTop() != null) {
            article.setIsTop(articleDTO.getIsTop());
        }
        articleMapper.updateById(article);
        log.info("文章更新成功: {}", article.getTitle());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteArticle(Long id) {
        Article article = articleMapper.selectById(id);
        if (article == null) {
            throw new BusinessException(ResultCode.ARTICLE_NOT_FOUND);
        }
        articleMapper.deleteById(id);
        log.info("文章删除成功: {}", article.getTitle());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void publishArticle(Long id) {
        Article article = articleMapper.selectById(id);
        if (article == null) {
            throw new BusinessException(ResultCode.ARTICLE_NOT_FOUND);
        }
        article.setStatus(1);
        article.setPublishedAt(LocalDateTime.now());
        articleMapper.updateById(article);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unpublishArticle(Long id) {
        Article article = articleMapper.selectById(id);
        if (article == null) {
            throw new BusinessException(ResultCode.ARTICLE_NOT_FOUND);
        }
        article.setStatus(2);
        articleMapper.updateById(article);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void incrementViewCount(Long id) {
        Article article = articleMapper.selectById(id);
        if (article != null) {
            article.setViewCount(article.getViewCount() + 1);
            articleMapper.updateById(article);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void likeArticle(Long id) {
        Article article = articleMapper.selectById(id);
        if (article != null) {
            article.setLikeCount(article.getLikeCount() + 1);
            articleMapper.updateById(article);
        }
    }

    @Override
    public IPage<Article> getArticlesByAuthor(Page<Article> page, Long authorId) {
        return articleMapper.selectByAuthorId(page, authorId);
    }

    @Override
    public List<Article> getHotArticles(Integer limit) {
        return articleMapper.selectHotArticles(limit);
    }

    @Override
    public List<Article> getTopArticles() {
        return articleMapper.selectTopArticles();
    }
}
