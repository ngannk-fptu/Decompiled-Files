/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.multimap;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.MultiMapContainsEntryCodec;
import com.hazelcast.client.impl.protocol.task.AbstractPartitionMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.multimap.impl.operations.ContainsEntryOperation;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.MultiMapPermission;
import com.hazelcast.spi.Operation;
import java.security.Permission;

public class MultiMapContainsEntryMessageTask
extends AbstractPartitionMessageTask<MultiMapContainsEntryCodec.RequestParameters> {
    public MultiMapContainsEntryMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        ContainsEntryOperation operation = new ContainsEntryOperation(((MultiMapContainsEntryCodec.RequestParameters)this.parameters).name, ((MultiMapContainsEntryCodec.RequestParameters)this.parameters).key, ((MultiMapContainsEntryCodec.RequestParameters)this.parameters).value);
        operation.setThreadId(((MultiMapContainsEntryCodec.RequestParameters)this.parameters).threadId);
        return operation;
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return MultiMapContainsEntryCodec.encodeResponse((Boolean)response);
    }

    @Override
    protected MultiMapContainsEntryCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return MultiMapContainsEntryCodec.decodeRequest(clientMessage);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:multiMapService";
    }

    @Override
    public String getDistributedObjectName() {
        return ((MultiMapContainsEntryCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "containsEntry";
    }

    @Override
    public Permission getRequiredPermission() {
        return new MultiMapPermission(((MultiMapContainsEntryCodec.RequestParameters)this.parameters).name, "read");
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((MultiMapContainsEntryCodec.RequestParameters)this.parameters).key, ((MultiMapContainsEntryCodec.RequestParameters)this.parameters).value};
    }
}

