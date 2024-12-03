/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.transaction.PlatformTransactionManager
 *  org.springframework.transaction.TransactionDefinition
 *  org.springframework.transaction.support.DefaultTransactionDefinition
 *  org.springframework.transaction.support.TransactionCallback
 *  org.springframework.transaction.support.TransactionTemplate
 */
package com.atlassian.confluence.util.transaction;

import java.util.concurrent.Callable;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

class ReadOnlyTransactionalTask<K>
implements Callable<K> {
    private final PlatformTransactionManager transactionManager;
    private final TransactionCallback action;

    public ReadOnlyTransactionalTask(PlatformTransactionManager transactionManager, TransactionCallback action) {
        this.transactionManager = transactionManager;
        this.action = action;
    }

    @Override
    public K call() {
        DefaultTransactionDefinition definition = new DefaultTransactionDefinition(0);
        definition.setReadOnly(true);
        TransactionTemplate ttp = new TransactionTemplate(this.transactionManager, (TransactionDefinition)definition);
        return (K)ttp.execute(this.action);
    }
}

