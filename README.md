#  Notification Dispatch System

![Java](https://img.shields.io/badge/Java-17+-orange?logo=openjdk)
![Jackson](https://img.shields.io/badge/Jackson-2.15.2-blue)
![Status](https://img.shields.io/badge/status-active-success)

A Java-based notification dispatch system using core OOP principles including abstract classes, interfaces, composition, and design patterns such as Template Method, Strategy, Repository, and Composite. 

---

## File Structure

| File | Purpose |
|------|---------|
| `Main.java` | Entry point and demo orchestrator |
| `Notification.java` | Abstract base class with template method pattern |
| `EmailNotification.java` | Email notification implementation |
| `SMSNotification.java` | SMS notification with phone number validation |
| `PushNotification.java` | Push notification implementation |
| `SystemNotification.java` | System-level notification implementation |
| `NotificationManager.java` | Core business logic and coordination |
| `NotificationRepository.java` | In-memory collection management |
| `NotificationStorage.java` | JSON file persistence using Jackson |
| `Logger.java` | Logging interface |
| `ConsoleLogger.java` | Logs to standard output |
| `FileLogger.java` | Logs to a timestamped file |
| `DualLogger.java` | Composite logger (console + file simultaneously) |
| `Repository.java` | Generic repository interface |
| `Storage.java` | Generic storage interface |
| `Sendable.java` | Interface for sendable notifications |

---

### Prerequisites

- Java 17+
- [Jackson Databind 2.15.2](https://repo1.maven.org/maven2/com/fasterxml/jackson/core/jackson-databind/2.15.2/)

### Compile & Run

```bash
javac -cp ".:jackson-databind-2.15.2.jar" *.java
java -cp ".:jackson-databind-2.15.2.jar" kyle.com.Main
```

---

## Usage

### Setup

```java
Logger logger = new DualLogger(
    new FileLogger("notifications.log"),
    new ConsoleLogger()
);

NotificationRepository repository = new NotificationRepository(new ArrayList<>());
Storage<Notification> storage = new NotificationStorage(logger, "notifications.json");
NotificationManager manager = new NotificationManager(repository, logger, storage);
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

This project demonstrates the following OOP and Java concepts:

**Object-Oriented Principles** — interface-based design, abstract classes with template methods, composition over inheritance, polymorphism and dynamic dispatch.

**Design Patterns** — Template Method, Strategy, Repository, and Composite.

**Technicals** — JSON serialization with polymorphism, exception handling and input validation, file I/O, generic programming, and the Java Collections Framework.

---

*Author: Kyle | Purpose: OOP principles, SOLID and design patterns in Java*
```
