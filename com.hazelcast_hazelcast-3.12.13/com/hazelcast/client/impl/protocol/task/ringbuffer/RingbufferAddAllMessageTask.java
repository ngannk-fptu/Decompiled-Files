/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.ringbuffer;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.RingbufferAddAllCodec;
import com.hazelcast.client.impl.protocol.task.AbstractPartitionMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.ringbuffer.OverflowPolicy;
import com.hazelcast.ringbuffer.impl.operations.AddAllOperation;
import com.hazelcast.security.permission.RingBufferPermission;
import com.hazelcast.spi.Operation;
import java.security.Permission;
import java.util.List;

public class RingbufferAddAllMessageTask
extends AbstractPartitionMessageTask<RingbufferAddAllCodec.RequestParameters> {
    public RingbufferAddAllMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        return new AddAllOperation(((RingbufferAddAllCodec.RequestParameters)this.parameters).name, this.items(), OverflowPolicy.getById(((RingbufferAddAllCodec.RequestParameters)this.parameters).overflowPolicy));
    }

    private Data[] items() {
        List<Data> valueList = ((RingbufferAddAllCodec.RequestParameters)this.parameters).valueList;
        Data[] array = new Data[valueList.size()];
        return valueList.toArray(array);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return RingbufferAddAllCodec.encodeResponse((Long)response);
    }

    @Override
    protected RingbufferAddAllCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return RingbufferAddAllCodec.decodeRequest(clientMessage);
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((RingbufferAddAllCodec.RequestParameters)this.parameters).valueList, OverflowPolicy.getById(((RingbufferAddAllCodec.RequestParameters)this.parameters).overflowPolicy)};
    }

    @Override
    public Permission getRequiredPermission() {
        return new RingBufferPermission(((RingbufferAddAllCodec.RequestParameters)this.parameters).name, "put");
    }

    @Override
    public String getMethodName() {
        return "addAll";
    }

    @Override
    public String getServiceName() {
        return "hz:impl:ringbufferService";
    }

    @Override
    public String getDistributedObjectName() {
        return ((RingbufferAddAllCodec.RequestParameters)this.parameters).name;
    }
}

