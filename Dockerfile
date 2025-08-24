FROM openjdk:17-jdk-slim

LABEL maintainer="tom@qq.com"

WORKDIR /app

COPY transaction-management-1.0.0.jar  app.jar

EXPOSE 8080

# 定义容器启动时的命令
ENTRYPOINT ["java", "-jar", "app.jar"]