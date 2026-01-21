
public class DualLogger implements Logger {
    private final FileLogger fileLogger;
    private final ConsoleLogger consoleLogger;

    DualLogger(FileLogger fileLogger, ConsoleLogger consoleLogger){

        if (fileLogger == null || consoleLogger == null) {
            throw new IllegalArgumentException("Loggers cannot be null");
        }

        this.fileLogger = fileLogger;
        this.consoleLogger = consoleLogger;
    }


    @Override
    public void info(String message) {
       fileLogger.info(message);
       consoleLogger.info(message);
    }

    @Override
    public void error(String message) {
       fileLogger.error(message);
       consoleLogger.error(message);
    }

    @Override
    public void debug(String message) {
        fileLogger.debug(message);
        consoleLogger.debug(message);
    }

    @Override
    public void warn(String message) {
        fileLogger.warn(message);
        consoleLogger.warn(message);
    }

}
