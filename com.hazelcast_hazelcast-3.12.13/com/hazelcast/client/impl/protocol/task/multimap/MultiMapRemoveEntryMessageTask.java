/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.multimap;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.MultiMapRemoveEntryCodec;
import com.hazelcast.client.impl.protocol.task.AbstractPartitionMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.multimap.impl.operations.RemoveOperation;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.MultiMapPermission;
import com.hazelcast.spi.Operation;
import java.security.Permission;

public class MultiMapRemoveEntryMessageTask
extends AbstractPartitionMessageTask<MultiMapRemoveEntryCodec.RequestParameters> {
    public MultiMapRemoveEntryMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        return new RemoveOperation(((MultiMapRemoveEntryCodec.RequestParameters)this.parameters).name, ((MultiMapRemoveEntryCodec.RequestParameters)this.parameters).key, ((MultiMapRemoveEntryCodec.RequestParameters)this.parameters).threadId, ((MultiMapRemoveEntryCodec.RequestParameters)this.parameters).value);
    }

    @Override
    protected MultiMapRemoveEntryCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return MultiMapRemoveEntryCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return MultiMapRemoveEntryCodec.encodeResponse((Boolean)response);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:multiMapService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new MultiMapPermission(((MultiMapRemoveEntryCodec.RequestParameters)this.parameters).name, "remove");
    }

    @Override
    public String getDistributedObjectName() {
        return ((MultiMapRemoveEntryCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "remove";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((MultiMapRemoveEntryCodec.RequestParameters)this.parameters).key, ((MultiMapRemoveEntryCodec.RequestParameters)this.parameters).value};
    }
}

