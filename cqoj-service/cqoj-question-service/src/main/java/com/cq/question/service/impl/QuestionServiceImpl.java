package com.cq.question.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cq.client.feign.UserFeignClient;
import com.cq.common.constants.CommonConstant;
import com.cq.common.exception.BusinessException;
import com.cq.common.response.ResultCodeEnum;
import com.cq.common.utils.CopyUtil;
import com.cq.common.utils.SqlUtils;
import com.cq.model.dto.question.JudgeConfig;
import com.cq.model.dto.question.QuestionQueryRequest;
import com.cq.model.entity.Question;
import com.cq.model.entity.User;
import com.cq.model.vo.QuestionManageVO;
import com.cq.model.vo.QuestionVO;
import com.cq.model.vo.UserVO;
import com.cq.question.mapper.QuestionMapper;
import com.cq.question.service.QuestionService;
import com.google.common.base.CaseFormat;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 针对表【question(题目)】的数据库操作Service实现
 *
 * @author 程崎
 * @since 2023/08/08
 */
@Service
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question>
        implements QuestionService {
    @Resource
    private UserFeignClient userFeignClient;

    private static final int TITLE_MAX_LEN = 80;
    private static final int CONTENT_MAX_LEN = 8192;

    /**
     * 校验题目是否合法
     *
     * @param question 题目
     * @param add      添加
     */
    @Override
    public void validQuestion(Question question, boolean add) {
        if (question == null) {
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR);
        }
        String title = question.getTitle();
        String content = question.getContent();
        String tags = question.getTags();
        String answer = question.getAnswer();
        String judgeCase = question.getJudgeCase();
        String judgeConfig = question.getJudgeConfig();
        // 创建时，参数不能为空
        if (add && StringUtils.isAnyBlank(title, content, tags)) {
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR);
        }
        // 有参数则校验
        if (StringUtils.isNotBlank(title) && title.length() > TITLE_MAX_LEN) {
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR, "标题过长");
        }
        if (StringUtils.isNotBlank(content) && content.length() > CONTENT_MAX_LEN) {
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR, "内容过长");
        }
        if (StringUtils.isNotBlank(answer) && answer.length() > CONTENT_MAX_LEN) {
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR, "答案过长");
        }
        if (StringUtils.isNotBlank(judgeCase) && judgeCase.length() > CONTENT_MAX_LEN) {
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR, "判题用例过长");
        }
        if (StringUtils.isNotBlank(judgeConfig) && judgeConfig.length() > CONTENT_MAX_LEN) {
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR, "判题配置过长");
        }
    }

    /**
     * 获取查询包装类（用户根据哪些字段查询，根据前端传来的请求对象，得到 mybatis 框架支持的查询 QueryWrapper 类）
     *
     * @param questionQueryRequest 题目查询请求
     * @return {@link QueryWrapper}<{@link Question}>
     */
    @Override
    public QueryWrapper<Question> getQueryWrapper(QuestionQueryRequest questionQueryRequest) {
        QueryWrapper<Question> queryWrapper = Wrappers.query();
        if (questionQueryRequest == null) {
            return queryWrapper;
        }
        Long id = questionQueryRequest.getId();
        String title = questionQueryRequest.getTitle();
        String content = questionQueryRequest.getContent();
        List<String> tags = questionQueryRequest.getTags();
        String answer = questionQueryRequest.getAnswer();
        Long userId = questionQueryRequest.getUserId();
        String sortField = questionQueryRequest.getSortField();
        String sortOrder = questionQueryRequest.getSortOrder();


        // 拼接查询条件
        queryWrapper.like(StringUtils.isNotBlank(title), "title", title);
        queryWrapper.like(StringUtils.isNotBlank(content), "content", content);
        queryWrapper.like(StringUtils.isNotBlank(answer), "answer", answer);
        if (!CollectionUtils.isEmpty(tags)) {
            for (String tag : tags) {
                queryWrapper.like("tags", tag);
            }
        }
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "user_id", userId);
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

    @Override
    public QuestionVO getQuestionVO(Question question) {
        QuestionVO questionVO = QuestionVO.objToVo(question);
        // 1. 关联查询用户信息
        Long userId = question.getUserId();
        User user = null;
        if (userId != null && userId > 0) {
            user = userFeignClient.getById(userId);
        }
        UserVO userVO = userFeignClient.getUserVO(user);
        questionVO.setUserVO(userVO);
        return questionVO;
    }

    @Override
    public Page<QuestionVO> getQuestionVoPage(Page<Question> questionPage) {
        List<Question> questionList = questionPage.getRecords();
        Page<QuestionVO> questionVoPage = new Page<>(questionPage.getCurrent(), questionPage.getSize(), questionPage.getTotal());
        if (CollectionUtils.isEmpty(questionList)) {
            return questionVoPage;
        }
        // 1. 关联查询用户信息
        Set<Long> userIdSet = questionList.stream().map(Question::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userFeignClient.listByIds(userIdSet)
                .stream()
                .collect(Collectors.groupingBy(User::getId));
        // 填充信息
        List<QuestionVO> questionVOList = questionList.stream().map(question -> {
            QuestionVO questionVO = QuestionVO.objToVo(question);
            Long userId = question.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            questionVO.setUserVO(userFeignClient.getUserVO(user));
            return questionVO;
        }).collect(Collectors.toList());
        questionVoPage.setRecords(questionVOList);
        return questionVoPage;
    }

    @Override
    public Page<QuestionManageVO> listManageQuestionByPage(Page<Question> questionPage, QueryWrapper<Question> queryWrapper) {
        this.page(questionPage, queryWrapper);
        Page<QuestionManageVO> questionManageVoPage = new Page<>();
        BeanUtils.copyProperties(questionPage, questionManageVoPage, "records");
        Set<Long> userIdSet = questionPage.getRecords().stream().map(Question::getUserId).collect(Collectors.toSet());
        List<User> userList = userFeignClient.list(userIdSet);
        Map<Long, List<User>> userIdToName = userList.stream().collect(Collectors.groupingBy(User::getId));
        questionManageVoPage.setRecords(
                questionPage.getRecords().stream().map(question -> {
                    QuestionManageVO questionManageVO = CopyUtil.copy(question, QuestionManageVO.class);
                    if (userIdToName.containsKey(question.getUserId())) {
                        questionManageVO.setUserName(userIdToName.get(question.getUserId()).get(0).getUserName());
                    }
                    JudgeConfig judgeConfig = JSONUtil.toBean(question.getJudgeConfig(), JudgeConfig.class);
                    questionManageVO.setTimeLimit(judgeConfig.getTimeLimit());
                    questionManageVO.setMemoryLimit(judgeConfig.getMemoryLimit());
                    questionManageVO.setStackLimit(judgeConfig.getStackLimit());
                    return questionManageVO;
                }).collect(Collectors.toList())
        );
        return questionManageVoPage;
    }
}




