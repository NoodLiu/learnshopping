package com.neuedu.service;

import com.neuedu.common.ServerResponse;

public interface UserMangerService {
    ServerResponse login(String username,String password);

    int updateToken(int userId,String token);
}
