/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.ringbuffer;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.RingbufferTailSequenceCodec;
import com.hazelcast.client.impl.protocol.task.AbstractPartitionMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.ringbuffer.impl.operations.GenericOperation;
import com.hazelcast.security.permission.RingBufferPermission;
import com.hazelcast.spi.Operation;
import java.security.Permission;

public class RingbufferTailSequenceMessageTask
extends AbstractPartitionMessageTask<RingbufferTailSequenceCodec.RequestParameters> {
    public RingbufferTailSequenceMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        return new GenericOperation(((RingbufferTailSequenceCodec.RequestParameters)this.parameters).name, 1);
    }

    @Override
    protected RingbufferTailSequenceCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return RingbufferTailSequenceCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return RingbufferTailSequenceCodec.encodeResponse((Long)response);
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
        return new RingBufferPermission(((RingbufferTailSequenceCodec.RequestParameters)this.parameters).name, "read");
    }

    @Override
    public String getMethodName() {
        return "tailSequence";
    }

    @Override
    public String getDistributedObjectName() {
        return ((RingbufferTailSequenceCodec.RequestParameters)this.parameters).name;
    }
}

