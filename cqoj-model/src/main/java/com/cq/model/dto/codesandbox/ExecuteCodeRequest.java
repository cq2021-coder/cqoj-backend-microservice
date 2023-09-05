package com.cq.model.dto.codesandbox;

import com.cq.model.enums.QuestionSubmitLanguageEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 执行代码请求
 *
 * @author 程崎
 * @since 2023/08/15
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExecuteCodeRequest {

    private List<String> inputList;

    private String code;

    private QuestionSubmitLanguageEnum language;
}
