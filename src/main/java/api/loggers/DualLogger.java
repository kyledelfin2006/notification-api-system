package api.loggers;

public class DualLogger implements Logger {
    private final Logger firstLogger;
    private final Logger secondLogger;

    public DualLogger(Logger firstLogger, Logger secondLogger){ // EXAMPLE OF COMPOSITION

        if (firstLogger == null || secondLogger == null) {
            throw new IllegalArgumentException("Loggers cannot be null");
        }

        this.firstLogger = firstLogger;
        this.secondLogger = secondLogger;
    }


    @Override
    public void info(String message) {
       firstLogger.info(message);
       secondLogger.info(message);
    }

    @Override
    public void error(String message) {
       firstLogger.error(message);
       secondLogger.error(message);
    }

    @Override
    public void debug(String message) {
        firstLogger.debug(message);
        secondLogger.debug(message);
    }

    @Override
    public void warn(String message) {
        firstLogger.warn(message);
        secondLogger.warn(message);
    }

}

