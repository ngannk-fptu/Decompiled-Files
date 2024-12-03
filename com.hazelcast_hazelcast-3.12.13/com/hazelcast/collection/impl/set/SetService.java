/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.collection.impl.set;

import com.hazelcast.collection.impl.collection.CollectionContainer;
import com.hazelcast.collection.impl.collection.CollectionService;
import com.hazelcast.collection.impl.set.SetContainer;
import com.hazelcast.collection.impl.set.SetProxyImpl;
import com.hazelcast.collection.impl.set.operations.SetReplicationOperation;
import com.hazelcast.collection.impl.txnset.TransactionalSetProxy;
import com.hazelcast.config.SetConfig;
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

public class SetService
extends CollectionService {
    public static final String SERVICE_NAME = "hz:impl:setService";
    private static final Object NULL_OBJECT = new Object();
    private final ConcurrentMap<String, SetContainer> containerMap = new ConcurrentHashMap<String, SetContainer>();
    private final ConcurrentMap<String, Object> quorumConfigCache = new ConcurrentHashMap<String, Object>();
    private final ContextMutexFactory quorumConfigCacheMutexFactory = new ContextMutexFactory();
    private final ConstructorFunction<String, Object> quorumConfigConstructor = new ConstructorFunction<String, Object>(){

        @Override
        public Object createNew(String name) {
            SetConfig lockConfig = SetService.this.nodeEngine.getConfig().findSetConfig(name);
            String quorumName = lockConfig.getQuorumName();
            return quorumName == null ? NULL_OBJECT : quorumName;
        }
    };

    public SetService(NodeEngine nodeEngine) {
        super(nodeEngine);
    }

    @Override
    public SetContainer getOrCreateContainer(String name, boolean backup) {
        SetContainer current;
        SetContainer container = (SetContainer)this.containerMap.get(name);
        if (container == null && (current = this.containerMap.putIfAbsent(name, container = new SetContainer(name, this.nodeEngine))) != null) {
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
        return new SetProxyImpl(objectId, this.nodeEngine, this);
    }

    @Override
    public void destroyDistributedObject(String name) {
        super.destroyDistributedObject(name);
        this.quorumConfigCache.remove(name);
    }

    public TransactionalSetProxy createTransactionalObject(String name, Transaction transaction) {
        return new TransactionalSetProxy(name, transaction, this.nodeEngine, this);
    }

    @Override
    public Operation prepareReplicationOperation(PartitionReplicationEvent event) {
        Map<String, CollectionContainer> migrationData = this.getMigrationData(event);
        return migrationData.isEmpty() ? null : new SetReplicationOperation(migrationData, event.getPartitionId(), event.getReplicaIndex());
    }

    @Override
    public String getQuorumName(String name) {
        Object quorumName = ConcurrencyUtil.getOrPutSynchronized(this.quorumConfigCache, name, this.quorumConfigCacheMutexFactory, this.quorumConfigConstructor);
        return quorumName == NULL_OBJECT ? null : (String)quorumName;
    }
}

