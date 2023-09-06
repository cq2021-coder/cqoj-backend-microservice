package com.cq.question.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cq.client.feign.UserFeignClient;
import com.cq.common.exception.BusinessException;
import com.cq.common.request.DeleteRequest;
import com.cq.common.response.CommonResponse;
import com.cq.common.response.ResultCodeEnum;
import com.cq.model.annotation.AuthCheck;
import com.cq.model.dto.question.*;
import com.cq.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.cq.model.dto.questionsubmit.QuestionSubmitQueryPageRequest;
import com.cq.model.entity.Question;
import com.cq.model.entity.User;
import com.cq.model.enums.QuestionSubmitLanguageEnum;
import com.cq.model.enums.UserRoleEnum;
import com.cq.model.vo.QuestionManageVO;
import com.cq.model.vo.QuestionSubmitViewVO;
import com.cq.model.vo.QuestionVO;
import com.cq.question.service.QuestionService;
import com.cq.question.service.QuestionSubmitService;
import com.google.gson.Gson;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * 题目接口
 *
 * @author 程崎
 * @since 2023/08/08
 */
@RestController
@Slf4j
@Api(tags = "question")
public class QuestionController {

    @Resource
    private QuestionService questionService;

    @Resource
    private UserFeignClient userFeignClient;

    @Resource
    private QuestionSubmitService questionSubmitService;

    private final static Gson GSON = new Gson();

    // region 增删改查

