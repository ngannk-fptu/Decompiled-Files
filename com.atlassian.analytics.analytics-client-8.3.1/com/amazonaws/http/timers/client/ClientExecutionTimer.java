/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.http.timers.client;

import com.amazonaws.annotation.SdkInternalApi;
import com.amazonaws.annotation.SdkTestInternalApi;
import com.amazonaws.annotation.ThreadSafe;
import com.amazonaws.http.timers.TimeoutThreadPoolBuilder;
import com.amazonaws.http.timers.client.ClientExecutionAbortTaskImpl;
import com.amazonaws.http.timers.client.ClientExecutionAbortTrackerTask;
import com.amazonaws.http.timers.client.ClientExecutionAbortTrackerTaskImpl;
import com.amazonaws.http.timers.client.NoOpClientExecutionAbortTrackerTask;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@SdkInternalApi
@ThreadSafe
public class ClientExecutionTimer {
    private static final String threadNamePrefix = "AwsSdkClientExecutionTimerThread";
    private volatile ScheduledThreadPoolExecutor executor;

    public ClientExecutionAbortTrackerTask startTimer(int clientExecutionTimeoutMillis) {
        if (this.isTimeoutDisabled(clientExecutionTimeoutMillis)) {
            return NoOpClientExecutionAbortTrackerTask.INSTANCE;
        }
        if (this.executor == null) {
            this.initializeExecutor();
        }
        return this.scheduleTimerTask(clientExecutionTimeoutMillis);
    }

    private synchronized void initializeExecutor() {
        if (this.executor == null) {
            this.executor = TimeoutThreadPoolBuilder.buildDefaultTimeoutThreadPool(threadNamePrefix);
        }
    }

    @SdkTestInternalApi
    public ScheduledThreadPoolExecutor getExecutor() {
        return this.executor;
    }

    public synchronized void shutdown() {
        if (this.executor != null) {
            this.executor.shutdown();
        }
    }

    private ClientExecutionAbortTrackerTask scheduleTimerTask(int clientExecutionTimeoutMillis) {
        ClientExecutionAbortTaskImpl timerTask = new ClientExecutionAbortTaskImpl(Thread.currentThread());
        ScheduledFuture<?> timerTaskFuture = this.executor.schedule(timerTask, (long)clientExecutionTimeoutMillis, TimeUnit.MILLISECONDS);
        return new ClientExecutionAbortTrackerTaskImpl(timerTask, timerTaskFuture);
    }

    private boolean isTimeoutDisabled(int clientExecutionTimeoutMillis) {
        return clientExecutionTimeoutMillis <= 0;
    }
}

