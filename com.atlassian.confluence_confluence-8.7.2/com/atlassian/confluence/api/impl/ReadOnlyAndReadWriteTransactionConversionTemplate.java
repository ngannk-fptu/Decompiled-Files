/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.transaction.PlatformTransactionManager
 *  org.springframework.transaction.TransactionDefinition
 *  org.springframework.transaction.support.DefaultTransactionDefinition
 *  org.springframework.transaction.support.TransactionSynchronizationManager
 *  org.springframework.transaction.support.TransactionTemplate
 */
package com.atlassian.confluence.api.impl;

import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionTemplate;

public class ReadOnlyAndReadWriteTransactionConversionTemplate<T> {
    private final PlatformTransactionManager transactionManager;
    private static final Logger log = LoggerFactory.getLogger(ReadOnlyAndReadWriteTransactionConversionTemplate.class);

    public ReadOnlyAndReadWriteTransactionConversionTemplate(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    public T executeInReadOnly(Supplier<T> inReadOnlyCallback, Supplier<T> notInReadOnlyCallback) {
        if (TransactionSynchronizationManager.isActualTransactionActive() && TransactionSynchronizationManager.isCurrentTransactionReadOnly()) {
            log.debug("Detected existing read-only transaction");
            return inReadOnlyCallback.get();
        }
        log.debug("Running within a read/write transaction");
        DefaultTransactionDefinition tranDef = new DefaultTransactionDefinition(3);
        tranDef.setReadOnly(true);
        return (T)new TransactionTemplate(this.transactionManager, (TransactionDefinition)tranDef).execute(status -> notInReadOnlyCallback.get());
    }

    public T executeInReadWrite(Supplier<T> callback) {
        if (TransactionSynchronizationManager.isActualTransactionActive() && TransactionSynchronizationManager.isCurrentTransactionReadOnly()) {
            log.debug("Running within a readonly transaction, propagating new read/write transaction");
            DefaultTransactionDefinition tranDef = new DefaultTransactionDefinition(3);
            return (T)new TransactionTemplate(this.transactionManager, (TransactionDefinition)tranDef).execute(status -> callback.get());
        }
        if (TransactionSynchronizationManager.isActualTransactionActive()) {
            log.debug("Detected existing read-write transaction");
            return callback.get();
        }
        log.debug("No active transaction detected, propagating new read/write transaction");
        DefaultTransactionDefinition tranDef = new DefaultTransactionDefinition(3);
        return (T)new TransactionTemplate(this.transactionManager, (TransactionDefinition)tranDef).execute(status -> callback.get());
    }
}

