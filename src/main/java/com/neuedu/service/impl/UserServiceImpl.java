package com.neuedu.service.impl;


import com.neuedu.common.Const;
import com.neuedu.common.ServerResponse;
import com.neuedu.dao.UserInfoMapper;
import com.neuedu.entity.UserInfo;
import com.neuedu.service.UserService;
import com.neuedu.util.TokenCache;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;


@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserInfoMapper userInfoMapper;

    /**
     * 登录
     * @param username
     * @param password
     * @return
     */
    @Override
    public ServerResponse login(@RequestParam(value = "username") String username,
                                @RequestParam(value = "password") String password) {
        /*step1:判断用户信息是否为空 */
        if (StringUtils.isBlank(username)) {
            return ServerResponse.createServerResponseByError("用户名不能为空");
        }
        if (StringUtils.isBlank(password)) {
            return ServerResponse.createServerResponseByError("密码不能为空");
        }
        /*step2:判断用户名是否存在 */
        Integer result = userInfoMapper.checkUsername(username);
        if (result <= 0) {//用户名不存在
            return ServerResponse.createServerResponseByError("用户名不存在");
        }
        /*step3:根据用户名和密码查询*/
        UserInfo userInfo = userInfoMapper.selectUserInfoByUsernameAndPassword(username,password);
        if (userInfo==null){
            return ServerResponse.createServerResponseByError("密码错误");
        }
        /* 返回之前密码制空 */
        userInfo.setPassword("");
        /* 处理结果返回 */
        return ServerResponse.createServerResponseBySuccess(null,userInfo);
    }

    /**
     * 注册
     * @param userInfo
     * @return
     */
    @Override
    public ServerResponse register(UserInfo userInfo) {
        /* step1:参数非空校验 */
        if (userInfo==null){
            return ServerResponse.createServerResponseByError("参数必须");
        }
        /* step1:校验用户名 */
        Integer result = userInfoMapper.checkUsername(userInfo.getUsername());
        if (result>0){
            return ServerResponse.createServerResponseByError("用户名已存在");
        }
        /* step1:校验邮箱 */
        Integer resultEmail = userInfoMapper.checkEmail(userInfo.getEmail());
        if (resultEmail>0){
            return ServerResponse.createServerResponseByError("邮箱已存在");
        }
        /* 设置角色 */
        userInfo.setRole(Const.RoleEnumn.ROLE_CUSTOMER.getCode());
        /* 注册 */
        int count = userInfoMapper.insert(userInfo);
        if (count>0){
            return ServerResponse.createServerResponseBySuccess("注册成功");
        }
        /* step1:前端返回结果 */

        return ServerResponse.createServerResponseByError("注册失败");
    }

    /**
     * 获取密保问题
     * @param username
     * @return
     */
    @Override
    public ServerResponse QuestionByUserName(String username) {
        /* step1: 参数校验 */
        if (StringUtils.isBlank(username)){
            return ServerResponse.createServerResponseByError("用户名不能为空");
        }
        /* step2:校验用户名 */
        Integer result = userInfoMapper.checkUsername(username);
        if (result==0){
            return ServerResponse.createServerResponseByError("用户名不存在");
        }
        /* step3:查找密保问题 */
        String question = userInfoMapper.QuestionByUserName(username);
        if (StringUtils.isBlank(question)){
            return ServerResponse.createServerResponseByError("密保问题空");
        }

        return ServerResponse.createServerResponseBySuccess(question);
    }

    @Override
    public ServerResponse QuestionToAnswer(String username,String question, String answer) {
        System.out.println(username+"sdhjfs===========");
        /* step1:参数校验 */
        if (StringUtils.isBlank(username)){
            return ServerResponse.createServerResponseByError("用户名不能为空");
        }
        if (StringUtils.isBlank(question)){
            return ServerResponse.createServerResponseByError("密保问题不能为空");
        }
        if (StringUtils.isBlank(answer)){
            return ServerResponse.createServerResponseByError("答案不能为空");
        }
        /* step2:根据username,question,answer查询 */
        int result = userInfoMapper.QuestionToAnswer(username,question,answer);
        System.out.println(result);
        if (result == 0){
            return ServerResponse.createServerResponseByError("答案错误");
        }
        /* step3:服务端生成一个token保存并将token返回给客户端 */
        String forgetToken = UUID.randomUUID().toString();
        TokenCache.set(username,forgetToken);
        return ServerResponse.createServerResponseBySuccess(forgetToken);
    }

    @Override
    public ServerResponse ResetPassword(String username,String newPassword, String Token) {
        /* step1 参数校验*/
        if (StringUtils.isBlank(username)){
            return ServerResponse.createServerResponseByError("用户名不能为空");
        }
        if (StringUtils.isBlank(newPassword)){
            return ServerResponse.createServerResponseByError("新密码不能为空");
        }
        if (StringUtils.isBlank(Token)){
            return ServerResponse.createServerResponseByError("Token不能为空");
        }
        /* step2 校验Token  防止修改别人密码*/
        String token = TokenCache.get(username);
        if (token==null){
            return ServerResponse.createServerResponseByError("token过期");
        }
        if (!token.equals(Token)){
            return ServerResponse.createServerResponseByError("无效的token");
        }
        /* step3 修改密码*/
        int result = userInfoMapper.updatePassword(username,newPassword);
        if (result>0){
            return ServerResponse.createServerResponseBySuccess("修改成功");
        }
        return ServerResponse.createServerResponseByError("修改失败");
    }
}
