package formatter;

import models.Message;

public class DefaultFormatter implements LogFormatter {
    @Override
    public String format(Message message) {
        return message.getTimestamp() + " [" + message.getMessageLevel() + "] ["
                + message.getServiceName() + "] " + message.getContent();
    }
}
