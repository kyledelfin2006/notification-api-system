# Notification Dispatch System

![Java](https://img.shields.io/badge/Java-17+-orange?logo=openjdk)
![Status](https://img.shields.io/badge/status-active-success)
![Design Patterns](https://img.shields.io/badge/patterns-Template%20Method%2C%20Strategy-blue)

A robust console-based notification dispatch system implementing OOP principles with dual logging (console + file) and retry mechanisms for reliable message delivery.

## ✨ Features

### Core Functionality
- **Multi-channel notifications**: Email, SMS, Push notifications
- **Dual logging system**: Console + File logging simultaneously
- **Automatic retry mechanism**: 3 attempts with 1-second delays
- **Delivery status tracking**: PENDING → SENT/FAILED with statistics
- **Input validation**: Comprehensive field validation with detailed errors
- **Graceful error handling**: Validation errors don't trigger retries

### Technical Highlights
- **Template Method Pattern**: `Notification.process()` defines the complete workflow
- **Strategy Pattern**: `Logger` interface with multiple implementations
- **Dual Logger**: Combined `FileLogger` and `ConsoleLogger` in `DualLogger`
- **Polymorphism**: Unified interface for different notification types
- **File Persistence**: All logs saved to file with timestamps
- **Status Management**: Real-time tracking with detailed statistics

## 🚀 Quick Start

### Prerequisites
- Java 17 or higher
- No external dependencies required

### Installation & Compilation
```bash
# Compile all Java files
javac *.java

# Run the system
java Main
```

## 📖 Usage

### Basic Setup
```java
// Create dual logger (console + file)
Logger logger = new DualLogger(
    new FileLogger("notifications.log"),
    new ConsoleLogger()
);

// Initialize manager
NotificationManager manager = new NotificationManager(logger);

// Create notifications
EmailNotification email = new EmailNotification(
    logger,
    "John Doe",                    // Sender name
    "john@example.com",           // Sender email
    "boss@company.com",           // Receiver email
    "Meeting at 3 PM"             // Message
);

SMSNotification sms = new SMSNotification(
    logger,
    "Jane Smith",                 // Sender name
    "09123456789",               // Sender phone
    "Your OTP is 123456",        // Message
    "09987654321"                // Receiver phone
);

PushNotification push = new PushNotification(
    logger,
    "Netflix",                    // App/Sender name
    "device-token-abc123",       // Device token
    "New episode available!"      // Message
);

// Manage and send
manager.addNotification(email);
manager.addNotification(sms);
manager.addNotification(push);

manager.sendAllMessages();      // Process all notifications
manager.printStats();           // Show success/failure rates
```

### Log Output Examples

**Console Output:**
```
[INFO] 🚀 STARTING NOTIFICATION SYSTEM DEMO 🚀
[INFO] === SCENARIO 1: Happy Path ===
[INFO] Processing: Notification#0 [PENDING]
[INFO] Adding of Notification#0 [PENDING] complete.
[INFO] ✓ Validating EmailNotification...
[INFO] Sending Email Notification: to boss@company.com:Meeting at 3 PM
[INFO] Email from john@example.com to boss@company.com Message: Meeting at 3 PM
[INFO] ✓ Logging delivery of notification
[INFO] Notification status: SENT
```

**File Log (notifications.log):**
```
Thu, Mar 20, 2025 14:30:45 | [INFO] | 🚀 STARTING NOTIFICATION SYSTEM DEMO 🚀
Thu, Mar 20, 2025 14:30:45 | [INFO] | === SCENARIO 1: Happy Path ===
Thu, Mar 20, 2025 14:30:45 | [INFO] | Processing: Notification#0 [PENDING]
```

### Error Handling
```java
try {
    // Invalid phone number (not 11 digits)
    SMSNotification invalidSMS = new SMSNotification(
        logger, "Test", "123", "Invalid!", "456"
    );
} catch (IllegalArgumentException e) {
    System.out.println("Gracefully caught: " + e.getMessage());
    // Output: [ERROR] Phone number must be exactly 11 digits
}
```

## 🏗️ Architecture

```
┌─────────────┐
│   Main.java │  ← Demo orchestrator
└──────┬──────┘
       │
┌──────▼──────────────────┐
│ NotificationManager.java│  ← Manages notification lifecycle
└──────┬──────────────────┘
       │
┌──────▼───────────────┐
│   Notification.java  │  ← Abstract base (Template Method Pattern)
└──────┬───────────────┘
       │
       ├──────────────┬──────────────┬─────────────┐
       │              │              │             │
┌──────▼─────┐  ┌────▼──────┐  ┌────▼──────┐     │
│   Email    │  │    SMS    │  │   Push    │     │
│Notification│  │Notification│  │Notification│    │
└────────────┘  └───────────┘  └───────────┘    │
                                                │
       ┌────────────────────────────────────────┘
       │
┌──────▼──────────┐    ┌──────────────────┐
│   Sendable.java │    │   Logger.java    │
│   (Interface)   │    │   (Interface)    │
└─────────────────┘    └────────┬─────────┘
                                │
               ┌────────────────┼─────────────────┐
               │                │                 │
        ┌──────▼──────┐  ┌─────▼──────┐  ┌──────▼────────┐
        │ Console     │  │   File     │  │    Dual       │
        │ Logger      │  │   Logger   │  │    Logger     │
        └─────────────┘  └────────────┘  └───────────────┘
```

## 📁 File Descriptions

| File | Purpose |
|------|---------|
| `Main.java` | Demo application with scenarios |
| `Notification.java` | Abstract base class with template method |
| `EmailNotification.java` | Email notification implementation |
| `SMSNotification.java` | SMS with phone validation |
| `PushNotification.java` | Push notification implementation |
| `NotificationManager.java` | Manages notifications and statistics |
| `Logger.java` | Logger interface |
| `ConsoleLogger.java` | Logs to console |
| `FileLogger.java` | Logs to file with timestamps |
| `DualLogger.java` | Combines console and file logging |
| `Sendable.java` | Interface for sendable objects |

## 🔍 Key Design Patterns

### 1. Template Method Pattern (`Notification.java`)
```java
public final void process() {
    // Fixed workflow
    validateNotification();
    sendMessage();      // Abstract - implemented by subclasses
    displayNotification(); // Abstract - implemented by subclasses
    logDelivery();
}
```

### 2. Strategy Pattern (`Logger.java`)
```java
public interface Logger {
    void info(String message);
    void error(String message);
    void debug(String message);
    void warn(String message);
}
```

### 3. Decorator-like Pattern (`DualLogger.java`)
```java
// Combines two loggers
public class DualLogger implements Logger {
    public void info(String message) {
        fileLogger.info(message);
        consoleLogger.info(message);
    }
}
```

## 🛠️ Technical Implementation Details

### Retry Mechanism
- **3 maximum attempts** for transient failures
- **1-second delay** between retries
- **Validation errors** don't retry (fail immediately)
- **Network/timeout errors** trigger retry logic

### Validation Rules
- **Email**: Sender/Receiver cannot be empty
- **SMS**: Phone numbers must be exactly 11 digits
- **Push**: Device token cannot be empty
- **All**: Message content cannot be empty

### Statistics Tracking
- Success/failure counters
- Success rate percentage
- Detailed delivery status per notification
- PENDING state detection

### Log Formatting
**Console**: `[LEVEL] message`  
**File**: `Timestamp | [LEVEL] | message`

## 💡 Example Scenarios

### Scenario 1: Happy Path (All Successful)
```java
manager.addNotification(email);
manager.addNotification(sms);
manager.addNotification(push);
manager.sendAllMessages();
manager.printStats();

// Output:
// Successful messages : 3
// Failed: 0
// Total: 3
// Success Rate: 100.0%
```

### Scenario 2: Error Handling
```java
// Invalid phone format triggers immediate failure
SMSNotification invalid = new SMSNotification(logger, "Test", "123", "Hi", "456");
// Throws: IllegalArgumentException with message about 11-digit requirement
```

### Scenario 3: Mixed Results
```java
// Simulates some successes and some failures
// Manager tracks both and calculates accurate statistics
```

## 📊 Statistics Output

After processing all notifications:
```
[INFO] Successful messages : 2
[INFO] Failed: 1
[INFO] Total: 3
[INFO] Success Rate: 66.7%
```

## 🔄 Workflow

1. **Create Notification** → Validation occurs in constructor
2. **Add to Manager** → Added to internal list
3. **Process** → Template method executes:
   - Validation
   - Send attempt (with retries)
   - Display
   - Logging
4. **Update Status** → SENT or FAILED
5. **Track Statistics** → Count successes/failures

## 🚀 Running the Demo

The `Main.java` includes two built-in scenarios:

1. **Happy Path**: Three valid notifications of different types
2. **Error Handling**: Demonstrates graceful validation failure

To run:
```bash
javac *.java
java Main
```

## 🧪 Testing Your Own Notifications

```java
public class TestYourOwn {
    public static void main(String[] args) {
        Logger logger = new DualLogger(
            new FileLogger("my-test.log"),
            new ConsoleLogger()
        );
        
        NotificationManager mgr = new NotificationManager(logger);
        
        // Test your notifications here
        EmailNotification testEmail = new EmailNotification(
            logger, "You", "you@test.com", "friend@test.com", "Hello!"
        );
        
        mgr.addNotification(testEmail);
        mgr.sendAllMessages();
        mgr.printStats();
    }
}
```

## 📝 Notes

- **File Path**: Update `FileLogger` path in `Main.java` for your system
- **Phone Format**: Philippine format (11 digits) used in SMS validation
- **Thread Safety**: Basic retry uses `Thread.sleep()`
- **Extensibility**: Easy to add new notification types or loggers

## 📈 Potential Enhancements

1. **Database Integration**: Store notifications persistently
2. **Priority System**: High/Medium/Low priority notifications
3. **Scheduling**: Send at specific times
4. **Rate Limiting**: Prevent too many notifications
5. **Template System**: Reusable message templates
6. **Web Interface**: REST API for remote management
7. **Multiple Languages**: Localized notification messages
8. **Attachment Support**: For email notifications

## 🤝 Contributing

Feel free to fork and extend this system! Some ideas:
- Add Slack/Teams notifications
- Implement database logging
- Create a GUI interface
- Add unit tests
- Implement notification templates

## 📄 License

This project is open source and available for educational use.

---

**Author**: Kyle Delfin, aldrinkyles@1219@gmail.com
**Project Type**: Educational - OOP Design Patterns Demonstration  
**Focus**: Clean architecture, design patterns, robust error handling

*"Good code is its own best documentation."*
