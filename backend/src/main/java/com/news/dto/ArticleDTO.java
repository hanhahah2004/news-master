package com.news.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ArticleDTO {
    private Long id;

    @NotBlank(message = "文章标题不能为空")
    @Size(max = 200, message = "标题长度不能超过200字符")
    private String title;

    @Size(max = 500, message = "摘要长度不能超过500字符")
    private String summary;

    @NotBlank(message = "文章内容不能为空")
    private String content;

    private String coverImage;

    @NotNull(message = "请选择分类")
    private Long categoryId;

    private Integer status;

    private Integer isTop;
}
