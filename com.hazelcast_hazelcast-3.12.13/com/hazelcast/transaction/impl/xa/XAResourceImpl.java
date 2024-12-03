/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.transaction.impl.xa;

import com.hazelcast.config.GroupConfig;
import com.hazelcast.core.HazelcastInstanceNotActiveException;
import com.hazelcast.core.Member;
import com.hazelcast.core.MemberLeftException;
import com.hazelcast.internal.cluster.ClusterService;
import com.hazelcast.logging.ILogger;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.AbstractDistributedObject;
import com.hazelcast.spi.InternalCompletableFuture;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.OperationService;
import com.hazelcast.spi.exception.TargetNotMemberException;
import com.hazelcast.spi.impl.SerializableList;
import com.hazelcast.spi.partition.IPartitionService;
import com.hazelcast.transaction.HazelcastXAResource;
import com.hazelcast.transaction.TransactionContext;
import com.hazelcast.transaction.TransactionOptions;
import com.hazelcast.transaction.impl.Transaction;
import com.hazelcast.transaction.impl.xa.SerializableXID;
import com.hazelcast.transaction.impl.xa.XAService;
import com.hazelcast.transaction.impl.xa.XATransactionContextImpl;
import com.hazelcast.transaction.impl.xa.operations.ClearRemoteTransactionOperation;
import com.hazelcast.transaction.impl.xa.operations.CollectRemoteTransactionsOperation;
import com.hazelcast.transaction.impl.xa.operations.FinalizeRemoteTransactionOperation;
import com.hazelcast.util.ExceptionUtil;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

