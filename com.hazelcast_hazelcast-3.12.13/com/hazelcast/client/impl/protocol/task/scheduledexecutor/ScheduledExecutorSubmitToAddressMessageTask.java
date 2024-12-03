/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.scheduledexecutor;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.ScheduledExecutorSubmitToAddressCodec;
import com.hazelcast.client.impl.protocol.task.AbstractAddressMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.Connection;
import com.hazelcast.scheduledexecutor.impl.TaskDefinition;
import com.hazelcast.scheduledexecutor.impl.operations.ScheduleTaskOperation;
import com.hazelcast.security.permission.ScheduledExecutorPermission;
import com.hazelcast.spi.Operation;
import java.security.Permission;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public class ScheduledExecutorSubmitToAddressMessageTask
extends AbstractAddressMessageTask<ScheduledExecutorSubmitToAddressCodec.RequestParameters> {
    public ScheduledExecutorSubmitToAddressMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        Callable callable = (Callable)this.serializationService.toObject(((ScheduledExecutorSubmitToAddressCodec.RequestParameters)this.parameters).task);
        TaskDefinition def = new TaskDefinition(TaskDefinition.Type.getById(((ScheduledExecutorSubmitToAddressCodec.RequestParameters)this.parameters).type), ((ScheduledExecutorSubmitToAddressCodec.RequestParameters)this.parameters).taskName, callable, ((ScheduledExecutorSubmitToAddressCodec.RequestParameters)this.parameters).initialDelayInMillis, ((ScheduledExecutorSubmitToAddressCodec.RequestParameters)this.parameters).periodInMillis, TimeUnit.MILLISECONDS);
        return new ScheduleTaskOperation(((ScheduledExecutorSubmitToAddressCodec.RequestParameters)this.parameters).schedulerName, def);
    }

    @Override
    protected Address getAddress() {
        return ((ScheduledExecutorSubmitToAddressCodec.RequestParameters)this.parameters).address;
    }

    @Override
    protected ScheduledExecutorSubmitToAddressCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        this.parameters = ScheduledExecutorSubmitToAddressCodec.decodeRequest(clientMessage);
        ((ScheduledExecutorSubmitToAddressCodec.RequestParameters)this.parameters).address = this.clientEngine.memberAddressOf(((ScheduledExecutorSubmitToAddressCodec.RequestParameters)this.parameters).address);
        return (ScheduledExecutorSubmitToAddressCodec.RequestParameters)this.parameters;
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return ScheduledExecutorSubmitToAddressCodec.encodeResponse();
    }

    @Override
    public String getServiceName() {
        return "hz:impl:scheduledExecutorService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new ScheduledExecutorPermission(((ScheduledExecutorSubmitToAddressCodec.RequestParameters)this.parameters).schedulerName, "modify");
    }

    @Override
    public String getDistributedObjectName() {
        return ((ScheduledExecutorSubmitToAddressCodec.RequestParameters)this.parameters).schedulerName;
    }

    @Override
    public String getMethodName() {
        return "submitToAddress";
    }

    @Override
    public Object[] getParameters() {
        Callable callable = (Callable)this.serializationService.toObject(((ScheduledExecutorSubmitToAddressCodec.RequestParameters)this.parameters).task);
        TaskDefinition def = new TaskDefinition(TaskDefinition.Type.getById(((ScheduledExecutorSubmitToAddressCodec.RequestParameters)this.parameters).type), ((ScheduledExecutorSubmitToAddressCodec.RequestParameters)this.parameters).taskName, callable, ((ScheduledExecutorSubmitToAddressCodec.RequestParameters)this.parameters).initialDelayInMillis, ((ScheduledExecutorSubmitToAddressCodec.RequestParameters)this.parameters).periodInMillis, TimeUnit.MILLISECONDS);
        return new Object[]{((ScheduledExecutorSubmitToAddressCodec.RequestParameters)this.parameters).schedulerName, ((ScheduledExecutorSubmitToAddressCodec.RequestParameters)this.parameters).address, def};
    }
}

