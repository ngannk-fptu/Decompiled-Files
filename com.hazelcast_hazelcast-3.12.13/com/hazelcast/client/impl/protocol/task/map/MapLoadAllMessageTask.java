/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.map;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.MapLoadAllCodec;
import com.hazelcast.client.impl.protocol.task.AbstractCallableMessageTask;
import com.hazelcast.client.impl.protocol.task.BlockingMessageTask;
import com.hazelcast.core.DistributedObject;
import com.hazelcast.instance.Node;
import com.hazelcast.map.impl.MapService;
import com.hazelcast.map.impl.proxy.MapProxyImpl;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.MapPermission;
import java.security.Permission;

public class MapLoadAllMessageTask
extends AbstractCallableMessageTask<MapLoadAllCodec.RequestParameters>
implements BlockingMessageTask {
    public MapLoadAllMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Object call() {
        MapService mapService = (MapService)this.getService("hz:impl:mapService");
        DistributedObject distributedObject = mapService.getMapServiceContext().getNodeEngine().getProxyService().getDistributedObject("hz:impl:mapService", ((MapLoadAllCodec.RequestParameters)this.parameters).name);
        MapProxyImpl mapProxy = (MapProxyImpl)distributedObject;
        mapProxy.loadAll(((MapLoadAllCodec.RequestParameters)this.parameters).replaceExistingValues);
        return null;
    }

    @Override
    protected MapLoadAllCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return MapLoadAllCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return MapLoadAllCodec.encodeResponse();
    }

    @Override
    public String getServiceName() {
        return "hz:impl:mapService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new MapPermission(((MapLoadAllCodec.RequestParameters)this.parameters).name, "remove");
    }

    @Override
    public String getDistributedObjectName() {
        return ((MapLoadAllCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "loadAll";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((MapLoadAllCodec.RequestParameters)this.parameters).replaceExistingValues};
    }
}

