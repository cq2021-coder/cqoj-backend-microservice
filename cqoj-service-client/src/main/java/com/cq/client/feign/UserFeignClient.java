package com.cq.client.feign;


import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.cq.model.entity.User;
import com.cq.model.vo.UserVO;

import javax.servlet.http.HttpSession;
import java.util.Collection;
import java.util.List;

/**
 * 用户服务
 *
 * @author 程崎
 * @since 2023/07/29
 */
public interface UserFeignClient {

    /**
     * 获取脱敏的用户信息
     *
     * @param user 用户
     * @return {@link UserVO}
     */
    UserVO getUserVO(User user);

    User getById(Long userId);

    List<User> listByIds(Collection<Long> userIdSet);

    List<User> list(Wrapper<User> queryWrapper);

    boolean isAdmin(HttpSession session);

    boolean isAdmin(User loginUser);

    User getLoginUser(HttpSession session);
}
