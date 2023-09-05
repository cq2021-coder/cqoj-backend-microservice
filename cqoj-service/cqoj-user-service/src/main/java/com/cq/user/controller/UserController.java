package com.cq.user.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cq.common.exception.BusinessException;
import com.cq.common.request.DeleteRequest;
import com.cq.common.response.CommonResponse;
import com.cq.common.response.ResultCodeEnum;
import com.cq.model.annotation.AuthCheck;
import com.cq.model.dto.user.*;
import com.cq.model.entity.User;
import com.cq.model.enums.UserRoleEnum;
import com.cq.model.vo.LoginUserVO;
import com.cq.model.vo.UserVO;
import com.cq.user.service.UserService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.List;

/**
 * 用户接口
 *
 * @author 程崎
 * @since 2023/07/29
 */
@RestController
@Slf4j
@Api(tags = "user")
public class UserController {

    /**
     * 账户最小长度
     */
    public static final int ACCOUNT_MIN_SIZE = 4;

    /**
     * 密码最小长度
     */
    public static final int PASSWORD_MIN_SIZE = 6;

    @Resource
    private UserService userService;
    // region 登录相关

    /**
     * 用户注册
     *
     * @param userRegisterRequest 用户注册请求
     * @return {@link CommonResponse}<{@link Long}>
     */
    @PostMapping("/register")
    public CommonResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        if (userRegisterRequest == null) {
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR);
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < ACCOUNT_MIN_SIZE) {
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR, "用户账号过短");
        }
        if (userPassword.length() < PASSWORD_MIN_SIZE || checkPassword.length() < PASSWORD_MIN_SIZE) {
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR, "用户密码过短");
        }
        // 密码和校验密码相同
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR, "两次输入的密码不一致");
        }
        long result = userService.userRegister(userAccount, userPassword, checkPassword);
        return CommonResponse.success(result);
    }

    /**
     * 用户登录
     *
     * @param userLoginRequest 用户登录请求
     * @param session          会话
     * @return {@link CommonResponse}<{@link LoginUserVO}>
     */
    @PostMapping("/login")
    public CommonResponse<LoginUserVO> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpSession session) {
        if (userLoginRequest == null) {
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR);
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < ACCOUNT_MIN_SIZE) {
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR, "账号错误");
        }
        if (userPassword.length() < PASSWORD_MIN_SIZE) {
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR, "密码错误");
        }
        LoginUserVO loginUserVO = userService.userLogin(userAccount, userPassword, session);
        return CommonResponse.success(loginUserVO);
    }

    /**
     * 用户注销
     *
     * @param session 会话
     * @return {@link CommonResponse}<{@link Boolean}>
     */
    @PostMapping("/logout")
    public CommonResponse<Boolean> userLogout(HttpSession session) {
        if (session == null) {
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR);
        }
        boolean result = userService.userLogout(session);
        return CommonResponse.success(result);
    }

    /**
     * 获取当前登录用户
     *
     * @param session 会话
     * @return {@link CommonResponse}<{@link LoginUserVO}>
     */
    @GetMapping("/get/login")
    public CommonResponse<LoginUserVO> getLoginUser(HttpSession session) {
        User user = userService.getLoginUser(session);
        return CommonResponse.success(userService.getLoginUserVO(user));
    }

    // endregion

    // region 增删改查

    /**
     * 创建用户
     *
     * @param userAddRequest 用户添加请求
     * @return {@link CommonResponse}<{@link Long}>
     */
    @PostMapping("/add")
    @AuthCheck(UserRoleEnum.ADMIN)
    public CommonResponse<Long> addUser(@RequestBody UserAddRequest userAddRequest) {
        if (userAddRequest == null) {
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR);
        }
        User user = new User();
        BeanUtils.copyProperties(userAddRequest, user);
        if (!userService.save(user)) {
            throw new BusinessException(ResultCodeEnum.OPERATION_ERROR);
        }
        return CommonResponse.success(user.getId());
    }

    /**
     * 删除用户
     *
     * @param deleteRequest 删除请求
     * @return {@link CommonResponse}<{@link Boolean}>
     */
    @PostMapping("/delete")
    @AuthCheck(UserRoleEnum.ADMIN)
    public CommonResponse<Boolean> deleteUser(@RequestBody DeleteRequest deleteRequest) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR);
        }
        return CommonResponse.success(userService.removeById(deleteRequest.getId()));
    }

    /**
     * 更新用户
     *
     * @param userUpdateRequest 用户更新请求
     * @return {@link CommonResponse}<{@link Boolean}>
     */
    @PostMapping("/update")
    @AuthCheck(UserRoleEnum.ADMIN)
    public CommonResponse<Boolean> updateUser(@RequestBody UserUpdateRequest userUpdateRequest) {
        if (userUpdateRequest == null || userUpdateRequest.getId() == null) {
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR);
        }
        User user = new User();
        BeanUtils.copyProperties(userUpdateRequest, user);
        if (!userService.updateById(user)) {
            throw new BusinessException(ResultCodeEnum.OPERATION_ERROR);
        }
        return CommonResponse.success(true);
    }

    /**
     * 根据 id 获取用户（仅管理员）
     *
     * @param id id
     * @return {@link CommonResponse}<{@link User}>
     */
    @GetMapping("/get")
    @AuthCheck(UserRoleEnum.ADMIN)
    public CommonResponse<User> getUserById(long id) {
        if (id <= 0) {
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR);
        }
        User user = userService.getById(id);
        if (ObjectUtils.isEmpty(user)) {
            throw new BusinessException(ResultCodeEnum.NOT_FOUND_ERROR);
        }
        return CommonResponse.success(user);
    }

    /**
     * 根据 id 获取包装类
     *
     * @param id id
     * @return {@link CommonResponse}<{@link UserVO}>
     */
    @GetMapping("/get/vo")
    @AuthCheck(UserRoleEnum.ADMIN)
    public CommonResponse<UserVO> getUserVoById(long id) {
        CommonResponse<User> response = getUserById(id);
        User user = response.getData();
        return CommonResponse.success(userService.getUserVO(user));
    }

    /**
     * 分页获取用户列表（仅管理员）
     *
     * @param userQueryRequest 用户查询请求
     * @return {@link CommonResponse}<{@link Page}<{@link User}>>
     */
    @PostMapping("/list/page")
    @AuthCheck(UserRoleEnum.ADMIN)
    public CommonResponse<Page<User>> listUserByPage(@RequestBody UserQueryRequest userQueryRequest) {
        long current = userQueryRequest.getCurrent();
        long size = userQueryRequest.getPageSize();
        Page<User> userPage = userService.page(new Page<>(current, size),
                userService.getQueryWrapper(userQueryRequest));
        return CommonResponse.success(userPage);
    }

    /**
     * 分页获取用户封装列表
     *
     * @param userQueryRequest 用户查询请求
     * @return {@link CommonResponse}<{@link Page}<{@link UserVO}>>
     */
    @PostMapping("/list/page/vo")
    public CommonResponse<Page<UserVO>> listUserVoByPage(@RequestBody UserQueryRequest userQueryRequest) {
        if (userQueryRequest == null) {
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR);
        }
        long current = userQueryRequest.getCurrent();
        long size = userQueryRequest.getPageSize();

        Page<User> userPage = userService.page(new Page<>(current, size),
                userService.getQueryWrapper(userQueryRequest));
        Page<UserVO> userVoPage = new Page<>(current, size, userPage.getTotal());
        List<UserVO> userVO = userService.getUserVO(userPage.getRecords());
        userVoPage.setRecords(userVO);
        return CommonResponse.success(userVoPage);
    }

    // endregion

    /**
     * 更新个人信息
     *
     * @param userUpdateMyRequest 用户更新请求
     * @param session             会话
     * @return {@link CommonResponse}<{@link Boolean}>
     */
    @PostMapping("/update/my")
    public CommonResponse<Boolean> updateMyUser(@RequestBody @Valid UserUpdateMyRequest userUpdateMyRequest, HttpSession session) {
        if (userUpdateMyRequest == null) {
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(session);
        User user = new User();
        BeanUtils.copyProperties(userUpdateMyRequest, user);
        user.setId(loginUser.getId());
        if (!userService.updateById(user)) {
            throw new BusinessException(ResultCodeEnum.OPERATION_ERROR);
        }
        return CommonResponse.success(true);
    }
}
