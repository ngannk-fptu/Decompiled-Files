/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.set;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.SetAddAllCodec;
import com.hazelcast.client.impl.protocol.task.AbstractPartitionMessageTask;
import com.hazelcast.collection.impl.collection.operations.CollectionAddAllOperation;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.SetPermission;
import com.hazelcast.spi.Operation;
import java.security.Permission;

public class SetAddAllMessageTask
extends AbstractPartitionMessageTask<SetAddAllCodec.RequestParameters> {
    public SetAddAllMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        return new CollectionAddAllOperation(((SetAddAllCodec.RequestParameters)this.parameters).name, ((SetAddAllCodec.RequestParameters)this.parameters).valueList);
    }

    @Override
    protected SetAddAllCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return SetAddAllCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return SetAddAllCodec.encodeResponse((Boolean)response);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:setService";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((SetAddAllCodec.RequestParameters)this.parameters).valueList};
    }

    @Override
    public Permission getRequiredPermission() {
        return new SetPermission(((SetAddAllCodec.RequestParameters)this.parameters).name, "add");
    }

    @Override
    public String getMethodName() {
        return "addAll";
    }

    @Override
    public String getDistributedObjectName() {
        return ((SetAddAllCodec.RequestParameters)this.parameters).name;
    }
}

