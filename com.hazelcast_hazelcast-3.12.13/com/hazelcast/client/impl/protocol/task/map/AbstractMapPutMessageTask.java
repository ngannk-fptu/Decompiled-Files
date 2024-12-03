/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.map;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.task.map.AbstractMapPartitionMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.map.impl.MapContainer;
import com.hazelcast.map.impl.MapService;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.MapPermission;
import java.security.Permission;

public abstract class AbstractMapPutMessageTask<P>
extends AbstractMapPartitionMessageTask<P> {
    protected transient long startTimeNanos;

    protected AbstractMapPutMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected void beforeProcess() {
        this.startTimeNanos = System.nanoTime();
    }

    @Override
    protected void beforeResponse() {
        long latencyNanos = System.nanoTime() - this.startTimeNanos;
        MapService mapService = (MapService)this.getService("hz:impl:mapService");
        MapContainer mapContainer = mapService.getMapServiceContext().getMapContainer(this.getDistributedObjectName());
        if (mapContainer.getMapConfig().isStatisticsEnabled()) {
            mapService.getMapServiceContext().getLocalMapStatsProvider().getLocalMapStatsImpl(this.getDistributedObjectName()).incrementPutLatencyNanos(latencyNanos);
        }
    }

    @Override
    public Permission getRequiredPermission() {
        return new MapPermission(this.getDistributedObjectName(), "put");
    }

    @Override
    public String getServiceName() {
        return "hz:impl:mapService";
    }
}

