/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.datastructures.atomicref.client;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.CPAtomicRefContainsCodec;
import com.hazelcast.cp.internal.client.AbstractCPMessageTask;
import com.hazelcast.cp.internal.datastructures.atomicref.operation.ContainsOp;
import com.hazelcast.cp.internal.raft.QueryPolicy;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.AtomicReferencePermission;
import java.security.Permission;

public class ContainsMessageTask
extends AbstractCPMessageTask<CPAtomicRefContainsCodec.RequestParameters> {
    public ContainsMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected void processMessage() {
        this.query(((CPAtomicRefContainsCodec.RequestParameters)this.parameters).groupId, new ContainsOp(((CPAtomicRefContainsCodec.RequestParameters)this.parameters).name, ((CPAtomicRefContainsCodec.RequestParameters)this.parameters).value), QueryPolicy.LINEARIZABLE);
    }

    @Override
    protected CPAtomicRefContainsCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return CPAtomicRefContainsCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return CPAtomicRefContainsCodec.encodeResponse((Boolean)response);
    }

    @Override
    public String getServiceName() {
        return "hz:raft:atomicRefService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new AtomicReferencePermission(((CPAtomicRefContainsCodec.RequestParameters)this.parameters).name, "read");
    }

    @Override
    public String getDistributedObjectName() {
        return ((CPAtomicRefContainsCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "contains";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((CPAtomicRefContainsCodec.RequestParameters)this.parameters).value};
    }
}

