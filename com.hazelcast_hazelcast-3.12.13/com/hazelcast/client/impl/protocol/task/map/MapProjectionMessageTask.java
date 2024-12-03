/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.map;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.MapProjectCodec;
import com.hazelcast.client.impl.protocol.codec.MapProjectWithPredicateCodec;
import com.hazelcast.client.impl.protocol.task.map.DefaultMapProjectMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.projection.Projection;
import com.hazelcast.security.permission.MapPermission;
import java.security.Permission;
import java.util.List;

public class MapProjectionMessageTask
extends DefaultMapProjectMessageTask<MapProjectCodec.RequestParameters> {
    public MapProjectionMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Projection<?, ?> getProjection() {
        return (Projection)this.nodeEngine.getSerializationService().toObject(((MapProjectCodec.RequestParameters)this.parameters).projection);
    }

    @Override
    protected MapProjectCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return MapProjectCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return MapProjectWithPredicateCodec.encodeResponse((List)response);
    }

    @Override
    public Permission getRequiredPermission() {
        return new MapPermission(((MapProjectCodec.RequestParameters)this.parameters).name, "projection");
    }

    @Override
    public String getDistributedObjectName() {
        return ((MapProjectCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "project";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((MapProjectCodec.RequestParameters)this.parameters).name, ((MapProjectCodec.RequestParameters)this.parameters).projection};
    }
}

