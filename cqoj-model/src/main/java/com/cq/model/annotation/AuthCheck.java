package com.cq.model.annotation;


import com.cq.model.enums.UserRoleEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 权限校验
 *
 * @author 程崎
 * @since 2023/07/29
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthCheck {

    /**
     * 必须有某个角色
     *
     * @return {@link UserRoleEnum}
     */
    UserRoleEnum[] value() default {UserRoleEnum.USER, UserRoleEnum.ADMIN};

}

