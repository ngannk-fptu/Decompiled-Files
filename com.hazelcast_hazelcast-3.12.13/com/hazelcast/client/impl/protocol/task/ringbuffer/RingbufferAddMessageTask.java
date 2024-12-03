/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.ringbuffer;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.RingbufferAddCodec;
import com.hazelcast.client.impl.protocol.task.AbstractPartitionMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.ringbuffer.OverflowPolicy;
import com.hazelcast.ringbuffer.impl.operations.AddOperation;
import com.hazelcast.security.permission.RingBufferPermission;
import com.hazelcast.spi.Operation;
import java.security.Permission;

public class RingbufferAddMessageTask
extends AbstractPartitionMessageTask<RingbufferAddCodec.RequestParameters> {
    public RingbufferAddMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        return new AddOperation(((RingbufferAddCodec.RequestParameters)this.parameters).name, ((RingbufferAddCodec.RequestParameters)this.parameters).value, OverflowPolicy.getById(((RingbufferAddCodec.RequestParameters)this.parameters).overflowPolicy));
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return RingbufferAddCodec.encodeResponse((Long)response);
    }

    @Override
    protected RingbufferAddCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return RingbufferAddCodec.decodeRequest(clientMessage);
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((RingbufferAddCodec.RequestParameters)this.parameters).value, OverflowPolicy.getById(((RingbufferAddCodec.RequestParameters)this.parameters).overflowPolicy)};
    }

    @Override
    public Permission getRequiredPermission() {
        return new RingBufferPermission(((RingbufferAddCodec.RequestParameters)this.parameters).name, "put");
    }

    @Override
    public String getMethodName() {
        return "add";
    }

    @Override
    public String getServiceName() {
        return "hz:impl:ringbufferService";
    }

    @Override
    public String getDistributedObjectName() {
        return ((RingbufferAddCodec.RequestParameters)this.parameters).name;
    }
}

