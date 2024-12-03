/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.map;

import com.hazelcast.client.impl.RemoveInterceptorOperationSupplier;
import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.MapRemoveInterceptorCodec;
import com.hazelcast.client.impl.protocol.task.AbstractMultiTargetMessageTask;
import com.hazelcast.core.Member;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.MapPermission;
import com.hazelcast.spi.Operation;
import com.hazelcast.util.function.Supplier;
import java.security.Permission;
import java.util.Collection;
import java.util.Map;

public class MapRemoveInterceptorMessageTask
extends AbstractMultiTargetMessageTask<MapRemoveInterceptorCodec.RequestParameters> {
    public MapRemoveInterceptorMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Supplier<Operation> createOperationSupplier() {
        return new RemoveInterceptorOperationSupplier(((MapRemoveInterceptorCodec.RequestParameters)this.parameters).id, ((MapRemoveInterceptorCodec.RequestParameters)this.parameters).name);
    }

    @Override
    protected Object reduce(Map<Member, Object> map) throws Throwable {
        for (Object result : map.values()) {
            if (!(result instanceof Throwable)) continue;
            throw (Throwable)result;
        }
        return true;
    }

    @Override
    public Collection<Member> getTargets() {
        return this.nodeEngine.getClusterService().getMembers();
    }

    @Override
    protected MapRemoveInterceptorCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return MapRemoveInterceptorCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return MapRemoveInterceptorCodec.encodeResponse((Boolean)response);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:mapService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new MapPermission(((MapRemoveInterceptorCodec.RequestParameters)this.parameters).name, "intercept");
    }

    @Override
    public String getDistributedObjectName() {
        return ((MapRemoveInterceptorCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "removeInterceptor";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((MapRemoveInterceptorCodec.RequestParameters)this.parameters).id};
    }
}

