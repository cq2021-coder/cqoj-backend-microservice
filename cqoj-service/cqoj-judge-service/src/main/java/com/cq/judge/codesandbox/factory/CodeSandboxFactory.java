package com.cq.judge.codesandbox.factory;

import com.cq.judge.codesandbox.CodeSandbox;
import com.cq.judge.codesandbox.impl.ExampleCodeSandbox;
import com.cq.judge.codesandbox.impl.RemoteCodeSandbox;
import com.cq.judge.codesandbox.impl.ThirdPartyCodeSandbox;
import com.cq.model.enums.CodeSandboxEnum;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 代码沙箱工厂
 *
 * @author 程崎
 * @since 2023/08/15
 */
@ConfigurationProperties(prefix = "codesandbox")
@Configuration
@Data
public class CodeSandboxFactory {

    private CodeSandboxEnum type = CodeSandboxEnum.REMOTE;

    /**
     * 获取一个新的代码沙箱实例，实例为yml配置的实例
     *
     * @return {@link CodeSandbox}
     */
    public CodeSandbox newInstance() {
        return newInstance(type);
    }

    /**
     * 获取一个新的代码沙箱实例，实例需要用户自己定义，不定义则默认为远程代码沙箱
     *
     * @param customerType 自定义类型
     * @return {@link CodeSandbox}
     */
    public CodeSandbox newInstance(CodeSandboxEnum customerType) {
        if (customerType == null) {
            customerType = CodeSandboxEnum.REMOTE;
        }
        switch (customerType) {
            case REMOTE:
                return new RemoteCodeSandbox();
            case THIRD_PARTY:
                return new ThirdPartyCodeSandbox();
            case EXAMPLE:
            default:
                return new ExampleCodeSandbox();
        }
    }
}
