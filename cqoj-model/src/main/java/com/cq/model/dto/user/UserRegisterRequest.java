package com.cq.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户注册请求体
 *
 * @author 程崎
 * @since 2023/07/29
 */
@Data
public class UserRegisterRequest implements Serializable {

    private static final long serialVersionUID = 156498921023L;

    private String userAccount;

    private String userPassword;

    private String checkPassword;
}
