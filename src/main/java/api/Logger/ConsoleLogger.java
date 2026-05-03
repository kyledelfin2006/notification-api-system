package api.Logger;

public class ConsoleLogger implements Logger {


    @Override
    public void info(String message) {
        System.out.println("[INFO] " + message);
    }

    @Override
    public void error(String message) {
        System.out.println("[ERROR] " + message);
    }

    @Override
    public void debug(String message) {
        System.out.println("[DEBUG] " + message);
    }

    @Override
    public void warn(String message) {
        System.out.println("[WARNING] " + message);
    }
}