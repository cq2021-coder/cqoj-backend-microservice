//package com.cq.sandbox.dao;
//
//import com.github.dockerjava.api.DockerClient;
//import com.github.dockerjava.api.async.ResultCallback;
//import com.github.dockerjava.api.command.CreateContainerResponse;
//import com.github.dockerjava.api.command.ExecCreateCmdResponse;
//import com.github.dockerjava.api.command.PullImageCmd;
//import com.github.dockerjava.api.model.HostConfig;
//import com.github.dockerjava.api.model.PullResponseItem;
//import com.github.dockerjava.api.model.Statistics;
//import com.github.dockerjava.core.command.ExecStartResultCallback;
//import com.github.dockerjava.core.command.PullImageResultCallback;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Repository;
//
//import javax.annotation.Resource;
//import java.io.InputStream;
//import java.util.concurrent.TimeUnit;
//
//@Repository
//@Slf4j
//public class DockerDao {
//
//    /**
//     * 默认超时时间
//     */
//    public static final Long DEFAULT_TIME_OUT = 100L;
//
//    @Resource
//    private DockerClient dockerClient;
//
//    /**
//     * 拉取镜像
//     *
//     * @param image 镜像名
//     */
//    public void pullImage(String image) {
//        PullImageCmd pullImageCmd = dockerClient.pullImageCmd(image);
//        try (PullImageResultCallback pullImageResultCallback = new PullImageResultCallback() {
//            @Override
//            public void onNext(PullResponseItem item) {
//                log.info("下载{}镜像: {}", image, item.getStatus());
//                super.onNext(item);
//            }
//        }) {
//            pullImageCmd
//                    .exec(pullImageResultCallback)
//                    .awaitCompletion();
//            log.info("{}下载完成", image);
//        } catch (Exception e) {
//            log.error("{}拉取镜像错误", image, e);
//            throw new RuntimeException("拉取镜像错误");
//        }
//    }
//
//    /**
//     * 创建一个容器
//     *
//     * @param image      镜像名
//     * @param hostConfig host配置
//     * @return {@link CreateContainerResponse}
//     */
//    public CreateContainerResponse createContainer(String image, HostConfig hostConfig) {
//        return dockerClient.createContainerCmd(image)
//                .withNetworkDisabled(false)
//                .withAttachStdin(true)
//                .withAttachStderr(true)
//                .withAttachStdout(true)
//                .withTty(true)
//                .withHostConfig(hostConfig)
//                .exec();
//    }
//
//    /**
//     * 启动容器
//     *
//     * @param containerId 容器id
//     */
//    public void startContainer(String containerId) {
//        dockerClient.startContainerCmd(containerId).exec();
//    }
//
//    /**
//     * 创建container执行cmd
//     *
//     * @param containerId 容器id
//     * @param cmd         命令
//     * @return {@link ExecCreateCmdResponse}
//     */
//    public ExecCreateCmdResponse executeCreateCmd(String containerId, String... cmd) {
//        return dockerClient
//                .execCreateCmd(containerId)
//                .withAttachStdin(true)
//                .withAttachStderr(true)
//                .withAttachStdout(true)
//                .withTty(true)
//                .withCmd(cmd)
//                .exec();
//    }
//
//    /**
//     * 执行启动
//     *
//     * @param execId                  execId
//     * @param inputStream             输入流
//     * @param execStartResultCallback 回调
//     */
//    public void executeStart(String execId, InputStream inputStream, ExecStartResultCallback execStartResultCallback) {
//        try {
//            dockerClient
//                    .execStartCmd(execId)
//                    .withDetach(false)
//                    .withTty(true)
//                    .withStdIn(inputStream)
//                    .exec(execStartResultCallback)
//                    .awaitCompletion();
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    /**
//     * 执行启动
//     *
//     * @param execId                  execId
//     * @param inputStream             输入流
//     * @param execStartResultCallback 回调
//     * @param timeOut                 超时时间
//     * @param unit                    时间单位
//     */
//    public void executeStart(String execId, InputStream inputStream, ExecStartResultCallback execStartResultCallback, long timeOut, TimeUnit unit) {
//        try {
//            dockerClient
//                    .execStartCmd(execId)
//                    .withTty(true)
//                    .withExecId(execId)
//                    .withStdIn(inputStream)
//                    .exec(execStartResultCallback)
//                    .awaitCompletion(timeOut, unit);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    /**
//     * 删除容器
//     *
//     * @param containerId 容器id
//     */
//    public void deleteContainer(String containerId) {
//        dockerClient.removeContainerCmd(containerId).withForce(true).exec();
//    }
//
//    /**
//     * 获取状态
//     * @param containerId 容器Id
//     * @param resultCallback 回调
//     * @return {@link ResultCallback<Statistics>}
//     */
//    public ResultCallback<Statistics> getStats(String containerId, ResultCallback<Statistics> resultCallback) {
//        return dockerClient.statsCmd(containerId).exec(resultCallback);
//    }
//}
