#!/usr/bin/env bash

FILE_PATH="/cluster-id/clusterID"
interval=5  # wait interval in seconds

while [ ! -e "$FILE_PATH" ] || [ ! -s "$FILE_PATH" ]; do
  echo "Waiting for $FILE_PATH to be created..."
  sleep $interval
done

export CLUSTER_ID=$(cat "$FILE_PATH")

echo "Using CLUSTER_ID: $CLUSTER_ID"

echo "kafka-storage format --ignore-formatted -t $CLUSTER_ID -c /etc/kafka/kafka.properties"
#echo "kafka-storage format --config /opt/kafka/config/server.properties --ignore-formatted --cluster-id $CLUSTER_ID"

exec /etc/confluent/docker/run
