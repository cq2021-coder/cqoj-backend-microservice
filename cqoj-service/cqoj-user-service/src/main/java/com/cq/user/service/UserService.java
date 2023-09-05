package com.cq.user.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cq.model.dto.user.UserQueryRequest;
import com.cq.model.entity.User;
import com.cq.model.vo.LoginUserVO;
import com.cq.model.vo.UserVO;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * 用户服务
 *
 * @author 程崎
 * @since 2023/07/29
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册
     *
     * @param userAccount   用户账户
     * @param userPassword  用户密码
     * @param checkPassword 校验密码
     * @return 新用户 id
     */
    long userRegister(String userAccount, String userPassword, String checkPassword);

    /**
     * 用户登录
     *
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @param session      会话
     * @return 脱敏后的用户信息
     */
    LoginUserVO userLogin(String userAccount, String userPassword, HttpSession session);

    /**
     * 获取当前登录用户
     *
     * @param session 请求
     * @return {@link User}
     */
    User getLoginUser(HttpSession session);

    /**
     * 用户注销
     *
     * @param session 会话
     * @return boolean
     */
    boolean userLogout(HttpSession session);

    /**
     * 获取脱敏的已登录用户信息
     *
     * @param user 用户
     * @return {@link LoginUserVO}
     */
    LoginUserVO getLoginUserVO(User user);

    /**
     * 获取脱敏的用户信息
     *
     * @param user 用户
     * @return {@link UserVO}
     */
    UserVO getUserVO(User user);

    /**
     * 获取脱敏的用户信息
     *
     * @param userList 用户列表
     * @return {@link List}<{@link UserVO}>
     */
    List<UserVO> getUserVO(List<User> userList);

    /**
     * 获取查询条件
     *
     * @param userQueryRequest 用户查询请求
     * @return {@link QueryWrapper}<{@link User}>
     */
    QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest);

    /**
     * 是否为管理员
     *
     * @param session 会话
     * @return boolean
     */
    boolean isAdmin(HttpSession session);


    /**
     * 是否为管理员
     *
     * @param user 用户
     * @return boolean
     */

    boolean isAdmin(User user);
}
