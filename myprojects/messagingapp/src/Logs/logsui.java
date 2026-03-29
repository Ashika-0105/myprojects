package Logs;

import java.util.logging.*;
import java.io.IOException;

public class logsui {
    private static final Logger logger = Logger.getLogger(logsui.class.getName());

    static {
        try {
            FileHandler fh = new FileHandler("chat_app.log", true);
            
            fh.setFormatter(new SimpleFormatter());
            
            logger.addHandler(fh);
        } catch (IOException e) {
            System.err.println("Could not setup logger to file: " + e.getMessage());
        }
    }

    public static void logError(String msg, Exception e) {
        logger.log(Level.WARNING, msg, e);
    }
}