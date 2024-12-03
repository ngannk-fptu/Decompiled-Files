/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.concurrent.lock;

import com.hazelcast.concurrent.lock.InternalLockNamespace;
import com.hazelcast.concurrent.lock.LockProxy;
import com.hazelcast.concurrent.lock.LockResource;
import com.hazelcast.concurrent.lock.LockService;
import com.hazelcast.concurrent.lock.LockStore;
import com.hazelcast.concurrent.lock.LockStoreContainer;
import com.hazelcast.concurrent.lock.LockStoreImpl;
import com.hazelcast.concurrent.lock.LockStoreInfo;
import com.hazelcast.concurrent.lock.LockStoreProxy;
import com.hazelcast.concurrent.lock.operations.LocalLockCleanupOperation;
import com.hazelcast.concurrent.lock.operations.LockReplicationOperation;
import com.hazelcast.concurrent.lock.operations.UnlockOperation;
import com.hazelcast.config.LockConfig;
import com.hazelcast.core.DistributedObject;
import com.hazelcast.instance.MemberImpl;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.partition.strategy.StringPartitioningStrategy;
import com.hazelcast.spi.ClientAwareService;
import com.hazelcast.spi.FragmentedMigrationAwareService;
import com.hazelcast.spi.ManagedService;
import com.hazelcast.spi.MemberAttributeServiceEvent;
import com.hazelcast.spi.MembershipAwareService;
import com.hazelcast.spi.MembershipServiceEvent;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.ObjectNamespace;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.OperationService;
import com.hazelcast.spi.PartitionMigrationEvent;
import com.hazelcast.spi.PartitionReplicationEvent;
import com.hazelcast.spi.QuorumAwareService;
import com.hazelcast.spi.RemoteService;
import com.hazelcast.spi.ServiceNamespace;
import com.hazelcast.spi.impl.PartitionSpecificRunnable;
import com.hazelcast.spi.impl.operationservice.InternalOperationService;
import com.hazelcast.spi.partition.MigrationEndpoint;
import com.hazelcast.spi.properties.GroupProperty;
import com.hazelcast.spi.properties.HazelcastProperties;
import com.hazelcast.util.Clock;
import com.hazelcast.util.ConcurrencyUtil;
import com.hazelcast.util.ConstructorFunction;
import com.hazelcast.util.ContextMutexFactory;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class LockServiceImpl
implements LockService,
ManagedService,
RemoteService,
MembershipAwareService,
FragmentedMigrationAwareService,
ClientAwareService,
QuorumAwareService {
    private static final Object NULL_OBJECT = new Object();
    private final NodeEngine nodeEngine;
    private final LockStoreContainer[] containers;
    private final ConcurrentMap<String, ConstructorFunction<ObjectNamespace, LockStoreInfo>> constructors = new ConcurrentHashMap<String, ConstructorFunction<ObjectNamespace, LockStoreInfo>>();
    private final ConcurrentMap<String, Object> quorumConfigCache = new ConcurrentHashMap<String, Object>();
    private final ContextMutexFactory quorumConfigCacheMutexFactory = new ContextMutexFactory();
    private final ConstructorFunction<String, Object> quorumConfigConstructor = new ConstructorFunction<String, Object>(){

        @Override
        public Object createNew(String name) {
            LockConfig lockConfig = LockServiceImpl.this.nodeEngine.getConfig().findLockConfig(name);
            String quorumName = lockConfig.getQuorumName();
            return quorumName == null ? NULL_OBJECT : quorumName;
        }
    };
    private final long maxLeaseTimeInMillis;

    public LockServiceImpl(NodeEngine nodeEngine) {
        this.nodeEngine = nodeEngine;
        this.containers = new LockStoreContainer[nodeEngine.getPartitionService().getPartitionCount()];
        for (int i = 0; i < this.containers.length; ++i) {
            this.containers[i] = new LockStoreContainer(this, i);
        }
        this.maxLeaseTimeInMillis = LockServiceImpl.getMaxLeaseTimeInMillis(nodeEngine.getProperties());
    }

    NodeEngine getNodeEngine() {
        return this.nodeEngine;
    }

    @Override
    public void init(NodeEngine nodeEngine, Properties properties) {
        this.registerLockStoreConstructor("hz:impl:lockService", new ConstructorFunction<ObjectNamespace, LockStoreInfo>(){

            @Override
            public LockStoreInfo createNew(ObjectNamespace key) {
                return new LockStoreInfo(){

                    @Override
                    public int getBackupCount() {
                        return 1;
                    }

                    @Override
                    public int getAsyncBackupCount() {
                        return 0;
                    }
                };
            }
        });
    }

    @Override
    public void reset() {
        for (LockStoreContainer container : this.containers) {
            for (LockStoreImpl lockStore : container.getLockStores()) {
                lockStore.clear();
            }
        }
    }

    @Override
    public void shutdown(boolean terminate) {
        for (LockStoreContainer container : this.containers) {
            container.clear();
        }
    }

    @Override
    public long getMaxLeaseTimeInMillis() {
        return this.maxLeaseTimeInMillis;
    }

    @Override
    public void registerLockStoreConstructor(String serviceName, ConstructorFunction<ObjectNamespace, LockStoreInfo> constructorFunction) {
        boolean put;
        boolean bl = put = this.constructors.putIfAbsent(serviceName, constructorFunction) == null;
        if (!put) {
            throw new IllegalArgumentException("LockStore constructor for service[" + serviceName + "] is already registered!");
        }
    }

    ConstructorFunction<ObjectNamespace, LockStoreInfo> getConstructor(String serviceName) {
        return (ConstructorFunction)this.constructors.get(serviceName);
    }

    @Override
    public LockStore createLockStore(int partitionId, ObjectNamespace namespace) {
        LockStoreContainer container = this.getLockContainer(partitionId);
        container.getOrCreateLockStore(namespace);
        return new LockStoreProxy(container, namespace);
    }

    @Override
    public void clearLockStore(int partitionId, ObjectNamespace namespace) {
        LockStoreContainer container = this.getLockContainer(partitionId);
        container.clearLockStore(namespace);
    }

    public LockStoreContainer getLockContainer(int partitionId) {
        return this.containers[partitionId];
    }

    public LockStoreImpl getLockStore(int partitionId, ObjectNamespace namespace) {
        return this.getLockContainer(partitionId).getOrCreateLockStore(namespace);
    }

    @Override
    public void memberAdded(MembershipServiceEvent event) {
    }

    @Override
    public void memberRemoved(MembershipServiceEvent event) {
        MemberImpl member = event.getMember();
        String uuid = member.getUuid();
        this.releaseLocksOwnedBy(uuid);
    }

    @Override
    public void memberAttributeChanged(MemberAttributeServiceEvent event) {
    }

    private void releaseLocksOwnedBy(final String uuid) {
        final InternalOperationService operationService = (InternalOperationService)this.nodeEngine.getOperationService();
        for (final LockStoreContainer container : this.containers) {
            operationService.execute(new PartitionSpecificRunnable(){

                @Override
                public void run() {
                    for (LockStoreImpl lockStore : container.getLockStores()) {
                        LockServiceImpl.this.cleanUpLock(operationService, uuid, container.getPartitionId(), lockStore);
                    }
                }

                @Override
                public int getPartitionId() {
                    return container.getPartitionId();
                }
            });
        }
    }

    private void cleanUpLock(OperationService operationService, String uuid, int partitionId, LockStoreImpl lockStore) {
        Collection<LockResource> locks = lockStore.getLocks();
        for (LockResource lock : locks) {
            Data key = lock.getKey();
            if (uuid.equals(lock.getOwner()) && !lock.isTransactional()) {
                UnlockOperation op = this.createLockCleanupOperation(partitionId, lockStore.getNamespace(), key, uuid);
                operationService.invokeOnTarget("hz:impl:lockService", op, this.nodeEngine.getThisAddress());
            }
            lockStore.cleanWaitersAndSignalsFor(key, uuid);
        }
    }

    private UnlockOperation createLockCleanupOperation(int partitionId, ObjectNamespace namespace, Data key, String uuid) {
        LocalLockCleanupOperation op = new LocalLockCleanupOperation(namespace, key, uuid);
        op.setAsyncBackup(true);
        op.setNodeEngine(this.nodeEngine);
        op.setServiceName("hz:impl:lockService");
        op.setService(this);
        op.setPartitionId(partitionId);
        op.setValidateTarget(false);
        return op;
    }

    @Override
    public Collection<LockResource> getAllLocks() {
        LinkedList<LockResource> locks = new LinkedList<LockResource>();
        for (LockStoreContainer container : this.containers) {
            for (LockStoreImpl lockStore : container.getLockStores()) {
                locks.addAll(lockStore.getLocks());
            }
        }
        return locks;
    }

    @Override
    public Collection<ServiceNamespace> getAllServiceNamespaces(PartitionReplicationEvent event) {
        int partitionId = event.getPartitionId();
        LockStoreContainer container = this.containers[partitionId];
        return container.getAllNamespaces(event.getReplicaIndex());
    }

    @Override
    public boolean isKnownServiceNamespace(ServiceNamespace namespace) {
        return namespace instanceof ObjectNamespace;
    }

    @Override
    public void beforeMigration(PartitionMigrationEvent partitionMigrationEvent) {
    }

    @Override
    public Operation prepareReplicationOperation(PartitionReplicationEvent event) {
        int replicaIndex;
        int partitionId = event.getPartitionId();
        LockStoreContainer container = this.containers[partitionId];
        LockReplicationOperation op = new LockReplicationOperation(container, partitionId, replicaIndex = event.getReplicaIndex());
        return op.isEmpty() ? null : op;
    }

    @Override
    public Operation prepareReplicationOperation(PartitionReplicationEvent event, Collection<ServiceNamespace> namespaces) {
        int replicaIndex;
        int partitionId = event.getPartitionId();
        LockStoreContainer container = this.containers[partitionId];
        LockReplicationOperation op = new LockReplicationOperation(container, partitionId, replicaIndex = event.getReplicaIndex(), namespaces);
        return op.isEmpty() ? null : op;
    }

    @Override
    public void commitMigration(PartitionMigrationEvent event) {
        if (event.getMigrationEndpoint() == MigrationEndpoint.SOURCE) {
            this.clearLockStoresHavingLesserBackupCountThan(event.getPartitionId(), event.getNewReplicaIndex());
        } else {
            this.scheduleEvictions(event.getPartitionId());
        }
        this.removeLocalLocks(event.getPartitionId());
    }

    private void removeLocalLocks(int partitionId) {
        LockStoreContainer container = this.containers[partitionId];
        for (LockStoreImpl lockStore : container.getLockStores()) {
            lockStore.removeLocalLocks();
        }
    }

    private void scheduleEvictions(int partitionId) {
        long now = Clock.currentTimeMillis();
        LockStoreContainer container = this.containers[partitionId];
        for (LockStoreImpl ls : container.getLockStores()) {
            for (LockResource lock : ls.getLocks()) {
                long expirationTime = lock.getExpirationTime();
                if (expirationTime == Long.MAX_VALUE || expirationTime < 0L) continue;
                long leaseTime = expirationTime - now;
                if (leaseTime <= 0L) {
                    ls.forceUnlock(lock.getKey());
                    continue;
                }
                ls.scheduleEviction(lock.getKey(), lock.getVersion(), leaseTime);
            }
        }
    }

    @Override
    public void rollbackMigration(PartitionMigrationEvent event) {
        if (event.getMigrationEndpoint() == MigrationEndpoint.DESTINATION) {
            this.clearLockStoresHavingLesserBackupCountThan(event.getPartitionId(), event.getCurrentReplicaIndex());
        }
    }

    private void clearLockStoresHavingLesserBackupCountThan(int partitionId, int thresholdReplicaIndex) {
        LockStoreContainer container = this.containers[partitionId];
        for (LockStoreImpl lockStore : container.getLockStores()) {
            if (thresholdReplicaIndex >= 0 && thresholdReplicaIndex <= lockStore.getTotalBackupCount()) continue;
            lockStore.clear();
        }
    }

    @Override
    public DistributedObject createDistributedObject(String objectId) {
        return new LockProxy(this.nodeEngine, this, objectId);
    }

    @Override
    public void destroyDistributedObject(String objectId) {
        Object key = this.nodeEngine.getSerializationService().toData(objectId, StringPartitioningStrategy.INSTANCE);
        int partitionId = this.nodeEngine.getPartitionService().getPartitionId((Data)key);
        final LockStoreImpl lockStore = this.containers[partitionId].getLockStore(new InternalLockNamespace(objectId));
        if (lockStore != null) {
            InternalOperationService operationService = (InternalOperationService)this.nodeEngine.getOperationService();
            operationService.execute(new PartitionSpecificRunnable((Data)key, partitionId){
                final /* synthetic */ Data val$key;
                final /* synthetic */ int val$partitionId;
                {
                    this.val$key = data;
                    this.val$partitionId = n;
                }

                @Override
                public void run() {
                    lockStore.forceUnlock(this.val$key);
                }

                @Override
                public int getPartitionId() {
                    return this.val$partitionId;
                }
            });
        }
        this.quorumConfigCache.remove(objectId);
    }

    @Override
    public void clientDisconnected(String clientUuid) {
        this.releaseLocksOwnedBy(clientUuid);
    }

    public static long getMaxLeaseTimeInMillis(HazelcastProperties hazelcastProperties) {
        return hazelcastProperties.getMillis(GroupProperty.LOCK_MAX_LEASE_TIME_SECONDS);
    }

    @Override
    public String getQuorumName(String name) {
        Object quorumName = ConcurrencyUtil.getOrPutSynchronized(this.quorumConfigCache, name, this.quorumConfigCacheMutexFactory, this.quorumConfigConstructor);
        return quorumName == NULL_OBJECT ? null : (String)quorumName;
    }
}

