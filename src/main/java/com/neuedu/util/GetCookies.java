package com.neuedu.util;

import com.neuedu.common.Const;
import com.neuedu.entity.UserInfo;
import com.neuedu.service.UserMangerService;
import com.neuedu.service.UserService;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.SocketException;
import java.net.UnknownHostException;

public class GetCookies {
    public static  void GetCookie(HttpServletRequest request, HttpServletResponse response, UserMangerService userMangerService, UserInfo userInfo){
        //生成token
        String ip = GetIpUtils.getRemoteAddress(request);
        try {
            String mac = GetIpUtils.getMACAddress(ip);
            String token = MD5Utils.getMD5Code(mac);
            //token保存到数据库
            userMangerService.updateToken(userInfo.getId(),token);
            //token保存到cookie中
            Cookie cookie = new Cookie(Const.TOKENKEY,token);
            cookie.setMaxAge(60*60*24*7);
            cookie.setPath("/");
            response.addCookie(cookie);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }
    public static  void GetCookie(HttpServletRequest request, HttpServletResponse response, UserService userService, UserInfo userInfo){
        //生成token
        String ip = GetIpUtils.getRemoteAddress(request);
        try {
            String mac = GetIpUtils.getMACAddress(ip);
            String token = MD5Utils.getMD5Code(mac);
            //token保存到数据库
            userService.updateToken(userInfo.getId(),token);
            //token保存到cookie中
            Cookie cookie = new Cookie(Const.TOKENKEY,token);
            cookie.setMaxAge(60*60*24*7);
            cookie.setPath("/");
            response.addCookie(cookie);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }
}
