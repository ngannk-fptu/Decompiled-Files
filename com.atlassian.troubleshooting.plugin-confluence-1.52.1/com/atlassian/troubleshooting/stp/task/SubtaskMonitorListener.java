/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.troubleshooting.stp.task;

import com.atlassian.troubleshooting.stp.action.Message;
import com.atlassian.troubleshooting.stp.task.MutableTaskMonitor;
import com.atlassian.troubleshooting.stp.task.TaskMonitor;
import com.atlassian.troubleshooting.stp.task.TaskMonitorListener;
import javax.annotation.Nonnull;

public class SubtaskMonitorListener<T>
implements TaskMonitorListener<T> {
    private final MutableTaskMonitor parentMonitor;
    private final int weight;
    private volatile int subtaskProgress;

    public SubtaskMonitorListener(MutableTaskMonitor parentMonitor, int weight) {
        this.parentMonitor = parentMonitor;
        this.weight = weight;
    }

    @Override
    public void onFinished(@Nonnull TaskMonitor<T> monitor) {
        this.updateProgress(monitor);
        for (Message warning : monitor.getWarnings()) {
            this.parentMonitor.addWarning(warning);
        }
        for (Message error : monitor.getErrors()) {
            this.parentMonitor.addError(error);
        }
    }

    @Override
    public void onUpdated(@Nonnull TaskMonitor<T> monitor) {
        this.updateProgress(monitor);
    }

    private void updateProgress(TaskMonitor<T> subtaskMonitor) {
        int percentage = subtaskMonitor.getProgressPercentage();
        int oldContribution = this.subtaskProgress * this.weight / 100;
        int newContribution = percentage * this.weight / 100;
        this.subtaskProgress = percentage;
        this.parentMonitor.updateProgress(this.parentMonitor.getProgressPercentage() - oldContribution + newContribution, subtaskMonitor.getProgressMessage());
    }
}

