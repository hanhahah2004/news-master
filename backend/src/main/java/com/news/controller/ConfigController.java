package com.news.controller;

import com.news.common.result.Result;
import com.news.service.ConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/config")
@RequiredArgsConstructor
public class ConfigController {
    private final ConfigService configService;

    @GetMapping("/site")
    public ResponseEntity<?> getSiteConfig() {
        Map<String, String> config = configService.getSiteConfig();
        return ResponseEntity.ok(Result.success(config));
    }

    @GetMapping("/{key}")
    public ResponseEntity<?> getConfigValue(@PathVariable String key) {
        String value = configService.getConfigValue(key);
        return ResponseEntity.ok(Result.success(Map.of("value", value)));
    }

    @PutMapping("/{key}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateConfig(@PathVariable String key, @RequestBody Map<String, String> body) {
        configService.updateConfig(key, body.get("value"));
        return ResponseEntity.ok().build();
    }
}
