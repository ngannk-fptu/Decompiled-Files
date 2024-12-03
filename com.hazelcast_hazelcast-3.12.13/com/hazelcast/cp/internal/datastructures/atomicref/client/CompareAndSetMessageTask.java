/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.datastructures.atomicref.client;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.CPAtomicRefCompareAndSetCodec;
import com.hazelcast.cp.internal.client.AbstractCPMessageTask;
import com.hazelcast.cp.internal.datastructures.atomicref.operation.CompareAndSetOp;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.AtomicReferencePermission;
import java.security.Permission;

public class CompareAndSetMessageTask
extends AbstractCPMessageTask<CPAtomicRefCompareAndSetCodec.RequestParameters> {
    public CompareAndSetMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected void processMessage() {
        CompareAndSetOp op = new CompareAndSetOp(((CPAtomicRefCompareAndSetCodec.RequestParameters)this.parameters).name, ((CPAtomicRefCompareAndSetCodec.RequestParameters)this.parameters).oldValue, ((CPAtomicRefCompareAndSetCodec.RequestParameters)this.parameters).newValue);
        this.invoke(((CPAtomicRefCompareAndSetCodec.RequestParameters)this.parameters).groupId, op);
    }

    @Override
    protected CPAtomicRefCompareAndSetCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return CPAtomicRefCompareAndSetCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return CPAtomicRefCompareAndSetCodec.encodeResponse((Boolean)response);
    }

    @Override
    public String getServiceName() {
        return "hz:raft:atomicRefService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new AtomicReferencePermission(((CPAtomicRefCompareAndSetCodec.RequestParameters)this.parameters).name, "modify");
    }

    @Override
    public String getDistributedObjectName() {
        return ((CPAtomicRefCompareAndSetCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "compareAndSet";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((CPAtomicRefCompareAndSetCodec.RequestParameters)this.parameters).oldValue, ((CPAtomicRefCompareAndSetCodec.RequestParameters)this.parameters).newValue};
    }
}

