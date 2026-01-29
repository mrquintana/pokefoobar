#!/bin/bash
set -e

echo "Starting Pokemon API in TROUBLESHOOTING mode..."

docker compose run --rm \
    -e JAVA_OPTS="-XX:+UseContainerSupport \
                  -XX:MaxRAMPercentage=75.0 \
                  -XX:+PrintFlagsFinal \
                  -XX:+HeapDumpOnOutOfMemoryError \
                  -XX:HeapDumpPath=/app/data/heapdump.hprof \
                  -Xlog:gc*:file=/app/data/gc.log:time,uptime,level,tags \
                  -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005" \
    -e LOGGING_LEVEL_ROOT=DEBUG \
    -e LOGGING_LEVEL_COM_POKEMON=TRACE \
    -p 8080:8080 \
    -p 5005:5005 \
    pokemon-api-dev

echo ""
echo "Troubleshooting mode active"
echo "GC logs: ./data/gc.log"
echo "Heap dumps on OOM: ./data/heapdump.hprof"
