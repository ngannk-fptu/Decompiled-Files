/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.executorservice;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.ExecutorServiceSubmitToPartitionCodec;
import com.hazelcast.client.impl.protocol.task.AbstractInvocationMessageTask;
import com.hazelcast.core.ExecutionCallback;
import com.hazelcast.executor.impl.operations.CallableTaskOperation;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.security.SecureCallable;
import com.hazelcast.security.SecurityContext;
import com.hazelcast.spi.InvocationBuilder;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.impl.operationservice.InternalOperationService;
import java.security.Permission;
import javax.security.auth.Subject;

public class ExecutorServiceSubmitToPartitionMessageTask
extends AbstractInvocationMessageTask<ExecutorServiceSubmitToPartitionCodec.RequestParameters>
implements ExecutionCallback {
    public ExecutorServiceSubmitToPartitionMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected InvocationBuilder getInvocationBuilder(Operation op) {
        if (((ExecutorServiceSubmitToPartitionCodec.RequestParameters)this.parameters).partitionId == -1) {
            throw new IllegalArgumentException("Partition ID is -1");
        }
        InternalOperationService operationService = this.nodeEngine.getOperationService();
        return operationService.createInvocationBuilder(this.getServiceName(), op, ((ExecutorServiceSubmitToPartitionCodec.RequestParameters)this.parameters).partitionId);
    }

    @Override
    protected Operation prepareOperation() {
        SecurityContext securityContext = this.clientEngine.getSecurityContext();
        Data callableData = ((ExecutorServiceSubmitToPartitionCodec.RequestParameters)this.parameters).callable;
        if (securityContext != null) {
            Subject subject = this.endpoint.getSubject();
            SecureCallable callable = (SecureCallable)this.serializationService.toObject(((ExecutorServiceSubmitToPartitionCodec.RequestParameters)this.parameters).callable);
            callable = securityContext.createSecureCallable(subject, callable);
            callableData = this.serializationService.toData(callable);
        }
        return new CallableTaskOperation(((ExecutorServiceSubmitToPartitionCodec.RequestParameters)this.parameters).name, ((ExecutorServiceSubmitToPartitionCodec.RequestParameters)this.parameters).uuid, callableData);
    }

    @Override
    protected ExecutorServiceSubmitToPartitionCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return ExecutorServiceSubmitToPartitionCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        Object data = this.serializationService.toData(response);
        return ExecutorServiceSubmitToPartitionCodec.encodeResponse(data);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:executorService";
    }

    @Override
    public Permission getRequiredPermission() {
        return null;
    }

    @Override
    public String getDistributedObjectName() {
        return ((ExecutorServiceSubmitToPartitionCodec.RequestParameters)this.parameters).name;
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

