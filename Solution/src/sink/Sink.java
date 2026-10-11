package sink;

import models.Message;

public interface Sink {
    void log(Message message);
}
