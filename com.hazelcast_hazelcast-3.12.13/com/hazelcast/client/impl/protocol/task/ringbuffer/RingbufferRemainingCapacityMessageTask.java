/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.ringbuffer;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.RingbufferRemainingCapacityCodec;
import com.hazelcast.client.impl.protocol.task.AbstractPartitionMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.ringbuffer.impl.operations.GenericOperation;
import com.hazelcast.security.permission.RingBufferPermission;
import com.hazelcast.spi.Operation;
import java.security.Permission;

public class RingbufferRemainingCapacityMessageTask
extends AbstractPartitionMessageTask<RingbufferRemainingCapacityCodec.RequestParameters> {
    public RingbufferRemainingCapacityMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        return new GenericOperation(((RingbufferRemainingCapacityCodec.RequestParameters)this.parameters).name, 3);
    }

    @Override
    protected RingbufferRemainingCapacityCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return RingbufferRemainingCapacityCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return RingbufferRemainingCapacityCodec.encodeResponse((Long)response);
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
        return new RingBufferPermission(((RingbufferRemainingCapacityCodec.RequestParameters)this.parameters).name, "read");
    }

    @Override
    public String getMethodName() {
        return "remainingCapacity";
    }

    @Override
    public String getDistributedObjectName() {
        return ((RingbufferRemainingCapacityCodec.RequestParameters)this.parameters).name;
    }
}

