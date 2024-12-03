/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.scheduledexecutor;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.ScheduledExecutorIsCancelledFromAddressCodec;
import com.hazelcast.client.impl.protocol.task.AbstractAddressMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.Connection;
import com.hazelcast.scheduledexecutor.ScheduledTaskHandler;
import com.hazelcast.scheduledexecutor.impl.ScheduledTaskHandlerImpl;
import com.hazelcast.scheduledexecutor.impl.operations.IsCanceledOperation;
import com.hazelcast.security.permission.ScheduledExecutorPermission;
import com.hazelcast.spi.Operation;
import java.security.Permission;

public class ScheduledExecutorTaskIsCancelledFromAddressMessageTask
extends AbstractAddressMessageTask<ScheduledExecutorIsCancelledFromAddressCodec.RequestParameters> {
    public ScheduledExecutorTaskIsCancelledFromAddressMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        ScheduledTaskHandler handler = ScheduledTaskHandlerImpl.of(((ScheduledExecutorIsCancelledFromAddressCodec.RequestParameters)this.parameters).address, ((ScheduledExecutorIsCancelledFromAddressCodec.RequestParameters)this.parameters).schedulerName, ((ScheduledExecutorIsCancelledFromAddressCodec.RequestParameters)this.parameters).taskName);
        return new IsCanceledOperation(handler);
    }

    @Override
    protected Address getAddress() {
        return ((ScheduledExecutorIsCancelledFromAddressCodec.RequestParameters)this.parameters).address;
    }

    @Override
    protected ScheduledExecutorIsCancelledFromAddressCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        this.parameters = ScheduledExecutorIsCancelledFromAddressCodec.decodeRequest(clientMessage);
        ((ScheduledExecutorIsCancelledFromAddressCodec.RequestParameters)this.parameters).address = this.clientEngine.memberAddressOf(((ScheduledExecutorIsCancelledFromAddressCodec.RequestParameters)this.parameters).address);
        return (ScheduledExecutorIsCancelledFromAddressCodec.RequestParameters)this.parameters;
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return ScheduledExecutorIsCancelledFromAddressCodec.encodeResponse((Boolean)response);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:scheduledExecutorService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new ScheduledExecutorPermission(((ScheduledExecutorIsCancelledFromAddressCodec.RequestParameters)this.parameters).schedulerName, "read");
    }

    @Override
    public String getDistributedObjectName() {
        return ((ScheduledExecutorIsCancelledFromAddressCodec.RequestParameters)this.parameters).schedulerName;
    }

    @Override
    public String getMethodName() {
        return "isCancelled";
    }

    @Override
    public Object[] getParameters() {
        return null;
    }
}

