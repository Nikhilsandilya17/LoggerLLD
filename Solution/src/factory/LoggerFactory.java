package factory;

import enums.MessageLevel;
import models.Logger;
import sink.FileSink;
import sink.Sink;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LoggerFactory {
    private final Map<String, Logger> loggers = new ConcurrentHashMap<>();

    public Logger getLogger(String serviceName, List<Sink> sinks) {
        return loggers.computeIfAbsent(serviceName,
                name -> new Logger(serviceName, MessageLevel.DEBUG, sinks));

    }
}
