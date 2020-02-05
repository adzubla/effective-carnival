FROM adoptopenjdk/openjdk11:alpine
VOLUME /tmp
ARG DEPENDENCY=target/dependency
COPY ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY ${DEPENDENCY}/BOOT-INF/classes /app
COPY ${DEPENDENCY}/META-INF /app/META-INF
COPY start.sh /
RUN chmod +x start.sh
USER 500
ENV STARTUP_WAIT_TIME=0
ENTRYPOINT [ "./start.sh" ]
EXPOSE 8081
