package com.neuedu.service.impl;

import com.neuedu.common.ServerResponse;
import com.neuedu.dao.UserInfoMapper;
import com.neuedu.entity.UserInfo;
import com.neuedu.service.UserMangerService;
import com.neuedu.util.MD5Utils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserMangerServiceImpl implements UserMangerService {

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Override
    public ServerResponse login(String username, String password) {
        /* step1 非空检验 */
        if (StringUtils.isBlank(username)) {
            return ServerResponse.createServerResponseByError("用户名未填写");
        }
        if (StringUtils.isBlank(password)) {
            return ServerResponse.createServerResponseByError("密码未填写");
        }
        /*step2:判断用户名是否存在 */
        Integer result = userInfoMapper.checkUsername(username);
        if (result <= 0) {//用户名不存在
            return ServerResponse.createServerResponseByError("用户名不存在");
        }

        UserInfo userInfo = userInfoMapper.selectUserInfoByUsernameAndPassword(username, MD5Utils.getMD5Code(password));
        if (userInfo == null) {
            return ServerResponse.createServerResponseByError("密码错误");
        }
        /* 判断权限是否足够 管理员*/

            return ServerResponse.createServerResponseBySuccess();
    }

    @Override
    public int updateToken(int userId,String token) {

            return userInfoMapper.updateToken(userId,token);

    }

}
