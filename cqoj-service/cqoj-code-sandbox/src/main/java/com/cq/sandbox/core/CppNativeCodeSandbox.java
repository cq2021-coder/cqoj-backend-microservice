package com.cq.sandbox.core;

import com.cq.sandbox.model.CodeSandboxCmd;
import lombok.extern.slf4j.Slf4j;

import java.io.File;


/**
 * cpp本机代码沙箱
 *
 * @author 程崎
 * @since 2023/08/21
 */
@Slf4j
public class CppNativeCodeSandbox extends CodeSandboxTemplate {
    private static final String PREFIX = File.separator + "cpp";

    private static final String GLOBAL_CODE_DIR_PATH = File.separator + "tempCode";

    private static final String GLOBAL_CPP_NAME = File.separator + "main.cpp";

    public CppNativeCodeSandbox() {
        super.prefix = PREFIX;
        super.globalCodeDirPath = GLOBAL_CODE_DIR_PATH;
        super.globalCodeFileName = GLOBAL_CPP_NAME;
    }

    @Override
    public CodeSandboxCmd getCmd(String userCodeParentPath, String userCodePath) {
        return CodeSandboxCmd
                .builder()
                .compileCmd(String.format("g++ -finput-charset=UTF-8 -fexec-charset=UTF-8 %s -o %s", userCodePath, userCodePath.substring(0, userCodePath.length() - 4)))
                .runCmd(userCodeParentPath + File.separator + "main")
                .build();
    }
}
