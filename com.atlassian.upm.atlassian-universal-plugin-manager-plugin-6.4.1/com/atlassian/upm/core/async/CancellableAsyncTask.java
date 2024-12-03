/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm.core.async;

import com.atlassian.upm.core.async.AsyncTask;
import com.atlassian.upm.core.async.AsyncTaskStatus;
import com.atlassian.upm.core.async.AsyncTaskStatusUpdater;
import com.atlassian.upm.core.async.AsyncTaskType;
import java.util.concurrent.CountDownLatch;

public class CancellableAsyncTask
implements AsyncTask {
    private CountDownLatch latch = new CountDownLatch(1);

    @Override
    public AsyncTaskType getType() {
        return AsyncTaskType.CANCELLABLE;
    }

    @Override
    public AsyncTaskStatus getInitialStatus() {
        return AsyncTaskStatus.empty();
    }

    @Override
    public AsyncTaskStatus run(AsyncTaskStatusUpdater statusUpdater) throws Exception {
        this.latch.await();
        return AsyncTaskStatus.empty();
    }

    public void cancel() {
        this.latch.countDown();
    }

    public Runnable getCanceller() {
        return this::cancel;
    }
}

