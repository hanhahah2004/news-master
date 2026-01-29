package com.news.service.impl;

import com.news.entity.FriendLink;
import com.news.mapper.FriendLinkMapper;
import com.news.service.FriendLinkService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class FriendLinkServiceImpl extends ServiceImpl<FriendLinkMapper, FriendLink> implements FriendLinkService {
}
