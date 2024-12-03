/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.ringbuffer;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.RingbufferReadOneCodec;
import com.hazelcast.client.impl.protocol.task.AbstractPartitionMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.ringbuffer.impl.operations.ReadOneOperation;
import com.hazelcast.security.permission.RingBufferPermission;
import com.hazelcast.spi.Operation;
import java.security.Permission;

public class RingbufferReadOneMessageTask
extends AbstractPartitionMessageTask<RingbufferReadOneCodec.RequestParameters> {
    public RingbufferReadOneMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        return new ReadOneOperation(((RingbufferReadOneCodec.RequestParameters)this.parameters).name, ((RingbufferReadOneCodec.RequestParameters)this.parameters).sequence);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return RingbufferReadOneCodec.encodeResponse((Data)response);
    }

    @Override
    protected RingbufferReadOneCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return RingbufferReadOneCodec.decodeRequest(clientMessage);
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((RingbufferReadOneCodec.RequestParameters)this.parameters).sequence};
    }

    @Override
    public Permission getRequiredPermission() {
        return new RingBufferPermission(((RingbufferReadOneCodec.RequestParameters)this.parameters).name, "read");
    }

    @Override
    public String getMethodName() {
        return "readOne";
    }

    @Override
    public String getServiceName() {
        return "hz:impl:ringbufferService";
    }

    @Override
    public String getDistributedObjectName() {
        return ((RingbufferReadOneCodec.RequestParameters)this.parameters).name;
    }
}

