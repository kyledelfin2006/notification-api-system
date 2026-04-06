package kyle.com;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class EmailNotificationTest {

    @Test
    void Constructor_ValidInputs_Success() {
        Logger logger = new ConsoleLogger();

        EmailNotification validEmail = new EmailNotification(logger,
                "Tester",
                "testsender@gmail.com",
                "testreceiver@gmail.com",
                "Test!");

        // Verify all critical fields exist
        assertNotNull(validEmail.getSender(), "Sender should not be null");
        assertNotNull(validEmail.getMessage(), "Message should not be null");
        assertNotNull(validEmail.getSenderEmail(), "Sender email should not be null");
        assertNotNull(validEmail.getReceiverEmail(), "Receiver email should not be null");

        }

    @Test
    void Constructor_NullSenderEmail_ThrowsException() {
        Logger logger = new ConsoleLogger();

        assertThrows(IllegalArgumentException.class, () -> {
            new EmailNotification(
                    logger,
                    "Sender",
                    "INVALID.EMAIL",
                    "receiver@gmail.com",
                    "Test 67"
            );
        });
    }

    @Test
    void constructor_EmptyReceiverEmail_ThrowsException() {
        Logger logger = new ConsoleLogger();

        assertThrows(IllegalArgumentException.class, () -> {
            new EmailNotification(
                    logger,
                    "Sender",
                    "INVALID.EMAIL",
                    "",
                    "Test 67"
            );
        });
    }

}
