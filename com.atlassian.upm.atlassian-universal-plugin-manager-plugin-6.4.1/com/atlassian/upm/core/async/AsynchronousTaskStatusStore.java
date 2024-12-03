/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm.core.async;

import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.core.async.AsyncTaskInfo;
import com.atlassian.upm.core.async.AsyncTaskStatus;

public interface AsynchronousTaskStatusStore {
    public void addTask(AsyncTaskInfo var1);

    public Option<AsyncTaskInfo> updateTaskStatus(String var1, AsyncTaskStatus var2);

    public void removeTask(String var1);

    public Option<AsyncTaskInfo> getTask(String var1);

    public Iterable<AsyncTaskInfo> getOngoingTasks();

    public void clearOngoingTasks();
}

