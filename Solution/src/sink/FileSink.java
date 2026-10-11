package sink;

import enums.MessageLevel;
import formatter.LogFormatter;
import models.Message;

public class FileSink implements Sink {

    private final String fileName;
    private final MessageLevel threshold;
    private final LogFormatter formatter;

    public FileSink(MessageLevel threshold, LogFormatter formatter) {
        this.threshold = threshold;
        this.fileName = "log.txt";
        this.formatter = formatter;
    }

    @Override
    public void log(Message message) {
        if (message.getMessageLevel().ordinal() < threshold.ordinal()) {
            return;
        }
        System.out.println(formatter.format(message));
    }

}
