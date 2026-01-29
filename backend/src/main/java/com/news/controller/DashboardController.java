package com.news.controller;

import com.news.common.result.Result;
import com.news.service.ArticleService;
import com.news.service.CommentService;
import com.news.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {
    private final ArticleService articleService;
    private final UserService userService;
    private final CommentService commentService;

    @GetMapping("/stats")
    public ResponseEntity<?> getDashboardStats() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("articleCount", articleService.count());
        stats.put("userCount", userService.countUsers());
        stats.put("commentCount", commentService.countComments());
        stats.put("viewCount", 0L); // 访问量功能尚未实现
        return ResponseEntity.ok(Result.success(stats));
    }
}