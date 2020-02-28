# Local

## Inicar o docker registry local

    docker run -d -p 5000:5000 --restart=always --name registry registry:2

## Iniciar IBM MQ

    docker run \
      --env LICENSE=accept \
      --env MQ_QMGR_NAME=QM1 \
      --publish 1414:1414 \
      --publish 9443:9443 \
      --detach \
      ibmcom/mq

    https://localhost:9443/ibmmq/console

    User: admin
    Password: passw0rd

## Build do demo

    cd scterm-server
    mvn clean install

## Deploy e execução do demo



# Minikube


## Criar cluster

    minikube start --cpus 4 --memory 8192

    eval $(minikube docker-env)

    kubectl apply -f k8s/config-reader.yaml
    
## Deploy IBM MQ

    kubectl apply -f k8s/ibmmq-deployment.yaml
    
    https://192.168.99.123:32443/ibmmq/console/

    User: admin
    Password: passw0rd
    
## Build e deploy das aplicações

    cd scterm-server
    mvn clean install
    mvn dockerfile:build
    
    cd scterm-client
    mvn clean install
    mvn dockerfile:build
