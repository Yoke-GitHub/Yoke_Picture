package com.yoke.yokepicturebackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yoke.yokepicturebackend.model.dto.user.UserQueryRequest;
import com.yoke.yokepicturebackend.model.entity.User;
import com.yoke.yokepicturebackend.model.vo.LoginUserVO;
import com.yoke.yokepicturebackend.model.vo.UserVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author H
 * @description 针对表【user(用户)】的数据库操作Service
 * @createDate 2024-12-24 00:08:41
 */
public interface UserService extends IService<User> {
    /**
     * 用户注册
     *
     * @param userAccount   用户账号
     * @param userPassword  用户密码
     * @param checkPassword 校验密码
     * @return 新用户id
     */
    long userRegister(String userAccount, String userPassword, String checkPassword);

    /**
     * 用户登录
     *
     * @param userAccount  用户账号
     * @param userPassword 用户密码
     * @param request
     * @return 脱敏后的用户信息
     */
    LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 获取加密后的密码
     *
     * @param userPassword 用户密码
     * @return
     */
    String getEncryptPassword(String userPassword);

    /**
     * 获取脱敏后的登录信息
     *
     * @param user 用户信息
     * @return
     */
    LoginUserVO getLoginUserVO(User user);

    /**
     * 用户注销
     *
     * @param request
     * @return
     */
    boolean getLogout(HttpServletRequest request);

    /**
     * 获取脱敏后的用户信息
     *
     * @param user 用户信息
     * @return
     */
    UserVO getUserVO(User user);

    /**
     * 获取脱敏后的用户信息列表（分页）
     *
     * @param userList 用户信息列表
     * @return
     */
    List<UserVO> getUserVOList(List<User> userList);

    /**
     * 获取查询类型
     * @param userQueryRequest 查询条件
     * @return
     */
    QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest);

    /**
     * 判断是否为管理员
     * @param user 用户
     * @return
     */
    boolean isAdmin(User user);
}

