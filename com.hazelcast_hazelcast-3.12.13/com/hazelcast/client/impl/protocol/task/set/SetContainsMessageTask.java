/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.set;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.SetContainsCodec;
import com.hazelcast.client.impl.protocol.task.AbstractPartitionMessageTask;
import com.hazelcast.collection.impl.collection.operations.CollectionContainsOperation;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.SetPermission;
import com.hazelcast.spi.Operation;
import java.security.Permission;
import java.util.Collections;

public class SetContainsMessageTask
extends AbstractPartitionMessageTask<SetContainsCodec.RequestParameters> {
    public SetContainsMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        return new CollectionContainsOperation(((SetContainsCodec.RequestParameters)this.parameters).name, Collections.singleton(((SetContainsCodec.RequestParameters)this.parameters).value));
    }

    @Override
    protected SetContainsCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return SetContainsCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return SetContainsCodec.encodeResponse((Boolean)response);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:setService";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((SetContainsCodec.RequestParameters)this.parameters).value};
    }

    @Override
    public Permission getRequiredPermission() {
        return new SetPermission(((SetContainsCodec.RequestParameters)this.parameters).name, "read");
    }

    @Override
    public String getMethodName() {
        return "contains";
    }

    @Override
    public String getDistributedObjectName() {
        return ((SetContainsCodec.RequestParameters)this.parameters).name;
    }
}

