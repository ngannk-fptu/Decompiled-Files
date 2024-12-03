/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.datastructures.atomicref.client;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.CPAtomicRefSetCodec;
import com.hazelcast.cp.internal.client.AbstractCPMessageTask;
import com.hazelcast.cp.internal.datastructures.atomicref.operation.SetOp;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.AtomicReferencePermission;
import java.security.Permission;

public class SetMessageTask
extends AbstractCPMessageTask<CPAtomicRefSetCodec.RequestParameters> {
    public SetMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected void processMessage() {
        this.invoke(((CPAtomicRefSetCodec.RequestParameters)this.parameters).groupId, new SetOp(((CPAtomicRefSetCodec.RequestParameters)this.parameters).name, ((CPAtomicRefSetCodec.RequestParameters)this.parameters).newValue, ((CPAtomicRefSetCodec.RequestParameters)this.parameters).returnOldValue));
    }

    @Override
    protected CPAtomicRefSetCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return CPAtomicRefSetCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return CPAtomicRefSetCodec.encodeResponse(this.serializationService.toData(response));
    }

    @Override
    public String getServiceName() {
        return "hz:raft:atomicRefService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new AtomicReferencePermission(((CPAtomicRefSetCodec.RequestParameters)this.parameters).name, "modify");
    }

    @Override
    public String getDistributedObjectName() {
        return ((CPAtomicRefSetCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return ((CPAtomicRefSetCodec.RequestParameters)this.parameters).returnOldValue ? "getAndSet" : "set";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((CPAtomicRefSetCodec.RequestParameters)this.parameters).newValue};
    }
}

