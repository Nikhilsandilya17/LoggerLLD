package formatter;

import models.Message;

public interface LogFormatter {
    String format(Message message);
}
