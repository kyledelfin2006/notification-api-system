# Notification API System

A Java-based notification management system that supports multiple notification channels (Email, SMS, Push, System) with persistent JSON storage, structured logging, and a RESTful API design.

![Java](https://img.shields.io/badge/Java-21+-orange)
![SparkJava](https://img.shields.io/badge/SparkJava-2.9.4-red)
![Jackson](https://img.shields.io/badge/Jackson-2.x-blue)
![Status](https://img.shields.io/badge/status-working-brightgreen)

---

## Architecture Overview

The project is organized around a layered architecture with clearly separated responsibilities:

```
HTTP Layer (Controllers / ApiResponse / ErrorResponse)
        |
Service Layer (NotificationManager, NotificationService)
        |
Repository Layer (NotificationRepository implements Repository<T>)
        |
Model Layer (Notification, EmailNotification, SMSNotification, ...)
        |
Storage Layer (NotificationStorage implements Storage<T>)
        |
Utility / Infrastructure (Logger, NotificationIDGenerator)
```

Each layer depends only on abstractions (interfaces), not concrete implementations, making the system loosely coupled and easy to extend.

---

## Package Structure

```
api/
├── dto/                    # Data Transfer Objects for incoming requests
│   ├── NotificationDTO.java
│   ├── EmailNotifDTO.java
│   ├── SMSNotifDTO.java
│   ├── PushNotifDTO.java
│   └── SystemNotifDTO.java
│
├── model/                  # Core domain objects
│   ├── Sendable.java       # Interface: sendMessage()
│   ├── Notification.java   # Abstract base class
│   ├── EmailNotification.java
│   ├── SMSNotification.java
│   ├── PushNotification.java
│   └── SystemNotification.java
│
├── repository/             # In-memory collection management
│   ├── Repository.java     # Generic interface
│   └── NotificationRepository.java
│
├── storage/                # JSON file persistence
│   ├── Storage.java        # Generic interface
│   └── NotificationStorage.java
│
├── service/                # Business logic
│   ├── NotificationManager.java
│   └── NotificationService.java
│
├── loggers/                # Logging infrastructure
│   ├── Logger.java         # Interface
│   ├── ConsoleLogger.java
│   ├── FileLogger.java
│   └── DualLogger.java
│
├── responses/              # HTTP response wrappers
│   ├── ApiResponse.java
│   └── ErrorResponse.java
│
└── util/
    └── NotificationIDGenerator.java
```

---

## Core Design Patterns

### Generic Interfaces

Both the repository and storage layers are backed by generic interfaces, allowing the same contract to serve different data types without duplication.

```java
public interface Repository<T> {
    void add(T type);
    void remove(T type);
    void addAll(List<T> type);
    List<T> getAll();
    void clear();
}

public interface Storage<T> {
    void save(List<T> items) throws IOException;
    List<T> load() throws IOException;
}
```

`NotificationRepository` implements `Repository<Notification>` and wraps an injected `List<Notification>`, returning an unmodifiable view to callers to protect the internal state.

### Dependency Injection

`NotificationManager` receives all its collaborators through its constructor: a `Logger`, a `Repository<Notification>`, a `Storage<Notification>`, and a `NotificationService`. No concrete class is instantiated internally. This makes the manager fully testable and interchangeable.

```java
public NotificationManager(Logger logger, Repository<Notification> repository,
                            Storage<Notification> storage, NotificationService service) {
    this.repository = repository;
    this.service = service;
    this.logger = logger;
    this.storage = storage;
    this.idIndex = new HashMap<>();
    loadFromStorage();
}
```

### ID Index (Fast Lookup)

In addition to the repository list, `NotificationManager` maintains a `Map<Integer, Notification>` that maps each notification's ID to its object. This provides O(1) lookup by ID without scanning the entire list.

```java
public Notification getNotificationById(int id) {
    return idIndex.get(id);
}
```

The index is rebuilt from the repository any time storage is loaded (`resetIndex()`), keeping both structures in sync.

---

## Polymorphism

The `Notification` abstract class is the backbone of the polymorphic design. It defines the shared contract and enforces behavior through abstract methods and a final template method.

### Abstract Base Class

```java
public abstract class Notification implements Sendable {
    public abstract void displayNotification();  // Subclass defines output
    
    public final void processNotification() {    // Cannot be overridden
        for (int attempt = 1; attempt <= getMaxRetryAttempts(); attempt++) {
            sendMessage();          // Polymorphic dispatch
            displayNotification();  // Polymorphic dispatch
            ...
        }
    }
}
```

`processNotification()` is declared `final`, meaning no subclass can alter the retry logic, status transitions, or logging behavior. What varies is encapsulated in `sendMessage()` and `displayNotification()`, each implemented differently per notification type.

### Concrete Implementations

| Class | Unique Fields | Validation |
|---|---|---|
| `EmailNotification` | `senderEmail`, `receiverEmail` | Regex + dot-rule checks |
| `SMSNotification` | `senderPhoneNumber`, `receiverPhoneNumber` | Must be exactly 11 digits |
| `PushNotification` | `deviceToken` | Non-null, non-empty |
| `SystemNotification` | `deviceOS`, `deviceToken` | Non-null, non-empty |

Each class calls `super(sender, message, logger)` and then performs its own type-specific field validation before storing the data, following a consistent construction pattern across all subtypes.

### Polymorphic Batch Processing

`sendAllMessages()` in `NotificationManager` iterates over a `List<Notification>` and calls `processNotification()` on each element. The correct `sendMessage()` and `displayNotification()` implementations are resolved at runtime through virtual dispatch:

```java
for (Notification notification : repository.getAll()) {
    notification.processNotification(); // resolves to Email, SMS, Push, or System at runtime
}
```

### Jackson Polymorphic Deserialization

The `Notification` class uses Jackson annotations to support serializing and deserializing the full class hierarchy from JSON. When loading from file, Jackson reads the `"type"` field and reconstructs the correct subclass automatically.

```java
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = EmailNotification.class, name = "email"),
    @JsonSubTypes.Type(value = SMSNotification.class, name = "sms"),
    @JsonSubTypes.Type(value = PushNotification.class, name = "push"),
    @JsonSubTypes.Type(value = SystemNotification.class, name = "system")
})
public abstract class Notification implements Sendable { ... }
```

This means a saved list of mixed notification types is restored with full type fidelity across restarts.

---

## Logging System

The logging infrastructure is built on the `Logger` interface, which declares four levels: `info`, `error`, `debug`, and `warn`. Three implementations are provided.

### ConsoleLogger

Writes formatted messages directly to standard output. Suitable for development and local debugging.

### FileLogger

Writes timestamped log entries to a file using `BufferedWriter`. Each entry follows the format:

```
Mon, May 05, 2026 14:32:00 | [INFO] | Notification #3 [SENT]
```

The file is opened in append mode, so log history is preserved across restarts.

### DualLogger (Composition)

`DualLogger` wraps two `Logger` instances and delegates each log call to both. This is a direct application of object composition over inheritance, allowing any two loggers to be combined without subclassing either.

```java
public class DualLogger implements Logger {
    private final Logger firstLogger;
    private final Logger secondLogger;

    public DualLogger(Logger firstLogger, Logger secondLogger) {
        this.firstLogger = firstLogger;
        this.secondLogger = secondLogger;
    }

    @Override
    public void info(String message) {
        firstLogger.info(message);
        secondLogger.info(message);
    }
}
```

A typical usage would be `new DualLogger(new ConsoleLogger(), new FileLogger("app.log"))`, which simultaneously logs to both outputs with no additional code.

---

## Storage and Persistence

`NotificationStorage` implements `Storage<Notification>` using Jackson's `ObjectMapper`. It handles both serialization to disk and deserialization back into typed objects.

On save, it writes the entire notification list as a pretty-printed JSON array. On load, it uses Jackson's `TypeFactory` to construct a `List<Notification>` with full subtype resolution:

```java
return mapper.readValue(
    filePath,
    mapper.getTypeFactory().constructCollectionType(List.class, Notification.class)
);
```

If the file does not exist on load, an empty list is returned and no exception is thrown, allowing the system to start fresh cleanly.

### ID Continuity Across Restarts

`NotificationIDGenerator` uses an `AtomicInteger` to produce sequential IDs. After loading from storage, `NotificationManager` scans all loaded notifications for the maximum ID and sets the generator's counter accordingly:

```java
int maxId = loaded.stream().mapToInt(Notification::getID).max().orElse(-1);
NotificationIDGenerator.setNextId(maxId + 1);
```

This prevents ID collisions across application restarts without requiring a database sequence.

---

## HTTP API Design

The API follows RESTful conventions. Responses are wrapped in `ApiResponse<T>` for successes and `ErrorResponse` for failures, both carrying a `timestamp` field generated at construction time.

### ApiResponse

```java
public class ApiResponse<T> {
    private final boolean success;
    private String message;
    private T data;
    private final long timestamp;
}
```

The generic type parameter `T` allows the same wrapper to carry any payload: a single `Notification`, a `List<Notification>`, a statistics `Map`, or a plain `String` message.

### ErrorResponse

```java
public class ErrorResponse {
    private final String error;
    private String details;
    private final int statusCode;
    private final long timestamp;
}
```

Used when validation fails, a resource is not found, or an unexpected exception is thrown. The `statusCode` field maps to standard HTTP status codes (400, 404, 500, etc.).

### Endpoint Conventions

| Method | Path | Description |
|---|---|---|
| GET | `/api/notifications` | Retrieve all notifications |
| GET | `/api/notifications/{id}` | Retrieve a single notification by ID |
| POST | `/api/notifications/email` | Create an email notification |
| POST | `/api/notifications/sms` | Create an SMS notification |
| POST | `/api/notifications/push` | Create a push notification |
| POST | `/api/notifications/system` | Create a system notification |
| POST | `/api/notifications/{id}/send` | Send a specific notification |
| POST | `/api/notifications/send-all` | Send all notifications |
| DELETE | `/api/notifications/{id}` | Delete a notification |
| DELETE | `/api/notifications` | Clear all notifications |
| GET | `/api/notifications/stats` | Get delivery statistics |

### Data Transfer Objects

Incoming request bodies are mapped to DTO classes rather than directly to model objects. This separates the HTTP contract from the domain model and prevents over-posting. Each DTO extends `NotificationDTO`, which holds the common `sender` and `message` fields, while subclasses add type-specific fields.

---

## Code Highlights

### Thread-Safe Delivery Counters

`NotificationManager` tracks successful and failed deliveries using `AtomicInteger`, which performs read-modify-write as a single atomic operation, making the counters safe for concurrent use without explicit synchronization.

```java
private final AtomicInteger successfulDeliveries = new AtomicInteger(0);
private final AtomicInteger failedDeliveries = new AtomicInteger(0);
```

### Retry Logic with Status Transitions

`processNotification()` retries up to `MAX_RETRY_ATTEMPTS` times. Validation errors (`IllegalArgumentException`) are non-retryable and immediately set the status to `FAILED`. All other exceptions consume a retry slot and log a warning. If all attempts are exhausted without success, the status is set to `FAILED` after the loop.

```java
public final void processNotification() {
    for (int attempt = 1; attempt <= getMaxRetryAttempts(); attempt++) {
        try {
            sendMessage();
            displayNotification();
            status = NotificationStatus.SENT;
            break;
        } catch (IllegalArgumentException e) {
            status = NotificationStatus.FAILED;
            break;                          // No retry for validation errors
        } catch (Exception e) {
            logger.warn("... " + attemptsLeft + " attempt(s) left.");
        }
    }
    if (status == NotificationStatus.PENDING) {
        status = NotificationStatus.FAILED;
    }
}
```

### Email Validation

`EmailNotification` applies a two-pass validation: a regex check for overall structure, then explicit checks for consecutive dots and leading or trailing dots in both the local part and the domain.

```java
String emailRegex = "^[\\p{L}\\p{N}+_.-]+@[\\p{L}\\p{N}.-]+\\.[\\p{L}]{2,}$";

if (localPart.contains("..") || localPart.startsWith(".") || localPart.endsWith(".")) {
    return false;
}
```

### Unmodifiable Repository View

`NotificationRepository.getAll()` wraps the internal list in `Collections.unmodifiableList()`, ensuring callers cannot modify the collection directly and must go through the repository's own `add` and `remove` methods.

```java
public List<Notification> getAll() {
    return Collections.unmodifiableList(notificationList);
}
```

---

## Notification Lifecycle

```
Constructor called
      |
Field validation (sender, message, type-specific fields)
      |
Status: PENDING
      |
processNotification() called
      |
      +-- attempt 1..3
      |       |
      |   sendMessage()          <- polymorphic
      |   displayNotification()  <- polymorphic
      |       |
      |   Success -> Status: SENT, break
      |   Validation error -> Status: FAILED, break
      |   Other error -> warn, retry
      |
All attempts exhausted -> Status: FAILED
      |
saveToStorage()
```

---

## Delivery Statistics

`getDeliveryStatistics()` returns a `Map<String, Object>` with four keys:

- `total` — current number of notifications in the repository
- `successful` — cumulative count of successfully delivered notifications
- `failed` — cumulative count of failed deliveries
- `successRate` — percentage of successful deliveries over total attempted; `null` if no deliveries have been attempted

Note that `total` reflects the current repository size, while `successful` and `failed` are session-level counters that accumulate across all `sendMessage` and `sendAllMessages` calls within a single application run.
