package com.cq.sandbox.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.PullImageCmd;
import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.api.model.PullResponseItem;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.command.LogContainerResultCallback;
import com.github.dockerjava.core.command.PullImageResultCallback;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@Slf4j
@SpringBootTest
public class DockerDemoTest {


    @Test
    public void testPullImage() {
        String image = "nginx:latest";
        try (DockerClient dockerClient = DockerClientBuilder.getInstance().build()) {
            PullImageCmd pullImageCmd = dockerClient.pullImageCmd(image);
            try (PullImageResultCallback pullImageResultCallback = new PullImageResultCallback() {
                @Override
                public void onNext(PullResponseItem item) {
                    log.info("下载镜像: {}", item.getStatus());
                    super.onNext(item);
                }
            }) {
                pullImageCmd
                        .exec(pullImageResultCallback)
                        .awaitCompletion();
                log.info("下载完成");
            } catch (Exception e) {
                log.error("拉取镜像错误");
            }
        } catch (IOException e) {
            log.error("初始化错误", e);
        }
    }

    @Test
    public void testHandleContainer() {
        try (DockerClient dockerClient = DockerClientBuilder.getInstance().build()) {
            String image = "nginx:latest";
            CreateContainerResponse createContainerResponse = dockerClient.createContainerCmd(image)
                    .withCmd("echo", "Hello Docker")
                    .exec();
            log.info("result: {}", createContainerResponse);
            String containerId = createContainerResponse.getId();
            log.info("containerId: {}", containerId);
            dockerClient.startContainerCmd(containerId).exec();
        } catch (IOException e) {
            log.error("初始化错误", e);
        }
    }

    @Test
    public void testCatLog() {
        try (DockerClient dockerClient = DockerClientBuilder.getInstance().build()) {
            String image = "nginx:latest";
            CreateContainerResponse createContainerResponse = dockerClient.createContainerCmd(image)
                    .withCmd("echo", "Hello Docker")
                    .exec();
            log.info("result: {}", createContainerResponse);
            String containerId = createContainerResponse.getId();
            log.info("containerId: {}", containerId);
            dockerClient.startContainerCmd(containerId).exec();

            // 查看日志
            dockerClient
                    .logContainerCmd(containerId)
                    .withStdErr(true)
                    .withStdOut(true)
                    .exec(new LogContainerResultCallback() {
                        @Override
                        public void onNext(Frame frame) {
                            log.info("日志: {}", new String(frame.getPayload()));
                        }
                    }).awaitCompletion();
        } catch (IOException | InterruptedException e) {
            log.error("初始化错误", e);
        }
    }

    @Test
    public void deleteContainer() {
        try (DockerClient dockerClient = DockerClientBuilder.getInstance().build()) {
            dockerClient.removeContainerCmd("96f0c165dbb5").withForce(true).exec();
        } catch (IOException e) {
            log.error("初始化错误", e);
        }
    }

    @Test
    public void deleteImage() {
        try (DockerClient dockerClient = DockerClientBuilder.getInstance().build()) {
            dockerClient.removeImageCmd("nginx:latest").withForce(true).exec();
        } catch (IOException e) {
            log.error("初始化错误", e);
        }
    }

}