    /**
     * 创建
     *
     * @param questionAddRequest 题目添加请求
     * @param session            会话
     * @return {@link CommonResponse}<{@link Long}>
     */
    @PostMapping("/add")
    public CommonResponse<Long> addQuestion(@RequestBody QuestionAddRequest questionAddRequest, HttpSession session) {
        if (questionAddRequest == null) {
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR);
        }
        Question question = new Question();
        BeanUtils.copyProperties(questionAddRequest, question);
        setQuestionValue(questionAddRequest, question, true);
        User loginUser = userFeignClient.getLoginUser(session);
        question.setUserId(loginUser.getId());
        question.setFavourNum(0);
        question.setThumbNum(0);
        boolean result = questionService.save(question);
        if (!result) {
            throw new BusinessException(ResultCodeEnum.OPERATION_ERROR);
        }
        return CommonResponse.success(question.getId());
    }

    /**
     * 删除
     *
     * @param deleteRequest 删除请求
     * @param session       会话
     * @return {@link CommonResponse}<{@link Boolean}>
     */
    @PostMapping("/delete")
    public CommonResponse<Boolean> deleteQuestion(@RequestBody DeleteRequest deleteRequest, HttpSession session) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR);
        }
        User user = userFeignClient.getLoginUser(session);
        long id = deleteRequest.getId();
        // 判断是否存在
        Question oldQuestion = questionService.getById(id);
        if (oldQuestion == null) {
            throw new BusinessException(ResultCodeEnum.NOT_FOUND_ERROR);
        }

        // 仅本人或管理员可删除
        if (!oldQuestion.getUserId().equals(user.getId()) && userFeignClient.isNotAdmin(user)) {
            throw new BusinessException(ResultCodeEnum.NO_AUTH_ERROR);
        }
        return CommonResponse.success(questionService.removeById(id));
    }

    /**
     * 更新（仅管理员）
     *
     * @param questionUpdateRequest 题目更新请求
     * @return {@link CommonResponse}<{@link Boolean}>
     */
    @PostMapping("/update")
    @AuthCheck(UserRoleEnum.ADMIN)
    public CommonResponse<Boolean> updateQuestion(@RequestBody QuestionUpdateRequest questionUpdateRequest) {
        if (questionUpdateRequest == null || questionUpdateRequest.getId() <= 0) {
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR);
        }
        Question question = new Question();
        BeanUtils.copyProperties(questionUpdateRequest, question);
        setQuestionValue(questionUpdateRequest, question, false);
        long id = questionUpdateRequest.getId();
        // 判断是否存在
        Question oldQuestion = questionService.getById(id);
        if (oldQuestion == null) {
            throw new BusinessException(ResultCodeEnum.NOT_FOUND_ERROR);
        }
        boolean result = questionService.updateById(question);
        return CommonResponse.success(result);
    }

    /**
     * 根据 id 获取(脱敏)
     *
     * @param id id
     * @return {@link CommonResponse}<{@link QuestionVO}>
     */
    @GetMapping("/get/vo")
    public CommonResponse<QuestionVO> getQuestionVoById(long id) {
        if (id <= 0) {
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR);
        }
        Question question = questionService.getById(id);
        if (question == null) {
            throw new BusinessException(ResultCodeEnum.NOT_FOUND_ERROR);
        }
        return CommonResponse.success(questionService.getQuestionVO(question));
    }

    /**
     * 根据 id 获取
     *
     * @param id id
     * @return {@link CommonResponse}<{@link QuestionVO}>
     */
    @GetMapping("/get")
    public CommonResponse<Question> getQuestionById(long id, HttpSession session) {
        if (id <= 0) {
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR);
        }
        Question question = questionService.getById(id);
        if (question == null) {
            throw new BusinessException(ResultCodeEnum.NOT_FOUND_ERROR);
        }
        User loginUser = userFeignClient.getLoginUser(session);
        if (!question.getUserId().equals(loginUser.getId()) && userFeignClient.isNotAdmin(loginUser)) {
            throw new BusinessException(ResultCodeEnum.NO_AUTH_ERROR);
        }
        return CommonResponse.success(question);
    }

    /**
     * 分页获取列表（封装类）
     *
     * @param questionQueryRequest 题目查询请求
     * @return {@link CommonResponse}<{@link Page}<{@link QuestionVO}>>
     */
    @PostMapping("/list/page/vo")
    public CommonResponse<Page<QuestionVO>> listQuestionVoByPage(@RequestBody QuestionQueryRequest questionQueryRequest) {
        long current = questionQueryRequest.getCurrent();
        long size = questionQueryRequest.getPageSize();
        Page<Question> questionPage = questionService.page(new Page<>(current, size),
                questionService.getQueryWrapper(questionQueryRequest));
        return CommonResponse.success(questionService.getQuestionVoPage(questionPage));
    }

    /**
     * 分页获取当前用户创建的资源列表
     *
     * @param questionQueryRequest 题目查询请求
     * @param session              会话
     * @return {@link CommonResponse}<{@link Page}<{@link QuestionVO}>>
     */
    @PostMapping("/my/list/page/vo")
    public CommonResponse<Page<QuestionVO>> listMyQuestionVoByPage(@RequestBody QuestionQueryRequest questionQueryRequest,
                                                                   HttpSession session) {
        if (questionQueryRequest == null) {
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR);
        }
        User loginUser = userFeignClient.getLoginUser(session);
        questionQueryRequest.setUserId(loginUser.getId());
        long current = questionQueryRequest.getCurrent();
        long size = questionQueryRequest.getPageSize();
        Page<Question> questionPage = questionService.page(new Page<>(current, size),
                questionService.getQueryWrapper(questionQueryRequest));
        return CommonResponse.success(questionService.getQuestionVoPage(questionPage));
    }

    /**
     * 分页获取题目列表（仅管理员）
     *
     * @param questionQueryRequest 题目查询请求
     * @return {@link CommonResponse}<{@link Page}<{@link Question}>>
     */
    @PostMapping("/list/page")
    @AuthCheck(UserRoleEnum.ADMIN)
    public CommonResponse<Page<QuestionManageVO>> listManageQuestionByPage(@RequestBody QuestionQueryRequest questionQueryRequest) {
        long current = questionQueryRequest.getCurrent();
        long size = questionQueryRequest.getPageSize();
        return CommonResponse.success(
                questionService.listManageQuestionByPage(new Page<>(current, size), questionService.getQueryWrapper(questionQueryRequest))
        );
    }

    // endregion

    /**
     * 编辑（用户）
     *
     * @param questionEditRequest 题目编辑请求
     * @param session             会话
     * @return {@link CommonResponse}<{@link Boolean}>
     */
    @PostMapping("/edit")
    public CommonResponse<Boolean> editQuestion(@RequestBody QuestionEditRequest questionEditRequest, HttpSession session) {
        if (questionEditRequest == null || questionEditRequest.getId() <= 0) {
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR);
        }
        Question question = new Question();
        BeanUtils.copyProperties(questionEditRequest, question);
        setQuestionValue(questionEditRequest, question, false);
        User loginUser = userFeignClient.getLoginUser(session);
        long id = questionEditRequest.getId();
        // 判断是否存在
        Question oldQuestion = questionService.getById(id);
        if (oldQuestion == null) {
            throw new BusinessException(ResultCodeEnum.NOT_FOUND_ERROR);
        }
        // 仅本人或管理员可编辑
        if (!oldQuestion.getUserId().equals(loginUser.getId()) && userFeignClient.isNotAdmin(loginUser)) {
            throw new BusinessException(ResultCodeEnum.NO_AUTH_ERROR);
        }
        return CommonResponse.success(questionService.updateById(question));
    }


    private void setQuestionValue(QuestionBaseRequest questionBaseRequest, Question question, boolean add) {
        List<String> tags = questionBaseRequest.getTags();
        if (tags != null) {
            question.setTags(GSON.toJson(tags));
        }
        List<JudgeCase> judgeCase = questionBaseRequest.getJudgeCase();
        if (judgeCase != null) {
            question.setJudgeCase(GSON.toJson(judgeCase));
        }
        JudgeConfig judgeConfig = questionBaseRequest.getJudgeConfig();
        if (judgeConfig != null) {
            question.setJudgeConfig(GSON.toJson(judgeConfig));
        }
        questionService.validQuestion(question, add);
    }

    @GetMapping("/get/language")
    public CommonResponse<List<String>> getCodeLanguage() {
        return CommonResponse.success(QuestionSubmitLanguageEnum.getValues());
    }


    /**
     * 提交题目
     *
     * @param questionSubmitAddRequest 题目提交添加请求
     * @param session                  会话
     * @return 提交记录的 id
     */
    @PostMapping("/question-submit/do")
    public CommonResponse<Long> doQuestionSubmit(@RequestBody QuestionSubmitAddRequest questionSubmitAddRequest, HttpSession session) {
        if (questionSubmitAddRequest == null || questionSubmitAddRequest.getQuestionId() <= 0) {
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR);
        }
        final User loginUser = userFeignClient.getLoginUser(session);
        long questionSubmitId = questionSubmitService.doQuestionSubmit(questionSubmitAddRequest, loginUser);
        return CommonResponse.success(questionSubmitId);
    }

    /**
     * 分页获取题目提交列表（除了管理员外，普通用户只能看到非答案、提交代码等公开信息）
     */
    /*@PostMapping("/question-submit/list/page")
    public CommonResponse<Page<QuestionSubmitVO>> listQuestionSubmitByPage(@RequestBody QuestionSubmitQueryRequest questionSubmitQueryRequest, HttpSession session) {
        long current = questionSubmitQueryRequest.getCurrent();
        long size = questionSubmitQueryRequest.getPageSize();
        // 从数据库中查询原始的题目提交分页信息
        Page<QuestionSubmit> questionSubmitPage = questionSubmitService.page(new Page<>(current, size),
                questionSubmitService.getQueryWrapper(questionSubmitQueryRequest));
        final User loginUser = userFeignClient.getLoginUser(session);
        // 返回脱敏信息
        return CommonResponse.success(questionSubmitService.getQuestionSubmitVoPage(questionSubmitPage, loginUser));
    }*/
    @GetMapping("/question-submit/list/page")
    public CommonResponse<Page<QuestionSubmitViewVO>> listQuestionSubmitByPage(QuestionSubmitQueryPageRequest questionSubmitQueryRequest) {
        long size = questionSubmitQueryRequest.getPageSize();
        long pageIndex = (questionSubmitQueryRequest.getCurrent() - 1) * size;
        String title = questionSubmitQueryRequest.getTitle();
        String language = questionSubmitQueryRequest.getLanguage();
        return CommonResponse.success(questionSubmitService.listQuestionSubmitByPage(title, language, pageIndex, size));
    }
}
