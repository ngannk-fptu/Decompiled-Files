/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.map;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.ContinuousQuerySetReadCursorCodec;
import com.hazelcast.client.impl.protocol.task.AbstractPartitionMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.map.impl.querycache.subscriber.operation.SetReadCursorOperation;
import com.hazelcast.nio.Connection;
import com.hazelcast.spi.Operation;
import java.security.Permission;

public class MapSetReadCursorMessageTask
extends AbstractPartitionMessageTask<ContinuousQuerySetReadCursorCodec.RequestParameters> {
    public MapSetReadCursorMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected ContinuousQuerySetReadCursorCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return ContinuousQuerySetReadCursorCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return ContinuousQuerySetReadCursorCodec.encodeResponse((Boolean)response);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:mapService";
    }

    @Override
    public Permission getRequiredPermission() {
        return null;
    }

    @Override
    public String getDistributedObjectName() {
        return ((ContinuousQuerySetReadCursorCodec.RequestParameters)this.parameters).mapName;
    }

    @Override
    public String getMethodName() {
        return null;
    }

    @Override
    public Object[] getParameters() {
        return null;
    }

    @Override
    protected Operation prepareOperation() {
        return new SetReadCursorOperation(((ContinuousQuerySetReadCursorCodec.RequestParameters)this.parameters).mapName, ((ContinuousQuerySetReadCursorCodec.RequestParameters)this.parameters).cacheName, ((ContinuousQuerySetReadCursorCodec.RequestParameters)this.parameters).sequence, this.clientMessage.getPartitionId());
    }
}

