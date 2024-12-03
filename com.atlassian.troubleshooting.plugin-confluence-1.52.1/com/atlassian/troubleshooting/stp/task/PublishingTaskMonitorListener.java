/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.troubleshooting.stp.task;

import com.atlassian.troubleshooting.stp.persistence.TaskMonitorRepository;
import com.atlassian.troubleshooting.stp.task.TaskMonitor;
import com.atlassian.troubleshooting.stp.task.TaskMonitorListener;
import java.util.Objects;
import javax.annotation.Nonnull;

class PublishingTaskMonitorListener<T>
implements TaskMonitorListener<T> {
    private static final long PUBLISH_INTERVAL_MS = Long.getLong("troubleshooting.tasks.pub.interval.ms", 100L);
    private final TaskMonitorRepository repository;
    private long lastPublishedTimestamp;
    private int lastPublishedPercentage;

    PublishingTaskMonitorListener(@Nonnull TaskMonitorRepository repository) {
        this.repository = Objects.requireNonNull(repository);
    }

    @Override
    public void onFinished(@Nonnull TaskMonitor<T> monitor) {
        this.maybePublish(monitor);
    }

    @Override
    public void onUpdated(@Nonnull TaskMonitor<T> monitor) {
        this.maybePublish(monitor);
    }

    private void maybePublish(TaskMonitor<T> monitor) {
        if (this.lastPublishedPercentage != monitor.getProgressPercentage() || System.currentTimeMillis() - this.lastPublishedTimestamp > PUBLISH_INTERVAL_MS || monitor.getProgressPercentage() == 100 || monitor.isDone()) {
            this.repository.updateTaskMonitor(monitor);
            this.lastPublishedTimestamp = System.currentTimeMillis();
            this.lastPublishedPercentage = monitor.getProgressPercentage();
        }
    }
}

