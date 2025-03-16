# 基础镜像
FROM openjdk:17-jdk

# 指定工作目录
WORKDIR /app

# 将 jar 包添加到工作目录
ADD target/cqoj-gateway-0.0.1-SNAPSHOT.jar .

# 暴露端口
EXPOSE 3050

# 启动命令
ENTRYPOINT ["java","-jar","/app/cqoj-gateway-0.0.1-SNAPSHOT.jar","--spring.profiles.active=dev"]
