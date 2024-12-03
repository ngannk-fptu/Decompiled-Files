/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.transaction.support.TransactionSynchronization
 */
package com.atlassian.confluence.internal.content.collab;

import com.atlassian.confluence.internal.content.collab.ReconcileContentRegisterTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.support.TransactionSynchronization;

public class ThreadLocalCleanUpSynchronization
implements TransactionSynchronization {
    private static final Logger log = LoggerFactory.getLogger(ThreadLocalCleanUpSynchronization.class);
    private final ThreadLocal<? extends ReconcileContentRegisterTask> successfulCommitTaskThreadLocal;

    public ThreadLocalCleanUpSynchronization(ThreadLocal<? extends ReconcileContentRegisterTask> successfulCommitTaskThreadLocal) {
        this.successfulCommitTaskThreadLocal = successfulCommitTaskThreadLocal;
    }

    public void afterCompletion(int status) {
        if (status == 0) {
            try {
                this.successfulCommitTaskThreadLocal.get().run();
            }
            catch (Exception ex) {
                log.error("Error executing task in post-commit", (Throwable)ex);
            }
        }
        log.info("Task is done. Clean up ThreadLocal data");
        this.successfulCommitTaskThreadLocal.remove();
    }
}

