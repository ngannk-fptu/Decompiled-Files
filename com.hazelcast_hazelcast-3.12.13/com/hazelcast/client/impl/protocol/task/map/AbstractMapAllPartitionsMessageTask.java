/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.map;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.task.AbstractAllPartitionsMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.map.impl.MapService;
import com.hazelcast.map.impl.MapServiceContext;
import com.hazelcast.map.impl.operation.MapOperationProvider;
import com.hazelcast.nio.Connection;

abstract class AbstractMapAllPartitionsMessageTask<P>
extends AbstractAllPartitionsMessageTask<P> {
    AbstractMapAllPartitionsMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    protected final MapOperationProvider getOperationProvider(String mapName) {
        MapService mapService = (MapService)this.getService("hz:impl:mapService");
        MapServiceContext mapServiceContext = mapService.getMapServiceContext();
        return mapServiceContext.getMapOperationProvider(mapName);
    }
}

