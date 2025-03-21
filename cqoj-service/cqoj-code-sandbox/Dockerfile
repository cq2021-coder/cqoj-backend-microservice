# 基础镜像
FROM openjdk:17-slim

# 安装 g++ 编译器
RUN sed -i 's@//.*archive.ubuntu.com@//mirrors.aliyun.com@g' /etc/apt/sources.list
RUN apt-get update
RUN apt-get install -y g++
RUN rm -rf /var/lib/apt/lists/*

# 指定工作目录
WORKDIR /app

# 将 jar 包添加到工作目录
ADD target/cqoj-code-sandbox-0.0.1-SNAPSHOT.jar .

# 暴露端口
EXPOSE 3040

# 启动命令
ENTRYPOINT ["java","-jar","/app/cqoj-code-sandbox-0.0.1-SNAPSHOT.jar","--spring.profiles.active=prod"]
