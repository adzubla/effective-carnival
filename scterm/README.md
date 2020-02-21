

## Inicar o docker registry local

    docker run -d -p 5000:5000 --restart=always --name registry registry:2

## Iniciar IBM MQ

    docker run ‑‑env LICENSE=accept ‑‑env MQ_QMGR_NAME=QM1
               ‑‑publish 1414:1414
               ‑‑publish 9443:9443
               ‑‑detach
               ibmcom/mq

## Build do demo

    mvn clean install

## Deploy e execução do demo

    mvn dockerfile:build dockerfile:push
    docker-compose -f docker-compose-demo.yaml up 
