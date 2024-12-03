/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joda.time.Duration
 */
package com.atlassian.upm.core.async;

import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.core.async.AsyncTaskStatus;
import com.atlassian.upm.core.async.AsyncTaskStatusUpdater;
import java.util.Timer;
import java.util.TimerTask;
import org.joda.time.Duration;

public class AutoProgressIncrementer {
    private static final long INTERVAL_MILLIS = 200L;
    private final AsyncTaskStatusUpdater updater;
    private final AsyncTaskStatus baseStatus;
    private final float startingProgress;
    private final float endingProgress;
    private final long totalMilliseconds;
    private final long startedTime;
    private final Timer timer;
    private final TimerTask task;

    public static AutoProgressIncrementer start(AsyncTaskStatusUpdater updater, AsyncTaskStatus baseStatus, float startingProgress, float endingProgress, Duration totalTime) {
        return new AutoProgressIncrementer(updater, baseStatus, startingProgress, endingProgress, totalTime);
    }

    private AutoProgressIncrementer(AsyncTaskStatusUpdater updater, AsyncTaskStatus baseStatus, float startingProgress, float endingProgress, Duration totalTime) {
        this.updater = updater;
        this.baseStatus = baseStatus;
        this.startingProgress = startingProgress;
        this.endingProgress = endingProgress;
        this.totalMilliseconds = totalTime.getMillis();
        this.startedTime = System.currentTimeMillis();
        this.timer = new Timer(this.getClass().getName());
        this.task = new UpdateTask();
        this.updateProgress(startingProgress);
        this.timer.schedule(this.task, 200L, 200L);
    }

    public void stop() {
        this.task.cancel();
        this.timer.cancel();
    }

    private void updateProgress(float value) {
        this.updater.updateStatus(AsyncTaskStatus.builder(this.baseStatus).progress(Option.some(Float.valueOf(value))).build());
    }

    private final class UpdateTask
    extends TimerTask {
        private UpdateTask() {
        }

        @Override
        public void run() {
            float value;
            long elapsedTime = System.currentTimeMillis() - AutoProgressIncrementer.this.startedTime;
            if (elapsedTime >= AutoProgressIncrementer.this.totalMilliseconds) {
                value = AutoProgressIncrementer.this.endingProgress;
                AutoProgressIncrementer.this.stop();
            } else {
                value = (float)elapsedTime * (AutoProgressIncrementer.this.endingProgress - AutoProgressIncrementer.this.startingProgress) / (float)AutoProgressIncrementer.this.totalMilliseconds + AutoProgressIncrementer.this.startingProgress;
            }
            AutoProgressIncrementer.this.updateProgress(value);
        }
    }
}

