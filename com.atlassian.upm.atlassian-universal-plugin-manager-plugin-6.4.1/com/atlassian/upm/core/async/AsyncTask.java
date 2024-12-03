/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm.core.async;

import com.atlassian.upm.core.async.AsyncTaskStatus;
import com.atlassian.upm.core.async.AsyncTaskStatusUpdater;
import com.atlassian.upm.core.async.AsyncTaskType;

public interface AsyncTask {
    public AsyncTaskStatus getInitialStatus();

    public AsyncTaskType getType();

    public AsyncTaskStatus run(AsyncTaskStatusUpdater var1) throws Exception;
}

