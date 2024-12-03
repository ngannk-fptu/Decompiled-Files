/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.scheduledexecutor;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.ScheduledExecutorCancelFromAddressCodec;
import com.hazelcast.client.impl.protocol.task.AbstractAddressMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.Connection;
import com.hazelcast.scheduledexecutor.ScheduledTaskHandler;
import com.hazelcast.scheduledexecutor.impl.ScheduledTaskHandlerImpl;
import com.hazelcast.scheduledexecutor.impl.operations.CancelTaskOperation;
import com.hazelcast.security.permission.ScheduledExecutorPermission;
import com.hazelcast.spi.Operation;
import java.security.Permission;

public class ScheduledExecutorTaskCancelFromAddressMessageTask
extends AbstractAddressMessageTask<ScheduledExecutorCancelFromAddressCodec.RequestParameters> {
    public ScheduledExecutorTaskCancelFromAddressMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        ScheduledTaskHandler handler = ScheduledTaskHandlerImpl.of(((ScheduledExecutorCancelFromAddressCodec.RequestParameters)this.parameters).address, ((ScheduledExecutorCancelFromAddressCodec.RequestParameters)this.parameters).schedulerName, ((ScheduledExecutorCancelFromAddressCodec.RequestParameters)this.parameters).taskName);
        return new CancelTaskOperation(handler, ((ScheduledExecutorCancelFromAddressCodec.RequestParameters)this.parameters).mayInterruptIfRunning);
    }

    @Override
    protected Address getAddress() {
        return ((ScheduledExecutorCancelFromAddressCodec.RequestParameters)this.parameters).address;
    }

    @Override
    protected ScheduledExecutorCancelFromAddressCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        this.parameters = ScheduledExecutorCancelFromAddressCodec.decodeRequest(clientMessage);
        ((ScheduledExecutorCancelFromAddressCodec.RequestParameters)this.parameters).address = this.clientEngine.memberAddressOf(((ScheduledExecutorCancelFromAddressCodec.RequestParameters)this.parameters).address);
        return (ScheduledExecutorCancelFromAddressCodec.RequestParameters)this.parameters;
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return ScheduledExecutorCancelFromAddressCodec.encodeResponse((Boolean)response);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:scheduledExecutorService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new ScheduledExecutorPermission(((ScheduledExecutorCancelFromAddressCodec.RequestParameters)this.parameters).schedulerName, "modify");
    }

    @Override
    public String getDistributedObjectName() {
        return ((ScheduledExecutorCancelFromAddressCodec.RequestParameters)this.parameters).schedulerName;
    }

    @Override
    public String getMethodName() {
        return "cancel";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((ScheduledExecutorCancelFromAddressCodec.RequestParameters)this.parameters).mayInterruptIfRunning};
    }
}

