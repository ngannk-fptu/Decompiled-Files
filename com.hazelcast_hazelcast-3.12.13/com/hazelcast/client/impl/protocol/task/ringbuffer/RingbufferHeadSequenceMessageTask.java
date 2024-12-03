/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.ringbuffer;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.RingbufferHeadSequenceCodec;
import com.hazelcast.client.impl.protocol.task.AbstractPartitionMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.ringbuffer.impl.operations.GenericOperation;
import com.hazelcast.security.permission.RingBufferPermission;
import com.hazelcast.spi.Operation;
import java.security.Permission;

public class RingbufferHeadSequenceMessageTask
extends AbstractPartitionMessageTask<RingbufferHeadSequenceCodec.RequestParameters> {
    public RingbufferHeadSequenceMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        return new GenericOperation(((RingbufferHeadSequenceCodec.RequestParameters)this.parameters).name, 2);
    }

    @Override
    protected RingbufferHeadSequenceCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return RingbufferHeadSequenceCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return RingbufferHeadSequenceCodec.encodeResponse((Long)response);
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
        return new RingBufferPermission(((RingbufferHeadSequenceCodec.RequestParameters)this.parameters).name, "read");
    }

    @Override
    public String getMethodName() {
        return "headSequence";
    }

    @Override
    public String getDistributedObjectName() {
        return ((RingbufferHeadSequenceCodec.RequestParameters)this.parameters).name;
    }
}

