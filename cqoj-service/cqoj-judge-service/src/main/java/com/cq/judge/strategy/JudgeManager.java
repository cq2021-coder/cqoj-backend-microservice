package com.cq.judge.strategy;

import com.cq.judge.strategy.impl.DefaultJudgeStrategy;
import com.cq.judge.strategy.impl.JavaJudgeStrategy;
import com.cq.model.dto.questionsubmit.JudgeInfo;
import com.cq.model.enums.QuestionSubmitLanguageEnum;
import org.springframework.stereotype.Service;

/**
 * 判题管理
 *
 * @author 程崎
 * @since 2023/08/16
 */
@Service
public class JudgeManager {

    public JudgeInfo doJudge(JudgeContext judgeContext) {
        QuestionSubmitLanguageEnum languageType = judgeContext.getLanguageType();
        JudgeStrategy judgeStrategy;
        if (QuestionSubmitLanguageEnum.JAVA.equals(languageType)) {
            judgeStrategy = new JavaJudgeStrategy();
        }else {
            judgeStrategy = new DefaultJudgeStrategy();
        }
        return judgeStrategy.doJudge(judgeContext);
    }

}
