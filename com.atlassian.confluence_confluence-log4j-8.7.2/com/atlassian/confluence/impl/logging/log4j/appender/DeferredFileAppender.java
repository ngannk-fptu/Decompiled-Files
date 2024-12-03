/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.impl.logging.LogAppenderController
 *  org.apache.log4j.Appender
 *  org.apache.log4j.AppenderSkeleton
 *  org.apache.log4j.Logger
 *  org.apache.log4j.spi.LoggingEvent
 */
package com.atlassian.confluence.impl.logging.log4j.appender;

import com.atlassian.confluence.impl.logging.LogAppenderController;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import org.apache.log4j.Appender;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;

final class DeferredFileAppender
extends AppenderSkeleton {
    private static final Logger log = Logger.getLogger(DeferredFileAppender.class);
    private final Function<File, Appender> fileAppenderFactory;
    private Appender currentAppender;
    private final Appender consoleAppender;
    private Appender fileAppender;
    private final List<LoggingEvent> logBuffer = Collections.synchronizedList(new ArrayList());
    private volatile boolean switchAttempted;

    public DeferredFileAppender(Appender consoleAppender, Function<File, Appender> fileAppenderFactory) {
        this.consoleAppender = Objects.requireNonNull(consoleAppender);
        this.fileAppenderFactory = Objects.requireNonNull(fileAppenderFactory);
        this.currentAppender = consoleAppender;
    }

    public void append(LoggingEvent event) {
        if (!this.switchAttempted) {
            this.logBuffer.add(event);
        }
        this.currentAppender.doAppend(event);
    }

    public void close() {
        log.debug((Object)"closing appender");
        this.consoleAppender.close();
        if (this.fileAppender != null) {
            this.fileAppender.close();
        }
    }

    public boolean requiresLayout() {
        return true;
    }

    void registerForLogDirectoryConfiguration() {
        LogAppenderController.registerLogDirectoryAware(logDirectory -> this.switchToFileAppender(logDirectory.toFile()));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void switchToFileAppender(File logDirectory) {
        this.switchAttempted = true;
        this.currentAppender = this.fileAppender = this.fileAppenderFactory.apply(logDirectory);
        List<LoggingEvent> list = this.logBuffer;
        synchronized (list) {
            for (LoggingEvent event : this.logBuffer) {
                this.currentAppender.doAppend(event);
            }
        }
        this.logBuffer.clear();
    }
}

