package com.cq.judge.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.cq.client.feign.QuestionFeignClient;
import com.cq.common.exception.BusinessException;
import com.cq.common.response.ResultCodeEnum;
import com.cq.judge.codesandbox.CodeSandbox;
import com.cq.judge.codesandbox.factory.CodeSandboxFactory;
import com.cq.judge.codesandbox.proxy.CodeSandboxProxy;
import com.cq.judge.service.JudgeService;
import com.cq.judge.strategy.JudgeContext;
import com.cq.judge.strategy.JudgeManager;
import com.cq.model.dto.codesandbox.ExecuteCodeRequest;
import com.cq.model.dto.codesandbox.ExecuteCodeResponse;
import com.cq.model.dto.question.JudgeCase;
import com.cq.model.dto.questionsubmit.JudgeInfo;
import com.cq.model.entity.Question;
import com.cq.model.entity.QuestionSubmit;
import com.cq.model.enums.QuestionSubmitLanguageEnum;
import com.cq.model.enums.QuestionSubmitStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 判题服务impl
 *
 * @author 程崎
 * @since 2023/08/15
 */
@Service
@Slf4j
public class JudgeServiceImpl implements JudgeService {

    @Resource
    private QuestionFeignClient questionFeignClient;

    @Resource
    private CodeSandboxFactory codeSandboxFactory;

    @Resource
    private JudgeManager judgeManager;

    @Override
    public QuestionSubmit doJudge(QuestionSubmit questionSubmit) {
        if (ObjectUtils.isEmpty(questionSubmit)) {
            throw new BusinessException(ResultCodeEnum.NOT_FOUND_ERROR, "提交信息不存在");
        }
        Long questionId = questionSubmit.getQuestionId();
        Question question = questionFeignClient.getOne(
                Wrappers.lambdaQuery(Question.class).eq(Question::getId, questionId).select(Question::getJudgeCase, Question::getJudgeConfig)
        );
        if (ObjectUtils.isEmpty(question)) {
            throw new BusinessException(ResultCodeEnum.NOT_FOUND_ERROR, "题目不存在");
        }
        Long questionSubmitId = questionSubmit.getId();
        String code = questionSubmit.getCode();
        QuestionSubmitLanguageEnum languageType = QuestionSubmitLanguageEnum.getEnumByValue(questionSubmit.getLanguage());
        List<JudgeCase> judgeCases = JSONUtil.toList(question.getJudgeCase(), JudgeCase.class);
        List<String> inputList = judgeCases.stream().map(JudgeCase::getInput).collect(Collectors.toList());
        List<String> outputList = judgeCases.stream().map(JudgeCase::getOutput).collect(Collectors.toList());


        //region 更新状态
        QuestionSubmit questionSubmitUpdate = new QuestionSubmit();
        questionSubmitUpdate.setId(questionSubmitId);
        questionSubmitUpdate.setStatus(QuestionSubmitStatusEnum.RUNNING.getValue());
        questionFeignClient.updateById(questionSubmitUpdate);
        //endregion

        //region 获取代码沙箱执行结果
        CodeSandbox codeSandbox = codeSandboxFactory.newInstance();
        ExecuteCodeRequest request = ExecuteCodeRequest
                .builder()
                .code(code)
                .inputList(inputList)
                .language(languageType)
                .build();
        ExecuteCodeResponse executeCodeResponse = new CodeSandboxProxy(codeSandbox).executeCode(request);
        //endregion

        List<String> outputListResult = executeCodeResponse.getOutputList();
        JudgeContext judgeContext = new JudgeContext();
        judgeContext.setJudgeInfo(executeCodeResponse.getJudgeInfo());
        judgeContext.setOutputList(outputList);
        judgeContext.setOutputListResult(outputListResult);
        judgeContext.setQuestion(question);
        judgeContext.setLanguageType(QuestionSubmitLanguageEnum.getEnumByValue(questionSubmit.getLanguage()));

        JudgeInfo judgeInfo = judgeManager.doJudge(judgeContext);
        questionSubmitUpdate.setStatus(QuestionSubmitStatusEnum.SUCCEED.getValue());
        questionSubmitUpdate.setJudgeInfo(JSONUtil.toJsonStr(judgeInfo));
        questionFeignClient.updateById(questionSubmitUpdate);
        return questionFeignClient.selectById(questionSubmit.getId());
    }
}