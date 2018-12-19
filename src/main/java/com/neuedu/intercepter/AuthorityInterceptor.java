package com.neuedu.intercepter;

import com.google.gson.Gson;
import com.neuedu.common.Const;
import com.neuedu.common.ServerResponse;
import com.neuedu.entity.UserInfo;
import com.neuedu.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.PrintWriter;

public class AuthorityInterceptor implements HandlerInterceptor {

    @Autowired
    UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        HttpSession session = httpServletRequest.getSession();
        UserInfo userInfo = (UserInfo) session.getAttribute(Const.CURRENT_USER);
        if (userInfo == null) {
            Cookie[] cookies = httpServletRequest.getCookies();
            if (cookies != null && cookies.length > 0) {
                for (Cookie cookie : cookies) {
                    String cookieName = cookie.getName();
                    if (cookieName.equals(Const.TOKENKEY)){
                        String autoLoginTOken = cookie.getValue();
                        //根据token查询用户信息
                        userInfo=userService.userInfoByToken(autoLoginTOken);
                        if (userInfo!=null){
                            session.setAttribute(Const.CURRENT_USER,userInfo);
                        }
                        break;
                    }
                }
            }
        }

        if (userInfo == null) {
            //重写HttpServletResponse
            httpServletResponse.reset();
            httpServletResponse.setCharacterEncoding("UTF-8");
            httpServletResponse.setContentType("application/json;charset=UTF-8");
            PrintWriter printWriter = httpServletResponse.getWriter();
            //未登录
            ServerResponse serverResponse = ServerResponse.createServerResponseByError("用户未登录");
            Gson gson = new Gson();
            /* 响应对象转json */
            String re = gson.toJson(serverResponse);
            printWriter.write(re);
            printWriter.flush();/* 刷新 */
            printWriter.close();
            httpServletResponse.sendRedirect(httpServletRequest.getServletContext().getContextPath() + "/user/login");
            return false;
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}
