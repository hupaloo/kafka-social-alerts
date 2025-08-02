#!/bin/bash

set -e

BOOTSTRAP_SERVER="broker1:9092,broker2:9092,broker3:9092"

kafka-topics --bootstrap-server $BOOTSTRAP_SERVER \
  --create --if-not-exists --topic tweets.raw --partitions 10 --replication-factor 3

kafka-topics --bootstrap-server $BOOTSTRAP_SERVER \
  --create --if-not-exists --topic tweets.sentiment --partitions 10 --replication-factor 3

kafka-topics --bootstrap-server $BOOTSTRAP_SERVER \
  --create --if-not-exists --topic alerts.volume --partitions 2 --replication-factor 3

kafka-topics --bootstrap-server $BOOTSTRAP_SERVER \
  --create --if-not-exists --topic alerts.sentiment --partitions 2 --replication-factor 3
