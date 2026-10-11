# LoggerLLD — Logging Framework (LLD / Machine Coding)

Design and implement a logging framework from scratch, as commonly asked in Amazon's machine coding round ("Design a Logger").

## Problem Statement

Implement a logging framework that multiple services/applications can use concurrently:

```java
Logger sink = LoggerFactory.getLogger("UserService");
sink.debug("message");
sink.error("message");
```

---

## Functional Requirements

### Core

1. **Log levels** — Support ordered log levels: `DEBUG`, `INFO`, `WARN`, `ERROR` (optionally `FATAL`).
   - A message is logged only if its level ≥ the sink's configured loggerThreshold.

2. **Multiple sinks** — Support at least two sinks/appenders:
   - `ConsoleSink` — writes to stdout
   - `FileSink` — writes to a log file

3. **Named loggers** — Support multiple named loggers (one per class/service, e.g. `LoggerFactory.getLogger("PaymentService")`), each with its own level and sink configuration.

4. **Message dispatch to sinks** — Each sink has its own level loggerThreshold. A message is dispatched to a sink only if the message's level ≥ that sink's loggerThreshold.
   - Example: console sink at `ERROR`, file sink at `DEBUG`
     - An `ERROR` message → written to **both** console and file
     - A `DEBUG` message → written to **file only**
   - Rationale: console is monitored live (errors only), file keeps the full history for debugging.

5. **Config-driven setup** — Loggers and sinks are configured via a config file (JSON/YAML):

   ```json
   {
     "loggers": {
       "UserService": { "level": "DEBUG", "sinks": ["file"] },
       "PaymentService": { "level": "ERROR", "sinks": ["console", "file"] }
     },
     "sinks": {
       "console": { "type": "console", "level": "ERROR" },
       "file": { "type": "file", "level": "DEBUG", "path": "app.log" }
     }
   }
   ```

6. **Message format** — Log messages follow a configurable layout:
   ```
   <timestamp> [<level>] [<sink-name>] <message>
   ```
   Formatting must be pluggable (strategy pattern) so custom formats can be added without changing core code.

7. **Concurrency** — The framework must be thread-safe; multiple threads/services must be able to log simultaneously without interleaved/corrupted output.

### Derived Requirements

8. **Sink fan-out** — A single `log()` call writes the formatted message to **all** sinks registered with that sink that satisfy their level filter.

9. **Extensibility (OCP)** — New sinks (DB, network, etc.) and new formatters must be addable without modifying existing framework code.

### Stretch Requirements (follow-ups)

10. **Async logging** — Non-blocking `log()` calls: messages go to a queue and a background worker thread writes to sinks (configurable queue size + drop policy).
11. **Log rotation** — Rotate files by size or time, with max history/retention.
12. **Dynamic reconfiguration** — Change sink levels at runtime without restarting the application.

---

## Design Patterns Used

| Pattern | Where | Why |
|---|---|---|
| **Factory** | `LoggerFactory` | Creates and caches `Logger` instances per service name; callers never construct `Logger` directly |
| **Factory (sink creation)** | `SinkFactory` (config-driven) | Creates the right `Sink` implementation from config type string — adding a `DBSink` needs no core code change |
| **Strategy** | `LogFormatter` / `DefaultFormatter` | Formatting algorithm is swappable per sink (JSON, plain text) without touching sinks or `Logger` |
| **Observer (fan-out)** | `Logger` → `List<Sink>` | One `log()` call notifies all registered sinks; each sink filters by its own threshold |
| **Singleton (scoped)** | `LoggerFactory` registry + per-name cache | One factory per JVM, and exactly one `Logger` instance per name — `Logger` itself is NOT a singleton (multiple named loggers required) |
| **Registry** | `Map<String, Logger>` in factory; sink registry by name | Ensures shared `FileSink` instances → single file handle across loggers |

## Key Design Decisions

1. **Logger is not a Singleton; the factory/registry is.** The requirement demands multiple named loggers (`UserService` at DEBUG, `PaymentService` at ERROR). Singleton-ness applies at two levels: one `LoggerFactory` per JVM + one `Logger` per name (guaranteed by the `ConcurrentHashMap` cache). This mirrors SLF4J/Logback.

2. **Per-name caching via `computeIfAbsent`.** `getLogger(name)` called from anywhere returns the same instance — config stays consistent and file handles are not duplicated. `containsKey`-then-put would race between check and put; `computeIfAbsent` makes check+create+store atomic.

3. **Two-level level filtering.** A message passes the logger threshold first (cheap early exit), then each sink applies its own threshold. This is what enables "ERROR → console + file, DEBUG → file only".

4. **Sinks are shared singletons by name.** Two loggers referencing sink `"file"` share one `FileSink` instance — one open file handle, not one per logger.

5. **Formatting delegated to `LogFormatter` strategy.** Sinks own *where* to write; formatters own *what* the line looks like. Adding JSON logging = new formatter class, zero changes to sinks (OCP + SRP).

6. **`ordinal()`-based level comparison.** Enum declaration order (DEBUG < INFO < WARN < ERROR < FATAL) is the priority — simple and sufficient; an explicit priority field would guard against future reordering.

7. **Fail-fast config (config-driven setup).** Invalid config (unknown sink type, bad level) throws at startup, not at first log call. Config is loaded once; `getLogger()` performs no I/O.

## Evaluation Criteria → Approach

| Concern | Approach |
|---|---|
| Creating loggers | Factory + registry (per-name singleton) |
| Sinks (console, file, DB) | Interface + implementations |
| Message formatting | Strategy pattern |
| Sink creation from config | Factory pattern |
| Dispatching to multiple sinks | Fan-out / observer |
| Thread safety | ConcurrentHashMap, single writer per sink |
| Adding new sinks/formatters | Open-Closed Principle |

## Class Diagram (High Level)

```
LoggerFactory ──> Logger ──> Sink (interface)
                    │            ├── ConsoleSink
                    │            └── FileSink
                    ├── MessageLevel (enum)
                    ├── Message
                    └── LogFormatter (interface)
                         └── DefaultFormatter
```

## Project Structure

```
Solution/src/
├── LoggerLLDDEmo.java          # Demo: multi-sink fan-out, level filtering
├── enums/
│   └── MessageLevel.java       # DEBUG..FATAL, order = priority
├── models/
│   ├── Message.java            # level, content, timestamp, serviceName
│   └── Logger.java             # level methods, threshold check, sink fan-out
├── sink/
│   ├── Sink.java               # interface
│   ├── ConsoleSink.java
│   └── FileSink.java
├── formatter/
│   ├── LogFormatter.java       # strategy interface
│   └── DefaultFormatter.java
└── factory/
    └── LoggerFactory.java      # per-name cache (computeIfAbsent)
```
