/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.scheduledexecutor;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.ScheduledExecutorGetResultFromAddressCodec;
import com.hazelcast.client.impl.protocol.task.AbstractAddressMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.Connection;
import com.hazelcast.scheduledexecutor.ScheduledTaskHandler;
import com.hazelcast.scheduledexecutor.impl.ScheduledTaskHandlerImpl;
import com.hazelcast.scheduledexecutor.impl.ScheduledTaskResult;
import com.hazelcast.scheduledexecutor.impl.operations.GetResultOperation;
import com.hazelcast.security.permission.ScheduledExecutorPermission;
import com.hazelcast.spi.Operation;
import java.security.Permission;

public class ScheduledExecutorTaskGetResultFromAddressMessageTask
extends AbstractAddressMessageTask<ScheduledExecutorGetResultFromAddressCodec.RequestParameters> {
    public ScheduledExecutorTaskGetResultFromAddressMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        ScheduledTaskHandler handler = ScheduledTaskHandlerImpl.of(((ScheduledExecutorGetResultFromAddressCodec.RequestParameters)this.parameters).address, ((ScheduledExecutorGetResultFromAddressCodec.RequestParameters)this.parameters).schedulerName, ((ScheduledExecutorGetResultFromAddressCodec.RequestParameters)this.parameters).taskName);
        return new GetResultOperation(handler);
    }

    @Override
    protected Address getAddress() {
        return ((ScheduledExecutorGetResultFromAddressCodec.RequestParameters)this.parameters).address;
    }

    @Override
    protected ScheduledExecutorGetResultFromAddressCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        this.parameters = ScheduledExecutorGetResultFromAddressCodec.decodeRequest(clientMessage);
        ((ScheduledExecutorGetResultFromAddressCodec.RequestParameters)this.parameters).address = this.clientEngine.memberAddressOf(((ScheduledExecutorGetResultFromAddressCodec.RequestParameters)this.parameters).address);
        return (ScheduledExecutorGetResultFromAddressCodec.RequestParameters)this.parameters;
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        Object data = this.nodeEngine.getSerializationService().toData(response);
        return ScheduledExecutorGetResultFromAddressCodec.encodeResponse(data);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:scheduledExecutorService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new ScheduledExecutorPermission(((ScheduledExecutorGetResultFromAddressCodec.RequestParameters)this.parameters).schedulerName, "read");
    }

    @Override
    public String getDistributedObjectName() {
        return ((ScheduledExecutorGetResultFromAddressCodec.RequestParameters)this.parameters).schedulerName;
    }

    @Override
    public String getMethodName() {
        return "getResultTimeout";
    }

    @Override
    public Object[] getParameters() {
        return null;
    }

    @Override
    protected void sendClientMessage(Throwable throwable) {
        if (throwable instanceof ScheduledTaskResult.ExecutionExceptionDecorator) {
            super.sendClientMessage(throwable.getCause());
        } else {
            super.sendClientMessage(throwable);
        }
    }
}

