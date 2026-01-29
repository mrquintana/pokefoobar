# Troubleshooting Guide

## Startup Issues

### Application Fails to Start

**Symptom**: Application crashes on startup with connection errors.

**Possible causes**:
1. Port 8080 already in use
2. PokeAPI unreachable
3. Insufficient memory

**Solutions**:
```bash
# Check if port is in use
lsof -i :8080

# Kill existing process
kill -9 <PID>

# Or run on different port
java -jar app.jar --server.port=8081
```

### Slow Startup

**Symptom**: Application takes > 30 seconds to start.

**Possible causes**:
1. DNS resolution issues
2. Network latency to PokeAPI
3. Large H2 database file

**Solutions**:
```bash
# Disable initial cache warmup (if implemented)
# Check network connectivity
curl -I https://pokeapi.co/api/v2/pokemon/1

# For H2 file mode, check database size
ls -la /app/data/pokemondb*
```

## Memory Issues

### OutOfMemoryError

**Symptom**: `java.lang.OutOfMemoryError: Java heap space`

**Possible causes**:
1. Container memory too low
2. Cache size too large
3. Memory leak

**Solutions**:
```bash
# Increase container memory
docker run -m 1g ...

# Reduce cache size in application.yml
spring:
  cache:
    caffeine:
      spec: maximumSize=200,expireAfterWrite=30m

# Generate heap dump for analysis
-XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/tmp/
```

### High Memory Usage

**Symptom**: Memory usage grows continuously.

**Solutions**:
```bash
# Check cache statistics
curl http://localhost:8080/actuator/caches

# Clear all caches (if exposed)
curl -X DELETE http://localhost:8080/actuator/caches
```

## API Issues

### PokeAPI Errors

**Symptom**: 502 Bad Gateway or connection timeouts.

**Possible causes**:
1. PokeAPI is down
2. Rate limiting
3. Network issues

**Solutions**:
```bash
# Test PokeAPI directly
curl https://pokeapi.co/api/v2/pokemon/1

# Check for rate limiting (should have no limit for reasonable use)
# Wait and retry if temporary issue
```

### 404 Not Found for Local Pokemon

**Symptom**: GET /api/v1/local-pokemon/{id} returns 404.

**Cause**: Pokemon hasn't been copied to local database.

**Solution**: First copy the Pokemon:
```bash
curl -X POST http://localhost:8080/api/v1/local-pokemon/1
```

### 400 Bad Request on Update

**Symptom**: PUT /api/v1/local-pokemon/{id} returns validation error.

**Possible causes**:
1. Invalid weight (must be positive)
2. Name too long (max 100 chars)

**Solution**: Check request body:
```bash
# Valid request
curl -X PUT http://localhost:8080/api/v1/local-pokemon/1 \
  -H "Content-Type: application/json" \
  -d '{"name": "ValidName", "weight": 100}'
```

## Cache Issues

### Stale Data

**Symptom**: API returns outdated Pokemon data.

**Cause**: Cached responses from PokeAPI.

**Solutions**:
```bash
# Caches expire after 30 minutes automatically
# For immediate refresh, restart the application
# Or implement cache eviction endpoint (not included by default)
```

### Cache Not Working

**Symptom**: Every request hits PokeAPI (visible in logs).

**Possible causes**:
1. Cache disabled in profile
2. CacheConfig not loaded

**Solutions**:
```bash
# Verify cache is enabled
curl http://localhost:8080/actuator/caches

# Check application.yml
spring:
  cache:
    type: caffeine  # Should NOT be 'none'
```

## H2 Database Issues

### Data Lost on Restart

**Symptom**: Local Pokemon disappear after restart.

**Cause**: Using in-memory mode (`jdbc:h2:mem:`) instead of file mode.

**Solution**: Use production profile:
```bash
java -jar app.jar --spring.profiles.active=prod
```

### Database Locked

**Symptom**: `Database may be already in use` error.

**Cause**: Another process has lock on H2 file.

**Solutions**:
```bash
# Find and kill other Java processes
ps aux | grep java
kill -9 <PID>

# Or delete lock file
rm /app/data/pokemondb.lock.db
```

## Logs

### Enable Debug Logging

```bash
# Via command line
java -jar app.jar --logging.level.com.pokemon=DEBUG

# Or in application.yml
logging:
  level:
    com.pokemon: DEBUG
    org.springframework.web: DEBUG
```

### View GC Logs

```bash
# Enable GC logging
java -Xlog:gc*:file=/tmp/gc.log:time,uptime,level,tags -jar app.jar

# Analyze GC log
cat /tmp/gc.log | grep "Pause"
```
