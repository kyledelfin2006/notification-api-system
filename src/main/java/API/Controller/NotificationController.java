package API.Controller;

import API.DTO.EmailNotifDTO;
import API.DTO.PushNotifDTO;
import API.DTO.SMSNotifDTO;
import API.DTO.SystemNotifDTO;
import API.Logger.ConsoleLogger;
import API.Logger.DualLogger;
import API.Logger.FileLogger;
import API.Logger.Logger;
import API.Model.*;
import API.Repository.NotificationRepository;
import API.Responses.ApiResponse;
import API.Responses.ErrorResponse;
import API.Service.NotificationManager;
import API.Service.NotificationService;
import API.Storage.NotificationStorage;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.util.ArrayList;

import static spark.Spark.*;


public class NotificationController {
    public static void main(String[] args) {


        port(8082);

        // Dependencies
        Logger logger = new DualLogger(new FileLogger("LoggingFile.txt"),new ConsoleLogger());
        NotificationRepository repository = new NotificationRepository(new ArrayList<>());
        NotificationService service = new NotificationService(repository);
        NotificationStorage storage = new NotificationStorage(logger,"notificationStorage.txt");
        NotificationManager manager = new NotificationManager(logger,repository,storage,service);
        ObjectMapper mapper = new ObjectMapper();

        // JSON Settings
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        printStartupMessage();


        get("/api/health", (req, res) -> {
            res.type("application/json");
            return mapper.writeValueAsString(new ApiResponse<>(true, " NOTIFICATION API is running"));
        });

        get("/api/notifications", (req, res) -> {

            res.type("application/json");
            return mapper.writeValueAsString(manager.getNotifications());

        });

        delete("/api/notifications", (req, res) -> {
            try {


                manager.clearAllNotifications();

                res.type("application/json");
                return mapper.writeValueAsString(new ApiResponse<>(true, " All notifications deleted successfully"));

            } catch (IllegalArgumentException e) {
                res.status(400);
                res.type("application/json");
                return mapper.writeValueAsString(new ErrorResponse("Validation failed", e.getMessage(), 400));
            } catch (Exception e) {
                res.status(500);
                res.type("application/json");
                return mapper.writeValueAsString(new ErrorResponse("Processing Error", e.getMessage(), 500));
            }
        });

        get("/api/notifications/sent", (req, res) -> {
            try {

                res.type("application/json");
                return mapper.writeValueAsString(new ApiResponse<>(true, "Sent count retrieved",service.getSentNotificationCount()));


            } catch (IllegalArgumentException e) {
                res.status(400);
                res.type("application/json");
                return mapper.writeValueAsString(new ErrorResponse("Validation failed", e.getMessage(), 400));
            } catch (Exception e) {
                res.status(500);
                res.type("application/json");
                return mapper.writeValueAsString(new ErrorResponse("Processing Error", e.getMessage(), 500));
            }
        });

        get("/api/notifications/pending", (req, res) -> {
            try {

                res.type("application/json");
                return mapper.writeValueAsString(new ApiResponse<>(true, "Pending count retrieved",service.getPendingNotifications()));

            } catch (IllegalArgumentException e) {
                res.status(400);
                res.type("application/json");
                return mapper.writeValueAsString(new ErrorResponse("Validation failed", e.getMessage(), 400));
            } catch (Exception e) {
                res.status(500);
                res.type("application/json");
                return mapper.writeValueAsString(new ErrorResponse("Processing Error", e.getMessage(), 500));
            }
        });

        get("/api/notifications/failed", (req, res) -> {
            try {

                res.type("application/json");
                return mapper.writeValueAsString(new ApiResponse<>(true, " Failed count retrieved",service.getFailedNotifications()));

            } catch (IllegalArgumentException e) {
                res.status(400);
                res.type("application/json");
                return mapper.writeValueAsString(new ErrorResponse("Validation failed", e.getMessage(), 400));
            } catch (Exception e) {
                res.status(500);
                res.type("application/json");
                return mapper.writeValueAsString(new ErrorResponse("Processing Error", e.getMessage(), 500));
            }
        });

        get("/api/notifications/stats", (req, res) -> {
            try {

                res.type("application/json");
                return mapper.writeValueAsString(manager.getDeliveryStatistics());

            } catch (IllegalArgumentException e) {
                res.status(400);
                res.type("application/json");
                return mapper.writeValueAsString(new ErrorResponse("Validation failed", e.getMessage(), 400));
            } catch (Exception e) {
                res.status(500);
                res.type("application/json");
                return mapper.writeValueAsString(new ErrorResponse("Processing Error", e.getMessage(), 500));
            }
        });

        get("/api/notifications/:id", (req, res) -> {

            String id = req.params(":id");

            if (id == null) {
                res.status(400);
                return mapper.writeValueAsString(new ErrorResponse("Missing ID parameter", 400));
            }

            try {

                Notification notification = manager.getNotificationById(Integer.parseInt(id));

                if (notification == null) {
                    res.status(404);
                    return mapper.writeValueAsString(new ErrorResponse("Notification not found", 404));
                }

                res.type("application/json");
                return mapper.writeValueAsString(notification);

            } catch (NumberFormatException e) {
                res.status(400);
                return mapper.writeValueAsString(new ErrorResponse("Invalid ID format. Must be a number.", 400));
            } catch (Exception e) {
                res.status(500);
                res.type("application/json");
                return mapper.writeValueAsString(new ErrorResponse("Processing Error", e.getMessage(), 500));
            }

        });

        post("/api/notifications/email", (req, res) -> {
            try {
                EmailNotifDTO input = mapper.readValue(req.body(), EmailNotifDTO.class);
                Notification notification = new EmailNotification(
                        logger,
                        input.getSender(),
                        input.getSenderEmail(),
                        input.getReceiverEmail(),
                        input.getMessage()
                );

                manager.addNotification(notification);

                res.type("application/json");
                res.status(201);

                return mapper.writeValueAsString(new ApiResponse<>(true, "Email Notification Added Successfully"));

            } catch (IllegalArgumentException e) {
                res.status(400);
                return mapper.writeValueAsString(new ErrorResponse("Validation failed", e.getMessage(), 400));
            } catch (Exception e) {
                res.status(500);
                res.type("application/json");
                return mapper.writeValueAsString(new ErrorResponse("Processing Error", e.getMessage(), 500));
            }
        });

        post("/api/notifications/sms", (req, res) -> {
            try {

                SMSNotifDTO input = mapper.readValue(req.body(), SMSNotifDTO.class);
                Notification notification = new SMSNotification(
                        logger,
                        input.getSender(),
                        input.getSenderPhoneNumber(),
                        input.getReceiverPhoneNumber(),
                        input.getMessage()
                );

                manager.addNotification(notification);

                res.type("application/json");
                res.status(201);

                return mapper.writeValueAsString(new ApiResponse<>(true, "SMS Notification Added Successfully"));

            } catch (IllegalArgumentException e) {
                res.status(400);
                return mapper.writeValueAsString(new ErrorResponse("Validation failed", e.getMessage(), 400));
            } catch (Exception e) {
                res.status(500);
                res.type("application/json");
                return mapper.writeValueAsString(new ErrorResponse("Processing Error", e.getMessage(), 500));
            }
        });

        post("/api/notifications/push", (req, res) -> {
            try {

                PushNotifDTO input = mapper.readValue(req.body(), PushNotifDTO.class);
                Notification notification = new PushNotification(
                        logger,
                        input.getSender(),
                        input.getDeviceToken(),
                        input.getMessage()
                );

                manager.addNotification(notification);

                res.type("application/json");
                res.status(201);

                return mapper.writeValueAsString(new ApiResponse<>(true, "Push Notification Added Successfully"));

            } catch (IllegalArgumentException e) {
                res.status(400);
                return mapper.writeValueAsString(new ErrorResponse("Validation failed", e.getMessage(), 400));
            } catch (Exception e) {
                res.status(500);
                res.type("application/json");
                return mapper.writeValueAsString(new ErrorResponse("Processing Error", e.getMessage(), 500));
            }
        });

        post("/api/notifications/system", (req, res) -> {
            try {

                SystemNotifDTO input = mapper.readValue(req.body(), SystemNotifDTO.class);
                Notification notification = new SystemNotification(
                        logger,
                        input.getSender(),
                        input.getDeviceOS(),
                        input.getDeviceToken(),
                        input.getMessage()
                );

                manager.addNotification(notification);

                res.type("application/json");
                res.status(201);

                return mapper.writeValueAsString(new ApiResponse<>(true, "System Notification Added Successfully"));

            } catch (IllegalArgumentException e) {
                res.status(400);
                return mapper.writeValueAsString(new ErrorResponse("Validation failed", e.getMessage(), 400));
            } catch (Exception e) {
                res.status(500);
                res.type("application/json");
                return mapper.writeValueAsString(new ErrorResponse("Processing Error", e.getMessage(), 500));
            }
        });

        post("/api/notifications/:id/send", (req, res) -> {
            String id = req.params(":id");

            if (id == null) {
                res.status(400);
                res.type("application/json");
                return mapper.writeValueAsString(new ErrorResponse("Missing ID parameter", 400));
            }

            try {
                Notification notification = manager.getNotificationById(Integer.parseInt(id));

                if (notification == null) {
                    res.status(404);
                    res.type("application/json");
                    return mapper.writeValueAsString(new ErrorResponse("Notification not found", 404));
                }

                manager.sendMessage(notification);

                res.type("application/json");  // ← Added content type
                return mapper.writeValueAsString(new ApiResponse<>(true, "Notification sent successfully", notification));

            } catch (IllegalArgumentException e) {
                res.status(400);
                res.type("application/json");
                return mapper.writeValueAsString(new ErrorResponse("Validation failed", e.getMessage(), 400));
            } catch (Exception e) {
                res.status(500);
                res.type("application/json");
                return mapper.writeValueAsString(new ErrorResponse("Processing Error", e.getMessage(), 500));
            }
        });

        post("/api/notifications/send-all", (req, res) -> {

            try {

                manager.sendAllMessages();

                res.type("application/json");  // ← Added content type
                return mapper.writeValueAsString(new ApiResponse<>(true, "Notification all sent successfully"));

            } catch (IllegalArgumentException e) {
                res.status(400);
                res.type("application/json");
                return mapper.writeValueAsString(new ErrorResponse("Validation failed", e.getMessage(), 400));
            } catch (Exception e) {
                res.status(500);
                res.type("application/json");
                return mapper.writeValueAsString(new ErrorResponse("Processing Error", e.getMessage(), 500));
            }
        });

        delete("/api/notifications/:id", (req, res) -> {
            String id = req.params(":id");

            if (id == null) {
                res.status(400);
                res.type("application/json");
                return mapper.writeValueAsString(new ErrorResponse("Missing ID parameter", 400));
            }

            try {
                Notification notification = manager.getNotificationById(Integer.parseInt(id));

                if (notification == null) {
                    res.status(404);
                    res.type("application/json");
                    return mapper.writeValueAsString(new ErrorResponse("Notification not found", 404));
                }

                manager.deleteNotification(notification);

                res.type("application/json");
                return mapper.writeValueAsString(new ApiResponse<>(true, "Notification deleted successfully", notification));

            } catch (IllegalArgumentException e) {
                res.status(400);
                res.type("application/json");
                return mapper.writeValueAsString(new ErrorResponse("Validation failed", e.getMessage(), 400));
            } catch (Exception e) {
                res.status(500);
                res.type("application/json");
                return mapper.writeValueAsString(new ErrorResponse("Processing Error", e.getMessage(), 500));
            }
        });



    }

