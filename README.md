# notification-dispatch-system

![Java](https://img.shields.io/badge/Java-17+-orange?logo=openjdk)
![Status](https://img.shields.io/badge/status-active-success)
![License](https://img.shields.io/badge/license-MIT-blue)

A robust console-based notification dispatch system implementing OOP principles, demonstrating polymorphism, abstraction, and retry mechanisms for reliable message delivery.

## ✨ Features

### Core Functionality
- ✅ Multi-channel notifications (Email, SMS, Push)
- ✅ Automatic retry mechanism with configurable attempts
- ✅ Comprehensive logging system
- ✅ Delivery status tracking and statistics
- ✅ Input validation with detailed error messages
- ✅ Graceful error handling

### Technical Highlights
- **Template Method Pattern**: Common notification workflow in abstract base class
- **Strategy Pattern**: Logger interface for flexible logging implementations
- **Polymorphism**: Unified interface for different notification types
- **Retry Logic**: Automatic retry with exponential backoff (3 attempts, 1000ms delay)
- **Status Management**: Real-time tracking (PENDING → SENT/FAILED)

## 🚀 Quick Start

### Prerequisites
- Java 17 or higher
- No external dependencies required

### Installation
```bash
# Clone the repository
git clone https://github.com/kyledelfin2006/notification-dispatch-system.git
cd notification-dispatch-system

# Compile
javac *.java

# Run
java Main
```

## 📖 Usage

### Creating Notifications

```java
Logger logger = new ConsoleLogger();
NotificationManager manager = new NotificationManager(logger);

// Email Notification
EmailNotification email = new EmailNotification(
    logger,
    "Kyle Delfin",
    "kyle@example.com",
    "boss@example.com",
    "Meeting at 3 PM"
);

// SMS Notification
SMSNotification sms = new SMSNotification(
    logger,
    "John Doe",
    "09123456789",
    "Reminder: Doctor's appointment",
    "09987654321"
);

// Push Notification
PushNotification push = new PushNotification(
    logger,
    "Netflix",
    "device-token-123",
    "New episode available!"
);

// Add and send
manager.addNotification(email);
manager.addNotification(sms);
manager.addNotification(push);
manager.sendAllMessages();
manager.printStats();
```

## 📸 Sample Output

### Successful Delivery
```
🚀 STARTING NOTIFICATION SYSTEM DEMO 🚀

=== SCENARIO 1: Happy Path ===
[INFO] Processing: Notification#0 [PENDING]
[INFO] Adding of Notification#0 [PENDING] complete.
[INFO] === Starting notification process ===
[INFO] Notification # 0 [PENDING]
[INFO] ✓ Validating EmailNotification...
[INFO] Sending Email Notification: to Boss123@Yahoo.com:Hello!
[INFO] Email from KyleDelfin@Gmail.com to Boss123@Yahoo.com Message: Hello!
[INFO] ✓ Logging delivery of notification
[INFO] Notification status: SENT
[INFO] === Notification process complete ===
[INFO]  ✓ Notification delivery successful: Notification#0 [SENT]
```

### Error Handling
```
=== SCENARIO 2: Error Handling ===
[ERROR] Phone number must be exactly 11 digits
[INFO] ✓ Gracefully caught: null
```

### Statistics
```
[INFO] Successful messages : 3
[INFO] Failed: 0
[INFO] Total: 3
[INFO] Success Rate: 100.0%
```

## 🏗️ Architecture

```
┌──────────────┐
│  Main.java   │  ← Application Entry Point
└──────┬───────┘
       │
┌──────▼──────────────────┐
│ NotificationManager.java│  ← Orchestration Layer
└──────┬──────────────────┘
       │
┌──────▼───────────────┐
│  Notification.java   │  ← Abstract Base Class (Template Method)
└──────┬───────────────┘
       │
       ├──────────────────┬──────────────────┬─────────────────┐
       │                  │                  │                 │
┌──────▼─────────┐ ┌─────▼────────┐ ┌──────▼─────────┐      │
│ Email          │ │ SMS          │ │ Push           │      │
│ Notification   │ │ Notification │ │ Notification   │      │
└────────────────┘ └──────────────┘ └────────────────┘      │
                                                             │
       ┌─────────────────────────────────────────────────────┘
       │
┌──────▼──────────┐          ┌────────────────┐
│ Sendable.java   │          │ Logger.java    │
│ (Interface)     │          │ (Interface)    │
└─────────────────┘          └────────┬───────┘
                                      │
                              ┌───────▼──────────┐
                              │ ConsoleLogger    │
                              └──────────────────┘
```

### Design Patterns Used
- **Template Method**: `Notification.process()` defines workflow, subclasses implement details
- **Strategy Pattern**: `Logger` interface allows swappable logging implementations
- **Factory-like**: `NotificationManager` handles creation and lifecycle management
- **Enum State Pattern**: `NotificationStatus` for tracking delivery states

## 🔍 Code Quality

- ✅ SOLID principles adherence
- ✅ Comprehensive exception handling
- ✅ Multi-layer validation (field, phone number format)
- ✅ Protected template method pattern
- ✅ Thread-safe retry mechanism
- ✅ Proper encapsulation and access modifiers
- ✅ Clean separation of concerns

## 🛠️ Technical Details

### Notification Class (Abstract)
- Template method `process()` with retry logic
- Protected validation methods
- Status management (PENDING → SENT/FAILED)
- Configurable retry attempts (3) and delay (1000ms)
- Auto-incrementing notification IDs

### Notification Types

**EmailNotification**
- Validates sender and receiver email fields
- Tracks sender/receiver email addresses
- Custom display format

**SMSNotification**
- Validates phone numbers (exactly 11 digits)
- Regex pattern matching for format validation
- Tracks sender/receiver phone numbers

**PushNotification**
- Validates device tokens
- Lightweight notification type
- Device-specific targeting

### NotificationManager
- Centralized notification orchestration
- Success/failure tracking
- Statistics calculation with success rate
- Null-safe operations
- Detailed logging for all operations

### Logger Interface
- Flexible logging implementation
- Four log levels: INFO, ERROR, DEBUG, WARN
- Easy to extend (FileLogger, NetworkLogger, etc.)

## 💡 What I Learned

This project demonstrates:
- Advanced OOP concepts (abstraction, polymorphism, inheritance)
- Design patterns in real-world scenarios
- Retry mechanisms and resilient systems
- Status state machines
- Clean architecture principles
- Defensive programming with validation
- Professional error handling

## 🚧 Future Enhancements

### High Priority
- [ ] Add `FileLogger` implementation for persistent logs
- [ ] Implement notification scheduling (delayed delivery)
- [ ] Add notification priority levels (HIGH, MEDIUM, LOW)
- [ ] Create notification templates for common messages

### Medium Priority
- [ ] Add notification history/audit trail
- [ ] Implement batch notification processing
- [ ] Add configurable retry strategies (exponential backoff)
- [ ] Create notification filtering and querying
- [ ] Add email/SMS delivery confirmation callbacks

### Nice to Have
- [ ] GUI interface (JavaFX) with real-time status updates
- [ ] Database persistence (SQLite/PostgreSQL)
- [ ] RESTful API for remote notification dispatch
- [ ] Webhook support for external integrations
- [ ] Multi-threaded notification processing
- [ ] Rate limiting for API-based notifications
- [ ] Notification analytics dashboard

### Code Polishing
- [ ] Add comprehensive unit tests (JUnit 5)
- [ ] Implement builder pattern for notification creation
- [ ] Add JavaDoc documentation
- [ ] Create configuration file for retry/delay settings
- [ ] Add more granular exception types
- [ ] Implement observer pattern for status changes

## 📝 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🤝 Contributing

Feedback and contributions are welcome!

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit changes (`git commit -m 'Add amazing feature'`)
4. Push to branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📬 Contact

Kyle Delfin - [@kyledelfin2006](https://github.com/kyledelfin2006)

Project Link: [https://github.com/kyledelfin2006/notification-dispatch-system](https://github.com/kyledelfin2006/notification-dispatch-system)

---

⭐ Star this repository if you found it helpful!
