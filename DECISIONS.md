# Technical Decisions

## 1. Entity-Attribute-Value (EAV) Pattern for Extensibility

**Decision**: Use EAV pattern for storing custom Pokemon attributes.

**Context**: The requirement states that users should be able to add new attributes like Pokemon names in different countries. A fixed schema would require migrations for each new attribute.

**Rationale**: EAV allows adding `name_jp`, `name_es`, `name_fr`, or any custom attribute without schema changes. The trade-off is slightly more complex queries, but for this use case the flexibility outweighs the complexity.

**Alternative considered**: JSON column for attributes. Rejected because H2's JSON support is limited and EAV provides better query capabilities.

## 2. Caffeine Cache over Redis

**Decision**: Use Caffeine for in-memory caching.

**Context**: The requirement mentions caching PokeAPI responses with 40% memory allocation.

**Rationale**: Caffeine is simpler to configure, has no external dependencies, and is sufficient for a single-instance deployment. Redis would be overkill for this use case and adds operational complexity.

**When to change**: If horizontal scaling is needed, migrate to Redis for distributed caching.

## 3. RestClient over WebClient

**Decision**: Use Spring's RestClient for HTTP calls to PokeAPI.

**Context**: Need to consume PokeAPI endpoints.

**Rationale**: RestClient provides a cleaner, synchronous API that's easier to understand and debug. WebClient's reactive approach adds complexity without benefit for this use case where we need to wait for responses anyway.

**When to change**: If the API needs to handle high concurrency with non-blocking I/O, consider WebClient.

## 4. H2 File Mode in Production

**Decision**: Use H2 in file mode for production persistence.

**Context**: Need a database for local Pokemon storage without external dependencies.

**Rationale**: Keeps the deployment simple (single JAR/container). Data persists across restarts. For an interview exercise, this demonstrates understanding of trade-offs without over-engineering.

**When to change**: For real production use, migrate to PostgreSQL.

## 5. Monorepo Structure

**Decision**: Keep backend and frontend in the same repository.

**Context**: Need to deliver a full-stack application.

**Rationale**: Simpler for demonstration and review. Single clone, single repository to share. The interviewer can see all code in one place.

## 6. Spring Boot 3.5 with Java 21

**Decision**: Use latest LTS Java version with modern Spring Boot.

**Context**: Need to choose Java and framework versions.

**Rationale**: Java 21 provides virtual threads, pattern matching, and other modern features. Spring Boot 3.5 is current and demonstrates familiarity with modern tooling.

## 7. Pagination Strategy

**Decision**: Use page/size parameters with 0-based indexing.

**Context**: PokeAPI uses offset/limit, but standard REST conventions use page/size.

**Rationale**: Page/size is more intuitive for API consumers. The service layer converts to offset/limit internally when calling PokeAPI.
