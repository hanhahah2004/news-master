package com.news.common.result;

import lombok.Getter;

@Getter
public enum ResultCode {
    SUCCESS(200, "操作成功"),
    ERROR(500, "操作失败"),
    UNAUTHORIZED(401, "未授权"),
    FORBIDDEN(403, "禁止访问"),
    NOT_FOUND(404, "资源不存在"),
    VALIDATION_ERROR(400, "参数验证失败"),
    TOKEN_EXPIRED(401, "Token已过期"),
    TOKEN_INVALID(401, "Token无效"),
    USER_NOT_FOUND(404, "用户不存在"),
    USER_PASSWORD_ERROR(400, "用户名或密码错误"),
    USER_DISABLED(403, "用户已被禁用"),
    USER_EXISTS(400, "用户已存在"),
    CATEGORY_NOT_FOUND(404, "分类不存在"),
    ARTICLE_NOT_FOUND(404, "文章不存在"),
    COMMENT_NOT_FOUND(404, "评论不存在"),
    FILE_UPLOAD_ERROR(500, "文件上传失败"),
    PERMISSION_DENIED(403, "权限不足");

    private final Integer code;
    private final String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
