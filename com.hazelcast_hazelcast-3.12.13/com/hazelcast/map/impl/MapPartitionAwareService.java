/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl;

import com.hazelcast.core.DistributedObject;
import com.hazelcast.map.impl.MapServiceContext;
import com.hazelcast.map.impl.proxy.MapProxyImpl;
import com.hazelcast.nio.Address;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.PartitionAwareService;
import com.hazelcast.spi.ProxyService;
import com.hazelcast.spi.partition.IPartitionLostEvent;
import java.util.Collection;

class MapPartitionAwareService
implements PartitionAwareService {
    private final MapServiceContext mapServiceContext;
    private final NodeEngine nodeEngine;
    private final ProxyService proxyService;

    public MapPartitionAwareService(MapServiceContext mapServiceContext) {
        this.mapServiceContext = mapServiceContext;
        this.nodeEngine = mapServiceContext.getNodeEngine();
        this.proxyService = this.nodeEngine.getProxyService();
    }

    @Override
    public void onPartitionLost(IPartitionLostEvent partitionLostEvent) {
        Address thisAddress = this.nodeEngine.getThisAddress();
        int partitionId = partitionLostEvent.getPartitionId();
        Collection<DistributedObject> result = this.proxyService.getDistributedObjects("hz:impl:mapService");
        for (DistributedObject object : result) {
            MapProxyImpl mapProxy = (MapProxyImpl)object;
            String mapName = mapProxy.getName();
            if (mapProxy.getTotalBackupCount() > partitionLostEvent.getLostReplicaIndex()) continue;
            this.mapServiceContext.getMapEventPublisher().publishMapPartitionLostEvent(thisAddress, mapName, partitionId);
        }
    }
}

