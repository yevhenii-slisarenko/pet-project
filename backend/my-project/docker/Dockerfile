FROM openjdk:21

WORKDIR /srv

COPY target/*.jar .
COPY docker/iam-service.sh .

RUN chmod +x iam-service.sh

EXPOSE 8189

ENTRYPOINT ["./iam-service.sh"]