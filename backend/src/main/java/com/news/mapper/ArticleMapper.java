package com.news.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.news.entity.Article;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ArticleMapper extends BaseMapper<Article> {
    @Select("""
        SELECT a.*, u.nickname as author_nickname, c.name as category_name
        FROM news_article a
        LEFT JOIN sys_user u ON a.author_id = u.id
        LEFT JOIN news_category c ON a.category_id = c.id
        WHERE a.status = 1
        ORDER BY a.is_top DESC, a.created_at DESC
        """)
    IPage<Article> selectArticlePage(Page<Article> page);

    @Select("""
        SELECT a.*, u.nickname as author_nickname, c.name as category_name
        FROM news_article a
        LEFT JOIN sys_user u ON a.author_id = u.id
        LEFT JOIN news_category c ON a.category_id = c.id
        WHERE a.status = 1 AND a.category_id = #{categoryId}
        ORDER BY a.is_top DESC, a.created_at DESC
        """)
    IPage<Article> selectByCategoryId(Page<Article> page, @Param("categoryId") Long categoryId);

    @Select("""
        SELECT a.*, u.nickname as author_nickname, c.name as category_name
        FROM news_article a
        LEFT JOIN sys_user u ON a.author_id = u.id
        LEFT JOIN news_category c ON a.category_id = c.id
        WHERE a.status = 1 AND (a.title LIKE CONCAT('%', #{keyword}, '%') OR a.content LIKE CONCAT('%', #{keyword}, '%'))
        ORDER BY a.created_at DESC
        """)
    IPage<Article> searchArticles(Page<Article> page, @Param("keyword") String keyword);

    @Select("""
        SELECT a.*, u.nickname as author_nickname, c.name as category_name
        FROM news_article a
        LEFT JOIN sys_user u ON a.author_id = u.id
        LEFT JOIN news_category c ON a.category_id = c.id
        WHERE a.id = #{id}
        """)
    Article findByIdWithDetail(@Param("id") Long id);

    @Select("""
        SELECT a.*, u.nickname as author_nickname, c.name as category_name
        FROM news_article a
        LEFT JOIN sys_user u ON a.author_id = u.id
        LEFT JOIN news_category c ON a.category_id = c.id
        WHERE a.status = 1 AND a.author_id = #{authorId}
        ORDER BY a.created_at DESC
        """)
    IPage<Article> selectByAuthorId(Page<Article> page, @Param("authorId") Long authorId);

    @Select("""
        SELECT a.*, u.nickname as author_nickname, c.name as category_name
        FROM news_article a
        LEFT JOIN sys_user u ON a.author_id = u.id
        LEFT JOIN news_category c ON a.category_id = c.id
        WHERE a.status = 1
        ORDER BY a.view_count DESC
        LIMIT #{limit}
        """)
    List<Article> selectHotArticles(@Param("limit") Integer limit);

    @Select("""
        SELECT a.*, u.nickname as author_nickname, c.name as category_name
        FROM news_article a
        LEFT JOIN sys_user u ON a.author_id = u.id
        LEFT JOIN news_category c ON a.category_id = c.id
        WHERE a.status = 1 AND a.is_top = 1
        ORDER BY a.created_at DESC
        """)
    List<Article> selectTopArticles();
}
