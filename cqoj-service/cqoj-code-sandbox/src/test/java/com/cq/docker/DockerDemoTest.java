package com.cq.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.PingCmd;
import com.github.dockerjava.core.DockerClientBuilder;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.IOException;

@Slf4j
public class DockerDemoTest {

    @Test
    public void test() {
        try (DockerClient dockerClient = DockerClientBuilder.getInstance().build()) {
            PingCmd pingCmd = dockerClient.pingCmd();
            pingCmd.exec();
        } catch (IOException e) {
            log.error("初始化错误", e);
        }
    }

}
