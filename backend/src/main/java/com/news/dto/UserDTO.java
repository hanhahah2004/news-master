package com.news.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserDTO {
    private Long id;

    @Size(max = 20, message = "昵称长度不能超过20位")
    private String nickname;

    @Size(max = 100, message = "邮箱长度不能超过100位")
    private String email;

    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    private String avatar;
}
