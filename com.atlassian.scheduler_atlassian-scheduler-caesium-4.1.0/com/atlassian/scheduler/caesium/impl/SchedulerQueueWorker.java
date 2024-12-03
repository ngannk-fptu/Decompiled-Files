/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.scheduler.caesium.impl;

import com.atlassian.scheduler.caesium.impl.QueuedJob;
import com.atlassian.scheduler.caesium.impl.SchedulerQueue;
import java.util.Objects;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class SchedulerQueueWorker
implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(SchedulerQueueWorker.class);
    private final SchedulerQueue queue;
    private final Consumer<QueuedJob> executeJob;

    SchedulerQueueWorker(SchedulerQueue queue, Consumer<QueuedJob> executeJob) {
        this.queue = Objects.requireNonNull(queue, "queue");
        this.executeJob = Objects.requireNonNull(executeJob, "executeJob");
    }

    @Override
    public void run() {
        while (true) {
            try {
                while (this.executeNextJob()) {
                }
            }
            catch (InterruptedException ie) {
                LOG.debug("Scheduler queue worker was interrupted; ignoring...", (Throwable)ie);
                continue;
            }
            break;
        }
        LOG.debug("Shutting down.");
    }

    private boolean executeNextJob() throws InterruptedException {
        QueuedJob job = this.queue.take();
        if (job == null) {
            LOG.debug("The scheduler queue has closed.");
            return false;
        }
        this.executeJob(job);
        return true;
    }

    private void executeJob(QueuedJob job) {
        try {
            this.executeJob.accept(job);
        }
        catch (Throwable e) {
            LOG.error("Unhandled exception thrown by job {}", (Object)job, (Object)e);
        }
    }
}

