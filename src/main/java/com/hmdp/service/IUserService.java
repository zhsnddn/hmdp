package com.hmdp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hmdp.dto.LoginFormDTO;
import com.hmdp.dto.Result;
import com.hmdp.entity.User;

import javax.servlet.http.HttpSession;

public interface IUserService extends IService<User> {

    /**
     * 发送验证码
     */
    Result sendCode(String phone, HttpSession session);

    /**
     * 登录功能
     */
    Result login(LoginFormDTO loginForm, HttpSession session);

    /**
     * 根据id查询用户
     */
    Result queryUserById(Long id);

    /**
     * 签到功能
     */
    Result sign();

    /**
     * 统计连续签到次数
     */
    Result signCount();

}
