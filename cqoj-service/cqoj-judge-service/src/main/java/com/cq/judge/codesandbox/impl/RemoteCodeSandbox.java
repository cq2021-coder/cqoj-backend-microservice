package com.cq.judge.codesandbox.impl;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.cq.common.exception.BusinessException;
import com.cq.common.response.ResultCodeEnum;
import com.cq.judge.codesandbox.CodeSandbox;
import com.cq.model.dto.codesandbox.ExecuteCodeRequest;
import com.cq.model.dto.codesandbox.ExecuteCodeResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * 远程代码沙箱
 *
 * @author 程崎
 * @since 2023/08/15
 */
@Slf4j
public class RemoteCodeSandbox implements CodeSandbox {

    private static final String URL = "http://120.48.83.118:3040/codesandbox/execute";


    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        String responseStr = HttpUtil.post(URL, JSONUtil.toJsonStr(executeCodeRequest));
        if (StringUtils.isBlank(responseStr)) {
            throw new BusinessException(ResultCodeEnum.SYSTEM_ERROR, "远程代码沙箱无法访问");
        }
        return JSONUtil.toBean(responseStr, ExecuteCodeResponse.class);
    }
}
