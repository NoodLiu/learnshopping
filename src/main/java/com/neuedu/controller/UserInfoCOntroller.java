package com.neuedu.controller;


import com.neuedu.dao.UserInfoMapper;
import com.neuedu.dto.UserDto;
import com.neuedu.entity.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * #### 一、用户模块
 *  ###### 登录
 *  ###### 注册
 *  ###### 忘记密码
 *  ###### 获取用户信息
 *  ###### 修改密码
 *  ###### 登出
 */
@RestController("/user")
public class UserInfoCOntroller {

    @Autowired
    private UserInfoMapper userInfoMapper;

    /**
     * 添加（注册）/修改 用户
     * @param
     */
    @GetMapping("/UpdateUser")
    public void save(UserDto userDto){

       /* userInfoMapper.insert(userInfo);
        userInfoMapper.updateByPrimaryKey(userInfo);*/
    }

    /**
     * 所有用户信息
     */
   @GetMapping("/list")
    public void list(){
        userInfoMapper.selectAll();
    }

    /**
     * 登出
     */
    @GetMapping("/delete")
    public void delete(UserInfo userInfo){
        userInfoMapper.deleteByPrimaryKey(userInfo.getId());
    }

    /**
     * 忘记密码
     */
    @GetMapping("/forget")
    public void forget(UserInfo userInfo){

    }
}
