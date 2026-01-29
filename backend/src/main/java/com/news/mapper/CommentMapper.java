package com.news.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.news.entity.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CommentMapper extends BaseMapper<Comment> {
    @Select("""
        SELECT c.*, u.nickname as user_nickname, u.avatar as user_avatar
        FROM news_comment c
        LEFT JOIN sys_user u ON c.user_id = u.id
        WHERE c.article_id = #{articleId} AND c.status = 1 AND c.parent_id = 0
        ORDER BY c.created_at DESC
        """)
    IPage<Comment> selectByArticleId(Page<Comment> page, @Param("articleId") Long articleId);

    @Select("""
        SELECT c.*, u.nickname as user_nickname, u.avatar as user_avatar
        FROM news_comment c
        LEFT JOIN sys_user u ON c.user_id = u.id
        WHERE c.parent_id = #{parentId} AND c.status = 1
        ORDER BY c.created_at ASC
        """)
    List<Comment> selectReplies(@Param("parentId") Long parentId);

    @Select("""
        SELECT COUNT(*) FROM news_comment
        WHERE article_id = #{articleId} AND status = 1
        """)
    Long countByArticleId(@Param("articleId") Long articleId);

    @Select("""
        SELECT c.*, a.title as article_title, u.nickname as user_nickname
        FROM news_comment c
        LEFT JOIN news_article a ON c.article_id = a.id
        LEFT JOIN sys_user u ON c.user_id = u.id
        WHERE c.user_id = #{userId} AND c.status = 1
        ORDER BY c.created_at DESC
        """)
    List<Comment> selectByUserId(@Param("userId") Long userId);

    @Select("""
        SELECT c.*, a.title as article_title, u.nickname as user_nickname
        FROM news_comment c
        LEFT JOIN news_article a ON c.article_id = a.id
        LEFT JOIN sys_user u ON c.user_id = u.id
        WHERE (a.title LIKE CONCAT('%', #{articleTitle}, '%') OR #{articleTitle} IS NULL OR #{articleTitle} = '')
        AND (c.status = #{status} OR #{status} IS NULL)
        ORDER BY c.created_at DESC
        """)
    IPage<Comment> selectAllComments(Page<Comment> page, @Param("articleTitle") String articleTitle, @Param("status") Integer status);
}
