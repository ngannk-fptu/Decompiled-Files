/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.upm.test.rest.resources.async;

import com.atlassian.upm.core.async.AsyncTask;
import com.atlassian.upm.core.async.AsyncTaskStatus;
import com.atlassian.upm.core.async.AsyncTaskStatusUpdater;
import com.atlassian.upm.core.async.AsyncTaskType;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IncrementationTestTask
implements AsyncTask {
    private static final Logger logger = LoggerFactory.getLogger(IncrementationTestTask.class);
    private final AtomicReference<Integer> count = new AtomicReference<Integer>(-1);
    private CountDownLatch latch = new CountDownLatch(1);

    public Runnable getCanceller() {
        return () -> this.latch.countDown();
    }

    @Override
    public AsyncTaskStatus getInitialStatus() {
        return AsyncTaskStatus.builder().build();
    }

    @Override
    public AsyncTaskType getType() {
        return AsyncTaskType.CANCELLABLE;
    }

    @Override
    public AsyncTaskStatus run(AsyncTaskStatusUpdater statusUpdater) throws InterruptedException {
        this.count.set(0);
        logger.warn("Starting incrementation.");
        while (this.latch.getCount() != 0L) {
            int newCount = this.count.get() + 1;
            this.count.set(newCount);
            logger.warn("Incremented to " + newCount);
            Thread.sleep(1000L);
        }
        logger.warn("Completing incrementation.");
        return AsyncTaskStatus.builder().build();
    }
}

