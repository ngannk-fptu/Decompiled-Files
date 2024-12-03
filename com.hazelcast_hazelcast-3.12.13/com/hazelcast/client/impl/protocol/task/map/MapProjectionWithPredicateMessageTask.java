/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.map;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.MapProjectWithPredicateCodec;
import com.hazelcast.client.impl.protocol.task.map.DefaultMapProjectMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.projection.Projection;
import com.hazelcast.query.Predicate;
import com.hazelcast.security.permission.MapPermission;
import java.security.Permission;
import java.util.List;

public class MapProjectionWithPredicateMessageTask
extends DefaultMapProjectMessageTask<MapProjectWithPredicateCodec.RequestParameters> {
    public MapProjectionWithPredicateMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Projection<?, ?> getProjection() {
        return (Projection)this.nodeEngine.getSerializationService().toObject(((MapProjectWithPredicateCodec.RequestParameters)this.parameters).projection);
    }

    @Override
    protected Predicate getPredicate() {
        return (Predicate)this.nodeEngine.getSerializationService().toObject(((MapProjectWithPredicateCodec.RequestParameters)this.parameters).predicate);
    }

    @Override
    protected MapProjectWithPredicateCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return MapProjectWithPredicateCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return MapProjectWithPredicateCodec.encodeResponse((List)response);
    }

    @Override
    public Permission getRequiredPermission() {
        return new MapPermission(((MapProjectWithPredicateCodec.RequestParameters)this.parameters).name, "projection");
    }

    @Override
    public String getDistributedObjectName() {
        return ((MapProjectWithPredicateCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "projectWithPredicate";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((MapProjectWithPredicateCodec.RequestParameters)this.parameters).name, ((MapProjectWithPredicateCodec.RequestParameters)this.parameters).projection, ((MapProjectWithPredicateCodec.RequestParameters)this.parameters).predicate};
    }
}

