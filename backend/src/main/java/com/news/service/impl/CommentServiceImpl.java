package com.news.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.news.common.exception.BusinessException;
import com.news.common.result.ResultCode;
import com.news.dto.CommentDTO;
import com.news.entity.Article;
import com.news.entity.Comment;
import com.news.mapper.ArticleMapper;
import com.news.mapper.CommentMapper;
import com.news.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {
    private final CommentMapper commentMapper;
    private final ArticleMapper articleMapper;

    @Override
    public IPage<Comment> getCommentsByArticleId(Page<Comment> page, Long articleId) {
        Article article = articleMapper.selectById(articleId);
        if (article == null) {
            throw new BusinessException(ResultCode.ARTICLE_NOT_FOUND);
        }
        return commentMapper.selectByArticleId(page, articleId);
    }

    @Override
    public List<Comment> getRepliesByParentId(Long parentId) {
        return commentMapper.selectReplies(parentId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createComment(CommentDTO commentDTO, Long userId) {
        Article article = articleMapper.selectById(commentDTO.getArticleId());
        if (article == null) {
            throw new BusinessException(ResultCode.ARTICLE_NOT_FOUND);
        }
        Comment comment = new Comment();
        comment.setArticleId(commentDTO.getArticleId());
        comment.setUserId(userId);
        comment.setParentId(commentDTO.getParentId() != null ? commentDTO.getParentId() : 0L);
        comment.setContent(commentDTO.getContent());
        comment.setStatus(1);
        commentMapper.insert(comment);
        article.setCommentCount(article.getCommentCount() + 1);
        articleMapper.updateById(article);
        log.info("评论创建成功: 文章ID={}", commentDTO.getArticleId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteComment(Long id) {
        Comment comment = commentMapper.selectById(id);
        if (comment == null) {
            throw new BusinessException(ResultCode.COMMENT_NOT_FOUND);
        }
        commentMapper.deleteById(id);
        Article article = articleMapper.selectById(comment.getArticleId());
        if (article != null && article.getCommentCount() > 0) {
            article.setCommentCount(article.getCommentCount() - 1);
            articleMapper.updateById(article);
        }
        log.info("评论删除成功: ID={}", id);
    }

    @Override
    public List<Comment> getCommentsByUserId(Long userId) {
        return commentMapper.selectByUserId(userId);
    }

    @Override
    public IPage<Comment> getAllComments(Page<Comment> page, String articleTitle, Integer status) {
        return commentMapper.selectAllComments(page, articleTitle, status);
    }

    @Override
    public Long countComments() {
        return commentMapper.selectCount(null);
    }
}
