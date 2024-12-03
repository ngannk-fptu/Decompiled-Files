/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.map;

import com.hazelcast.client.impl.AddInterceptorOperationSupplier;
import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.MapAddInterceptorCodec;
import com.hazelcast.client.impl.protocol.task.AbstractMultiTargetMessageTask;
import com.hazelcast.core.Member;
import com.hazelcast.instance.Node;
import com.hazelcast.map.MapInterceptor;
import com.hazelcast.map.impl.MapService;
import com.hazelcast.map.impl.MapServiceContext;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.MapPermission;
import com.hazelcast.spi.Operation;
import com.hazelcast.util.function.Supplier;
import java.security.Permission;
import java.util.Collection;
import java.util.Map;

public class MapAddInterceptorMessageTask
extends AbstractMultiTargetMessageTask<MapAddInterceptorCodec.RequestParameters> {
    private transient String id;

    public MapAddInterceptorMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Supplier<Operation> createOperationSupplier() {
        MapService mapService = (MapService)this.getService("hz:impl:mapService");
        MapServiceContext mapServiceContext = mapService.getMapServiceContext();
        MapInterceptor mapInterceptor = (MapInterceptor)this.serializationService.toObject(((MapAddInterceptorCodec.RequestParameters)this.parameters).interceptor);
        this.id = mapServiceContext.generateInterceptorId(((MapAddInterceptorCodec.RequestParameters)this.parameters).name, mapInterceptor);
        return new AddInterceptorOperationSupplier(this.id, ((MapAddInterceptorCodec.RequestParameters)this.parameters).name, mapInterceptor);
    }

    @Override
    protected Object reduce(Map<Member, Object> map) throws Throwable {
        for (Object result : map.values()) {
            if (!(result instanceof Throwable)) continue;
            throw (Throwable)result;
        }
        return this.id;
    }

    @Override
    public Collection<Member> getTargets() {
        return this.nodeEngine.getClusterService().getMembers();
    }

    @Override
    protected MapAddInterceptorCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return MapAddInterceptorCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return MapAddInterceptorCodec.encodeResponse((String)response);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:mapService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new MapPermission(((MapAddInterceptorCodec.RequestParameters)this.parameters).name, "intercept");
    }

    @Override
    public String getDistributedObjectName() {
        return ((MapAddInterceptorCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "addInterceptor";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((MapAddInterceptorCodec.RequestParameters)this.parameters).interceptor};
    }
}

