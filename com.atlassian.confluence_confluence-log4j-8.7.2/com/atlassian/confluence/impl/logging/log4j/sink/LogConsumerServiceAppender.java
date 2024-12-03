/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.logging.sink.LogEvent
 *  com.atlassian.confluence.logging.sink.LogEvent$Level
 *  org.apache.logging.log4j.core.LogEvent
 *  org.apache.logging.log4j.core.appender.AbstractAppender
 *  org.apache.logging.log4j.spi.StandardLevel
 */
package com.atlassian.confluence.impl.logging.log4j.sink;

import com.atlassian.confluence.logging.sink.LogEvent;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.spi.StandardLevel;

final class LogConsumerServiceAppender
extends AbstractAppender {
    private final Map<String, Consumer<com.atlassian.confluence.logging.sink.LogEvent>> consumers = new ConcurrentHashMap<String, Consumer<com.atlassian.confluence.logging.sink.LogEvent>>();

    LogConsumerServiceAppender(String name) {
        super(name, null, null, false, null);
    }

    void addConsumer(String key, Consumer<com.atlassian.confluence.logging.sink.LogEvent> consumer) {
        this.consumers.put(key, consumer);
    }

    void removeConsumer(String key) {
        this.consumers.remove(key);
    }

    public void append(LogEvent event) {
        this.consumers.forEach((key, consumer) -> consumer.accept(new Event(Instant.ofEpochMilli(event.getInstant().getEpochMillisecond()), event.getMessage().getFormattedMessage(), event.getThrown(), Event.level(event.getLevel().getStandardLevel()), event.getLoggerName(), event.getThreadName(), event.getContextData().toMap())));
    }

    static final class Event
    implements com.atlassian.confluence.logging.sink.LogEvent {
        private final Instant instant;
        private final String message;
        private final Throwable thrown;
        private final LogEvent.Level level;
        private final String loggerName;
        private final String threadName;
        private final Map<String, String> threadContext;

        private Event(Instant instant, String message, Throwable thrown, LogEvent.Level level, String loggerName, String threadName, Map<String, String> threadContext) {
            this.instant = instant;
            this.message = message;
            this.thrown = thrown;
            this.level = level;
            this.loggerName = loggerName;
            this.threadName = threadName;
            this.threadContext = Map.copyOf(threadContext);
        }

        public Instant getTimestamp() {
            return this.instant;
        }

        public String getMessage() {
            return this.message;
        }

        public Optional<Throwable> getThrowable() {
            return Optional.ofNullable(this.thrown);
        }

        public LogEvent.Level getLevel() {
            return this.level;
        }

        public String getLoggerName() {
            return this.loggerName;
        }

        public Map<String, String> getThreadContext() {
            return this.threadContext;
        }

        public String getThreadName() {
            return this.threadName;
        }

        private static LogEvent.Level level(StandardLevel level) {
            switch (level) {
                case DEBUG: {
                    return LogEvent.Level.DEBUG;
                }
                case INFO: {
                    return LogEvent.Level.INFO;
                }
                case WARN: {
                    return LogEvent.Level.WARN;
                }
                case ERROR: {
                    return LogEvent.Level.ERROR;
                }
            }
            return null;
        }
    }
}

