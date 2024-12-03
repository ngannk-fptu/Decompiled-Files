/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.transaction.TransactionSystemException
 */
package org.springframework.transaction.compensating.support;

import java.util.Stack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.transaction.compensating.CompensatingTransactionOperationExecutor;
import org.springframework.transaction.compensating.CompensatingTransactionOperationFactory;
import org.springframework.transaction.compensating.CompensatingTransactionOperationManager;
import org.springframework.transaction.compensating.CompensatingTransactionOperationRecorder;

public class DefaultCompensatingTransactionOperationManager
implements CompensatingTransactionOperationManager {
    private static Logger log = LoggerFactory.getLogger(DefaultCompensatingTransactionOperationManager.class);
    private Stack<CompensatingTransactionOperationExecutor> operationExecutors = new Stack();
    private CompensatingTransactionOperationFactory operationFactory;

    public DefaultCompensatingTransactionOperationManager(CompensatingTransactionOperationFactory operationFactory) {
        this.operationFactory = operationFactory;
    }

    @Override
    public void performOperation(Object resource, String operation, Object[] args) {
        CompensatingTransactionOperationRecorder recorder = this.operationFactory.createRecordingOperation(resource, operation);
        CompensatingTransactionOperationExecutor executor = recorder.recordOperation(args);
        executor.performOperation();
        this.operationExecutors.push(executor);
    }

    @Override
    public void rollback() {
        log.debug("Performing rollback");
        while (!this.operationExecutors.isEmpty()) {
            CompensatingTransactionOperationExecutor rollbackOperation = this.operationExecutors.pop();
            try {
                rollbackOperation.rollback();
            }
            catch (Exception e) {
                throw new TransactionSystemException("Error occurred during rollback", (Throwable)e);
            }
        }
    }

    protected Stack<CompensatingTransactionOperationExecutor> getOperationExecutors() {
        return this.operationExecutors;
    }

    void setOperationExecutors(Stack<CompensatingTransactionOperationExecutor> operationExecutors) {
        this.operationExecutors = operationExecutors;
    }

    @Override
    public void commit() {
        log.debug("Performing commit");
        for (CompensatingTransactionOperationExecutor operationExecutor : this.operationExecutors) {
            try {
                operationExecutor.commit();
            }
            catch (Exception e) {
                throw new TransactionSystemException("Error occurred during commit", (Throwable)e);
            }
        }
    }
}

