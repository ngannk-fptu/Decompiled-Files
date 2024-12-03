/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.map;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.MapSetTtlCodec;
import com.hazelcast.client.impl.protocol.task.map.AbstractMapPartitionMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.internal.cluster.Versions;
import com.hazelcast.map.impl.operation.MapOperationProvider;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.MapPermission;
import com.hazelcast.spi.Operation;
import java.security.Permission;
import java.util.concurrent.TimeUnit;

public class MapSetTtlMessageTask
extends AbstractMapPartitionMessageTask<MapSetTtlCodec.RequestParameters> {
    public MapSetTtlMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected void beforeProcess() {
        super.beforeProcess();
        if (this.nodeEngine.getClusterService().getClusterVersion().isLessThan(Versions.V3_11)) {
            throw new UnsupportedOperationException("Modifying TTL is available when cluster version is 3.11 or higher");
        }
    }

    @Override
    protected Operation prepareOperation() {
        MapOperationProvider operationProvider = this.getMapOperationProvider(((MapSetTtlCodec.RequestParameters)this.parameters).name);
        return operationProvider.createSetTtlOperation(((MapSetTtlCodec.RequestParameters)this.parameters).name, ((MapSetTtlCodec.RequestParameters)this.parameters).key, ((MapSetTtlCodec.RequestParameters)this.parameters).ttl);
    }

    @Override
    protected MapSetTtlCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return MapSetTtlCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return MapSetTtlCodec.encodeResponse((Boolean)response);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:mapService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new MapPermission(((MapSetTtlCodec.RequestParameters)this.parameters).name, "remove");
    }

    @Override
    public String getDistributedObjectName() {
        return ((MapSetTtlCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "setTtl";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((MapSetTtlCodec.RequestParameters)this.parameters).key, ((MapSetTtlCodec.RequestParameters)this.parameters).ttl, TimeUnit.MILLISECONDS};
    }
}

