/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.transaction.PlatformTransactionManager
 *  org.springframework.transaction.TransactionDefinition
 *  org.springframework.transaction.TransactionException
 *  org.springframework.transaction.TransactionStatus
 *  org.springframework.transaction.support.SimpleTransactionStatus
 */
package com.atlassian.confluence.spring.transaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.SimpleTransactionStatus;

public class NoopTransactionManager
implements PlatformTransactionManager {
    private static final Logger log = LoggerFactory.getLogger(NoopTransactionManager.class);

    public TransactionStatus getTransaction(TransactionDefinition definition) throws TransactionException {
        return new SimpleTransactionStatus();
    }

    public void commit(TransactionStatus status) throws TransactionException {
        if (log.isTraceEnabled()) {
            log.trace(status.toString(), new Throwable());
        }
    }

    public void rollback(TransactionStatus status) throws TransactionException {
        if (log.isTraceEnabled()) {
            log.trace(status.toString(), new Throwable());
        }
    }
}

