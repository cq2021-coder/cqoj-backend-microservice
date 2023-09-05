package com.cq.judge.strategy.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.cq.judge.strategy.JudgeContext;
import com.cq.judge.strategy.JudgeStrategy;
import com.cq.model.dto.question.JudgeConfig;
import com.cq.model.dto.questionsubmit.JudgeInfo;
import com.cq.model.entity.Question;
import com.cq.model.enums.JudgeInfoMessageEnum;

import java.util.ArrayList;
import java.util.List;


public class DefaultJudgeStrategy implements JudgeStrategy {
    @Override
    public JudgeInfo doJudge(JudgeContext judgeContext) {
        JudgeInfo judgeInfo = ObjectUtil.defaultIfNull(judgeContext.getJudgeInfo(), new JudgeInfo());

        List<String> outputList = ObjectUtil.defaultIfNull(judgeContext.getOutputList(), new ArrayList<>());
        List<String> outputListResult = ObjectUtil.defaultIfNull(judgeContext.getOutputListResult(), new ArrayList<>());

        Question question = ObjectUtil.defaultIfNull(judgeContext.getQuestion(), new Question());

        JudgeInfoMessageEnum judgeInfoMessage;

        Long memory = ObjectUtil.defaultIfNull(judgeInfo.getMemory(), 0L);
        Long time = ObjectUtil.defaultIfNull(judgeInfo.getTime(), 0L);

        JudgeInfo judgeInfoResponse = new JudgeInfo();
        judgeInfoResponse.setMemory(memory);
        judgeInfoResponse.setTime(time);

        if (outputList.size() != outputListResult.size()) {
            judgeInfoMessage = JudgeInfoMessageEnum.WRONG_ANSWER;
            judgeInfoResponse.setMessage(judgeInfoMessage.getText());
            return judgeInfoResponse;
        }

        for (int i = 0; i < outputList.size(); i++) {
            if (!outputList.get(i).equals(outputListResult.get(i))) {
                judgeInfoMessage = JudgeInfoMessageEnum.WRONG_ANSWER;
                judgeInfoResponse.setMessage(judgeInfoMessage.getText());
                return judgeInfoResponse;
            }
        }

        // 判断题目限制
        JudgeConfig judgeConfigResult = JSONUtil.toBean(question.getJudgeConfig(), JudgeConfig.class);
        Long timeLimit = judgeConfigResult.getTimeLimit();
        Long memoryLimit = judgeConfigResult.getMemoryLimit();
        if (memory > memoryLimit) {
            judgeInfoMessage = JudgeInfoMessageEnum.MEMORY_LIMIT_EXCEEDED;
            judgeInfoResponse.setMessage(judgeInfoMessage.getText());
            return judgeInfoResponse;
        }

        if (time > timeLimit) {
            judgeInfoMessage = JudgeInfoMessageEnum.TIME_LIMIT_EXCEEDED;
            judgeInfoResponse.setMessage(judgeInfoMessage.getText());
            return judgeInfoResponse;
        }
        judgeInfoResponse.setMessage(JudgeInfoMessageEnum.ACCEPTED.getText());
        return judgeInfoResponse;
    }
}
