/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.datastructures.atomiclong.client;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.CPAtomicLongAlterCodec;
import com.hazelcast.core.IFunction;
import com.hazelcast.cp.internal.client.AbstractCPMessageTask;
import com.hazelcast.cp.internal.datastructures.atomiclong.operation.AlterOp;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.AtomicLongPermission;
import java.security.Permission;

public class AlterMessageTask
extends AbstractCPMessageTask<CPAtomicLongAlterCodec.RequestParameters> {
    public AlterMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected void processMessage() {
        IFunction function = (IFunction)this.serializationService.toObject(((CPAtomicLongAlterCodec.RequestParameters)this.parameters).function);
        AlterOp.AlterResultType resultType = AlterOp.AlterResultType.fromValue(((CPAtomicLongAlterCodec.RequestParameters)this.parameters).returnValueType);
        this.invoke(((CPAtomicLongAlterCodec.RequestParameters)this.parameters).groupId, new AlterOp(((CPAtomicLongAlterCodec.RequestParameters)this.parameters).name, function, resultType));
    }

    @Override
    protected CPAtomicLongAlterCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return CPAtomicLongAlterCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return CPAtomicLongAlterCodec.encodeResponse((Long)response);
    }

    @Override
    public String getServiceName() {
        return "hz:raft:atomicLongService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new AtomicLongPermission(((CPAtomicLongAlterCodec.RequestParameters)this.parameters).name, "modify");
    }

    @Override
    public String getDistributedObjectName() {
        return ((CPAtomicLongAlterCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        if (((CPAtomicLongAlterCodec.RequestParameters)this.parameters).returnValueType == AlterOp.AlterResultType.OLD_VALUE.value()) {
            return "getAndAlter";
        }
        if (((CPAtomicLongAlterCodec.RequestParameters)this.parameters).returnValueType == AlterOp.AlterResultType.NEW_VALUE.value()) {
            return "alterAndGet";
        }
        return "alter";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((CPAtomicLongAlterCodec.RequestParameters)this.parameters).function};
    }
}

