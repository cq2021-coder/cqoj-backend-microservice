package com.cq.user.controller;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.cq.model.entity.User;
import com.cq.user.service.UserService;
import org.springframework.web.bind.annotation.*;

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
    public List<User> listByIds(Collection<Long> userIds) {
        return userService.listByIds(userIds);
    }

    @PostMapping("/get/list")
    List<User> list(@RequestBody Wrapper<User> queryWrapper) {
        return userService.list(queryWrapper);
    }

}
