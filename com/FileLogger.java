package kyle.com;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FileLogger implements Logger {

    private final String filePath;

    public FileLogger(String filepath){
        this.filePath = filepath;
    }

    protected void writeLog(String level, String message){

        DateTimeFormatter formattedDateOfCreation = DateTimeFormatter.ofPattern("EEE, MMM dd, yyyy HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        String timestamp = now.format(formattedDateOfCreation);

        try(BufferedWriter writer = new BufferedWriter(
            new FileWriter(this.filePath,true))) {

            writer.write(timestamp + " | " + level + " | " + message + "\n");
            writer.flush();

        } catch (IOException e){
            System.out.println("Error writing to log file: " + e.getMessage());
       }
    }


    @Override
    public void info(String message) {
       writeLog("[INFO]", message);
            }

    @Override
    public void error(String message) {
        writeLog("[ERROR] ", message);
    }

    @Override
    public void debug(String message) {
        writeLog("[DEBUG]", message);
    }

    @Override
    public void warn(String message) {
        writeLog("[WARNING] ", message);
            }


}
