/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.transaction.impl;

import com.hazelcast.core.HazelcastInstanceNotActiveException;
import com.hazelcast.core.MemberLeftException;
import com.hazelcast.internal.cluster.ClusterService;
import com.hazelcast.logging.ILogger;
import com.hazelcast.nio.Address;
import com.hazelcast.spi.InternalCompletableFuture;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.OperationService;
import com.hazelcast.spi.exception.TargetNotMemberException;
import com.hazelcast.transaction.TransactionException;
import com.hazelcast.transaction.TransactionNotActiveException;
import com.hazelcast.transaction.TransactionOptions;
import com.hazelcast.transaction.TransactionTimedOutException;
import com.hazelcast.transaction.impl.Transaction;
import com.hazelcast.transaction.impl.TransactionLog;
import com.hazelcast.transaction.impl.TransactionLogRecord;
import com.hazelcast.transaction.impl.TransactionManagerServiceImpl;
import com.hazelcast.transaction.impl.operations.CreateTxBackupLogOperation;
import com.hazelcast.transaction.impl.operations.PurgeTxBackupLogOperation;
import com.hazelcast.transaction.impl.operations.ReplicateTxBackupLogOperation;
import com.hazelcast.transaction.impl.operations.RollbackTxBackupLogOperation;
import com.hazelcast.util.Clock;
import com.hazelcast.util.ExceptionUtil;
import com.hazelcast.util.FutureUtil;
import com.hazelcast.util.UuidUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;

