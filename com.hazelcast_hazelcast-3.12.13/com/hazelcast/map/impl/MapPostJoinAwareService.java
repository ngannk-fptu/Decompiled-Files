/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl;

import com.hazelcast.map.impl.MapContainer;
import com.hazelcast.map.impl.MapServiceContext;
import com.hazelcast.map.impl.operation.PostJoinMapOperation;
import com.hazelcast.map.impl.querycache.accumulator.AccumulatorInfo;
import com.hazelcast.map.impl.querycache.publisher.MapPublisherRegistry;
import com.hazelcast.map.impl.querycache.publisher.PartitionAccumulatorRegistry;
import com.hazelcast.map.impl.querycache.publisher.PublisherContext;
import com.hazelcast.map.impl.querycache.publisher.PublisherRegistry;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.PostJoinAwareService;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

class MapPostJoinAwareService
implements PostJoinAwareService {
    private final MapServiceContext mapServiceContext;

    public MapPostJoinAwareService(MapServiceContext mapServiceContext) {
        this.mapServiceContext = mapServiceContext;
    }

    @Override
    public Operation getPostJoinOperation() {
        PostJoinMapOperation postJoinOp = new PostJoinMapOperation();
        Map<String, MapContainer> mapContainers = this.mapServiceContext.getMapContainers();
        for (MapContainer mapContainer : mapContainers.values()) {
            postJoinOp.addMapInterceptors(mapContainer);
        }
        List<AccumulatorInfo> infoList = this.getAccumulatorInfoList();
        postJoinOp.setInfoList(infoList);
        postJoinOp.setNodeEngine(this.mapServiceContext.getNodeEngine());
        return postJoinOp;
    }

    private List<AccumulatorInfo> getAccumulatorInfoList() {
        ArrayList<AccumulatorInfo> infoList = new ArrayList<AccumulatorInfo>();
        PublisherContext publisherContext = this.mapServiceContext.getQueryCacheContext().getPublisherContext();
        MapPublisherRegistry mapPublisherRegistry = publisherContext.getMapPublisherRegistry();
        Map<String, PublisherRegistry> cachesOfMaps = mapPublisherRegistry.getAll();
        Collection<PublisherRegistry> publisherRegistries = cachesOfMaps.values();
        for (PublisherRegistry publisherRegistry : publisherRegistries) {
            Collection<PartitionAccumulatorRegistry> partitionAccumulatorRegistries = publisherRegistry.getAll().values();
            for (PartitionAccumulatorRegistry accumulatorRegistry : partitionAccumulatorRegistries) {
                AccumulatorInfo info = accumulatorRegistry.getInfo();
                infoList.add(info);
            }
        }
        return infoList;
    }
}

