package com.news.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.news.dto.CommentDTO;
import com.news.entity.Comment;

import java.util.List;

public interface CommentService extends IService<Comment> {
    IPage<Comment> getCommentsByArticleId(Page<Comment> page, Long articleId);

    List<Comment> getRepliesByParentId(Long parentId);

    void createComment(CommentDTO commentDTO, Long userId);

    void deleteComment(Long id);

    List<Comment> getCommentsByUserId(Long userId);

    IPage<Comment> getAllComments(Page<Comment> page, String articleTitle, Integer status);

    Long countComments();
}
