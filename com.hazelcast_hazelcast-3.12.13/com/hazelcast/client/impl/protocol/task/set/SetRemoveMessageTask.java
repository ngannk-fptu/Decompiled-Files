/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.set;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.SetRemoveCodec;
import com.hazelcast.client.impl.protocol.task.AbstractPartitionMessageTask;
import com.hazelcast.collection.impl.collection.operations.CollectionRemoveOperation;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.SetPermission;
import com.hazelcast.spi.Operation;
import java.security.Permission;

public class SetRemoveMessageTask
extends AbstractPartitionMessageTask<SetRemoveCodec.RequestParameters> {
    public SetRemoveMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        return new CollectionRemoveOperation(((SetRemoveCodec.RequestParameters)this.parameters).name, ((SetRemoveCodec.RequestParameters)this.parameters).value);
    }

    @Override
    protected SetRemoveCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return SetRemoveCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return SetRemoveCodec.encodeResponse((Boolean)response);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:setService";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((SetRemoveCodec.RequestParameters)this.parameters).value};
    }

    @Override
    public Permission getRequiredPermission() {
        return new SetPermission(((SetRemoveCodec.RequestParameters)this.parameters).name, "remove");
    }

    @Override
    public String getMethodName() {
        return "remove";
    }

    @Override
    public String getDistributedObjectName() {
        return ((SetRemoveCodec.RequestParameters)this.parameters).name;
    }
}