public final class XAResourceImpl
extends AbstractDistributedObject<XAService>
implements HazelcastXAResource {
    private static final int DEFAULT_TIMEOUT_SECONDS = (int)TimeUnit.MILLISECONDS.toSeconds(TransactionOptions.DEFAULT_TIMEOUT_MILLIS);
    private final ConcurrentMap<Long, TransactionContext> threadContextMap = new ConcurrentHashMap<Long, TransactionContext>();
    private final ConcurrentMap<Xid, List<TransactionContext>> xidContextMap = new ConcurrentHashMap<Xid, List<TransactionContext>>();
    private final String groupName;
    private final AtomicInteger timeoutInSeconds = new AtomicInteger(DEFAULT_TIMEOUT_SECONDS);
    private final ILogger logger;

    public XAResourceImpl(NodeEngine nodeEngine, XAService service) {
        super(nodeEngine, service);
        GroupConfig groupConfig = nodeEngine.getConfig().getGroupConfig();
        this.groupName = groupConfig.getName();
        this.logger = nodeEngine.getLogger(this.getClass());
    }

    @Override
    public void start(Xid xid, int flags) throws XAException {
        long threadId = this.currentThreadId();
        TransactionContext threadContext = (TransactionContext)this.threadContextMap.get(this.currentThreadId());
        switch (flags) {
            case 0: {
                CopyOnWriteArrayList<TransactionContext> contexts = new CopyOnWriteArrayList<TransactionContext>();
                List currentContexts = this.xidContextMap.putIfAbsent(xid, contexts);
                if (currentContexts != null) {
                    throw new XAException("There is already TransactionContexts for the given xid: " + xid);
                }
                TransactionContext context = this.createTransactionContext(xid);
                contexts.add(context);
                this.threadContextMap.put(threadId, context);
                break;
            }
            case 0x200000: 
            case 0x8000000: {
                List contextList = (List)this.xidContextMap.get(xid);
                if (contextList == null) {
                    throw new XAException("There is no TransactionContexts for the given xid: " + xid);
                }
                if (threadContext != null) break;
                threadContext = this.createTransactionContext(xid);
                this.threadContextMap.put(threadId, threadContext);
                contextList.add(threadContext);
                break;
            }
            default: {
                throw new XAException("Unknown flag! " + flags);
            }
        }
    }

    private TransactionContext createTransactionContext(Xid xid) {
        XAService xaService = (XAService)this.getService();
        TransactionContext context = xaService.newXATransactionContext(xid, null, this.timeoutInSeconds.get(), false);
        this.getTransaction(context).begin();
        return context;
    }

    @Override
    public void end(Xid xid, int flags) throws XAException {
        List contexts;
        long threadId = this.currentThreadId();
        TransactionContext threadContext = (TransactionContext)this.threadContextMap.remove(threadId);
        if (threadContext == null && this.logger.isFinestEnabled()) {
            this.logger.finest("There is no TransactionContext for the current thread: " + threadId);
        }
        if ((contexts = (List)this.xidContextMap.get(xid)) == null && this.logger.isFinestEnabled()) {
            this.logger.finest("There is no TransactionContexts for the given xid: " + xid);
        }
    }

    @Override
    public int prepare(Xid xid) throws XAException {
        List contexts = (List)this.xidContextMap.get(xid);
        if (contexts == null) {
            throw new XAException("There is no TransactionContexts for the given xid: " + xid);
        }
        for (TransactionContext context : contexts) {
            Transaction transaction = this.getTransaction(context);
            transaction.prepare();
        }
        return 0;
    }

    @Override
    public void commit(Xid xid, boolean onePhase) throws XAException {
        List contexts = (List)this.xidContextMap.remove(xid);
        if (contexts == null && onePhase) {
            throw new XAException("There is no TransactionContexts for the given xid: " + xid);
        }
        if (contexts == null) {
            this.finalizeTransactionRemotely(xid, true);
            return;
        }
        for (TransactionContext context : contexts) {
            Transaction transaction = this.getTransaction(context);
            if (onePhase) {
                transaction.prepare();
            }
            transaction.commit();
        }
        this.clearRemoteTransactions(xid);
    }

    @Override
    public void rollback(Xid xid) throws XAException {
        List contexts = (List)this.xidContextMap.remove(xid);
        if (contexts == null) {
            this.finalizeTransactionRemotely(xid, false);
            return;
        }
        for (TransactionContext context : contexts) {
            this.getTransaction(context).rollback();
        }
        this.clearRemoteTransactions(xid);
    }

    private void finalizeTransactionRemotely(Xid xid, boolean isCommit) throws XAException {
        Integer errorCode;
        NodeEngine nodeEngine = this.getNodeEngine();
        IPartitionService partitionService = nodeEngine.getPartitionService();
        OperationService operationService = nodeEngine.getOperationService();
        SerializableXID serializableXID = new SerializableXID(xid.getFormatId(), xid.getGlobalTransactionId(), xid.getBranchQualifier());
        Data xidData = nodeEngine.toData(serializableXID);
        int partitionId = partitionService.getPartitionId(xidData);
        FinalizeRemoteTransactionOperation operation = new FinalizeRemoteTransactionOperation(xidData, isCommit);
        InternalCompletableFuture future = operationService.invokeOnPartition("hz:impl:xaService", operation, partitionId);
        try {
            errorCode = (Integer)future.get();
        }
        catch (Exception e) {
            throw ExceptionUtil.rethrow(e);
        }
        if (errorCode != null) {
            throw new XAException(errorCode);
        }
    }

    private void clearRemoteTransactions(Xid xid) {
        NodeEngine nodeEngine = this.getNodeEngine();
        IPartitionService partitionService = nodeEngine.getPartitionService();
        OperationService operationService = nodeEngine.getOperationService();
        SerializableXID serializableXID = new SerializableXID(xid.getFormatId(), xid.getGlobalTransactionId(), xid.getBranchQualifier());
        Data xidData = nodeEngine.toData(serializableXID);
        int partitionId = partitionService.getPartitionId(xidData);
        ClearRemoteTransactionOperation operation = new ClearRemoteTransactionOperation(xidData);
        operationService.invokeOnPartition("hz:impl:xaService", operation, partitionId);
    }

    @Override
    public void forget(Xid xid) throws XAException {
        List contexts = (List)this.xidContextMap.remove(xid);
        if (contexts == null) {
            throw new XAException("No context with the given xid: " + xid);
        }
        this.clearRemoteTransactions(xid);
    }

    @Override
    public boolean isSameRM(XAResource xaResource) throws XAException {
        if (this == xaResource) {
            return true;
        }
        if (xaResource instanceof XAResourceImpl) {
            XAResourceImpl otherXaResource = (XAResourceImpl)xaResource;
            return this.groupName.equals(otherXaResource.groupName);
        }
        return xaResource.isSameRM(this);
    }

    @Override
    public Xid[] recover(int flag) throws XAException {
        NodeEngine nodeEngine = this.getNodeEngine();
        XAService xaService = (XAService)this.getService();
        OperationService operationService = nodeEngine.getOperationService();
        ClusterService clusterService = nodeEngine.getClusterService();
        Set<Member> memberList = clusterService.getMembers();
        ArrayList futureList = new ArrayList();
        for (Member member : memberList) {
            if (member.localMember()) continue;
            CollectRemoteTransactionsOperation collectRemoteTransactionsOperation = new CollectRemoteTransactionsOperation();
            Address address = member.getAddress();
            InternalCompletableFuture future = operationService.invokeOnTarget("hz:impl:xaService", collectRemoteTransactionsOperation, address);
            futureList.add(future);
        }
        HashSet<SerializableXID> xids = new HashSet<SerializableXID>(xaService.getPreparedXids());
        for (Future future : futureList) {
            try {
                SerializableList xidSet = (SerializableList)future.get();
                for (Data xidData : xidSet) {
                    SerializableXID xid = (SerializableXID)nodeEngine.toObject(xidData);
                    xids.add(xid);
                }
            }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new XAException(-3);
            }
            catch (MemberLeftException e) {
                this.logger.warning("Member left while recovering", e);
            }
            catch (ExecutionException e) {
                Throwable cause = e.getCause();
                if (cause instanceof HazelcastInstanceNotActiveException || cause instanceof TargetNotMemberException) {
                    this.logger.warning("Member left while recovering", e);
                    continue;
                }
                throw new XAException(-3);
            }
        }
        return xids.toArray(new SerializableXID[0]);
    }

    @Override
    public int getTransactionTimeout() throws XAException {
        return this.timeoutInSeconds.get();
    }

    @Override
    public boolean setTransactionTimeout(int seconds) throws XAException {
        this.timeoutInSeconds.set(seconds == 0 ? DEFAULT_TIMEOUT_SECONDS : seconds);
        return true;
    }

    @Override
    public String getServiceName() {
        return "hz:impl:xaService";
    }

    @Override
    public String getName() {
        return "hz:impl:xaService";
    }

    @Override
    public TransactionContext getTransactionContext() {
        long threadId = Thread.currentThread().getId();
        TransactionContext transactionContext = (TransactionContext)this.threadContextMap.get(threadId);
        if (transactionContext == null) {
            throw new IllegalStateException("No TransactionContext associated with current thread: " + threadId);
        }
        return transactionContext;
    }

    public String getGroupName() {
        return this.groupName;
    }

    private Transaction getTransaction(TransactionContext context) {
        return ((XATransactionContextImpl)context).getTransaction();
    }

    private long currentThreadId() {
        return Thread.currentThread().getId();
    }

    @Override
    public String toString() {
        return "HazelcastXaResource {" + this.groupName + '}';
    }
}

