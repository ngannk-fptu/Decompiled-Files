/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.set;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.SetCompareAndRemoveAllCodec;
import com.hazelcast.client.impl.protocol.task.AbstractPartitionMessageTask;
import com.hazelcast.collection.impl.collection.operations.CollectionCompareAndRemoveOperation;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.security.permission.SetPermission;
import com.hazelcast.spi.Operation;
import java.security.Permission;
import java.util.HashSet;

public class SetCompareAndRemoveAllMessageTask
extends AbstractPartitionMessageTask<SetCompareAndRemoveAllCodec.RequestParameters> {
    public SetCompareAndRemoveAllMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        HashSet<Data> values = new HashSet<Data>(((SetCompareAndRemoveAllCodec.RequestParameters)this.parameters).values);
        return new CollectionCompareAndRemoveOperation(((SetCompareAndRemoveAllCodec.RequestParameters)this.parameters).name, false, values);
    }

    @Override
    protected SetCompareAndRemoveAllCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return SetCompareAndRemoveAllCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return SetCompareAndRemoveAllCodec.encodeResponse((Boolean)response);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:setService";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((SetCompareAndRemoveAllCodec.RequestParameters)this.parameters).values};
    }

    @Override
    public Permission getRequiredPermission() {
        return new SetPermission(((SetCompareAndRemoveAllCodec.RequestParameters)this.parameters).name, "remove");
    }

    @Override
    public String getMethodName() {
        return "removeAll";
    }

    @Override
    public String getDistributedObjectName() {
        return ((SetCompareAndRemoveAllCodec.RequestParameters)this.parameters).name;
    }
}

