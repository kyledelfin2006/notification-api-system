package kyle.com;
public interface Logger {
    void info(String message);
    void error(String message);
    void debug(String message);
    void warn(String message);

}