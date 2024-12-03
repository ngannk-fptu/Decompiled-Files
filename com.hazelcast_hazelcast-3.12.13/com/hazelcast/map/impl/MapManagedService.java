/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl;

import com.hazelcast.concurrent.lock.LockService;
import com.hazelcast.concurrent.lock.LockStoreInfo;
import com.hazelcast.map.impl.MapContainer;
import com.hazelcast.map.impl.MapServiceContext;
import com.hazelcast.spi.ManagedService;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.ObjectNamespace;
import com.hazelcast.util.ConstructorFunction;
import java.util.Properties;

public class MapManagedService
implements ManagedService {
    private final MapServiceContext mapServiceContext;

    MapManagedService(MapServiceContext mapServiceContext) {
        this.mapServiceContext = mapServiceContext;
    }

    @Override
    public void init(NodeEngine nodeEngine, Properties properties) {
        LockService lockService = (LockService)nodeEngine.getSharedService("hz:impl:lockService");
        if (lockService != null) {
            lockService.registerLockStoreConstructor("hz:impl:mapService", new ObjectNamespaceLockStoreInfoConstructorFunction());
        }
        this.mapServiceContext.initPartitionsContainers();
    }

    @Override
    public void reset() {
        this.mapServiceContext.reset();
    }

    @Override
    public void shutdown(boolean terminate) {
        if (!terminate) {
            this.mapServiceContext.flushMaps();
            this.mapServiceContext.destroyMapStores();
        }
        this.mapServiceContext.shutdown();
    }

    private class ObjectNamespaceLockStoreInfoConstructorFunction
    implements ConstructorFunction<ObjectNamespace, LockStoreInfo> {
        private ObjectNamespaceLockStoreInfoConstructorFunction() {
        }

        @Override
        public LockStoreInfo createNew(ObjectNamespace key) {
            final MapContainer mapContainer = MapManagedService.this.mapServiceContext.getMapContainer(key.getObjectName());
            return new LockStoreInfo(){

                @Override
                public int getBackupCount() {
                    return mapContainer.getBackupCount();
                }

                @Override
                public int getAsyncBackupCount() {
                    return mapContainer.getAsyncBackupCount();
                }
            };
        }
    }
}

