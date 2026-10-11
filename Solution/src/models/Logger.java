package models;

import enums.MessageLevel;
import sink.Sink;

import java.util.List;

public class Logger {
    private final String name;
    private final MessageLevel loggerThreshold;
    private final List<Sink> observers;

    public Logger(String name, MessageLevel loggerThreshold, List<Sink> sinks) {
        this.name = name;
        this.loggerThreshold = loggerThreshold;
        this.observers = sinks;
    }

    public String getName() {
        return name;
    }

    public MessageLevel getLoggerThreshold() {
        return loggerThreshold;
    }

    public List<Sink> getSinks() {
        return observers;
    }

    public void error(String content){
        notifyObservers(content, MessageLevel.ERROR);
    }

    public void info(String content){
        notifyObservers(content, MessageLevel.INFO);
    }
    public void debug(String content){
        notifyObservers(content, MessageLevel.DEBUG);
    }
    public void warn(String content){
        notifyObservers(content, MessageLevel.WARN);
    }

    private void notifyObservers(String content, MessageLevel messageLevel) {
        Message message = new Message(messageLevel, content, this.name);
        if(messageLevel.ordinal() >= loggerThreshold.ordinal()) {
            for (Sink observer : observers) {
                observer.log(message);
            }
        }
    }
}
