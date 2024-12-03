/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.util.concurrent.ThreadFactories
 *  com.atlassian.util.concurrent.ThreadFactories$Type
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.transaction.PlatformTransactionManager
 *  org.springframework.transaction.support.TransactionCallback
 */
package com.atlassian.confluence.util.transaction;

import com.atlassian.confluence.impl.util.concurrent.ConfluenceExecutors;
import com.atlassian.confluence.util.transaction.ReadOnlyTransactionalTask;
import com.atlassian.confluence.util.transaction.TransactionExecutor;
import com.atlassian.util.concurrent.ThreadFactories;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionCallback;

public class ReadOnlyTransactionExecutor<K>
implements TransactionExecutor<K>,
DisposableBean {
    private final ExecutorService executorService;
    private final PlatformTransactionManager transactionManager;

    public ReadOnlyTransactionExecutor(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
        this.executorService = ConfluenceExecutors.newSingleThreadExecutor(ThreadFactories.namedThreadFactory((String)"read-only-transaction", (ThreadFactories.Type)ThreadFactories.Type.DAEMON));
    }

    @Override
    public Future<K> performTransactionAction(TransactionCallback action) {
        return this.executorService.submit(new ReadOnlyTransactionalTask(this.transactionManager, action));
    }

    public void destroy() throws Exception {
        this.executorService.shutdownNow();
    }
}

