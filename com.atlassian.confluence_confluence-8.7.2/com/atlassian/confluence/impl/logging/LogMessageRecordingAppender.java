/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 */
package com.atlassian.confluence.impl.logging;

import com.atlassian.confluence.logging.sink.LogConsumerService;
import com.atlassian.confluence.logging.sink.LogEvent;
import java.util.Collection;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

public final class LogMessageRecordingAppender {
    private static final int CAPACITY = 10;
    private static final Queue<String> logMessages = new LinkedBlockingQueue<String>(10);
    public static final String ENABLED_SYSPROP = "LogMessageRecordingAppender.enabled";
    private final LogConsumerService logConsumerService;

    public LogMessageRecordingAppender(LogConsumerService logConsumerService) {
        this.logConsumerService = logConsumerService;
    }

    @PostConstruct
    void register() {
        if (LogMessageRecordingAppender.isEnabled()) {
            this.logConsumerService.registerLogConsumer(this.getClass().getName(), this::log);
        }
    }

    @PreDestroy
    void unregister() {
        this.logConsumerService.unregisterLogConsumer(this.getClass().getName());
    }

    private static boolean isEnabled() {
        return Boolean.getBoolean(ENABLED_SYSPROP);
    }

    private void log(LogEvent event) {
        if (event.getLevel() == LogEvent.Level.ERROR && LogMessageRecordingAppender.isEnabled()) {
            logMessages.offer(String.format("[%s] %s: %s", event.getThreadName(), event.getLoggerName(), event.getMessage()));
        }
    }

    public Collection<String> getRecordedMessages() {
        return List.copyOf(logMessages);
    }

    public void clearRecordedMessages() {
        logMessages.clear();
    }
}

