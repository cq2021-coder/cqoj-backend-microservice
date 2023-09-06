package com.cq.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cq.common.constants.CommonConstant;
import com.cq.common.exception.BusinessException;
import com.cq.common.response.ResultCodeEnum;
import com.cq.common.utils.CopyUtil;
import com.cq.common.utils.SqlUtils;
import com.cq.model.dto.user.UserQueryRequest;
import com.cq.model.entity.User;
import com.cq.model.enums.UserRoleEnum;
import com.cq.model.vo.LoginUserVO;
import com.cq.model.vo.UserVO;
import com.cq.user.mapper.UserMapper;
import com.cq.user.service.FileService;
import com.cq.user.service.UserService;
import com.google.common.base.CaseFormat;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.UUID;

import static com.cq.common.constants.UserConstant.USER_LOGIN_STATE;

/**
 * 用户服务impl
 *
 * @author 程崎
 * @since 2023/07/29
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    /**
     * 盐值，混淆密码
     */
    private static final String SALT = "cq2023-cqoj";

    @Resource
    private FileService fileService;


    /**
     * 获取当前登录用户
     *
     * @param session session
     * @return {@link User}
     */
    @Override
    public User getLoginUser(HttpSession session) {
        // 先判断是否已登录
        Object userObj = session.getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null || currentUser.getId() == null) {
            throw new BusinessException(ResultCodeEnum.NOT_LOGIN_ERROR);
        }
        // 从数据库查询（追求性能的话可以注释，直接走缓存）
        long userId = currentUser.getId();
        currentUser = this.getById(userId);
        if (currentUser == null) {
            throw new BusinessException(ResultCodeEnum.NOT_LOGIN_ERROR);
        }
        setUserTempAccessAvatar(currentUser);
        return currentUser;
    }

    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        synchronized (userAccount.intern()) {
            // 账户不能重复
            long count = this.baseMapper.selectCount(Wrappers.lambdaQuery(User.class).eq(User::getUserAccount, userAccount));
            if (count > 0) {
                throw new BusinessException(ResultCodeEnum.PARAMS_ERROR, "账号重复");
            }
            // 2. 加密
            String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
            // 3. 插入数据
            User user = new User();
            user.setUserAccount(userAccount);
            user.setUserPassword(encryptPassword);
            user.setUserName("用户-" + UUID.randomUUID());
            boolean saveResult = this.save(user);
            if (!saveResult) {
                throw new BusinessException(ResultCodeEnum.SYSTEM_ERROR, "注册失败，数据库错误");
            }
            return user.getId();
        }
    }

    @Override
    public LoginUserVO userLogin(String userAccount, String userPassword, HttpSession session) {
        // 2. 加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        // 查询用户是否存在
        User user = this.baseMapper.selectOne(
                Wrappers.lambdaQuery(User.class)
                        .eq(User::getUserAccount, userAccount)
                        .eq(User::getUserPassword, encryptPassword)
        );
        // 用户不存在
        if (user == null) {
            log.info("user login failed, userAccount cannot match userPassword");
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR, "用户不存在或密码错误");
        }
        // 3. 记录用户的登录态
        session.setAttribute(USER_LOGIN_STATE, user);
        return this.getLoginUserVO(user);
    }

    /**
     * 用户注销
     *
     * @param session 会话
     * @return boolean
     */
    @Override
    public boolean userLogout(HttpSession session) {
        if (session.getAttribute(USER_LOGIN_STATE) == null) {
            throw new BusinessException(ResultCodeEnum.OPERATION_ERROR, "未登录");
        }
        // 移除登录态
        session.removeAttribute(USER_LOGIN_STATE);
        return true;
    }

    @Override
    public LoginUserVO getLoginUserVO(User user) {
        if (user == null) {
            return null;
        }
        LoginUserVO userVO = CopyUtil.copy(user, LoginUserVO.class);
        userVO.setUserRoleName(userVO.getUserRole().getText());
        return userVO;
    }

    @Override
    public UserVO getUserVO(User user) {
        if (user == null) {
            return null;
        }
        UserVO userVO = CopyUtil.copy(user, UserVO.class);
        userVO.setUserRoleName(userVO.getUserRole().getText());
        return userVO;
    }

    @Override
    public List<UserVO> getUserVO(List<User> userList) {
        return CopyUtil.copyList(userList, UserVO.class);
    }

    @Override
    public QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest) {
        if (userQueryRequest == null) {
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR, "请求参数为空");
        }
        Long id = userQueryRequest.getId();
        String unionId = userQueryRequest.getUnionId();
        String mpOpenId = userQueryRequest.getMpOpenId();
        String userName = userQueryRequest.getUserName();
        String userProfile = userQueryRequest.getUserProfile();
        String userRole = userQueryRequest.getUserRole();
        String sortField = userQueryRequest.getSortField();
        String sortOrder = userQueryRequest.getSortOrder();
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(id != null, "id", id);
        queryWrapper.eq(StringUtils.isNotBlank(unionId), "union_id", unionId);
        queryWrapper.eq(StringUtils.isNotBlank(mpOpenId), "mp_open_id", mpOpenId);
        queryWrapper.eq(StringUtils.isNotBlank(userRole), "user_role", userRole);
        queryWrapper.like(StringUtils.isNotBlank(userProfile), "user_profile", userProfile);
        queryWrapper.like(StringUtils.isNotBlank(userName), "user_name", userName);
        boolean isSort = SqlUtils.validSortField(sortField);
        if (isSort) {
            sortField = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, sortField);
        }
        queryWrapper.orderBy(
                isSort,
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField
        );
        return queryWrapper;
    }

    /**
     * 是否为管理员
     *
     * @param session 会话
     * @return boolean
     */
    @Override
    public boolean isAdmin(HttpSession session) {
        // 仅管理员可查询
        Object userObj = session.getAttribute(USER_LOGIN_STATE);
        User user = (User) userObj;
        return isAdmin(user);
    }

    @Override
    public boolean isAdmin(User user) {
        return user != null && UserRoleEnum.ADMIN.equals(user.getUserRole());
    }


    private void setUserTempAccessAvatar(User currentUser) {
        String userAvatar = currentUser.getUserAvatar();
        if (StringUtils.isNotBlank(userAvatar)) {
            currentUser.setUserAvatar(fileService.getTmpAccess(userAvatar));
        }
    }
}




