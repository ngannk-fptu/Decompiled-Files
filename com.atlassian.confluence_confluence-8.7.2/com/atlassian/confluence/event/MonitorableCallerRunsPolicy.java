/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.event;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.confluence.impl.event.DurationChecker;
import java.util.Queue;
import java.util.concurrent.ThreadPoolExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MonitorableCallerRunsPolicy
extends ThreadPoolExecutor.CallerRunsPolicy {
    private static final Logger log = LoggerFactory.getLogger(MonitorableCallerRunsPolicy.class);
    private final DurationChecker durationChecker;

    public MonitorableCallerRunsPolicy(DurationChecker durationChecker) {
        this.durationChecker = durationChecker;
    }

    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
        if (this.durationChecker.thresholdElapsed()) {
            log.info("Asynchronous queue is full. The task will be processed synchronously in the current thread instead. We'll print this message every {} seconds until there's enough space in queue to start processing messages asynchronously again. Executor: [{}]", (Object)this.durationChecker.getThresholdInSeconds(), (Object)e);
            if (log.isDebugEnabled()) {
                log.debug(this.getQueueInfo(e.getQueue()));
            }
        }
        log.debug("Asynchronous queue is full. The task will be processed synchronously in the current thread instead. Runnable: [{}]; Executor: [{}]", (Object)r, (Object)e);
        super.rejectedExecution(r, e);
    }

    @VisibleForTesting
    String getQueueInfo(Queue<Runnable> queue) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("ThreadPoolExecutor queue content: %s", System.lineSeparator()));
        for (Runnable runnable : queue) {
            sb.append(String.format("[%s];%s", runnable, System.lineSeparator()));
        }
        return sb.toString();
    }
}

