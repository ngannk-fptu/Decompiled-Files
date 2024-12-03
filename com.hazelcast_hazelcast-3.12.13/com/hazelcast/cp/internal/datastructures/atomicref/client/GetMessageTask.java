/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.datastructures.atomicref.client;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.CPAtomicRefGetCodec;
import com.hazelcast.cp.internal.client.AbstractCPMessageTask;
import com.hazelcast.cp.internal.datastructures.atomicref.operation.GetOp;
import com.hazelcast.cp.internal.raft.QueryPolicy;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.AtomicReferencePermission;
import java.security.Permission;

public class GetMessageTask
extends AbstractCPMessageTask<CPAtomicRefGetCodec.RequestParameters> {
    public GetMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected void processMessage() {
        this.query(((CPAtomicRefGetCodec.RequestParameters)this.parameters).groupId, new GetOp(((CPAtomicRefGetCodec.RequestParameters)this.parameters).name), QueryPolicy.LINEARIZABLE);
    }

    @Override
    protected CPAtomicRefGetCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return CPAtomicRefGetCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return CPAtomicRefGetCodec.encodeResponse(this.serializationService.toData(response));
    }

    @Override
    public String getServiceName() {
        return "hz:raft:atomicRefService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new AtomicReferencePermission(((CPAtomicRefGetCodec.RequestParameters)this.parameters).name, "read");
    }

    @Override
    public String getDistributedObjectName() {
        return ((CPAtomicRefGetCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "get";
    }

    @Override
    public Object[] getParameters() {
        return new Object[0];
    }
}

