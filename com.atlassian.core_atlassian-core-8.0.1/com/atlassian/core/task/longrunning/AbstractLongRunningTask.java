/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.util.profiling.Metrics
 *  com.atlassian.util.profiling.Metrics$Builder
 *  com.atlassian.util.profiling.Ticker
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.core.task.longrunning;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.core.task.longrunning.LongRunningTask;
import com.atlassian.core.util.DateUtils;
import com.atlassian.core.util.ProgressMeter;
import com.atlassian.util.profiling.Metrics;
import com.atlassian.util.profiling.Ticker;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractLongRunningTask
implements LongRunningTask {
    public static final Logger log = LoggerFactory.getLogger(AbstractLongRunningTask.class);
    @VisibleForTesting
    protected static final String METRIC_NAME = "longRunningTask";
    @VisibleForTesting
    protected static final String TASK_CLASSNAME_TAG = "taskClass";
    @VisibleForTesting
    protected static final String TASK_NAME_TAG = "taskName";
    long startTime = System.currentTimeMillis();
    long stopTime = 0L;
    protected ProgressMeter progress = new ProgressMeter();
    private final Metrics.Builder metricBuilder;
    private Ticker metricTicker;

    protected AbstractLongRunningTask() {
        this.progress.setStatus("Initializing... ");
        this.metricBuilder = Metrics.metric((String)METRIC_NAME).tag(TASK_CLASSNAME_TAG, this.getClass().getCanonicalName()).withInvokerPluginKey().withAnalytics();
    }

    @Override
    public void run() {
        this.progress.setStatus("Starting... ");
        this.startTime = System.currentTimeMillis();
        this.metricTicker = this.metricBuilder.tag(TASK_NAME_TAG, this.getName()).startLongRunningTimer();
    }

    @Override
    public String getNameKey() {
        return null;
    }

    @Override
    public int getPercentageComplete() {
        return this.progress.getPercentageComplete();
    }

    @Override
    public String getCurrentStatus() {
        return this.progress.getStatus();
    }

    @Override
    public long getElapsedTime() {
        return (this.stopTime == 0L ? System.currentTimeMillis() : this.stopTime) - this.startTime;
    }

    @Override
    public long getEstimatedTimeRemaining() {
        long elapsedTime = this.getElapsedTime();
        if (this.getPercentageComplete() == 0) {
            return 0L;
        }
        long totalTimeEstimate = 100L * elapsedTime / (long)this.getPercentageComplete();
        return totalTimeEstimate - elapsedTime;
    }

    @Override
    public boolean isComplete() {
        return this.getPercentageComplete() == 100;
    }

    @Override
    public String getPrettyElapsedTime() {
        return this.prettyTime(this.getElapsedTime());
    }

    protected abstract ResourceBundle getResourceBundle();

    private String prettyTime(long time) {
        if (time < 1000L) {
            return "Less than a second";
        }
        if (time / DateUtils.SECOND_MILLIS < 60L) {
            return time / DateUtils.SECOND_MILLIS + " seconds";
        }
        String minutesAndAbove = null;
        try {
            minutesAndAbove = DateUtils.getDurationPretty(time / DateUtils.SECOND_MILLIS, this.getResourceBundle());
        }
        catch (MissingResourceException e) {
            log.error("Could not load resourcebundle for 'minute'!'", (Throwable)e);
        }
        long secondsRemainder = time / DateUtils.SECOND_MILLIS % 60L;
        if (secondsRemainder > 0L) {
            minutesAndAbove = minutesAndAbove + ", " + secondsRemainder + " second" + (secondsRemainder == 1L ? "" : "s");
        }
        return minutesAndAbove;
    }

    @Override
    public String getPrettyTimeRemaining() {
        long estimatedTimeRemaining = this.getEstimatedTimeRemaining();
        if (estimatedTimeRemaining == 0L) {
            return "Unknown";
        }
        return this.prettyTime(estimatedTimeRemaining);
    }

    @Override
    public boolean isSuccessful() {
        return this.progress.isCompletedSuccessfully();
    }

    protected void stopTimer() {
        this.stopTime = System.currentTimeMillis();
        this.metricTicker.close();
    }
}

