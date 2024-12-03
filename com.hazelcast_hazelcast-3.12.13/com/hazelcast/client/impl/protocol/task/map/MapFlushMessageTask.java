/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.map;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.MapFlushCodec;
import com.hazelcast.client.impl.protocol.task.AbstractCallableMessageTask;
import com.hazelcast.client.impl.protocol.task.BlockingMessageTask;
import com.hazelcast.core.DistributedObject;
import com.hazelcast.instance.Node;
import com.hazelcast.map.impl.MapService;
import com.hazelcast.map.impl.MapServiceContext;
import com.hazelcast.map.impl.proxy.MapProxyImpl;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.MapPermission;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.ProxyService;
import java.security.Permission;

public class MapFlushMessageTask
extends AbstractCallableMessageTask<MapFlushCodec.RequestParameters>
implements BlockingMessageTask {
    public MapFlushMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Object call() throws Exception {
        MapService mapService = (MapService)this.getService("hz:impl:mapService");
        MapServiceContext mapServiceContext = mapService.getMapServiceContext();
        NodeEngine nodeEngine = mapServiceContext.getNodeEngine();
        ProxyService proxyService = nodeEngine.getProxyService();
        DistributedObject distributedObject = proxyService.getDistributedObject("hz:impl:mapService", ((MapFlushCodec.RequestParameters)this.parameters).name);
        MapProxyImpl mapProxy = (MapProxyImpl)distributedObject;
        mapProxy.flush();
        return null;
    }

    @Override
    protected MapFlushCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return MapFlushCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return MapFlushCodec.encodeResponse();
    }

    @Override
    public String getServiceName() {
        return "hz:impl:mapService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new MapPermission(((MapFlushCodec.RequestParameters)this.parameters).name, "put");
    }

    @Override
    public String getDistributedObjectName() {
        return ((MapFlushCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "flush";
    }

    @Override
    public Object[] getParameters() {
        return null;
    }
}

