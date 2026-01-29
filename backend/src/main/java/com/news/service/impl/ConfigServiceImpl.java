package com.news.service.impl;

import com.news.entity.Config;
import com.news.mapper.ConfigMapper;
import com.news.service.ConfigService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ConfigServiceImpl extends ServiceImpl<ConfigMapper, Config> implements ConfigService {
    private final ConfigMapper configMapper;

    @Override
    public Map<String, String> getSiteConfig() {
        List<Config> configs = configMapper.selectSiteConfig();
        Map<String, String> configMap = new HashMap<>();
        for (Config config : configs) {
            configMap.put(config.getConfigKey(), config.getConfigValue());
        }
        return configMap;
    }

    @Override
    public String getConfigValue(String key) {
        Config config = configMapper.findByKey(key);
        return config != null ? config.getConfigValue() : null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateConfig(String key, String value) {
        Config config = configMapper.findByKey(key);
        if (config != null) {
            config.setConfigValue(value);
            configMapper.updateById(config);
        }
    }
}
