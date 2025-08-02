#!/usr/bin/env bash
set -e

CLUSTER_DIR="/cluster-id"
FILE_PATH="$CLUSTER_DIR/clusterID"

mkdir -p "$CLUSTER_DIR"

if [ ! -f "$FILE_PATH" ]; then
  echo "Generating new cluster.id"
  kafka-storage random-uuid > "$FILE_PATH"
fi
