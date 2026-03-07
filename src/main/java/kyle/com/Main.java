package kyle.com;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) throws Exception {

        // Dual Logger Composition
        Logger logger = new DualLogger(new FileLogger("Logger.txt"), new ConsoleLogger());

        // Notification kyle.com.Repository
        NotificationRepository repository = new NotificationRepository(new ArrayList<>());


        // Storage (Of NotificationStorage Variant)
        Storage<Notification> storage = new NotificationStorage(logger, "notifications.json");



        // Manager manages repository, logger, storage.
        NotificationManager manager = new NotificationManager(repository,logger,storage);

        logger.info("🚀 STARTING NOTIFICATION SYSTEM DEMO 🚀");

        // Create notifications (4 Sub Class)
        EmailNotification email = new EmailNotification(
                logger, "LeBron James",
                "LeBron James@Gmail.com",
                "Boss123@Yahoo.com",
                "Hello Kobe!"
        );

        SMSNotification sms = new SMSNotification(
                logger, "Kobe Bryant",
                "09123456789",
                "09476384433",
                "Hello LBJ!"
        );

        PushNotification push = new PushNotification(
                logger, "Netflix",
                "device123",
                "Payment Accepted!"
        );

        SystemNotification system = new SystemNotification(
                logger, "System",
                "Android",
                "device67",
                "Update available"
        );

        // Add them - this automatically saves to file
        manager.addNotification(email);
        manager.addNotification(sms);
        manager.addNotification(push);
        manager.addNotification(system);

        // Send them





        logger.info("✅ All notifications saved to notifications.json");

        // NOW: Run the program AGAIN - it will load existing notifications!
        // The second run will show you loading works
    }
}