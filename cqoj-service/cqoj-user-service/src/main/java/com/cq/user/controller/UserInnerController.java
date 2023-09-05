package com.cq.user.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.cq.model.entity.User;
import com.cq.user.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/inner")
public class UserInnerController {

    @Resource
    private UserService userService;

    @GetMapping("/get/id")
    public User getById(Long userId) {
        return userService.getById(userId);
    }

    @GetMapping("/get/ids")
    public List<User> listByIds(@RequestParam("userIdList") Collection<Long> userIdList) {
        return userService.listByIds(userIdList);
    }

    @GetMapping("/get/list")
    public List<User> list(@RequestParam("userIdList") Collection<Long> userIdList) {
        return userService.list(Wrappers.lambdaQuery(User.class).select(User::getUserName, User::getId).in(User::getId, userIdList));
    }

}
