package com.news.service.impl;

import com.news.entity.Category;
import com.news.mapper.CategoryMapper;
import com.news.service.CategoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
}
