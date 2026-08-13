# =========================================================================
# Lightweight Java 21 Non-Root Container Execution Runtime Environment
# =========================================================================
FROM eclipse-temurin:21-jre-alpine AS runtime-engine
WORKDIR /platform/runtime

# Establish a highly restricted, non-root application system execution group
RUN addgroup -S platformgroup && adduser -S platformuser -G platformgroup
USER platformuser

# Copy the fat executable jar compiled by the Azure DevOps Pipeline host agent dynamically
COPY target/*.jar analytics-service.jar

# Enforce secure network socket bindings and optimize memory expansion footprints
ENV JAVA_OPTS="-XX:+UseG1GC -XX:+ExitOnOutOfMemoryError -Xms256m -Xmx512m"

# Expose streaming metric dashboard and aggregate service administration port
EXPOSE 8088

ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar analytics-service.jar"]
