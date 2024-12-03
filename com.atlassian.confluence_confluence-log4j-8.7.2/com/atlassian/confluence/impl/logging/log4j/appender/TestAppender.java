/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  org.apache.commons.collections.buffer.CircularFifoBuffer
 *  org.apache.log4j.WriterAppender
 *  org.apache.log4j.spi.LoggingEvent
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.logging.log4j.appender;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import javax.annotation.Nonnull;
import org.apache.commons.collections.buffer.CircularFifoBuffer;
import org.apache.log4j.WriterAppender;
import org.apache.log4j.spi.LoggingEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestAppender
extends WriterAppender {
    private static final Logger log = LoggerFactory.getLogger(TestAppender.class);
    private Set enabledLoggers = new HashSet();
    private Map messagesByLogger = new HashMap();
    private static final int BUFFER_MAX_ITEMS = 100;
    private static final AtomicReference<TestAppender> activatedInstance = new AtomicReference();

    public void enableLogger(String className) {
        this.enabledLoggers.add(className);
    }

    public void disableLogger(String className) {
        this.enabledLoggers.remove(className);
        this.messagesByLogger.remove(className);
    }

    @Nonnull
    public static TestAppender getInstance() {
        TestAppender instance = activatedInstance.get();
        if (instance == null) {
            throw new IllegalStateException("No instance of TestAppender has been configured and activated");
        }
        return instance;
    }

    public void activateOptions() {
        super.activateOptions();
        if (!activatedInstance.compareAndSet(null, this)) {
            log.warn("Multiple TestAppenders cannot be configured");
        }
    }

    public void close() {
        activatedInstance.set(null);
        super.close();
    }

    public void append(LoggingEvent loggingEvent) {
        String logger = loggingEvent.getLoggerName();
        if (!this.enabledLoggers.contains(logger)) {
            return;
        }
        if (!this.messagesByLogger.containsKey(logger)) {
            this.messagesByLogger.put(logger, new CircularFifoBuffer(100));
        }
        CircularFifoBuffer buffer = (CircularFifoBuffer)this.messagesByLogger.get(logger);
        buffer.add((Object)this.layout.format(loggingEvent));
    }

    public String getLeastRecentMessageForLogger(String loggerName) {
        CircularFifoBuffer buffer = (CircularFifoBuffer)this.messagesByLogger.get(loggerName);
        if (buffer == null) {
            return null;
        }
        if (buffer.isEmpty()) {
            return null;
        }
        Object o = buffer.get();
        String s = o.toString();
        buffer.remove();
        return s;
    }
}

