/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.datastructures.atomiclong.client;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.CPAtomicLongCompareAndSetCodec;
import com.hazelcast.cp.internal.client.AbstractCPMessageTask;
import com.hazelcast.cp.internal.datastructures.atomiclong.operation.CompareAndSetOp;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.AtomicLongPermission;
import java.security.Permission;

public class CompareAndSetMessageTask
extends AbstractCPMessageTask<CPAtomicLongCompareAndSetCodec.RequestParameters> {
    public CompareAndSetMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected void processMessage() {
        this.invoke(((CPAtomicLongCompareAndSetCodec.RequestParameters)this.parameters).groupId, new CompareAndSetOp(((CPAtomicLongCompareAndSetCodec.RequestParameters)this.parameters).name, ((CPAtomicLongCompareAndSetCodec.RequestParameters)this.parameters).expected, ((CPAtomicLongCompareAndSetCodec.RequestParameters)this.parameters).updated));
    }

    @Override
    public String getServiceName() {
        return "hz:raft:atomicLongService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new AtomicLongPermission(((CPAtomicLongCompareAndSetCodec.RequestParameters)this.parameters).name, "modify");
    }

    @Override
    public String getDistributedObjectName() {
        return ((CPAtomicLongCompareAndSetCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "compareAndSet";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((CPAtomicLongCompareAndSetCodec.RequestParameters)this.parameters).expected, ((CPAtomicLongCompareAndSetCodec.RequestParameters)this.parameters).updated};
    }

    @Override
    protected CPAtomicLongCompareAndSetCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return CPAtomicLongCompareAndSetCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return CPAtomicLongCompareAndSetCodec.encodeResponse((Boolean)response);
    }
}

