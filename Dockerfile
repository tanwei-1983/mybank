# 使用 OpenJDK 17 作为基础镜像
FROM openjdk:17-jdk-slim

# 设置镜像维护者信息
LABEL maintainer="tom@xx.com"

# 创建工作目录并切换到该目录
WORKDIR /app

# 将打包后的 JAR 文件复制到容器中
COPY transaction-management-1.0.0.jar app.jar

# 暴露 Spring Boot 默认端口
EXPOSE 8080

# 定义容器启动时的命令
ENTRYPOINT ["java", "-jar", "app.jar"]