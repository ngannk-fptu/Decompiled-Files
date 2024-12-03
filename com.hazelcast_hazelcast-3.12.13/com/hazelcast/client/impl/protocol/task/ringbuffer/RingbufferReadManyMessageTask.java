/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.ringbuffer;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.RingbufferReadManyCodec;
import com.hazelcast.client.impl.protocol.task.AbstractPartitionMessageTask;
import com.hazelcast.core.IFunction;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.ringbuffer.impl.ReadResultSetImpl;
import com.hazelcast.ringbuffer.impl.operations.ReadManyOperation;
import com.hazelcast.security.permission.RingBufferPermission;
import com.hazelcast.spi.Operation;
import java.security.Permission;
import java.util.ArrayList;

public class RingbufferReadManyMessageTask
extends AbstractPartitionMessageTask<RingbufferReadManyCodec.RequestParameters> {
    public RingbufferReadManyMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        IFunction function = (IFunction)this.serializationService.toObject(((RingbufferReadManyCodec.RequestParameters)this.parameters).filter);
        return new ReadManyOperation(((RingbufferReadManyCodec.RequestParameters)this.parameters).name, ((RingbufferReadManyCodec.RequestParameters)this.parameters).startSequence, ((RingbufferReadManyCodec.RequestParameters)this.parameters).minCount, ((RingbufferReadManyCodec.RequestParameters)this.parameters).maxCount, function);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        ReadResultSetImpl resultSet = (ReadResultSetImpl)this.nodeEngine.getSerializationService().toObject(response);
        ArrayList<Data> items = new ArrayList<Data>(resultSet.size());
        long[] seqs = new long[resultSet.size()];
        Data[] dataItems = resultSet.getDataItems();
        for (int k = 0; k < resultSet.size(); ++k) {
            items.add(dataItems[k]);
            seqs[k] = resultSet.getSequence(k);
        }
        return RingbufferReadManyCodec.encodeResponse(resultSet.readCount(), items, seqs, resultSet.getNextSequenceToReadFrom());
    }

    @Override
    protected RingbufferReadManyCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return RingbufferReadManyCodec.decodeRequest(clientMessage);
    }

    @Override
    public String getMethodName() {
        return "readMany";
    }

    @Override
    public String getServiceName() {
        return "hz:impl:ringbufferService";
    }

    @Override
    public String getDistributedObjectName() {
        return ((RingbufferReadManyCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public Permission getRequiredPermission() {
        return new RingBufferPermission(((RingbufferReadManyCodec.RequestParameters)this.parameters).name, "read");
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((RingbufferReadManyCodec.RequestParameters)this.parameters).startSequence, ((RingbufferReadManyCodec.RequestParameters)this.parameters).minCount, ((RingbufferReadManyCodec.RequestParameters)this.parameters).maxCount, null};
    }
}

