package com.cq.client.feign;


import com.cq.common.exception.BusinessException;
import com.cq.common.response.ResultCodeEnum;
import com.cq.model.entity.User;
import com.cq.model.enums.UserRoleEnum;
import com.cq.model.vo.UserVO;
import org.springframework.beans.BeanUtils;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.Collection;
import java.util.List;

import static com.cq.common.constants.UserConstant.USER_LOGIN_STATE;

/**
 * 用户服务
 *
 * @author 程崎
 * @since 2023/07/29
 */
@FeignClient(name = "cqoj-user-service", path = "/api/user/inner")
public interface UserFeignClient {

    @GetMapping("/get/id")
    User getById(@RequestParam("userId") Long userId);

    @GetMapping("/get/ids")
    List<User> listByIds(@RequestParam("userIdList") Collection<Long> userIdList);

    @GetMapping("/get/list")
    List<User> list(@RequestParam("userIdList") Collection<Long> userIdList);

    /**
     * 获取当前登录用户
     *
     * @param session session
     * @return {@link User}
     */
    default User getLoginUser(HttpSession session) {
        // 先判断是否已登录
        Object userObj = session.getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null || currentUser.getId() == null) {
            throw new BusinessException(ResultCodeEnum.NOT_LOGIN_ERROR);
        }
        // 可以考虑在这里做全局权限校验
        return currentUser;
    }

    /**
     * 是管理员
     * 是否为管理员
     *
     * @param loginUser 登录用户
     * @return boolean
     */
    default boolean isNotAdmin(User loginUser) {
        return loginUser == null || !UserRoleEnum.ADMIN.equals(loginUser.getUserRole());
    }

    /**
     * 获取脱敏的用户信息
     *
     * @param user 用户
     * @return {@link UserVO}
     */
    default UserVO getUserVO(User user) {
        if (user == null) {
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }


}
