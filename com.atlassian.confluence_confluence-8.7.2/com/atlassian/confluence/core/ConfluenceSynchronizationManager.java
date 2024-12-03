/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.transaction.support.TransactionSynchronization
 *  org.springframework.transaction.support.TransactionSynchronizationManager
 */
package com.atlassian.confluence.core;

import com.atlassian.confluence.core.SynchronizationManager;
import com.atlassian.confluence.util.profiling.DurationThresholdWarningTimingHelper;
import com.atlassian.confluence.util.profiling.DurationThresholdWarningTimingHelperFactory;
import java.time.Duration;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

public class ConfluenceSynchronizationManager
implements SynchronizationManager {
    private static final Logger log = LoggerFactory.getLogger(ConfluenceSynchronizationManager.class);
    private final DurationThresholdWarningTimingHelper timingHelper = DurationThresholdWarningTimingHelperFactory.createFromSystemProperty("confluence.postcommit.taskDurationWarnThresholdMillis", Duration.ofMillis(5000L));

    @Override
    public void runOnSuccessfulCommit(Runnable task) {
        log.debug("Queueing task for run on txn commit: {}", (Object)task);
        if (!this.isTransactionActive()) {
            log.debug("No transaction found. Running task immediately: {}", (Object)task);
            this.runTask(task);
            return;
        }
        for (Object o : TransactionSynchronizationManager.getSynchronizations()) {
            if (!(o instanceof PostCommitRunnableSynchronization)) continue;
            ((PostCommitRunnableSynchronization)o).addTask(task);
            return;
        }
        PostCommitRunnableSynchronization synchronization = new PostCommitRunnableSynchronization();
        synchronization.addTask(task);
        this.registerSynchronization(synchronization);
    }

    @Override
    public boolean isTransactionActive() {
        return TransactionSynchronizationManager.isSynchronizationActive();
    }

    @Override
    public void registerSynchronization(TransactionSynchronization synchronization) {
        TransactionSynchronizationManager.registerSynchronization((TransactionSynchronization)synchronization);
    }

    @Override
    public List getSynchronizations() {
        return TransactionSynchronizationManager.getSynchronizations();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void runTask(Runnable task) {
        DurationThresholdWarningTimingHelper.Timer timer = this.timingHelper.newWarningTimer("post-commit task %s", task).start();
        try {
            task.run();
        }
        catch (Exception t) {
            log.error("Error executing task in post-commit queue: {}", (Object)task, (Object)t);
        }
        finally {
            timer.stopAndCheckTiming();
        }
    }

    private class PostCommitRunnableSynchronization
    implements TransactionSynchronization {
        private final Deque<Runnable> taskQueue = new LinkedList<Runnable>();

        private PostCommitRunnableSynchronization() {
        }

        public void afterCompletion(int i) {
            if (i != 0) {
                return;
            }
            while (!this.taskQueue.isEmpty()) {
                Runnable task = this.taskQueue.removeFirst();
                log.debug("Running post-commit task: {}", (Object)task);
                ConfluenceSynchronizationManager.this.runTask(task);
            }
        }

        void addTask(Runnable task) {
            log.debug("Adding post-commit task: {}", (Object)task);
            this.taskQueue.add(task);
        }
    }
}

