FROM eclipse-temurin:21-jdk-alpine

# 设置环境变量
ENV JAVA_OPTS="-Xms512m -Xmx512m -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/home/java/errorDump.hprof -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -XX:G1HeapRegionSize=4M"
ENV APP_HOME=/app
#ENV SPRING_PROFILES_ACTIVE=local

WORKDIR $APP_HOME

# 复制JAR文件
COPY libs/application.jar app.jar

EXPOSE 22222

# 健康检查
#HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
#  CMD curl -f http://localhost:9962/health-check.json || exit 1

#ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Dspring.profiles.active=$SPRING_PROFILES_ACTIVE -jar app.jar" ]
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -jar app.jar" ]