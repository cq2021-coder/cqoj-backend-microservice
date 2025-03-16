package com.cq.sandbox.core;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ObjectUtil;
import com.cq.sandbox.model.*;
import com.cq.sandbox.model.enums.JudgeInfoMessageEnum;
import com.cq.sandbox.utils.ProcessUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StopWatch;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

/**
 * 代码沙箱模板
 * 注意每个实现类必须自定义代码存放路径，参考{@link JavaNativeCodeSandbox}
 *
 * @author 程崎
 * @since 2023/09/01
 */
@Slf4j
public abstract class CodeSandboxTemplate implements CodeSandbox {

    String prefix;

    String globalCodeDirPath;

    String globalCodeFileName;

    /**
     * 超时时间，超过10秒则结束
     */
    public static final Long DEFAULT_TIME_OUT = 10000L;


    /**
     * 每个实现类必须实现编译以及运行的cmd
     *
     * @param userCodeParentPath 代码所在的父目录
     * @param userCodePath       代码所在目录
     * @return {@link CodeSandboxCmd}
     */
    abstract CodeSandboxCmd getCmd(String userCodeParentPath, String userCodePath);

    /**
     * 保存代码到文件中，注意这里需要实现，不同编程语言要放到不同文件夹中
     * 保存到文件中的格式应为: UUID/代码文件，后面删除代码文件需要将代码文件的父文件删除
     *
     * @param code 代码
     * @return {@link File}
     */
    private File saveCodeToFile(String code) {
        String globalCodePath = System.getProperty("user.dir") + globalCodeDirPath;
        if (!FileUtil.exist(globalCodePath)) {
            FileUtil.mkdir(globalCodePath);
        }

        // 存放用户代码
        String userCodeParentPath = globalCodePath + prefix + File.separator + UUID.randomUUID();
        String userCodePath = userCodeParentPath + globalCodeFileName;
        return FileUtil.writeString(code, userCodePath, StandardCharsets.UTF_8);
    }

    /**
     * 编译代码，注意编译代码要返回编译的信息
     *
     * @param compileCmd 编译命令
     * @return {@link ExecuteMessage}
     * @throws IOException IOException
     */
    private ExecuteMessage compileCode(String compileCmd) throws IOException {
        Process compileProcess = Runtime.getRuntime().exec(compileCmd);
        return ProcessUtil.handleProcessMessage(compileProcess, "编译");
    }


    /**
     * 运行代码
     *
     * @param inputList 输入用例
     * @param runCmd    运行的cmd
     * @return {@link List}<{@link ExecuteMessage}>
     * @throws RuntimeException RuntimeException
     */
    private List<ExecuteMessage> runCode(List<String> inputList, String runCmd) throws RuntimeException {
        List<ExecuteMessage> executeMessageList = new LinkedList<>();
        for (String input : inputList) {
            Process runProcess;
            Thread computeTimeThread;
            try {
                runProcess = Runtime.getRuntime().exec(runCmd);
                computeTimeThread = new Thread(() -> {
                    try {
                        Thread.sleep(DEFAULT_TIME_OUT);
                        if (runProcess.isAlive()) {
                            log.info("超时了，中断");
                            runProcess.destroy();
                        }
                        executeMessageList.add(
                                ExecuteMessage
                                        .builder()
                                        .exitCode(-10001)
                                        .errorMessage("超时")
                                        .time(DEFAULT_TIME_OUT)
                                        .build()
                        );
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                });
                computeTimeThread.start();
                StopWatch stopWatch = new StopWatch();
                stopWatch.start();
                ExecuteMessage executeMessage = ProcessUtil.handleProcessInteraction(runProcess, input, "运行");
                stopWatch.stop();
                computeTimeThread.stop();
                executeMessage.setTime(stopWatch.getLastTaskTimeMillis());
                executeMessageList.add(executeMessage);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
        return executeMessageList;
    }


    @Override
    public final ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        List<String> inputList = executeCodeRequest.getInputList();
        String code = executeCodeRequest.getCode();
        // 保存代码
        File userCodeFile = saveCodeToFile(code);
        String userCodePath = userCodeFile.getAbsolutePath();
        String userCodeParentPath = userCodeFile.getParentFile().getAbsolutePath();
        CodeSandboxCmd cmdFromLanguage = getCmd(userCodeParentPath, userCodePath);
        String compileCmd = cmdFromLanguage.getCompileCmd();
        String runCmd = cmdFromLanguage.getRunCmd();
        // 编译代码
        try {
            ExecuteMessage executeMessage = compileCode(compileCmd);
            if (executeMessage.getExitCode() != 0) {
                FileUtil.del(userCodeParentPath);
                return ExecuteCodeResponse
                        .builder()
                        .status(2)
                        .message("编译错误")
                        .resultType(JudgeInfoMessageEnum.COMPILE_ERROR)
                        .build();
            }
        } catch (IOException e) {
            FileUtil.del(userCodeParentPath);
            return errorResponse(e);
        }

        // 执行代码
        try {
            List<ExecuteMessage> executeMessageList = runCode(inputList, runCmd);
            // 返回处理结果
            ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();
            executeCodeResponse.setStatus(1);
            executeCodeResponse.setResultType(JudgeInfoMessageEnum.ACCEPTED);
            JudgeInfo judgeInfo = new JudgeInfo();
            executeCodeResponse.setJudgeInfo(judgeInfo);
            List<String> outputList = new LinkedList<>();
            long maxTime = 0;

            for (ExecuteMessage executeMessage : executeMessageList) {
                if (ObjectUtil.equal(0, executeMessage.getExitCode())) {
                    outputList.add(executeMessage.getMessage());
                } else if (ObjectUtil.equal(-10001, executeMessage.getExitCode())) {
                    executeCodeResponse.setMessage(executeMessage.getErrorMessage());
                    executeCodeResponse.setResultType(JudgeInfoMessageEnum.TIME_LIMIT_EXCEEDED);
                    JudgeInfo timeoutJudgeInfo = new JudgeInfo();
                    timeoutJudgeInfo.setTime(executeMessage.getTime());
                    executeCodeResponse.setJudgeInfo(timeoutJudgeInfo);
                    executeCodeResponse.setStatus(3);
                    break;
                } else {
                    executeCodeResponse.setMessage(executeMessage.getErrorMessage());
                    executeCodeResponse.setResultType(JudgeInfoMessageEnum.RUNTIME_ERROR);
                    executeCodeResponse.setStatus(3);
                    break;
                }
                maxTime = Math.max(maxTime, executeMessage.getTime());
            }
            judgeInfo.setTime(maxTime);
            executeCodeResponse.setOutputList(outputList);
            FileUtil.del(userCodeParentPath);
            return executeCodeResponse;
        } catch (RuntimeException e) {
            FileUtil.del(userCodeParentPath);
            return errorResponse(e);
        }
    }

    final ExecuteCodeResponse errorResponse(Throwable e) {
        return ExecuteCodeResponse
                .builder()
                .outputList(new ArrayList<>())
                .resultType(JudgeInfoMessageEnum.SYSTEM_ERROR)
                .message(e.getMessage())
                .judgeInfo(new JudgeInfo())
                .status(2)
                .build();
    }
}
