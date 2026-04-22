#  Notification Dispatch System

![Java](https://img.shields.io/badge/Java-17+-orange?logo=openjdk)
![Jackson](https://img.shields.io/badge/Jackson-2.15.2-blue)
![Status](https://img.shields.io/badge/status-active-success)

A Java-based notification dispatch system using core OOP principles including abstract classes, interfaces, composition, and design patterns such as Template Method, Strategy, Repository, and Composite. 

---

## Why I Built This

Before starting Spring Boot, I wanted a solid understanding of what a web framework is actually doing, not just how to use one.

Spring Boot abstracts a lot: routing, JSON parsing, request/response handling, error management, dependency injection. That's powerful, but if you jump straight into it without understanding the foundations, you end up copying annotations without knowing what problem they solve.

So I built this first, with three specific goals:

---

### 1. Understand HTTP methods and what they mean semantically

Not just "GET retrieves data", but why you use `POST` instead of `PUT` for creation, why `DELETE` returns `404` when something doesn't exist, and why `POST` returns `201` instead of `200`.

**What this project taught me:** Every endpoint in `NotificationController` maps directly to an HTTP verb that signals intent — not just functionality.

---

### 2. Understand polymorphic HTTP handling

This was the biggest insight. A single `/api/notifications` endpoint can't just return a list — it returns `EmailNotification`, `SMSNotification`, `PushNotification`, and `SystemNotification` all mixed together.

**How this project handles it:**
- Jackson's `@JsonTypeInfo` with `property = "type"` tells the serializer to include a discriminator field
- `@JsonSubTypes` maps `"email"`, `"sms"`, `"push"`, `"system"` to their respective classes
- The same mechanism works in reverse: when a `POST` request comes in with a `type` field, Jackson instantiates the correct subclass automatically

**What this taught me:** Polymorphism isn't just an OOP concept — it has real HTTP/JSON implications. Spring Boot's `@JsonTypeInfo` works exactly the same way, but building it manually showed me why it's necessary.

---

### 3. Understand HTTP status codes and when to use them

Every response has a deliberately chosen status code:

| Status | When |
|--------|------|
| `200 OK` | Successful `GET`, `DELETE`, or batch operation |
| `201 CREATED` | Resource successfully created via `POST` |
| `400 BAD REQUEST` | Validation failed (invalid email, malformed phone number) |
| `404 NOT FOUND` | Notification ID doesn't exist |
| `500 INTERNAL SERVER ERROR` | Unexpected processing failure |

**What this taught me:** Status codes are part of the HTTP contract, not optional decorations. Clients rely on them.

---

### 4. Understand how a request actually flows through a layered application

From raw HTTP request → JSON parsing → DTO validation → domain model creation → repository storage → JSON persistence → structured response.

**The flow this project exposes:**
```
HTTP POST /api/notifications/email
    ↓
Spark parses raw request
    ↓
Jackson maps JSON → EmailNotifDTO
    ↓
NotificationController creates EmailNotification (validation happens in constructor)
    ↓
NotificationManager.addNotification()
    ↓
Repository.add() + idIndex.put() + Storage.save()
    ↓
201 CREATED + ApiResponse wrapper
```

**What this taught me:** Spring Boot automates this entire wiring. That's convenient, but building it manually showed me *what* it's automating and *why* each layer exists.

---

## Did this project accomplish its intended goals?

**Yes.**

I now understand:
- Why frameworks like Spring Boot exist (they solve real wiring complexity)
- What polymorphism means for JSON serialization (not just Java objects)
- Why HTTP status codes matter beyond "it worked or it didn't"
- How to structure layered applications without magic annotations

When I move to Spring Boot, I won't be copying patterns — I'll be recognizing solutions to problems I've already solved manually.

## Project Structure

```
src/
└── main/java/API/
    ├── Controller/
    │   └── NotificationController.java        
    │  
    ├── DTO/
    │    ├── EmailNotifDTO.java 
    │    ├── NotificationDTO.java
    │    ├── PushNotifDTO.java 
    │    ├── SMSNotifDTO.java
    │    └── NotificationDTO.java
    │
    ├── Logger/  # Represents what the client sends in a request body.
    │    ├── ConsoleLogger.java # logs messages to system console.
    │    ├── FileLogger.java # writes formatted logs with timestamps to a file.
    │    ├── DualLogger.java # composes two loggers and delegates to both.
    │    └── Logger.java # Logging contract interface.
    │
    ├── Model/ # The Model entities. Validates its own fields on construction.
    │   ├── EmailNotification.java               
    │   ├── PushNotification.java             
    │   ├── SMSNotification.java       
    │   ├── SystemNotification.java        
    │   └── Notification.java
    │     
    ├── Repository/
    │   ├── Repository.java       
    │   └── NotificationRepository.java
    ├── Storage/
    │   ├── Storage.java          
    │   └── NotificationStorage.java  
    ├── Responses/
    │   ├── ApiResponse.java        
    │   └── ErrorResponse.java      
    ├── Service/
    │   ├── NotificationManager.java     
    │   └── NotificationService.java     
    └── util/
        └── NotificationIDGenerator.java    
```
---

## Tech Stack

| Tool | Purpose |
|------|---------|
| Java 21 | Core language |
| SparkJava 2.9.4 | Lightweight HTTP web framework |
| Jackson | JSON serialization and deserialization |
| Maven | Dependency and build management |
| JUnit 5 | Unit testing |

---
### Compile & Run

```bash
javac -cp ".:jackson-databind-2.15.2.jar" *.java
java -cp ".:jackson-databind-2.15.2.jar" kyle.com.Main
```

### Setup

```java
Logger logger = new DualLogger(
    new FileLogger("notifications.log"),
    new ConsoleLogger()
);

NotificationRepository repository = new NotificationRepository(new ArrayList<>());
Storage<Notification> storage = new NotificationStorage(logger, "notifications.json");
NotificationService service = new NotificationService(repository);
NotificationManager manager = new NotificationManager(repository,service,logger,storage);
```

### Creating Notifications

```java
// Email
EmailNotification email = new EmailNotification(
    logger, "LeBron James", "lebron@gmail.com", "boss@yahoo.com", "Meeting tomorrow!"
);

// SMS (Philippine format — exactly 11 digits)
SMSNotification sms = new SMSNotification(
    logger, "Kobe Bryant", "09123456789", "09476384433", "Hello LBJ!"
);

// Push
PushNotification push = new PushNotification(
    logger, "Netflix", "device-abc-123", "New episode available!"
);

// System
SystemNotification system = new SystemNotification(
    logger, "System", "Android", "device-xyz-789", "Update available"
);
```

### Managing Notifications

```java
manager.addNotification(email);       // Adds and auto-saves to JSON
manager.sendMessage(email);           // Process a single notification
manager.sendAllMessages();            // Process all pending notifications
manager.printStats();                 // Print delivery statistics
manager.deleteNotification(sms);      // Remove a notification
manager.clearAllNotifications();      // Remove all notifications
```

---

## Sample Output

**Console**
```
[INFO] 🚀 STARTING NOTIFICATION SYSTEM DEMO 🚀
[INFO] Adding of Notification#0 [PENDING] complete.
[INFO] Sending Email Notification: to boss@yahoo.com: Meeting tomorrow!
[INFO] Notification status: SENT
[INFO] Successful messages: 1
[INFO] Failed: 0
[INFO] Total: 4
[INFO] Success Rate: 100.0%
```

**File Log (`notifications.log`)**
```
Sat, Mar 21, 2025 10:30:45 | [INFO] | 🚀 STARTING NOTIFICATION SYSTEM DEMO 🚀
Sat, Mar 21, 2025 10:30:45 | [INFO] | Adding of Notification#0 [PENDING] complete.
Sat, Mar 21, 2025 10:30:45 | [INFO] | Sending Email Notification: to boss@yahoo.com: Meeting tomorrow!
Sat, Mar 21, 2025 10:30:45 | [INFO] | Notification status: SENT
```

**JSON Storage (`notifications.json`)**
```json
[
  {
    "type": "email",
    "sender": "LeBron James",
    "message": "Meeting tomorrow!",
    "status": "SENT",
    "id": 0,
    "senderEmail": "lebron@gmail.com",
    "receiverEmail": "boss@yahoo.com"
  },
  {
    "type": "sms",
    "sender": "Kobe Bryant",
    "message": "Hello LBJ!",
    "status": "PENDING",
    "id": 1,
    "senderPhoneNumber": "09123456789",
    "receiverPhoneNumber": "09476384433"
  }
]
```

---

### Retry Logic (`Notification.java`)

```java
for (int attempt = 1; attempt <= getMaxRetryAttempts(); attempt++) {
    try {
        sendMessage();
        displayNotification();
        status = NotificationStatus.SENT;
        break;
    } catch (IllegalArgumentException e) {
        status = NotificationStatus.FAILED; // No retry on validation errors
        break;
    } catch (Exception e) {
        logger.warn("Processing failed. " + (getMaxRetryAttempts() - attempt) + " attempt(s) left.");
    }
}
```

### Validation Rules

| Notification Type | Rules |
|-------------------|-------|
| Email | Sender and receiver emails cannot be null or empty |
| SMS | Phone numbers must be exactly 11 digits |
| Push | Device token cannot be null or empty |
| System | Device token and OS cannot be null or empty |
| All | Sender name and message cannot be null or empty |

### JSON Polymorphism

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

---

## System Architecture

```
┌──────────────────────────────────────────────┐
│                   Main.java                   │
│                   (Demo)                      │
└──────────────────────┬───────────────────────┘
                       │
                       ▼
┌──────────────────────────────────────────────┐
│            NotificationManager               │
│  ┌────────────────────────────────────────┐  │
│  │              Manages:                  │  │
│  │  ┌──────────┐ ┌────────┐ ┌─────────┐  │  │
│  │  │Repository│ │ Logger │ │ Storage │  │  │
│  │  │(contains)│ │ (uses) │ │  (uses) │  │  │
│  │  └──────────┘ └────────┘ └─────────┘  │  │
│  └────────────────────────────────────────┘  │
└──────────────────────┬───────────────────────┘
                       │
                       ▼
┌──────────────────────────────────────────────┐
│           Notification (abstract)            │
│  ┌────────────────────────────────────────┐  │
│  │  +processNotification()  [final]       │  │
│  │  +sendMessage()          [abstract]    │  │
│  │  +displayNotification()  [abstract]    │  │
│  │  #validateField()                      │  │
│  └────────────────────────────────────────┘  │
│         │         │         │         │      │
│         ▼         ▼         ▼         ▼      │
│  ┌──────────┐ ┌───────┐ ┌──────┐ ┌────────┐ │
│  │  Email   │ │  SMS  │ │ Push │ │ System │ │
│  │Notif.    │ │Notif. │ │Notif.│ │ Notif. │ │
│  └──────────┘ └───────┘ └──────┘ └────────┘ │
└──────────────────────┬───────────────────────┘
                       │
          ┌────────────┴────────────┐
          ▼                         ▼
┌──────────────────┐     ┌──────────────────┐
│ Logger           │     │ Sendable         │
│ (interface)      │     │ (interface)      │
│ +info()          │     │ +sendMessage()   │
│ +error()         │     └──────────────────┘
│ +debug()         │
│ +warn()          │
└────────┬─────────┘
         │
   ┌─────┼─────┐
   ▼     ▼     ▼
┌───────┐ ┌───────┐ ┌───────┐
│Console│ │ File  │ │ Dual  │
│Logger │ │Logger │ │Logger │
└───────┘ └───────┘ └───────┘
```

---

## Design Patterns Used

| Pattern | Where |
|---------|-------|
| Template Method | `Notification.processNotification()` defines the fixed workflow |
| Strategy | Pluggable `Logger` implementations |
| Repository | `NotificationRepository` manages the collection |
| Composite | `DualLogger` delegates to multiple loggers |

---

## Error Handling

```java
// Example: invalid SMS phone number
try {
    SMSNotification invalid = new SMSNotification(
        logger, "Test", "123", "456", "Hello"
    );
} catch (IllegalArgumentException e) {
    // Output: [ERROR] Phone number must be exactly 11 digits
}
```

---

## Statistics

```java
manager.sendAllMessages();
manager.printStats();
// [INFO] Successful messages: 3
// [INFO] Failed: 1
// [INFO] Total: 4
// [INFO] Success Rate: 75.0%
```

---

##  Notes

| Topic | Detail |
|-------|--------|
| File paths | Update `Logger.txt` and `notifications.json` paths as needed |
| SMS format | Validates Philippine format (11 digits) — update regex for other regions |
| JSON library | Requires Jackson Databind 2.15.2+ |
| Thread safety | Not thread-safe — single-threaded use only |
| ID generation | Static counter resets on JVM restart; IDs may not be unique across sessions |
| Run behavior | First run creates `notifications.json`; subsequent runs load from it |

---

##  Possible Enhancements

| Possible Enhancements |
| | Replace JSON file storage with a proper database |
| Add unit tests with mocking (e.g., JUnit + Mockito) |
| Notification templates with placeholders |
| Priority queues for urgent notifications |
| Batch processing and rate limiting |
| Webhook support for external integrations |
| Scheduled delivery (time-based sending) |
| International phone number validation library |

---

## Requirements

- Java 17+
- Jackson Databind 2.15.2+
- Write permissions for `notifications.log` and `notifications.json`

---

## Developer Learning Objectives

**Object-Oriented Principles** — includes interface-based design, abstract classes with template methods, composition over inheritance, polymorphism, and dynamic dispatch.

**RESTful API Concepts** — covers the request–response lifecycle, data modeling, HTTP status codes, exception handling, API response structure, and error response handling.

**Design Patterns** — Template Method, Strategy, Repository, and Composite.

**Technicals** — JSON serialization with polymorphism, exception handling and input validation, file I/O, generic programming, and the Java Collections Framework.

---

*Author: Kyle | Purpose: OOP principles, Rest API, SOLID and design patterns in Java*
