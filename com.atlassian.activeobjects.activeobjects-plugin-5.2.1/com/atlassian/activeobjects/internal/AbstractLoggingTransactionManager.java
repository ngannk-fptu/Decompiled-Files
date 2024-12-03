/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.transaction.TransactionCallback
 *  com.atlassian.util.profiling.Metrics
 *  com.atlassian.util.profiling.Ticker
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.activeobjects.internal;

import com.atlassian.activeobjects.internal.TransactionManager;
import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.util.profiling.Metrics;
import com.atlassian.util.profiling.Ticker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract class AbstractLoggingTransactionManager
implements TransactionManager {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    private static final String DB_AO_TRANSACTION_MANAGER_TIMER_NAME = "db.ao.executeInTransaction";
    private static final String TASK_NAME = "taskName";

    AbstractLoggingTransactionManager() {
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public final <T> T doInTransaction(TransactionCallback<T> callback) {
        try (Ticker ignored = Metrics.metric((String)DB_AO_TRANSACTION_MANAGER_TIMER_NAME).withAnalytics().withInvokerPluginKey().tag(TASK_NAME, callback.getClass().getCanonicalName()).startLongRunningTimer();){
            T t = this.inTransaction(callback);
            return t;
        }
        catch (RuntimeException e) {
            this.logger.debug("Exception thrown within transaction", (Throwable)e);
            throw e;
        }
    }

    abstract <T> T inTransaction(TransactionCallback<T> var1);
}

