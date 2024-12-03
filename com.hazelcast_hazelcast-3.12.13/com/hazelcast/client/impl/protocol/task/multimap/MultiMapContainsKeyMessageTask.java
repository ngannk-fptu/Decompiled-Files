/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.multimap;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.MultiMapContainsKeyCodec;
import com.hazelcast.client.impl.protocol.task.AbstractPartitionMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.multimap.impl.operations.ContainsEntryOperation;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.MultiMapPermission;
import com.hazelcast.spi.Operation;
import java.security.Permission;

public class MultiMapContainsKeyMessageTask
extends AbstractPartitionMessageTask<MultiMapContainsKeyCodec.RequestParameters> {
    public MultiMapContainsKeyMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        ContainsEntryOperation operation = new ContainsEntryOperation(((MultiMapContainsKeyCodec.RequestParameters)this.parameters).name, ((MultiMapContainsKeyCodec.RequestParameters)this.parameters).key, null);
        operation.setThreadId(((MultiMapContainsKeyCodec.RequestParameters)this.parameters).threadId);
        return operation;
    }

    @Override
    protected MultiMapContainsKeyCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return MultiMapContainsKeyCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return MultiMapContainsKeyCodec.encodeResponse((Boolean)response);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:multiMapService";
    }

    @Override
    public String getDistributedObjectName() {
        return ((MultiMapContainsKeyCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "containsKey";
    }

    @Override
    public Permission getRequiredPermission() {
        return new MultiMapPermission(((MultiMapContainsKeyCodec.RequestParameters)this.parameters).name, "read");
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((MultiMapContainsKeyCodec.RequestParameters)this.parameters).key};
    }
}

