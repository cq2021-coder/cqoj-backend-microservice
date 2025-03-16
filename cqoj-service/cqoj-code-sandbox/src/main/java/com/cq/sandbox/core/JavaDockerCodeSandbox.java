//package com.cq.sandbox.core;
//
//import cn.hutool.core.io.FileUtil;
//import cn.hutool.core.io.IoUtil;
//import cn.hutool.core.util.ArrayUtil;
//import com.cq.sandbox.dao.DockerDao;
//import com.cq.sandbox.model.ExecuteCodeRequest;
//import com.cq.sandbox.model.ExecuteCodeResponse;
//import com.cq.sandbox.model.ExecuteMessage;
//import com.cq.sandbox.model.JudgeInfo;
//import com.cq.sandbox.model.enums.LanguageImageEnum;
//import com.cq.sandbox.utils.ProcessUtil;
//import com.github.dockerjava.api.async.ResultCallback;
//import com.github.dockerjava.api.command.CreateContainerResponse;
//import com.github.dockerjava.api.model.*;
//import com.github.dockerjava.core.command.ExecStartResultCallback;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//import org.springframework.util.StopWatch;
//
//import javax.annotation.Resource;
//import java.io.ByteArrayOutputStream;
//import java.io.Closeable;
//import java.io.File;
//import java.io.IOException;
//import java.nio.charset.StandardCharsets;
//import java.util.ArrayList;
//import java.util.LinkedList;
//import java.util.List;
//import java.util.UUID;
//
//
///**
// * java本机代码沙箱
// *
// * @author 程崎
// * @since 2023/08/21
// */
//@Slf4j
//@Service
//public class JavaDockerCodeSandbox implements CodeSandbox {
//
//    private static final String PREFIX = File.separator + "java";
//
//    private static final String GLOBAL_CODE_DIR_PATH = File.separator + "tempCode";
//
//    private static final String GLOBAL_JAVA_CLASS_NAME = File.separator + "Main.java";
//
//
//    /**
//     * 第一次拉取
//     */
//    private static boolean FIRST_PULL = true;
//
//    @Resource
//    private DockerDao dockerDao;
//
//
//    @Override
//    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
//        List<String> inputList = executeCodeRequest.getInputList();
//        String code = executeCodeRequest.getCode();
//        String userDir = System.getProperty("user.dir");
//        String globalCodePath = userDir + GLOBAL_CODE_DIR_PATH;
//        if (!FileUtil.exist(globalCodePath)) {
//            FileUtil.mkdir(globalCodePath);
//        }
//
//        // 存放用户代码
//        String userCodeParentPath = globalCodePath + PREFIX + File.separator + UUID.randomUUID();
//        String userCodePath = userCodeParentPath + GLOBAL_JAVA_CLASS_NAME;
//        FileUtil.writeString(code, userCodePath, StandardCharsets.UTF_8);
//
//
//        // 编译代码
//        try {
//            String compileCmd = String.format("javac -encoding utf-8 %s", userCodePath);
//            Process compileProcess = Runtime.getRuntime().exec(compileCmd);
//            ProcessUtil.handleProcessMessage(compileProcess, "编译");
//        } catch (IOException e) {
//            return errorResponse(e);
//        }
//        // 执行代码
//        String image = LanguageImageEnum.JAVA.getImage();
//        ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();
//        if (FIRST_PULL) {
//            try {
//                dockerDao.pullImage(image);
//                FIRST_PULL = false;
//            } catch (Exception e) {
//                throw new RuntimeException(e);
//            }
//        }
//
//        HostConfig hostConfig = new HostConfig();
//        hostConfig.withMemory(100 * 1000 * 1000L);
//        hostConfig.withCpuCount(1L);
//        hostConfig.setBinds(new Bind(userCodeParentPath, new Volume("/app")));
//        CreateContainerResponse containerResponse = dockerDao.createContainer(image, hostConfig);
//        String containerId = containerResponse.getId();
//        dockerDao.startContainer(containerId);
//
//        List<String> outputList = new LinkedList<>();
//        long maxTime = 0;
//        final long[] maxMemory = {0};
//        // 是否超时
//        final boolean[] timeout = {true};
//        for (String input : inputList) {
//            String[] cmd = ArrayUtil.append(new String[]{"java", "-Dfile.encoding=UTF-8", "-cp", "/app", "Main"}, input.split(" "));
////        String[] cmd = {"java", "-Dfile.encoding=UTF-8", "-cp", "/app", "Main"};
//            String execId = dockerDao.executeCreateCmd(containerId, cmd).getId();
//            ExecuteMessage executeMessage = new ExecuteMessage();
//            ByteArrayOutputStream resultStream = new ByteArrayOutputStream();
//            ByteArrayOutputStream errorResultStream = new ByteArrayOutputStream();
//            ExecStartResultCallback execStartResultCallback = new ExecStartResultCallback() {
//
//                @Override
//                public void onComplete() {
//                    timeout[0] = false;
//                    super.onComplete();
//                }
//
//                @Override
//                public void onNext(Frame frame) {
//                    StreamType streamType = frame.getStreamType();
//                    byte[] payload = frame.getPayload();
//                    if (StreamType.STDERR.equals(streamType)) {
//                        try {
//                            errorResultStream.write(payload);
//                        } catch (IOException e) {
//                            throw new RuntimeException(e);
//                        }
//                    } else {
//                        try {
//                            resultStream.write(payload);
//                        } catch (IOException e) {
//                            throw new RuntimeException(e);
//                        }
//                    }
//                }
//            };
//            ResultCallback<Statistics> resultCallback = new ResultCallback<>() {
//                @Override
//                public void onStart(Closeable closeable) {
//
//                }
//
//                @Override
//                public void onNext(Statistics statistics) {
//                    Long usage = statistics.getMemoryStats().getUsage();
//                    if (usage == null) {
//                        usage = 0L;
//                    }
//                    log.info("内存占用: {}", usage);
//                    maxMemory[0] = Math.max(maxMemory[0], usage);
//                }
//
//                @Override
//                public void onError(Throwable throwable) {
//
//                }
//
//                @Override
//                public void onComplete() {
//
//                }
//
//                @Override
//                public void close() {
//
//                }
//            };
//            try (ResultCallback<Statistics> ignored = dockerDao.getStats(containerId, resultCallback)) {
////                InputStream inputStream = new ByteArrayInputStream(().getBytes());
//                StopWatch stopWatch = new StopWatch();
//                stopWatch.start();
//                dockerDao.executeStart(execId, IoUtil.toStream(input + "\n", StandardCharsets.UTF_8), execStartResultCallback);
//                stopWatch.stop();
//                maxTime = Math.max(maxTime, stopWatch.getLastTaskTimeMillis());
//                if (resultStream.size() != 0) {
//                    log.info("正常输出: {}", resultStream);
//                    executeMessage.setMessage(resultStream.toString());
//                }
//                if (errorResultStream.size() != 0) {
//                    log.info("错误输出: {}", errorResultStream);
//                    executeMessage.setMessage(errorResultStream.toString());
//                }
//                outputList.add(resultStream.toString());
//            } catch (IOException e) {
//                log.error("执行失败", e);
//            }
//            log.info("是否超时: {}", timeout[0]);
//        }
//        FileUtil.del(userCodeParentPath);
//        JudgeInfo judgeInfo = new JudgeInfo();
//        judgeInfo.setTime(maxTime);
//        judgeInfo.setMemory(maxMemory[0]);
//        executeCodeResponse.setJudgeInfo(judgeInfo);
//        executeCodeResponse.setOutputList(outputList);
//        dockerDao.deleteContainer(containerId);
//        return executeCodeResponse;
//    }
//
//    private ExecuteCodeResponse errorResponse(Throwable e) {
//        return ExecuteCodeResponse
//                .builder()
//                .outputList(new ArrayList<>())
//                .message(e.getMessage())
//                .judgeInfo(new JudgeInfo())
//                .status(2)
//                .build();
//    }
//}
