package com.news.dto;
import lombok.Data;

@Data
public class ArticleQueryDTO {
    private Long categoryId;
    private String keyword;
    private Integer status;
    private Integer pageNum = 1;
    private Integer pageSize = 10;
    private String orderBy = "created_at";
    private String orderType = "desc";
}
