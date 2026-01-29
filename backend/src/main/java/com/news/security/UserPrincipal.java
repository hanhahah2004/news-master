package com.news.security;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class UserPrincipal implements Serializable {
    private Long userId;
    private String username;
    private Integer role;
}
