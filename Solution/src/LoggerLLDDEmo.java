import enums.MessageLevel;
import factory.LoggerFactory;
import formatter.DefaultFormatter;
import formatter.LogFormatter;
import models.Logger;
import sink.ConsoleSink;
import sink.FileSink;

import java.util.ArrayList;
import java.util.List;

public class LoggerLLDDEmo {
    public static void main(String[] args) {
        System.out.println("LoggerLLDDEmo");
        LogFormatter formatter = new DefaultFormatter();
        LoggerFactory loggerFactory = new LoggerFactory();
        Logger logger = loggerFactory.getLogger("PaymentService", new ArrayList<>(List.of(
                new FileSink(MessageLevel.DEBUG, formatter),
                new ConsoleSink(MessageLevel.ERROR, formatter))));
        logger.info("This is a info message");
        logger.debug("This is a debug message");
        logger.warn("This is a warn message");
        logger.error("This is a error message");

    }
}
