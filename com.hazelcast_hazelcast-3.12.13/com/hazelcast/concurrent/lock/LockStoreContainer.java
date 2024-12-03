/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.concurrent.lock;

import com.hazelcast.concurrent.lock.LockEvictionProcessor;
import com.hazelcast.concurrent.lock.LockServiceImpl;
import com.hazelcast.concurrent.lock.LockStoreImpl;
import com.hazelcast.concurrent.lock.LockStoreInfo;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.ObjectNamespace;
import com.hazelcast.spi.ServiceNamespace;
import com.hazelcast.spi.TaskScheduler;
import com.hazelcast.util.ConcurrencyUtil;
import com.hazelcast.util.ConstructorFunction;
import com.hazelcast.util.scheduler.EntryTaskScheduler;
import com.hazelcast.util.scheduler.EntryTaskSchedulerFactory;
import com.hazelcast.util.scheduler.ScheduleType;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class LockStoreContainer {
    private final LockServiceImpl lockService;
    private final int partitionId;
    private final ConcurrentMap<ObjectNamespace, LockStoreImpl> lockStores = new ConcurrentHashMap<ObjectNamespace, LockStoreImpl>();
    private final ConstructorFunction<ObjectNamespace, LockStoreImpl> lockStoreConstructor = new ConstructorFunction<ObjectNamespace, LockStoreImpl>(){

        @Override
        public LockStoreImpl createNew(ObjectNamespace namespace) {
            LockStoreInfo info;
            ConstructorFunction<ObjectNamespace, LockStoreInfo> ctor = LockStoreContainer.this.lockService.getConstructor(namespace.getServiceName());
            if (ctor != null && (info = ctor.createNew(namespace)) != null) {
                int backupCount = info.getBackupCount();
                int asyncBackupCount = info.getAsyncBackupCount();
                EntryTaskScheduler entryTaskScheduler = LockStoreContainer.this.createScheduler(namespace);
                return new LockStoreImpl(LockStoreContainer.this.lockService, namespace, entryTaskScheduler, backupCount, asyncBackupCount);
            }
            throw new IllegalArgumentException("No LockStore constructor is registered!");
        }
    };

    public LockStoreContainer(LockServiceImpl lockService, int partitionId) {
        this.lockService = lockService;
        this.partitionId = partitionId;
    }

    void clearLockStore(ObjectNamespace namespace) {
        LockStoreImpl lockStore = (LockStoreImpl)this.lockStores.remove(namespace);
        if (lockStore != null) {
            lockStore.clear();
        }
    }

    LockStoreImpl getOrCreateLockStore(ObjectNamespace namespace) {
        return ConcurrencyUtil.getOrPutIfAbsent(this.lockStores, namespace, this.lockStoreConstructor);
    }

    public LockStoreImpl getLockStore(ObjectNamespace namespace) {
        return (LockStoreImpl)this.lockStores.get(namespace);
    }

    public Collection<LockStoreImpl> getLockStores() {
        return Collections.unmodifiableCollection(this.lockStores.values());
    }

    void clear() {
        for (LockStoreImpl lockStore : this.lockStores.values()) {
            lockStore.clear();
        }
        this.lockStores.clear();
    }

    int getPartitionId() {
        return this.partitionId;
    }

    public void put(LockStoreImpl ls) {
        ls.setLockService(this.lockService);
        ls.setEntryTaskScheduler(this.createScheduler(ls.getNamespace()));
        this.lockStores.put(ls.getNamespace(), ls);
    }

    private EntryTaskScheduler<Data, Integer> createScheduler(ObjectNamespace namespace) {
        NodeEngine nodeEngine = this.lockService.getNodeEngine();
        LockEvictionProcessor entryProcessor = new LockEvictionProcessor(nodeEngine, namespace);
        TaskScheduler globalScheduler = nodeEngine.getExecutionService().getGlobalTaskScheduler();
        return EntryTaskSchedulerFactory.newScheduler(globalScheduler, entryProcessor, ScheduleType.FOR_EACH);
    }

    public Collection<ServiceNamespace> getAllNamespaces(int replicaIndex) {
        HashSet<ServiceNamespace> namespaces = new HashSet<ServiceNamespace>();
        for (LockStoreImpl lockStore : this.lockStores.values()) {
            if (lockStore.getTotalBackupCount() < replicaIndex) continue;
            namespaces.add(lockStore.getNamespace());
        }
        return namespaces;
    }
}

