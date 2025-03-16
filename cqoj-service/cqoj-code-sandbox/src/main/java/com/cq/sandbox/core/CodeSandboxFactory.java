package com.cq.sandbox.core;

import com.cq.sandbox.model.enums.QuestionSubmitLanguageEnum;

public class CodeSandboxFactory {
    public static CodeSandboxTemplate getInstance(QuestionSubmitLanguageEnum language) {
        return switch (language) {
            case JAVA -> new JavaNativeCodeSandbox();
            case CPP -> new CppNativeCodeSandbox();
            default -> throw new RuntimeException("暂不支持");
        };
    }
}
