/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.transaction.impl.xa;

import com.hazelcast.core.DistributedObject;
import com.hazelcast.internal.partition.InternalPartitionService;
import com.hazelcast.spi.ManagedService;
import com.hazelcast.spi.MigrationAwareService;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.PartitionMigrationEvent;
import com.hazelcast.spi.PartitionReplicationEvent;
import com.hazelcast.spi.RemoteService;
import com.hazelcast.spi.impl.NodeEngineImpl;
import com.hazelcast.spi.partition.MigrationEndpoint;
import com.hazelcast.transaction.TransactionContext;
import com.hazelcast.transaction.impl.xa.SerializableXID;
import com.hazelcast.transaction.impl.xa.XAResourceImpl;
import com.hazelcast.transaction.impl.xa.XATransaction;
import com.hazelcast.transaction.impl.xa.XATransactionContextImpl;
import com.hazelcast.transaction.impl.xa.XATransactionDTO;
import com.hazelcast.transaction.impl.xa.operations.XaReplicationOperation;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.transaction.xa.Xid;

public class XAService
implements ManagedService,
RemoteService,
MigrationAwareService {
    public static final String SERVICE_NAME = "hz:impl:xaService";
    private final NodeEngineImpl nodeEngine;
    private final XAResourceImpl xaResource;
    private final ConcurrentMap<SerializableXID, List<XATransaction>> transactions = new ConcurrentHashMap<SerializableXID, List<XATransaction>>();

    public XAService(NodeEngineImpl nodeEngine) {
        this.nodeEngine = nodeEngine;
        this.xaResource = new XAResourceImpl((NodeEngine)nodeEngine, this);
    }

    @Override
    public void init(NodeEngine nodeEngine, Properties properties) {
    }

    @Override
    public void reset() {
    }

    @Override
    public void shutdown(boolean terminate) {
    }

    @Override
    public DistributedObject createDistributedObject(String objectName) {
        return this.xaResource;
    }

    @Override
    public void destroyDistributedObject(String objectName) {
    }

    public TransactionContext newXATransactionContext(Xid xid, String ownerUuid, int timeout, boolean originatedFromClient) {
        return new XATransactionContextImpl(this.nodeEngine, xid, ownerUuid, timeout, originatedFromClient);
    }

    public void putTransaction(XATransaction transaction) {
        SerializableXID xid = transaction.getXid();
        CopyOnWriteArrayList<XATransaction> list = (CopyOnWriteArrayList<XATransaction>)this.transactions.get(xid);
        if (list == null) {
            list = new CopyOnWriteArrayList<XATransaction>();
            this.transactions.put(xid, list);
        }
        list.add(transaction);
    }

    public List<XATransaction> removeTransactions(SerializableXID xid) {
        return (List)this.transactions.remove(xid);
    }

    public Set<SerializableXID> getPreparedXids() {
        return this.transactions.keySet();
    }

    @Override
    public Operation prepareReplicationOperation(PartitionReplicationEvent event) {
        if (event.getReplicaIndex() > 1) {
            return null;
        }
        ArrayList<XATransactionDTO> migrationData = new ArrayList<XATransactionDTO>();
        InternalPartitionService partitionService = this.nodeEngine.getPartitionService();
        for (Map.Entry entry : this.transactions.entrySet()) {
            SerializableXID xid = (SerializableXID)entry.getKey();
            int partitionId = partitionService.getPartitionId(xid);
            List xaTransactionList = (List)entry.getValue();
            for (XATransaction xaTransaction : xaTransactionList) {
                if (partitionId != event.getPartitionId()) continue;
                migrationData.add(new XATransactionDTO(xaTransaction));
            }
        }
        if (migrationData.isEmpty()) {
            return null;
        }
        return new XaReplicationOperation(migrationData, event.getPartitionId(), event.getReplicaIndex());
    }

    @Override
    public void beforeMigration(PartitionMigrationEvent event) {
    }

    @Override
    public void commitMigration(PartitionMigrationEvent event) {
        int thresholdReplicaIndex;
        if (event.getMigrationEndpoint() == MigrationEndpoint.SOURCE && ((thresholdReplicaIndex = event.getNewReplicaIndex()) == -1 || thresholdReplicaIndex > 1)) {
            this.clearPartitionReplica(event.getPartitionId());
        }
    }

    @Override
    public void rollbackMigration(PartitionMigrationEvent event) {
        int thresholdReplicaIndex;
        if (event.getMigrationEndpoint() == MigrationEndpoint.DESTINATION && ((thresholdReplicaIndex = event.getCurrentReplicaIndex()) == -1 || thresholdReplicaIndex > 1)) {
            this.clearPartitionReplica(event.getPartitionId());
        }
    }

    private void clearPartitionReplica(int partitionId) {
        InternalPartitionService partitionService = this.nodeEngine.getPartitionService();
        Iterator iterator = this.transactions.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = iterator.next();
            SerializableXID xid = (SerializableXID)entry.getKey();
            int xidPartitionId = partitionService.getPartitionId(xid);
            if (xidPartitionId != partitionId) continue;
            iterator.remove();
        }
    }
}

