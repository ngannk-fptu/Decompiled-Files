/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.ringbuffer;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.RingbufferCapacityCodec;
import com.hazelcast.client.impl.protocol.task.AbstractPartitionMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.ringbuffer.impl.operations.GenericOperation;
import com.hazelcast.security.permission.RingBufferPermission;
import com.hazelcast.spi.Operation;
import java.security.Permission;

public class RingbufferCapacityMessageTask
extends AbstractPartitionMessageTask<RingbufferCapacityCodec.RequestParameters> {
    public RingbufferCapacityMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        return new GenericOperation(((RingbufferCapacityCodec.RequestParameters)this.parameters).name, 4);
    }

    @Override
    protected RingbufferCapacityCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return RingbufferCapacityCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return RingbufferCapacityCodec.encodeResponse((Long)response);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:ringbufferService";
    }

    @Override
    public Object[] getParameters() {
        return null;
    }

    @Override
    public Permission getRequiredPermission() {
        return new RingBufferPermission(((RingbufferCapacityCodec.RequestParameters)this.parameters).name, "read");
    }

    @Override
    public String getMethodName() {
        return "capacity";
    }

    @Override
    public String getDistributedObjectName() {
        return ((RingbufferCapacityCodec.RequestParameters)this.parameters).name;
    }
}

