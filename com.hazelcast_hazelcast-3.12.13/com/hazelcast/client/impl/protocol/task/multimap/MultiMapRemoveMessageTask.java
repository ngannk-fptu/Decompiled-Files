/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.multimap;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.MultiMapRemoveCodec;
import com.hazelcast.client.impl.protocol.task.AbstractPartitionMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.multimap.impl.MultiMapRecord;
import com.hazelcast.multimap.impl.operations.MultiMapResponse;
import com.hazelcast.multimap.impl.operations.RemoveAllOperation;
import com.hazelcast.nio.Connection;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.security.permission.MultiMapPermission;
import com.hazelcast.spi.Operation;
import java.security.Permission;
import java.util.ArrayList;
import java.util.Collection;

public class MultiMapRemoveMessageTask
extends AbstractPartitionMessageTask<MultiMapRemoveCodec.RequestParameters> {
    public MultiMapRemoveMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        return new RemoveAllOperation(((MultiMapRemoveCodec.RequestParameters)this.parameters).name, ((MultiMapRemoveCodec.RequestParameters)this.parameters).key, ((MultiMapRemoveCodec.RequestParameters)this.parameters).threadId);
    }

    @Override
    protected MultiMapRemoveCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return MultiMapRemoveCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        MultiMapResponse multiMapResponse = (MultiMapResponse)response;
        Collection collection = multiMapResponse.getCollection();
        ArrayList<Data> resultCollection = new ArrayList<Data>(collection.size());
        for (MultiMapRecord multiMapRecord : collection) {
            resultCollection.add((Data)this.serializationService.toData(multiMapRecord.getObject()));
        }
        return MultiMapRemoveCodec.encodeResponse(resultCollection);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:multiMapService";
    }

    @Override
    public String getDistributedObjectName() {
        return ((MultiMapRemoveCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public Permission getRequiredPermission() {
        return new MultiMapPermission(((MultiMapRemoveCodec.RequestParameters)this.parameters).name, "remove");
    }

    @Override
    public String getMethodName() {
        return "remove";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((MultiMapRemoveCodec.RequestParameters)this.parameters).key};
    }
}

