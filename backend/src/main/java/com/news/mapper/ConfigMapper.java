package com.news.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.news.entity.Config;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ConfigMapper extends BaseMapper<Config> {
    @Select("SELECT * FROM news_config WHERE config_key = #{configKey}")
    Config findByKey(@Param("configKey") String configKey);

    @Select("SELECT * FROM news_config WHERE config_key IN ('site_name', 'site_description', 'site_keywords', 'icp_number', 'default_avatar')")
    List<Config> selectSiteConfig();
}
