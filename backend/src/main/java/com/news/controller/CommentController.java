package com.news.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.news.common.page.PageResult;
import com.news.common.result.Result;
import com.news.dto.CommentDTO;
import com.news.entity.Comment;
import com.news.security.UserPrincipal;
import com.news.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @GetMapping("/article/{articleId}")
    public ResponseEntity<?> getCommentsByArticleId(
            @PathVariable Long articleId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<Comment> page = new Page<>(pageNum, pageSize);
        IPage<Comment> result = commentService.getCommentsByArticleId(page, articleId);
        return ResponseEntity.ok(Result.success(PageResult.of(result.getTotal(), pageNum, pageSize, result.getRecords())));
    }

    @GetMapping("/replies/{parentId}")
    public ResponseEntity<?> getRepliesByParentId(@PathVariable Long parentId) {
        List<Comment> replies = commentService.getRepliesByParentId(parentId);
        Map<String, Object> result = new HashMap<>();
        result.put("replies", replies);
        return ResponseEntity.ok(Result.success(result));
    }

    @PostMapping
    public ResponseEntity<?> createComment(
            @Valid @RequestBody CommentDTO commentDTO,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        commentService.createComment(commentDTO, userPrincipal.getUserId());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteComment(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        commentService.deleteComment(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/user")
    public ResponseEntity<?> getCommentsByCurrentUser(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        List<Comment> comments = commentService.getCommentsByUserId(userPrincipal.getUserId());
        return ResponseEntity.ok(Result.success(comments));
    }

    @GetMapping("/admin")
    public ResponseEntity<?> getAllComments(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String articleTitle,
            @RequestParam(required = false) Integer status) {
        Page<Comment> page = new Page<>(pageNum, pageSize);
        IPage<Comment> result = commentService.getAllComments(page, articleTitle, status);
        return ResponseEntity.ok(Result.success(PageResult.of(result.getTotal(), pageNum, pageSize, result.getRecords())));
    }
}
