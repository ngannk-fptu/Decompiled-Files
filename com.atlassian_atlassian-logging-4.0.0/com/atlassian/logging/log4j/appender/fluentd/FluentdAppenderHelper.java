/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.logging.log4j.appender.fluentd;

import com.atlassian.logging.log4j.appender.fluentd.FluentdHttpSender;
import com.atlassian.logging.log4j.appender.fluentd.FluentdLogQueueSendTask;
import com.atlassian.logging.log4j.appender.fluentd.FluentdSender;
import com.atlassian.logging.log4j.appender.fluentd.FluentdStdOutSender;
import com.atlassian.logging.log4j.appender.fluentd.LoggingEventQueue;
import java.io.Serializable;
import java.util.Timer;
import java.util.function.Function;

public class FluentdAppenderHelper<T> {
    private static final int DEFAULT_BATCH_PERIOD_MS = 1000;
    public static final int DEFAULT_MAX_NUM_LOG_EVENTS = 2000;
    private static final int DEFAULT_MAX_RETRY_PERIOD_MS = 14400000;
    private static final int DEFAULT_BACKOFF_MULTIPLIER = 10;
    private static final int DEFAULT_MAX_BACKOFF_MINUTES = 10;
    private static final String SYS_PROP_ENABLE_KEY = "atlassian.logging.cloud.enabled";
    private static final String SYS_PROP_STD_OUT_MODE = "atlassian.logging.cloud.stdOutMode";
    private long batchPeriodMs = 1000L;
    private long maxNumLogEvents = 2000L;
    private int maxRetryPeriodMs = 14400000;
    private int backoffMultiplier = 10;
    private int maxBackoffMinutes = 10;
    private String fluentdEndpoint;
    private LoggingEventQueue<T> loggingEventQueue;
    private FluentdLogQueueSendTask<T> sendTask;
    private volatile boolean isEnabled = false;
    private Function<T, Serializable> layout;

    public void restart() {
        this.close();
        this.initialise();
    }

    public void enable() {
        System.setProperty(SYS_PROP_ENABLE_KEY, "true");
    }

    public void disable() {
        System.clearProperty(SYS_PROP_ENABLE_KEY);
    }

    public void initialise() {
        if (Boolean.getBoolean(SYS_PROP_ENABLE_KEY)) {
            this.loggingEventQueue = new LoggingEventQueue(this.maxNumLogEvents);
            FluentdSender fluentdSender = Boolean.getBoolean(SYS_PROP_STD_OUT_MODE) ? new FluentdStdOutSender() : new FluentdHttpSender(this.fluentdEndpoint);
            this.sendTask = new FluentdLogQueueSendTask<T>(this.layout, this.loggingEventQueue, fluentdSender, this.maxRetryPeriodMs, this.backoffMultiplier, this.maxBackoffMinutes);
            this.isEnabled = true;
            new Timer().schedule(this.sendTask, 0L, this.batchPeriodMs);
        } else {
            this.isEnabled = false;
        }
    }

    public void append(T loggingEvent) {
        if (this.isEnabled) {
            this.loggingEventQueue.enqueue(loggingEvent);
        }
    }

    public void close() {
        if (!this.isEnabled) {
            return;
        }
        this.isEnabled = false;
        this.sendTask.cancel();
        this.sendTask.clean();
        this.loggingEventQueue = null;
        this.sendTask = null;
    }

    public boolean isEnabled() {
        return this.isEnabled;
    }

    public void setFluentdEndpoint(String fluentdEndpoint) {
        this.fluentdEndpoint = fluentdEndpoint;
    }

    public void setBatchPeriodMs(long batchPeriodMs) {
        this.batchPeriodMs = batchPeriodMs;
    }

    public void setMaxNumEvents(long maxNumLogEvents) {
        this.maxNumLogEvents = maxNumLogEvents;
    }

    public void setMaxRetryPeriodMs(int maxRetryPeriodMs) {
        this.maxRetryPeriodMs = maxRetryPeriodMs;
    }

    public void setBackoffMultiplier(int backoffMultiplier) {
        this.backoffMultiplier = backoffMultiplier;
    }

    public void setMaxBackoffMinutes(int maxBackoffMinutes) {
        this.maxBackoffMinutes = maxBackoffMinutes;
    }

    public void setLayout(Function<T, Serializable> layout) {
        this.layout = layout;
    }
}