    private static void printStartupMessage() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("  NOTIFICATION API IS RUNNING ");
        System.out.println("=".repeat(60));
        System.out.println(" URL: http://localhost:8082");
        System.out.println("\n  AVAILABLE ENDPOINTS:");
        System.out.println(" ┌─────────────────────────────────────────────────────────────┐");
        System.out.println(" │ GET    /api/health                                          │");
        System.out.println(" │ GET    /api/notifications                                  │");
        System.out.println(" │ GET    /api/notifications/:id                              │");
        System.out.println(" │ POST   /api/notifications/email                            │");
        System.out.println(" │ POST   /api/notifications/sms                              │");
        System.out.println(" │ POST   /api/notifications/push                             │");
        System.out.println(" │ POST   /api/notifications/system                           │");
        System.out.println(" │ POST   /api/notifications/:id/send                         │");
        System.out.println(" │ POST   /api/notifications/send-all                         │");
        System.out.println(" │ DELETE /api/notifications/:id                              │");
        System.out.println(" │ DELETE /api/notifications                                  │");
        System.out.println(" │ GET   /api/notifications/sent                              │");
        System.out.println(" │ GET    /api/notifications/pending                          │");
        System.out.println(" │ GET    /api/notifications/failed                           │");
        System.out.println(" └────────────────────────────────────────────────────────────┘");
        System.out.println("\n TIP: Use Postman or curl to test the API");
        System.out.println("=".repeat(60) + "\n");
    }

}