public class TransactionImpl
implements Transaction {
    private static final Address[] EMPTY_ADDRESSES = new Address[0];
    private static final ThreadLocal<Boolean> TRANSACTION_EXISTS = new ThreadLocal();
    private final FutureUtil.ExceptionHandler rollbackExceptionHandler;
    private final FutureUtil.ExceptionHandler rollbackTxExceptionHandler;
    private final FutureUtil.ExceptionHandler replicationTxExceptionHandler;
    private final TransactionManagerServiceImpl transactionManagerService;
    private final NodeEngine nodeEngine;
    private final String txnId;
    private final int durability;
    private final TransactionOptions.TransactionType transactionType;
    private final boolean checkThreadAccess;
    private final ILogger logger;
    private final String txOwnerUuid;
    private final TransactionLog transactionLog;
    private Long threadId;
    private long timeoutMillis;
    private Transaction.State state = Transaction.State.NO_TXN;
    private long startTime;
    private Address[] backupAddresses = EMPTY_ADDRESSES;
    private boolean backupLogsCreated;
    private boolean originatedFromClient;

    public TransactionImpl(TransactionManagerServiceImpl transactionManagerService, NodeEngine nodeEngine, TransactionOptions options, String txOwnerUuid) {
        this(transactionManagerService, nodeEngine, options, txOwnerUuid, false);
    }

    public TransactionImpl(TransactionManagerServiceImpl transactionManagerService, NodeEngine nodeEngine, TransactionOptions options, String txOwnerUuid, boolean originatedFromClient) {
        this.transactionLog = new TransactionLog();
        this.transactionManagerService = transactionManagerService;
        this.nodeEngine = nodeEngine;
        this.txnId = UuidUtil.newUnsecureUuidString();
        this.timeoutMillis = options.getTimeoutMillis();
        this.transactionType = options.getTransactionType() == TransactionOptions.TransactionType.LOCAL ? TransactionOptions.TransactionType.ONE_PHASE : options.getTransactionType();
        this.durability = this.transactionType == TransactionOptions.TransactionType.ONE_PHASE ? 0 : options.getDurability();
        this.txOwnerUuid = txOwnerUuid == null ? nodeEngine.getLocalMember().getUuid() : txOwnerUuid;
        this.checkThreadAccess = txOwnerUuid == null;
        this.logger = nodeEngine.getLogger(this.getClass());
        this.rollbackExceptionHandler = FutureUtil.logAllExceptions(this.logger, "Error during rollback!", Level.FINEST);
        this.rollbackTxExceptionHandler = FutureUtil.logAllExceptions(this.logger, "Error during tx rollback backup!", Level.FINEST);
        this.replicationTxExceptionHandler = TransactionImpl.createReplicationTxExceptionHandler(this.logger);
        this.originatedFromClient = originatedFromClient;
    }

    TransactionImpl(TransactionManagerServiceImpl transactionManagerService, NodeEngine nodeEngine, String txnId, List<TransactionLogRecord> transactionLog, long timeoutMillis, long startTime, String txOwnerUuid) {
        this.transactionLog = new TransactionLog(transactionLog);
        this.transactionManagerService = transactionManagerService;
        this.nodeEngine = nodeEngine;
        this.txnId = txnId;
        this.timeoutMillis = timeoutMillis;
        this.startTime = startTime;
        this.durability = 0;
        this.transactionType = TransactionOptions.TransactionType.TWO_PHASE;
        this.state = Transaction.State.PREPARED;
        this.txOwnerUuid = txOwnerUuid;
        this.checkThreadAccess = false;
        this.logger = nodeEngine.getLogger(this.getClass());
        this.rollbackExceptionHandler = FutureUtil.logAllExceptions(this.logger, "Error during rollback!", Level.FINEST);
        this.rollbackTxExceptionHandler = FutureUtil.logAllExceptions(this.logger, "Error during tx rollback backup!", Level.FINEST);
        this.replicationTxExceptionHandler = TransactionImpl.createReplicationTxExceptionHandler(this.logger);
    }

    @Override
    public String getTxnId() {
        return this.txnId;
    }

    public long getStartTime() {
        return this.startTime;
    }

    @Override
    public String getOwnerUuid() {
        return this.txOwnerUuid;
    }

    @Override
    public boolean isOriginatedFromClient() {
        return this.originatedFromClient;
    }

    @Override
    public Transaction.State getState() {
        return this.state;
    }

    @Override
    public long getTimeoutMillis() {
        return this.timeoutMillis;
    }

    protected TransactionLog getTransactionLog() {
        return this.transactionLog;
    }

    @Override
    public void add(TransactionLogRecord record) {
        if (this.state != Transaction.State.ACTIVE) {
            throw new TransactionNotActiveException("Transaction is not active!");
        }
        this.checkThread();
        this.transactionLog.add(record);
    }

    @Override
    public TransactionLogRecord get(Object key) {
        return this.transactionLog.get(key);
    }

    @Override
    public void remove(Object key) {
        this.transactionLog.remove(key);
    }

    private void checkThread() {
        if (this.checkThreadAccess && this.threadId != null && this.threadId.longValue() != Thread.currentThread().getId()) {
            throw new IllegalStateException("Transaction cannot span multiple threads!");
        }
    }

    @Override
    public void begin() throws IllegalStateException {
        if (this.state == Transaction.State.ACTIVE) {
            throw new IllegalStateException("Transaction is already active");
        }
        if (TRANSACTION_EXISTS.get() != null) {
            throw new IllegalStateException("Nested transactions are not allowed!");
        }
        this.startTime = Clock.currentTimeMillis();
        this.backupAddresses = this.transactionManagerService.pickBackupLogAddresses(this.durability);
        if (this.threadId == null) {
            this.threadId = Thread.currentThread().getId();
            this.setThreadFlag(Boolean.TRUE);
        }
        this.state = Transaction.State.ACTIVE;
        this.transactionManagerService.startCount.inc();
    }

    private void setThreadFlag(Boolean flag) {
        if (this.checkThreadAccess) {
            TRANSACTION_EXISTS.set(flag);
        }
    }

    @Override
    public void prepare() throws TransactionException {
        if (this.state != Transaction.State.ACTIVE) {
            throw new TransactionNotActiveException("Transaction is not active");
        }
        this.checkThread();
        this.checkTimeout();
        try {
            this.createBackupLogs();
            this.state = Transaction.State.PREPARING;
            List<Future> futures = this.transactionLog.prepare(this.nodeEngine);
            FutureUtil.waitUntilAllRespondedWithDeadline(futures, this.timeoutMillis, TimeUnit.MILLISECONDS, FutureUtil.RETHROW_TRANSACTION_EXCEPTION);
            this.state = Transaction.State.PREPARED;
            this.replicateTxnLog();
        }
        catch (Throwable e) {
            throw ExceptionUtil.rethrow(e, TransactionException.class);
        }
    }

    public boolean requiresPrepare() {
        if (this.transactionType == TransactionOptions.TransactionType.ONE_PHASE) {
            return false;
        }
        return this.transactionLog.size() > 1;
    }

    @Override
    public void commit() throws TransactionException, IllegalStateException {
        try {
            if (this.transactionType == TransactionOptions.TransactionType.TWO_PHASE) {
                if (this.transactionLog.size() > 1) {
                    if (this.state != Transaction.State.PREPARED) {
                        throw new IllegalStateException("Transaction is not prepared");
                    }
                } else if (this.state != Transaction.State.PREPARED && this.state != Transaction.State.ACTIVE) {
                    throw new IllegalStateException("Transaction is not prepared or active");
                }
            } else if (this.transactionType == TransactionOptions.TransactionType.ONE_PHASE && this.state != Transaction.State.ACTIVE) {
                throw new IllegalStateException("Transaction is not active");
            }
            this.checkThread();
            this.checkTimeout();
            try {
                this.state = Transaction.State.COMMITTING;
                List<Future> futures = this.transactionLog.commit(this.nodeEngine);
                FutureUtil.waitWithDeadline(futures, Long.MAX_VALUE, TimeUnit.MILLISECONDS, FutureUtil.RETHROW_TRANSACTION_EXCEPTION);
                this.state = Transaction.State.COMMITTED;
                this.transactionLog.onCommitSuccess();
                this.transactionManagerService.commitCount.inc();
            }
            catch (Throwable e) {
                this.state = Transaction.State.COMMIT_FAILED;
                this.transactionLog.onCommitFailure();
                throw ExceptionUtil.rethrow(e, TransactionException.class);
            }
            finally {
                this.purgeBackupLogs();
            }
        }
        finally {
            this.setThreadFlag(null);
        }
    }

    private void checkTimeout() throws TransactionException {
        if (this.startTime + this.timeoutMillis < Clock.currentTimeMillis()) {
            throw new TransactionException("Transaction is timed-out!");
        }
    }

    @Override
    public void rollback() throws IllegalStateException {
        try {
            if (this.state == Transaction.State.NO_TXN || this.state == Transaction.State.ROLLED_BACK) {
                throw new IllegalStateException("Transaction is not active");
            }
            this.checkThread();
            this.state = Transaction.State.ROLLING_BACK;
            try {
                this.rollbackBackupLogs();
                List<Future> futures = this.transactionLog.rollback(this.nodeEngine);
                FutureUtil.waitWithDeadline(futures, Long.MAX_VALUE, TimeUnit.MILLISECONDS, this.rollbackExceptionHandler);
                this.purgeBackupLogs();
            }
            catch (Throwable e) {
                throw ExceptionUtil.rethrow(e);
            }
            finally {
                this.state = Transaction.State.ROLLED_BACK;
                this.transactionManagerService.rollbackCount.inc();
            }
        }
        finally {
            this.setThreadFlag(null);
        }
    }

    private void replicateTxnLog() {
        if (this.skipBackupLogReplication()) {
            return;
        }
        OperationService operationService = this.nodeEngine.getOperationService();
        ClusterService clusterService = this.nodeEngine.getClusterService();
        ArrayList futures = new ArrayList(this.backupAddresses.length);
        for (Address backupAddress : this.backupAddresses) {
            if (clusterService.getMember(backupAddress) == null) continue;
            ReplicateTxBackupLogOperation op = this.createReplicateTxBackupLogOperation();
            InternalCompletableFuture f = operationService.invokeOnTarget("hz:core:txManagerService", op, backupAddress);
            futures.add(f);
        }
        FutureUtil.waitWithDeadline(futures, this.timeoutMillis, TimeUnit.MILLISECONDS, this.replicationTxExceptionHandler);
    }

    public void ensureBackupLogsExist() {
        if (this.backupLogsCreated || this.backupAddresses.length == 0) {
            return;
        }
        this.forceCreateBackupLogs();
    }

    private void createBackupLogs() {
        if (this.backupLogsCreated || this.skipBackupLogReplication()) {
            return;
        }
        this.forceCreateBackupLogs();
    }

    private void forceCreateBackupLogs() {
        this.backupLogsCreated = true;
        OperationService operationService = this.nodeEngine.getOperationService();
        ArrayList futures = new ArrayList(this.backupAddresses.length);
        for (Address backupAddress : this.backupAddresses) {
            if (this.nodeEngine.getClusterService().getMember(backupAddress) == null) continue;
            CreateTxBackupLogOperation op = this.createCreateTxBackupLogOperation();
            InternalCompletableFuture f = operationService.invokeOnTarget("hz:core:txManagerService", op, backupAddress);
            futures.add(f);
        }
        FutureUtil.waitWithDeadline(futures, this.timeoutMillis, TimeUnit.MILLISECONDS, this.replicationTxExceptionHandler);
    }

    private void rollbackBackupLogs() {
        if (!this.backupLogsCreated) {
            return;
        }
        OperationService operationService = this.nodeEngine.getOperationService();
        ClusterService clusterService = this.nodeEngine.getClusterService();
        ArrayList futures = new ArrayList(this.backupAddresses.length);
        for (Address backupAddress : this.backupAddresses) {
            if (clusterService.getMember(backupAddress) == null) continue;
            InternalCompletableFuture f = operationService.invokeOnTarget("hz:core:txManagerService", this.createRollbackTxBackupLogOperation(), backupAddress);
            futures.add(f);
        }
        FutureUtil.waitWithDeadline(futures, this.timeoutMillis, TimeUnit.MILLISECONDS, this.rollbackTxExceptionHandler);
    }

    private void purgeBackupLogs() {
        if (!this.backupLogsCreated) {
            return;
        }
        OperationService operationService = this.nodeEngine.getOperationService();
        ClusterService clusterService = this.nodeEngine.getClusterService();
        for (Address backupAddress : this.backupAddresses) {
            if (clusterService.getMember(backupAddress) == null) continue;
            try {
                operationService.invokeOnTarget("hz:core:txManagerService", this.createPurgeTxBackupLogOperation(), backupAddress);
            }
            catch (Throwable e) {
                this.logger.warning("Error during purging backups!", e);
            }
        }
    }

    private boolean skipBackupLogReplication() {
        return this.durability == 0 || this.transactionLog.size() <= 1 || this.backupAddresses.length == 0;
    }

    protected CreateTxBackupLogOperation createCreateTxBackupLogOperation() {
        return new CreateTxBackupLogOperation(this.txOwnerUuid, this.txnId);
    }

    protected ReplicateTxBackupLogOperation createReplicateTxBackupLogOperation() {
        return new ReplicateTxBackupLogOperation(this.transactionLog.getRecords(), this.txOwnerUuid, this.txnId, this.timeoutMillis, this.startTime);
    }

    protected RollbackTxBackupLogOperation createRollbackTxBackupLogOperation() {
        return new RollbackTxBackupLogOperation(this.txnId);
    }

    protected PurgeTxBackupLogOperation createPurgeTxBackupLogOperation() {
        return new PurgeTxBackupLogOperation(this.txnId);
    }

    @Override
    public TransactionOptions.TransactionType getTransactionType() {
        return this.transactionType;
    }

    public String toString() {
        return "Transaction{txnId='" + this.txnId + '\'' + ", state=" + (Object)((Object)this.state) + ", txType=" + (Object)((Object)this.transactionType) + ", timeoutMillis=" + this.timeoutMillis + '}';
    }

    static FutureUtil.ExceptionHandler createReplicationTxExceptionHandler(final ILogger logger) {
        return new FutureUtil.ExceptionHandler(){

            @Override
            public void handleException(Throwable throwable) {
                Throwable cause;
                if (throwable instanceof TimeoutException) {
                    throw new TransactionTimedOutException(throwable);
                }
                if (throwable instanceof MemberLeftException) {
                    logger.warning("Member left while replicating tx begin: " + throwable);
                    return;
                }
                if (throwable instanceof ExecutionException && ((cause = throwable.getCause()) instanceof TargetNotMemberException || cause instanceof HazelcastInstanceNotActiveException)) {
                    logger.warning("Member left while replicating tx begin: " + cause);
                    return;
                }
                throw ExceptionUtil.rethrow(throwable);
            }
        };
    }
}

