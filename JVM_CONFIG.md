# JVM Configuration

## Production Settings

```bash
-XX:+UseContainerSupport
-XX:MaxRAMPercentage=75.0
-XX:InitialRAMPercentage=50.0
-XX:+UseG1GC
-XX:+UseStringDeduplication
-Djava.security.egd=file:/dev/./urandom
```

### Flag Explanations

| Flag | Purpose | When to Change |
|------|---------|----------------|
| `-XX:+UseContainerSupport` | Respects container memory limits | Never disable in containers |
| `-XX:MaxRAMPercentage=75.0` | Uses 75% of container RAM for heap | Lower if OOM occurs, raise if more heap needed |
| `-XX:InitialRAMPercentage=50.0` | Starts with 50% of max heap | Lower for faster startup, raise for fewer GC pauses |
| `-XX:+UseG1GC` | Garbage collector with balanced throughput/latency | Use ZGC for < 10ms pauses, Parallel for max throughput |
| `-XX:+UseStringDeduplication` | Reduces memory for duplicate strings | Disable if CPU is constrained |
| `-Djava.security.egd` | Faster random number generation | Keep for containerized environments |

## Memory Allocation Strategy

Per the requirements:
- 40% of available memory for caching (PokeAPI responses)
- 500MB-1GB for write operations (local Pokemon storage)

For a 1GB container:
- Total heap: ~750MB (75% of container)
- Caffeine cache: ~300MB (40% of heap)
- Application + H2: ~450MB remaining

## Development Settings

```bash
-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005
-Dspring.profiles.active=dev
```

Enables remote debugging on port 5005.

## Troubleshooting Settings

```bash
-XX:+HeapDumpOnOutOfMemoryError
-XX:HeapDumpPath=/app/data/heapdump.hprof
-Xlog:gc*:file=/app/data/gc.log:time,uptime,level,tags
```

| Flag | Purpose |
|------|---------|
| `-XX:+HeapDumpOnOutOfMemoryError` | Creates heap dump on OOM for analysis |
| `-XX:HeapDumpPath` | Location for heap dumps |
| `-Xlog:gc*` | Detailed GC logging |

## Recommended Container Sizes

| Environment | Container Memory | Notes |
|-------------|-----------------|-------|
| Development | 512MB | Minimal for quick iteration |
| Production | 1GB | Per requirements (500MB-1GB for writes) |
| Load Testing | 2GB | Headroom for cache growth |
