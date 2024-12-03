/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.springframework.ldap.transaction.compensating;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.compensating.CompensatingTransactionOperationExecutor;

public class NullOperationExecutor
implements CompensatingTransactionOperationExecutor {
    private static Logger log = LoggerFactory.getLogger(NullOperationExecutor.class);

    @Override
    public void rollback() {
        log.info("Rolling back null operation");
    }

    @Override
    public void commit() {
        log.info("Committing back null operation");
    }

    @Override
    public void performOperation() {
        log.info("Performing null operation");
    }
}

