package com.news.service;

import com.news.dto.LoginDTO;
import com.news.dto.RegisterDTO;
import com.news.dto.UserDTO;
import com.news.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

public interface UserService extends IService<User> {
    String login(LoginDTO loginDTO);

    void register(RegisterDTO registerDTO);

    User getUserByUsername(String username);

    void updateUser(UserDTO userDTO);

    User getCurrentUser(Long userId);

    List<User> getAllUsers();

    Long countUsers();
}
