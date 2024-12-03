/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.ParametersAreNonnullByDefault
 */
package com.atlassian.troubleshooting.stp.task;

import com.atlassian.troubleshooting.stp.hercules.LogScanMonitor;
import com.atlassian.troubleshooting.stp.task.DefaultTaskMonitor;
import com.atlassian.troubleshooting.stp.task.MutableTaskMonitor;
import com.atlassian.troubleshooting.stp.task.TaskMonitorFactory;
import com.atlassian.troubleshooting.stp.task.TaskType;
import com.atlassian.troubleshooting.stp.zip.CreateSupportZipMonitor;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class DefaultTaskMonitorFactory
implements TaskMonitorFactory {
    @Override
    @Nonnull
    public <V> MutableTaskMonitor<V> newInstance(TaskType taskType) {
        switch (taskType) {
            case SUPPORT_ZIP: {
                return new CreateSupportZipMonitor();
            }
            case HERCULES: {
                return new LogScanMonitor();
            }
        }
        return new DefaultTaskMonitor();
    }
}

