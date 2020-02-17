
# Configuração com Minikube

Procedimento para a instalação e execução do demo no ambiente local com Minikube.

Pre-requisitos

- Ubuntu 18.04 LTS
- Docker
- VirtualBox

### Instalação do Kubectl

    sudo apt-get update && sudo apt-get install -y apt-transport-https
    curl -s https://packages.cloud.google.com/apt/doc/apt-key.gpg | sudo apt-key add -
    echo "deb https://apt.kubernetes.io/ kubernetes-xenial main" | sudo tee -a /etc/apt/sources.list.d/kubernetes.list
    sudo apt-get update
    sudo apt-get install -y kubectl

### Instalação do Minikube

    curl -Lo minikube https://storage.googleapis.com/minikube/releases/latest/minikube-linux-amd64 && chmod +x minikube
    sudo install minikube /usr/local/bin

### Prepara ambiente Minikube

Criar uma máquina virtual do minikube, com 4 CPUs e 8 GB de memória
    
    minikube start --cpus 4 --memory 8192

Fazer isso em todo terminal que usar Minikube!

    eval $(minikube docker-env)

Abrir o dashboard

    minikube dashboard &

### Instalar Kakfa pelo Strimzi

    kubectl create namespace kafka
    curl -L https://github.com/strimzi/strimzi-kafka-operator/releases/download/0.16.2/strimzi-cluster-operator-0.16.2.yaml \
      | sed 's/namespace: .*/namespace: kafka/' \
      | kubectl apply -f - -n kafka
    kubectl apply -f https://raw.githubusercontent.com/strimzi/strimzi-kafka-operator/0.16.2/examples/kafka/kafka-persistent-single.yaml -n kafka 
    kubectl wait kafka/my-cluster --for=condition=Ready --timeout=300s -n kafka 

# Configuração de permissões

É necessário alterar permissões para o Spring Cloud acessar os Config Maps e Secrets

    kubectl apply -f minikube/spring-config-reader.yaml
 
# Build e deploy das aplicações

Configura docker para guardar as imagens no minikube

    eval $(minikube docker-env)

Build e deploy das aplicações
  
    mvn clean install
    mvn dockerfile:build
    kubectl apply -f k8s/demo-deployment.yaml

### URL dos serviços do Istio

As portas locais são abertas pelo script `port-forward.sh`

- Kiali

    http://localhost:20001/kiali/
    
- Grafana

    http://localhost:3000/
    
- Jaeger

    http://localhost:16686/jaeger/
    
- Prometheus

    http://localhost:9090/
