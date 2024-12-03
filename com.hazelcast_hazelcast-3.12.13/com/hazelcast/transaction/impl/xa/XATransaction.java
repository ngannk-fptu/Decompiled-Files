/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.transaction.impl.xa;

import com.hazelcast.core.ExecutionCallback;
import com.hazelcast.logging.ILogger;
import com.hazelcast.spi.InternalCompletableFuture;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.OperationService;
import com.hazelcast.spi.partition.IPartitionService;
import com.hazelcast.transaction.TransactionException;
import com.hazelcast.transaction.TransactionNotActiveException;
import com.hazelcast.transaction.TransactionOptions;
import com.hazelcast.transaction.impl.Transaction;
import com.hazelcast.transaction.impl.TransactionLog;
import com.hazelcast.transaction.impl.TransactionLogRecord;
import com.hazelcast.transaction.impl.xa.SerializableXID;
import com.hazelcast.transaction.impl.xa.operations.PutRemoteTransactionOperation;
import com.hazelcast.util.Clock;
import com.hazelcast.util.ExceptionUtil;
import com.hazelcast.util.FutureUtil;
import com.hazelcast.util.UuidUtil;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import javax.transaction.xa.XAException;
import javax.transaction.xa.Xid;

public final class XATransaction
implements Transaction {
    private static final int ROLLBACK_TIMEOUT_MINUTES = 5;
    private static final int COMMIT_TIMEOUT_MINUTES = 5;
    private final FutureUtil.ExceptionHandler commitExceptionHandler;
    private final FutureUtil.ExceptionHandler rollbackExceptionHandler;
    private final NodeEngine nodeEngine;
    private final long timeoutMillis;
    private final String txnId;
    private final SerializableXID xid;
    private final String txOwnerUuid;
    private final TransactionLog transactionLog;
    private Transaction.State state = Transaction.State.NO_TXN;
    private long startTime;
    private boolean originatedFromClient;

    public XATransaction(NodeEngine nodeEngine, Xid xid, String txOwnerUuid, int timeout, boolean originatedFromClient) {
        this.nodeEngine = nodeEngine;
        this.transactionLog = new TransactionLog();
        this.timeoutMillis = TimeUnit.SECONDS.toMillis(timeout);
        this.txnId = UuidUtil.newUnsecureUuidString();
        this.xid = new SerializableXID(xid.getFormatId(), xid.getGlobalTransactionId(), xid.getBranchQualifier());
        this.txOwnerUuid = txOwnerUuid == null ? nodeEngine.getLocalMember().getUuid() : txOwnerUuid;
        ILogger logger = nodeEngine.getLogger(this.getClass());
        this.commitExceptionHandler = FutureUtil.logAllExceptions(logger, "Error during commit!", Level.WARNING);
        this.rollbackExceptionHandler = FutureUtil.logAllExceptions(logger, "Error during rollback!", Level.WARNING);
        this.originatedFromClient = originatedFromClient;
    }

    public XATransaction(NodeEngine nodeEngine, Collection<TransactionLogRecord> logs, String txnId, SerializableXID xid, String txOwnerUuid, long timeoutMillis, long startTime) {
        this.nodeEngine = nodeEngine;
        this.transactionLog = new TransactionLog(logs);
        this.timeoutMillis = timeoutMillis;
        this.txnId = txnId;
        this.xid = xid;
        this.txOwnerUuid = txOwnerUuid;
        ILogger logger = nodeEngine.getLogger(this.getClass());
        this.commitExceptionHandler = FutureUtil.logAllExceptions(logger, "Error during commit!", Level.WARNING);
        this.rollbackExceptionHandler = FutureUtil.logAllExceptions(logger, "Error during rollback!", Level.WARNING);
        this.startTime = startTime;
        this.state = Transaction.State.PREPARED;
    }

    @Override
    public void begin() throws IllegalStateException {
        if (this.state == Transaction.State.ACTIVE) {
            throw new IllegalStateException("Transaction is already active");
        }
        this.startTime = Clock.currentTimeMillis();
        this.state = Transaction.State.ACTIVE;
    }

    @Override
    public void prepare() throws TransactionException {
        if (this.state != Transaction.State.ACTIVE) {
            throw new TransactionNotActiveException("Transaction is not active");
        }
        this.checkTimeout();
        try {
            this.state = Transaction.State.PREPARING;
            List<Future> futures = this.transactionLog.prepare(this.nodeEngine);
            FutureUtil.waitWithDeadline(futures, this.timeoutMillis, TimeUnit.MILLISECONDS, FutureUtil.RETHROW_TRANSACTION_EXCEPTION);
            futures.clear();
            this.putTransactionInfoRemote();
            this.state = Transaction.State.PREPARED;
        }
        catch (Throwable e) {
            throw ExceptionUtil.rethrow(e, TransactionException.class);
        }
    }

    private void putTransactionInfoRemote() throws ExecutionException, InterruptedException {
        PutRemoteTransactionOperation operation = new PutRemoteTransactionOperation(this.transactionLog.getRecords(), this.txnId, this.xid, this.txOwnerUuid, this.timeoutMillis, this.startTime);
        OperationService operationService = this.nodeEngine.getOperationService();
        IPartitionService partitionService = this.nodeEngine.getPartitionService();
        int partitionId = partitionService.getPartitionId(this.xid);
        InternalCompletableFuture future = operationService.invokeOnPartition("hz:impl:xaService", operation, partitionId);
        future.get();
    }

    @Override
    public void commit() throws TransactionException, IllegalStateException {
        if (this.state != Transaction.State.PREPARED) {
            throw new IllegalStateException("Transaction is not prepared");
        }
        this.checkTimeout();
        try {
            this.state = Transaction.State.COMMITTING;
            List<Future> futures = this.transactionLog.commit(this.nodeEngine);
            FutureUtil.waitWithDeadline(futures, 5L, TimeUnit.MINUTES, this.commitExceptionHandler);
            this.state = Transaction.State.COMMITTED;
            this.transactionLog.onCommitSuccess();
        }
        catch (Throwable e) {
            this.state = Transaction.State.COMMIT_FAILED;
            this.transactionLog.onCommitFailure();
            throw ExceptionUtil.rethrow(e, TransactionException.class);
        }
    }

    public void commitAsync(ExecutionCallback callback) {
        if (this.state != Transaction.State.PREPARED) {
            throw new IllegalStateException("Transaction is not prepared");
        }
        this.checkTimeout();
        this.state = Transaction.State.COMMITTING;
        this.transactionLog.commitAsync(this.nodeEngine, this.wrapExecutionCallback(callback));
        this.state = Transaction.State.COMMITTED;
    }

    private ExecutionCallback wrapExecutionCallback(final ExecutionCallback callback) {
        return new ExecutionCallback(){

            public void onResponse(Object response) {
                try {
                    callback.onResponse(response);
                }
                finally {
                    XATransaction.this.transactionLog.onCommitSuccess();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                try {
                    callback.onFailure(t);
                }
                finally {
                    XATransaction.this.transactionLog.onCommitFailure();
                }
            }
        };
    }

    @Override
    public void rollback() throws IllegalStateException {
        if (this.state == Transaction.State.NO_TXN || this.state == Transaction.State.ROLLED_BACK) {
            throw new IllegalStateException("Transaction is not active");
        }
        this.state = Transaction.State.ROLLING_BACK;
        try {
            List<Future> futures = this.transactionLog.rollback(this.nodeEngine);
            FutureUtil.waitWithDeadline(futures, 5L, TimeUnit.MINUTES, this.rollbackExceptionHandler);
        }
        catch (Throwable e) {
            throw ExceptionUtil.rethrow(e);
        }
        finally {
            this.state = Transaction.State.ROLLED_BACK;
        }
    }

    public void rollbackAsync(ExecutionCallback callback) {
        if (this.state == Transaction.State.NO_TXN || this.state == Transaction.State.ROLLED_BACK) {
            throw new IllegalStateException("Transaction is not active");
        }
        this.state = Transaction.State.ROLLING_BACK;
        this.transactionLog.rollbackAsync(this.nodeEngine, callback);
        this.state = Transaction.State.ROLLED_BACK;
    }

    @Override
    public String getTxnId() {
        return this.txnId;
    }

    public long getStartTime() {
        return this.startTime;
    }

    public Collection<TransactionLogRecord> getTransactionRecords() {
        return this.transactionLog.getRecords();
    }

    @Override
    public Transaction.State getState() {
        return this.state;
    }

    @Override
    public TransactionOptions.TransactionType getTransactionType() {
        return TransactionOptions.TransactionType.TWO_PHASE;
    }

    @Override
    public long getTimeoutMillis() {
        return this.timeoutMillis;
    }

    @Override
    public void add(TransactionLogRecord record) {
        if (this.state != Transaction.State.ACTIVE) {
            throw new TransactionNotActiveException("Transaction is not active!");
        }
        this.transactionLog.add(record);
    }

    @Override
    public void remove(Object key) {
        this.transactionLog.remove(key);
    }

    @Override
    public TransactionLogRecord get(Object key) {
        return this.transactionLog.get(key);
    }

    @Override
    public String getOwnerUuid() {
        return this.txOwnerUuid;
    }

    @Override
    public boolean isOriginatedFromClient() {
        return this.originatedFromClient;
    }

    public SerializableXID getXid() {
        return this.xid;
    }

    private void checkTimeout() {
        if (this.startTime + this.timeoutMillis < Clock.currentTimeMillis()) {
            ExceptionUtil.sneakyThrow(new XAException(106));
        }
    }
}

