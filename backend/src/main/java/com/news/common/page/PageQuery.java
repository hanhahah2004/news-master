package com.news.common.page;

import lombok.Data;
import java.io.Serializable;

@Data
public class PageQuery implements Serializable {
    private Integer pageNum = 1;
    private Integer pageSize = 10;
    private String orderBy = "created_at";
    private String orderType = "desc";
}
