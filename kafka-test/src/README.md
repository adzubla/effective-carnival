
## Iniciar Kafka

    docker-compose up -d

## Abrir shell no container Kakfa

    ./exec_bash.sh

## Criar tópico 

    kafka-topics --bootstrap-server localhost:9092 --create --partitions 2 --replication-factor 1 --topic my-topic

    kafka-topics --bootstrap-server localhost:9092 --list



Topics:

* pc-info - pc_id:latitude,longitude,descricao


    kafka-topics --bootstrap-server localhost:9092 --create --topic pc-info --partitions 2 --replication-factor 1 --config cleanup.policy=compact --config min.cleanable.dirty.ratio=0.001 --config segment.ms=1000
    
    kafka-console-producer --broker-list localhost:9092 --topic pc-info --property parse.key=true --property key.separator=:
    
    1:100,200,300,Shopping Barueri 1
    2:101,201,301,Shopping Barueri 2
    3:400,400,400,Shopping Iguatemi 1
    4:401,401,401,Shopping Iguatemi 2
    5:200,200,200,Tecban Alphaville
    6:300,300,300,Farmacia


* pc-status - pc_id:status


    kafka-topics --bootstrap-server localhost:9092 --create --topic pc-status --partitions 2 --replication-factor 1 --config cleanup.policy=compact --config min.cleanable.dirty.ratio=0.001 --config segment.ms=1000

    kafka-console-producer --broker-list localhost:9092 --topic pc-status --property parse.key=true --property key.separator=:
    
    1:online
    2:online
    3:online
    4:online
    5:online
    6:online
    1:offline
    1:online
    4:offline
    4:offline
    4:online
    4:online
    2:offline

    
* pc-joined - pc_id:status


    kafka-topics --bootstrap-server localhost:9092 --create --topic pc-joined --partitions 2 --replication-factor 1 --config cleanup.policy=compact --config min.cleanable.dirty.ratio=0.001 --config segment.ms=1000

    kafka-console-consumer --bootstrap-server localhost:9092 --topic pc-joined --property print.key=true --property key.separator=:


## Redis

    docker run --name some-redis -d redis
