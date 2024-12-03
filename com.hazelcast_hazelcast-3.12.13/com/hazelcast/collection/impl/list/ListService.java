/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.collection.impl.list;

import com.hazelcast.collection.impl.collection.CollectionContainer;
import com.hazelcast.collection.impl.collection.CollectionService;
import com.hazelcast.collection.impl.list.ListContainer;
import com.hazelcast.collection.impl.list.ListProxyImpl;
import com.hazelcast.collection.impl.list.operations.ListReplicationOperation;
import com.hazelcast.collection.impl.txnlist.TransactionalListProxy;
import com.hazelcast.config.ListConfig;
import com.hazelcast.core.DistributedObject;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.PartitionReplicationEvent;
import com.hazelcast.transaction.impl.Transaction;
import com.hazelcast.util.ConcurrencyUtil;
import com.hazelcast.util.ConstructorFunction;
import com.hazelcast.util.ContextMutexFactory;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ListService
extends CollectionService {
    public static final String SERVICE_NAME = "hz:impl:listService";
    private static final Object NULL_OBJECT = new Object();
    private final ConcurrentMap<String, ListContainer> containerMap = new ConcurrentHashMap<String, ListContainer>();
    private final ConcurrentMap<String, Object> quorumConfigCache = new ConcurrentHashMap<String, Object>();
    private final ContextMutexFactory quorumConfigCacheMutexFactory = new ContextMutexFactory();
    private final ConstructorFunction<String, Object> quorumConfigConstructor = new ConstructorFunction<String, Object>(){

        @Override
        public Object createNew(String name) {
            ListConfig lockConfig = ListService.this.nodeEngine.getConfig().findListConfig(name);
            String quorumName = lockConfig.getQuorumName();
            return quorumName == null ? NULL_OBJECT : quorumName;
        }
    };

    public ListService(NodeEngine nodeEngine) {
        super(nodeEngine);
    }

    @Override
    public ListContainer getOrCreateContainer(String name, boolean backup) {
        ListContainer current;
        ListContainer container = (ListContainer)this.containerMap.get(name);
        if (container == null && (current = this.containerMap.putIfAbsent(name, container = new ListContainer(name, this.nodeEngine))) != null) {
            container = current;
        }
        return container;
    }

    @Override
    public ConcurrentMap<String, ? extends CollectionContainer> getContainerMap() {
        return this.containerMap;
    }

    @Override
    public String getServiceName() {
        return SERVICE_NAME;
    }

    @Override
    public DistributedObject createDistributedObject(String objectId) {
        return new ListProxyImpl(objectId, this.nodeEngine, this);
    }

    @Override
    public void destroyDistributedObject(String name) {
        super.destroyDistributedObject(name);
        this.quorumConfigCache.remove(name);
    }

    public TransactionalListProxy createTransactionalObject(String name, Transaction transaction) {
        return new TransactionalListProxy(name, transaction, this.nodeEngine, this);
    }

    @Override
    public Operation prepareReplicationOperation(PartitionReplicationEvent event) {
        Map<String, CollectionContainer> migrationData = this.getMigrationData(event);
        return migrationData.isEmpty() ? null : new ListReplicationOperation(migrationData, event.getPartitionId(), event.getReplicaIndex());
    }

    @Override
    public String getQuorumName(String name) {
        Object quorumName = ConcurrencyUtil.getOrPutSynchronized(this.quorumConfigCache, name, this.quorumConfigCacheMutexFactory, this.quorumConfigConstructor);
        return quorumName == NULL_OBJECT ? null : (String)quorumName;
    }
}

