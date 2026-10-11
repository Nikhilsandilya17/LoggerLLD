package sink;

import enums.MessageLevel;
import formatter.LogFormatter;
import models.Message;

public class ConsoleSink implements Sink {
    private final MessageLevel threshold;
    private final LogFormatter formatter;

    public ConsoleSink(MessageLevel threshold, LogFormatter formatter) {
        this.threshold = threshold;
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
