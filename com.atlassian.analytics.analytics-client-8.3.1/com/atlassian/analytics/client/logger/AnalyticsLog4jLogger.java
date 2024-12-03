/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.analytics.client.logger;

import com.atlassian.analytics.client.logger.AnalyticsLogger;
import com.atlassian.analytics.event.ProcessedEvent;
import com.atlassian.analytics.event.logging.LogEventFormatter;
import java.io.IOException;
import java.util.Objects;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.appender.RollingFileAppender;
import org.apache.logging.log4j.core.appender.rolling.RollingFileManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AnalyticsLog4jLogger
implements AnalyticsLogger {
    private static final Logger LOG = LoggerFactory.getLogger(AnalyticsLog4jLogger.class);
    private final LogEventFormatter logEventFormatter;
    private org.apache.logging.log4j.Logger analyticsLogger;

    public AnalyticsLog4jLogger(LogEventFormatter logEventFormatter) {
        Objects.requireNonNull(logEventFormatter, "The log formatter is mandatory to build the logger.");
        this.analyticsLogger = LogManager.getLogger("com.atlassian.analytics.client.btflogger");
        this.logEventFormatter = logEventFormatter;
    }

    @Override
    public void logEvent(ProcessedEvent event) {
        try {
            this.analyticsLogger.info(this.logEventFormatter.formatEvent(event));
        }
        catch (IOException exception) {
            LOG.debug("Couldn't log event information to file, failed to serialize the event properties.");
        }
    }

    @Override
    public void logCleanupDeletion(String deletionMessage) {
        this.analyticsLogger.debug("Deleted " + deletionMessage);
    }

    @Override
    public void reset() {
        Object rolling = ((org.apache.logging.log4j.core.Logger)this.analyticsLogger).getContext().getConfiguration().getAppender("rolling");
        ((RollingFileManager)((RollingFileAppender)rolling).getManager()).rollover();
    }
}

