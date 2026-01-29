package com.news.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("news_friend_link")
public class FriendLink implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String url;
    private String description;
    private String logo;
    private Integer sort;
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
