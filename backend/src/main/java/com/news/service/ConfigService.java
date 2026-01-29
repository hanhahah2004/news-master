package com.news.service;

import com.news.entity.Config;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

public interface ConfigService extends IService<Config> {
    Map<String, String> getSiteConfig();

    String getConfigValue(String key);

    void updateConfig(String key, String value);
}
