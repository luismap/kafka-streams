#remove container broker container
docker ps -a | grep broker | awk '{print $1}' | xargs docker container rm

#remove volume
docker volume rm docker_broker-kafka-streams

#remove container zookeeper
docker ps -a | grep zookeeper | awk '{print $1}' | xargs docker container rm

#remove volume for zookeeper
docker volume rm docker_zk-kafka-streams