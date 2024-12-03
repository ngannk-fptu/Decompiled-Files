/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.executorservice;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.ExecutorServiceSubmitToAddressCodec;
import com.hazelcast.client.impl.protocol.task.AbstractInvocationMessageTask;
import com.hazelcast.core.ExecutionCallback;
import com.hazelcast.executor.impl.operations.MemberCallableTaskOperation;
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

public class ExecutorServiceSubmitToAddressMessageTask
extends AbstractInvocationMessageTask<ExecutorServiceSubmitToAddressCodec.RequestParameters>
implements ExecutionCallback {
    public ExecutorServiceSubmitToAddressMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected InvocationBuilder getInvocationBuilder(Operation op) {
        InternalOperationService operationService = this.nodeEngine.getOperationService();
        return operationService.createInvocationBuilder(this.getServiceName(), op, ((ExecutorServiceSubmitToAddressCodec.RequestParameters)this.parameters).address);
    }

    @Override
    protected Operation prepareOperation() {
        SecurityContext securityContext = this.clientEngine.getSecurityContext();
        Data callableData = ((ExecutorServiceSubmitToAddressCodec.RequestParameters)this.parameters).callable;
        if (securityContext != null) {
            SecureCallable callable = (SecureCallable)this.serializationService.toObject(((ExecutorServiceSubmitToAddressCodec.RequestParameters)this.parameters).callable);
            Subject subject = this.endpoint.getSubject();
            callable = securityContext.createSecureCallable(subject, callable);
            callableData = this.serializationService.toData(callable);
        }
        MemberCallableTaskOperation op = new MemberCallableTaskOperation(((ExecutorServiceSubmitToAddressCodec.RequestParameters)this.parameters).name, ((ExecutorServiceSubmitToAddressCodec.RequestParameters)this.parameters).uuid, callableData);
        op.setCallerUuid(this.endpoint.getUuid());
        return op;
    }

    @Override
    protected ExecutorServiceSubmitToAddressCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        this.parameters = ExecutorServiceSubmitToAddressCodec.decodeRequest(clientMessage);
        ((ExecutorServiceSubmitToAddressCodec.RequestParameters)this.parameters).address = this.clientEngine.memberAddressOf(((ExecutorServiceSubmitToAddressCodec.RequestParameters)this.parameters).address);
        return (ExecutorServiceSubmitToAddressCodec.RequestParameters)this.parameters;
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        Object data = this.serializationService.toData(response);
        return ExecutorServiceSubmitToAddressCodec.encodeResponse(data);
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
        return ((ExecutorServiceSubmitToAddressCodec.RequestParameters)this.parameters).name;
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

