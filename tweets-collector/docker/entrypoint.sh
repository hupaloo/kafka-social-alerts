#!/bin/bash
set -e

DEBUG_OPTS=""

if [ "$ENABLE_DEBUG" = "true" ]; then
  if [ -z "$DEBUG_PORT" ]; then
    echo "ENABLE_DEBUG=true but DEBUG_PORT is not set. Please specify a port."
    exit 1
  fi

  echo "Debug mode enabled on port $DEBUG_PORT"
  DEBUG_OPTS="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:$DEBUG_PORT"
fi

exec java $DEBUG_OPTS -jar tweets-collector.jar
