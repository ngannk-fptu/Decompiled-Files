/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.executorservice.durable;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.DurableExecutorSubmitToPartitionCodec;
import com.hazelcast.client.impl.protocol.task.AbstractPartitionMessageTask;
import com.hazelcast.core.ExecutionCallback;
import com.hazelcast.durableexecutor.impl.operations.TaskOperation;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.security.SecureCallable;
import com.hazelcast.security.SecurityContext;
import com.hazelcast.spi.Operation;
import java.security.Permission;
import javax.security.auth.Subject;

public class DurableExecutorSubmitToPartitionMessageTask
extends AbstractPartitionMessageTask<DurableExecutorSubmitToPartitionCodec.RequestParameters>
implements ExecutionCallback {
    public DurableExecutorSubmitToPartitionMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        SecurityContext securityContext = this.clientEngine.getSecurityContext();
        Data callableData = ((DurableExecutorSubmitToPartitionCodec.RequestParameters)this.parameters).callable;
        if (securityContext != null) {
            Subject subject = this.endpoint.getSubject();
            SecureCallable callable = (SecureCallable)this.serializationService.toObject(((DurableExecutorSubmitToPartitionCodec.RequestParameters)this.parameters).callable);
            callable = securityContext.createSecureCallable(subject, callable);
            callableData = this.serializationService.toData(callable);
        }
        return new TaskOperation(((DurableExecutorSubmitToPartitionCodec.RequestParameters)this.parameters).name, callableData);
    }

    @Override
    protected DurableExecutorSubmitToPartitionCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return DurableExecutorSubmitToPartitionCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return DurableExecutorSubmitToPartitionCodec.encodeResponse((Integer)response);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:durableExecutorService";
    }

    @Override
    public Permission getRequiredPermission() {
        return null;
    }

    @Override
    public String getDistributedObjectName() {
        return ((DurableExecutorSubmitToPartitionCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return null;
    }

    @Override
    public Object[] getParameters() {
        return null;
    }
}

