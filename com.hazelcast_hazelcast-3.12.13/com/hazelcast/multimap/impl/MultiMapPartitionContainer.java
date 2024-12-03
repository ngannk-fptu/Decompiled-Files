/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.multimap.impl;

import com.hazelcast.concurrent.lock.LockService;
import com.hazelcast.config.MultiMapConfig;
import com.hazelcast.multimap.impl.MultiMapContainer;
import com.hazelcast.multimap.impl.MultiMapService;
import com.hazelcast.spi.DistributedObjectNamespace;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.ServiceNamespace;
import com.hazelcast.util.ConcurrencyUtil;
import com.hazelcast.util.ConstructorFunction;
import com.hazelcast.util.MapUtil;
import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.ConcurrentMap;

public class MultiMapPartitionContainer {
    final ConcurrentMap<String, MultiMapContainer> containerMap = MapUtil.createConcurrentHashMap(1000);
    final MultiMapService service;
    final int partitionId;
    private final ConstructorFunction<String, MultiMapContainer> containerConstructor = new ConstructorFunction<String, MultiMapContainer>(){

        @Override
        public MultiMapContainer createNew(String name) {
            return new MultiMapContainer(name, MultiMapPartitionContainer.this.service, MultiMapPartitionContainer.this.partitionId);
        }
    };

    MultiMapPartitionContainer(MultiMapService service, int partitionId) {
        this.service = service;
        this.partitionId = partitionId;
    }

    public MultiMapContainer getOrCreateMultiMapContainer(String name) {
        return this.getOrCreateMultiMapContainer(name, true);
    }

    public MultiMapContainer getOrCreateMultiMapContainer(String name, boolean isAccess) {
        MultiMapContainer container = ConcurrencyUtil.getOrPutIfAbsent(this.containerMap, name, this.containerConstructor);
        if (isAccess) {
            container.access();
        }
        return container;
    }

    public MultiMapContainer getMultiMapContainerWithoutAccess(String name) {
        return this.getMultiMapContainer(name, false);
    }

    public MultiMapContainer getMultiMapContainer(String name) {
        return this.getMultiMapContainer(name, true);
    }

    private MultiMapContainer getMultiMapContainer(String name, boolean isAccess) {
        MultiMapContainer container = (MultiMapContainer)this.containerMap.get(name);
        if (container != null && isAccess) {
            container.access();
        }
        return container;
    }

    public Collection<ServiceNamespace> getAllNamespaces(int replicaIndex) {
        HashSet<ServiceNamespace> namespaces = new HashSet<ServiceNamespace>();
        for (MultiMapContainer container : this.containerMap.values()) {
            MultiMapConfig config = container.getConfig();
            if (config.getTotalBackupCount() < replicaIndex) continue;
            namespaces.add(container.getObjectNamespace());
        }
        return namespaces;
    }

    void destroyMultiMap(String name) {
        MultiMapContainer container = (MultiMapContainer)this.containerMap.remove(name);
        if (container != null) {
            container.destroy();
        } else {
            this.clearLockStore(name);
        }
    }

    private void clearLockStore(String name) {
        NodeEngine nodeEngine = this.service.getNodeEngine();
        LockService lockService = (LockService)nodeEngine.getSharedService("hz:impl:lockService");
        if (lockService != null) {
            DistributedObjectNamespace namespace = new DistributedObjectNamespace("hz:impl:multiMapService", name);
            lockService.clearLockStore(this.partitionId, namespace);
        }
    }

    void destroy() {
        for (MultiMapContainer container : this.containerMap.values()) {
            container.destroy();
        }
        this.containerMap.clear();
    }
}

