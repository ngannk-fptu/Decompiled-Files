/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.transaction.impl;

import com.hazelcast.core.Member;
import com.hazelcast.instance.MemberImpl;
import com.hazelcast.internal.cluster.ClusterService;
import com.hazelcast.internal.metrics.Probe;
import com.hazelcast.internal.metrics.ProbeLevel;
import com.hazelcast.internal.util.counters.Counter;
import com.hazelcast.internal.util.counters.MwCounter;
import com.hazelcast.logging.ILogger;
import com.hazelcast.nio.Address;
import com.hazelcast.spi.ClientAwareService;
import com.hazelcast.spi.InternalCompletableFuture;
import com.hazelcast.spi.ManagedService;
import com.hazelcast.spi.MemberAttributeServiceEvent;
import com.hazelcast.spi.MembershipAwareService;
import com.hazelcast.spi.MembershipServiceEvent;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.impl.NodeEngineImpl;
import com.hazelcast.spi.impl.operationservice.InternalOperationService;
import com.hazelcast.transaction.TransactionContext;
import com.hazelcast.transaction.TransactionException;
import com.hazelcast.transaction.TransactionManagerService;
import com.hazelcast.transaction.TransactionOptions;
import com.hazelcast.transaction.TransactionalTask;
import com.hazelcast.transaction.impl.AllowedDuringPassiveStateTransactionImpl;
import com.hazelcast.transaction.impl.Transaction;
import com.hazelcast.transaction.impl.TransactionContextImpl;
import com.hazelcast.transaction.impl.TransactionImpl;
import com.hazelcast.transaction.impl.TransactionLogRecord;
import com.hazelcast.transaction.impl.operations.BroadcastTxRollbackOperation;
import com.hazelcast.util.FutureUtil;
import com.hazelcast.util.Preconditions;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class TransactionManagerServiceImpl
implements TransactionManagerService,
ManagedService,
MembershipAwareService,
ClientAwareService {
    public static final String SERVICE_NAME = "hz:core:txManagerService";
    private static final Address[] EMPTY_ADDRESSES = new Address[0];
    final ConcurrentMap<String, TxBackupLog> txBackupLogs = new ConcurrentHashMap<String, TxBackupLog>();
    @Probe(level=ProbeLevel.MANDATORY)
    Counter startCount = MwCounter.newMwCounter();
    @Probe(level=ProbeLevel.MANDATORY)
    Counter rollbackCount = MwCounter.newMwCounter();
    @Probe(level=ProbeLevel.MANDATORY)
    Counter commitCount = MwCounter.newMwCounter();
    private final FutureUtil.ExceptionHandler finalizeExceptionHandler;
    private final NodeEngineImpl nodeEngine;
    private final ILogger logger;

    public TransactionManagerServiceImpl(NodeEngineImpl nodeEngine) {
        this.nodeEngine = nodeEngine;
        this.logger = nodeEngine.getLogger(TransactionManagerService.class);
        this.finalizeExceptionHandler = FutureUtil.logAllExceptions(this.logger, "Error while rolling-back tx!", Level.WARNING);
        nodeEngine.getMetricsRegistry().scanAndRegister(this, "transactions");
    }

    public String getGroupName() {
        return this.nodeEngine.getConfig().getGroupConfig().getName();
    }

    @Override
    public <T> T executeTransaction(TransactionOptions options, TransactionalTask<T> task) throws TransactionException {
        Preconditions.checkNotNull(task, "TransactionalTask is required!");
        TransactionContext context = this.newTransactionContext(options);
        context.beginTransaction();
        try {
            T value = task.execute(context);
            context.commitTransaction();
            return value;
        }
        catch (Throwable e) {
            context.rollbackTransaction();
            if (e instanceof TransactionException) {
                throw (TransactionException)e;
            }
            if (e.getCause() instanceof TransactionException) {
                throw (TransactionException)e.getCause();
            }
            if (e instanceof RuntimeException) {
                throw (RuntimeException)e;
            }
            throw new TransactionException(e);
        }
    }

    @Override
    public TransactionContext newTransactionContext(TransactionOptions options) {
        return new TransactionContextImpl(this, this.nodeEngine, options, null, false);
    }

    @Override
    public TransactionContext newClientTransactionContext(TransactionOptions options, String clientUuid) {
        return new TransactionContextImpl(this, this.nodeEngine, options, clientUuid, true);
    }

    public Transaction newTransaction(TransactionOptions options) {
        return new TransactionImpl(this, this.nodeEngine, options, null);
    }

    public Transaction newAllowedDuringPassiveStateTransaction(TransactionOptions options) {
        return new AllowedDuringPassiveStateTransactionImpl(this, this.nodeEngine, options, null);
    }

    @Override
    public void init(NodeEngine nodeEngine, Properties properties) {
    }

    @Override
    public void reset() {
        this.txBackupLogs.clear();
    }

    @Override
    public void shutdown(boolean terminate) {
        this.reset();
    }

    @Override
    public void memberAdded(MembershipServiceEvent event) {
    }

    @Override
    public void memberRemoved(MembershipServiceEvent event) {
        MemberImpl member = event.getMember();
        final String uuid = member.getUuid();
        if (this.nodeEngine.isRunning()) {
            this.logger.info("Committing/rolling-back live transactions of " + member.getAddress() + ", UUID: " + uuid);
            this.nodeEngine.getExecutionService().execute("hz:system", new Runnable(){

                @Override
                public void run() {
                    TransactionManagerServiceImpl.this.finalizeTransactionsOf(uuid);
                }
            });
        } else if (this.logger.isFinestEnabled()) {
            this.logger.finest("Will not commit/roll-back transactions of " + member.getAddress() + ", UUID: " + uuid + " because this member is not running");
        }
    }

    @Override
    public void memberAttributeChanged(MemberAttributeServiceEvent event) {
    }

    private void finalizeTransactionsOf(String callerUuid) {
        Iterator it = this.txBackupLogs.entrySet().iterator();
        while (it.hasNext()) {
            TxBackupLog log;
            Map.Entry entry = it.next();
            String txnId = (String)entry.getKey();
            if (!this.finalize(callerUuid, txnId, log = (TxBackupLog)entry.getValue())) continue;
            it.remove();
        }
    }

    private boolean finalize(String uuid, String txnId, TxBackupLog log) {
        InternalOperationService operationService = this.nodeEngine.getOperationService();
        if (!uuid.equals(log.callerUuid)) {
            return false;
        }
        if (log.state == Transaction.State.ACTIVE) {
            if (this.logger.isFinestEnabled()) {
                this.logger.finest("Rolling-back transaction[id:" + txnId + ", state:ACTIVE] of endpoint " + uuid);
            }
            Set<Member> memberList = this.nodeEngine.getClusterService().getMembers();
            ArrayList futures = new ArrayList(memberList.size());
            for (Member member : memberList) {
                BroadcastTxRollbackOperation op = new BroadcastTxRollbackOperation(txnId);
                InternalCompletableFuture f = operationService.invokeOnTarget(SERVICE_NAME, op, member.getAddress());
                futures.add(f);
            }
            long timeoutMillis = TransactionOptions.getDefault().getTimeoutMillis();
            FutureUtil.waitWithDeadline(futures, timeoutMillis, TimeUnit.MILLISECONDS, this.finalizeExceptionHandler);
        } else {
            TransactionImpl tx = log.allowedDuringPassiveState ? new AllowedDuringPassiveStateTransactionImpl(this, this.nodeEngine, txnId, log.records, log.timeoutMillis, log.startTime, log.callerUuid) : new TransactionImpl(this, this.nodeEngine, txnId, log.records, log.timeoutMillis, log.startTime, log.callerUuid);
            if (log.state == Transaction.State.COMMITTING) {
                if (this.logger.isFinestEnabled()) {
                    this.logger.finest("Committing transaction[id:" + txnId + ", state:COMMITTING] of endpoint " + uuid);
                }
                try {
                    tx.commit();
                }
                catch (Throwable e) {
                    this.logger.warning("Error during committing from tx backup!", e);
                }
            } else {
                if (this.logger.isFinestEnabled()) {
                    this.logger.finest("Rolling-back transaction[id:" + txnId + ", state:" + (Object)((Object)log.state) + "] of endpoint " + uuid);
                }
                try {
                    tx.rollback();
                }
                catch (Throwable e) {
                    this.logger.warning("Error during rolling-back from tx backup!", e);
                }
            }
        }
        return true;
    }

    @Override
    public void clientDisconnected(String clientUuid) {
        this.logger.info("Committing/rolling-back live transactions of client, UUID: " + clientUuid);
        this.finalizeTransactionsOf(clientUuid);
    }

    Address[] pickBackupLogAddresses(int durability) {
        if (durability == 0) {
            return EMPTY_ADDRESSES;
        }
        ClusterService clusterService = this.nodeEngine.getClusterService();
        ArrayList<MemberImpl> members = new ArrayList<MemberImpl>(clusterService.getMemberImpls());
        members.remove(this.nodeEngine.getLocalMember());
        int c = Math.min(members.size(), durability);
        Collections.shuffle(members);
        Address[] addresses = new Address[c];
        for (int i = 0; i < c; ++i) {
            addresses[i] = ((MemberImpl)members.get(i)).getAddress();
        }
        return addresses;
    }

    public void createBackupLog(String callerUuid, String txnId) {
        this.createBackupLog(callerUuid, txnId, false);
    }

    public void createAllowedDuringPassiveStateBackupLog(String callerUuid, String txnId) {
        this.createBackupLog(callerUuid, txnId, true);
    }

    private void createBackupLog(String callerUuid, String txnId, boolean allowedDuringPassiveState) {
        TxBackupLog log = new TxBackupLog(Collections.emptyList(), callerUuid, Transaction.State.ACTIVE, -1L, -1L, allowedDuringPassiveState);
        if (this.txBackupLogs.putIfAbsent(txnId, log) != null) {
            throw new TransactionException("TxLog already exists!");
        }
    }

    public void replicaBackupLog(List<TransactionLogRecord> records, String callerUuid, String txnId, long timeoutMillis, long startTime) {
        TxBackupLog beginLog = (TxBackupLog)this.txBackupLogs.get(txnId);
        if (beginLog == null) {
            throw new TransactionException("Could not find begin tx log!");
        }
        if (beginLog.state != Transaction.State.ACTIVE) {
            throw new TransactionException("TxLog already exists!");
        }
        TxBackupLog newTxBackupLog = new TxBackupLog(records, callerUuid, Transaction.State.COMMITTING, timeoutMillis, startTime, beginLog.allowedDuringPassiveState);
        if (!this.txBackupLogs.replace(txnId, beginLog, newTxBackupLog)) {
            throw new TransactionException("TxLog already exists!");
        }
    }

    public void rollbackBackupLog(String txnId) {
        TxBackupLog log = (TxBackupLog)this.txBackupLogs.get(txnId);
        if (log == null) {
            this.logger.warning("No tx backup log is found, tx -> " + txnId);
        } else {
            log.state = Transaction.State.ROLLING_BACK;
        }
    }

    public void purgeBackupLog(String txnId) {
        this.txBackupLogs.remove(txnId);
    }

    static final class TxBackupLog {
        final List<TransactionLogRecord> records;
        final String callerUuid;
        final long timeoutMillis;
        final long startTime;
        final boolean allowedDuringPassiveState;
        volatile Transaction.State state;

        private TxBackupLog(List<TransactionLogRecord> records, String callerUuid, Transaction.State state, long timeoutMillis, long startTime, boolean allowedDuringPassiveState) {
            this.records = records;
            this.callerUuid = callerUuid;
            this.state = state;
            this.timeoutMillis = timeoutMillis;
            this.startTime = startTime;
            this.allowedDuringPassiveState = allowedDuringPassiveState;
        }

        public String toString() {
            return "TxBackupLog{records=" + this.records + ", callerUuid='" + this.callerUuid + '\'' + ", timeoutMillis=" + this.timeoutMillis + ", startTime=" + this.startTime + ", state=" + (Object)((Object)this.state) + ", allowedDuringPassiveState=" + this.allowedDuringPassiveState + '}';
        }
    }
}

