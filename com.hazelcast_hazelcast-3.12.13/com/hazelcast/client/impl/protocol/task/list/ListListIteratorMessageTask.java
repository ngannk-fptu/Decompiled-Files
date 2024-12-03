/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.list;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.ListIteratorCodec;
import com.hazelcast.client.impl.protocol.codec.ListListIteratorCodec;
import com.hazelcast.client.impl.protocol.task.AbstractPartitionMessageTask;
import com.hazelcast.collection.impl.list.operations.ListSubOperation;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.ListPermission;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.impl.SerializableList;
import java.security.Permission;

public class ListListIteratorMessageTask
extends AbstractPartitionMessageTask<ListListIteratorCodec.RequestParameters> {
    public ListListIteratorMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        return new ListSubOperation(((ListListIteratorCodec.RequestParameters)this.parameters).name, ((ListListIteratorCodec.RequestParameters)this.parameters).index, -1);
    }

    @Override
    protected ListListIteratorCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return ListListIteratorCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return ListIteratorCodec.encodeResponse(((SerializableList)response).getCollection());
    }

    @Override
    public String getServiceName() {
        return "hz:impl:listService";
    }

    @Override
    public Object[] getParameters() {
        if (((ListListIteratorCodec.RequestParameters)this.parameters).index > 0) {
            return new Object[]{((ListListIteratorCodec.RequestParameters)this.parameters).index};
        }
        return null;
    }

    @Override
    public Permission getRequiredPermission() {
        return new ListPermission(((ListListIteratorCodec.RequestParameters)this.parameters).name, "read");
    }

    @Override
    public String getMethodName() {
        return "listIterator";
    }

    @Override
    public String getDistributedObjectName() {
        return ((ListListIteratorCodec.RequestParameters)this.parameters).name;
    }
}

