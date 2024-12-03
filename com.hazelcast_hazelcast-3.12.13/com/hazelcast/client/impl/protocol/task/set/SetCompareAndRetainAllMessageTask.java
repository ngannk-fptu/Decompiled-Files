/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.set;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.SetCompareAndRetainAllCodec;
import com.hazelcast.client.impl.protocol.task.AbstractPartitionMessageTask;
import com.hazelcast.collection.impl.collection.operations.CollectionCompareAndRemoveOperation;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.security.permission.SetPermission;
import com.hazelcast.spi.Operation;
import java.security.Permission;
import java.util.HashSet;

public class SetCompareAndRetainAllMessageTask
extends AbstractPartitionMessageTask<SetCompareAndRetainAllCodec.RequestParameters> {
    public SetCompareAndRetainAllMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        HashSet<Data> values = new HashSet<Data>(((SetCompareAndRetainAllCodec.RequestParameters)this.parameters).values);
        return new CollectionCompareAndRemoveOperation(((SetCompareAndRetainAllCodec.RequestParameters)this.parameters).name, true, values);
    }

    @Override
    protected SetCompareAndRetainAllCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return SetCompareAndRetainAllCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return SetCompareAndRetainAllCodec.encodeResponse((Boolean)response);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:setService";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((SetCompareAndRetainAllCodec.RequestParameters)this.parameters).values};
    }

    @Override
    public Permission getRequiredPermission() {
        return new SetPermission(((SetCompareAndRetainAllCodec.RequestParameters)this.parameters).name, "remove");
    }

    @Override
    public String getMethodName() {
        return "retainAll";
    }

    @Override
    public String getDistributedObjectName() {
        return ((SetCompareAndRetainAllCodec.RequestParameters)this.parameters).name;
    }
}

