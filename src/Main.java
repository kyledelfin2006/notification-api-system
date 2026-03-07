public class Main {
    public static void main(String[] args) throws InterruptedException {

        Logger logger = new DualLogger(new FileLogger("C:\\Users\\User\\Desktop\\random\\Logger.txt"),new ConsoleLogger());
        NotificationManager manager = new NotificationManager(logger);

        // STORE them in variables so we can reference them later

        EmailNotification email = new EmailNotification(logger,"Kyle Delfin", "KyleDelfin@Gmail.com", "Boss123@Yahoo.com","Hello!");
        PushNotification push = new PushNotification(logger,"Netflix","device123", "Payment Accepted!");
        SMSNotification sms = new SMSNotification(logger,"Kobe Bryant", "09123456789","hello!", "09476384433");




        logger.info("🚀 STARTING NOTIFICATION SYSTEM DEMO 🚀");
        Thread.sleep(1000);

        logger.info("\n=== SCENARIO 1: Happy Path ===");
        manager.addNotification(email);
        manager.addNotification(push);
        manager.addNotification(sms);

        manager.sendAllMessages();
        manager.printStats();


        logger.info("\n=== SCENARIO 2: Error Handling ===");
        try {
            SMSNotification invalidSMS = new SMSNotification(
                    logger, "Test", "123", "Invalid!", "456"  // Wrong phone format
            );
            manager.addNotification(invalidSMS);
            manager.sendAllMessages();
        } catch (IllegalArgumentException e) {
            logger.info("✓ Gracefully caught: " + e.getMessage());
        }










    }
}