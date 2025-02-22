/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.transaction.impl;

import com.hazelcast.core.ExecutionCallback;
import com.hazelcast.nio.Address;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.OperationService;
import com.hazelcast.spi.impl.operationservice.InternalOperationService;
import com.hazelcast.transaction.impl.TargetAwareTransactionLogRecord;
import com.hazelcast.transaction.impl.TransactionLogRecord;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

public class TransactionLog {
    private final Map<Object, TransactionLogRecord> recordMap = new HashMap<Object, TransactionLogRecord>();

    public TransactionLog() {
    }

    public TransactionLog(Collection<TransactionLogRecord> transactionLog) {
        for (TransactionLogRecord record : transactionLog) {
            this.add(record);
        }
    }

    public void add(TransactionLogRecord record) {
        Object key = record.getKey();
        if (key == null) {
            key = new Object();
        }
        this.recordMap.put(key, record);
    }

    public TransactionLogRecord get(Object key) {
        return this.recordMap.get(key);
    }

    public Collection<TransactionLogRecord> getRecords() {
        return this.recordMap.values();
    }

    public void remove(Object key) {
        this.recordMap.remove(key);
    }

    public int size() {
        return this.recordMap.size();
    }

    public List<Future> commit(NodeEngine nodeEngine) {
        ArrayList<Future> futures = new ArrayList<Future>(this.size());
        for (TransactionLogRecord record : this.recordMap.values()) {
            Future future = this.invoke(nodeEngine, record, record.newCommitOperation());
            futures.add(future);
        }
        return futures;
    }

    public void onCommitSuccess() {
        for (TransactionLogRecord record : this.recordMap.values()) {
            record.onCommitSuccess();
        }
    }

    public void onCommitFailure() {
        for (TransactionLogRecord record : this.recordMap.values()) {
            record.onCommitFailure();
        }
    }

    public List<Future> prepare(NodeEngine nodeEngine) {
        ArrayList<Future> futures = new ArrayList<Future>(this.size());
        for (TransactionLogRecord record : this.recordMap.values()) {
            Future future = this.invoke(nodeEngine, record, record.newPrepareOperation());
            futures.add(future);
        }
        return futures;
    }

    public List<Future> rollback(NodeEngine nodeEngine) {
        ArrayList<Future> futures = new ArrayList<Future>(this.size());
        for (TransactionLogRecord record : this.recordMap.values()) {
            Future future = this.invoke(nodeEngine, record, record.newRollbackOperation());
            futures.add(future);
        }
        return futures;
    }

    private Future invoke(NodeEngine nodeEngine, TransactionLogRecord record, Operation op) {
        OperationService operationService = nodeEngine.getOperationService();
        if (record instanceof TargetAwareTransactionLogRecord) {
            Address target = ((TargetAwareTransactionLogRecord)record).getTarget();
            return operationService.invokeOnTarget(op.getServiceName(), op, target);
        }
        return operationService.invokeOnPartition(op.getServiceName(), op, op.getPartitionId());
    }

    public void commitAsync(NodeEngine nodeEngine, ExecutionCallback callback) {
        for (TransactionLogRecord record : this.recordMap.values()) {
            this.invokeAsync(nodeEngine, callback, record, record.newCommitOperation());
        }
    }

    public void rollbackAsync(NodeEngine nodeEngine, ExecutionCallback callback) {
        for (TransactionLogRecord record : this.recordMap.values()) {
            this.invokeAsync(nodeEngine, callback, record, record.newRollbackOperation());
        }
    }

    private void invokeAsync(NodeEngine nodeEngine, ExecutionCallback callback, TransactionLogRecord record, Operation op) {
        InternalOperationService operationService = (InternalOperationService)nodeEngine.getOperationService();
        if (record instanceof TargetAwareTransactionLogRecord) {
            Address target = ((TargetAwareTransactionLogRecord)record).getTarget();
            operationService.invokeOnTarget(op.getServiceName(), op, target);
        } else {
            operationService.asyncInvokeOnPartition(op.getServiceName(), op, op.getPartitionId(), callback);
        }
    }
}

