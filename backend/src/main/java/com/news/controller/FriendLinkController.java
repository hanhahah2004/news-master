package com.news.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.news.common.result.Result;
import com.news.entity.FriendLink;
import com.news.service.FriendLinkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/friend-links")
@RequiredArgsConstructor
public class FriendLinkController {
    private final FriendLinkService friendLinkService;

    @GetMapping
    public ResponseEntity<?> getFriendLinkList() {
        List<FriendLink> friendLinks = friendLinkService.list(
                new LambdaQueryWrapper<FriendLink>()
                        .eq(FriendLink::getStatus, 1)
                        .orderByAsc(FriendLink::getSort)
        );
        return ResponseEntity.ok(Result.success(friendLinks));
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllFriendLinks() {
        List<FriendLink> friendLinks = friendLinkService.list();
        return ResponseEntity.ok(Result.success(friendLinks));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getFriendLinkById(@PathVariable Long id) {
        FriendLink friendLink = friendLinkService.getById(id);
        return ResponseEntity.ok(Result.success(friendLink));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createFriendLink(@RequestBody FriendLink friendLink) {
        friendLinkService.save(friendLink);
        return ResponseEntity.ok(Result.success());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateFriendLink(@PathVariable Long id, @RequestBody FriendLink friendLink) {
        friendLink.setId(id);
        friendLinkService.updateById(friendLink);
        return ResponseEntity.ok(Result.success());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteFriendLink(@PathVariable Long id) {
        friendLinkService.removeById(id);
        return ResponseEntity.ok(Result.success());
    }
}
