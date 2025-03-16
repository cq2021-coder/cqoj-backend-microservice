# 基础镜像
FROM openjdk:17-jdk

# 指定工作目录
WORKDIR /app

# 将 jar 包添加到工作目录
ADD target/cqoj-user-service-0.0.1-SNAPSHOT.jar .

# 暴露端口
EXPOSE 3051

# 启动命令
ENTRYPOINT ["java","-jar","/app/cqoj-user-service-0.0.1-SNAPSHOT.jar","--spring.profiles.active=dev"]
