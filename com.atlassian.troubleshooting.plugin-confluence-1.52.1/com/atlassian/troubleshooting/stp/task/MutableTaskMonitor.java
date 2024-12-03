/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.util.concurrent.ListenableFutureTask
 *  javax.annotation.ParametersAreNonnullByDefault
 */
package com.atlassian.troubleshooting.stp.task;

import com.atlassian.troubleshooting.stp.action.Message;
import com.atlassian.troubleshooting.stp.task.TaskMonitor;
import com.atlassian.troubleshooting.stp.task.TaskMonitorListener;
import com.google.common.util.concurrent.ListenableFutureTask;
import java.io.Serializable;
import java.util.Map;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface MutableTaskMonitor<V>
extends TaskMonitor<V> {
    public void init(String var1, ListenableFutureTask<V> var2);

    public void addError(Message var1);

    public void addWarning(Message var1);

    public void updateProgress(int var1, String var2);

    public void addListener(TaskMonitorListener<V> var1);

    public void setNodeId(String var1);

    public void setClusteredTaskId(String var1);

    public void setCreatedTimestamp(long var1);

    public void setCustomAttributes(Map<String, Serializable> var1);
}

