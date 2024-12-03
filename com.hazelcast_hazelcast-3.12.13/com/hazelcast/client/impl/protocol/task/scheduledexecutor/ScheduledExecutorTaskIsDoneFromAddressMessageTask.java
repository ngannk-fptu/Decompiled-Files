/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.scheduledexecutor;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.ScheduledExecutorIsDoneFromAddressCodec;
import com.hazelcast.client.impl.protocol.task.AbstractAddressMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.Connection;
import com.hazelcast.scheduledexecutor.ScheduledTaskHandler;
import com.hazelcast.scheduledexecutor.impl.ScheduledTaskHandlerImpl;
import com.hazelcast.scheduledexecutor.impl.operations.IsDoneOperation;
import com.hazelcast.security.permission.ScheduledExecutorPermission;
import com.hazelcast.spi.Operation;
import java.security.Permission;

public class ScheduledExecutorTaskIsDoneFromAddressMessageTask
extends AbstractAddressMessageTask<ScheduledExecutorIsDoneFromAddressCodec.RequestParameters> {
    public ScheduledExecutorTaskIsDoneFromAddressMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        ScheduledTaskHandler handler = ScheduledTaskHandlerImpl.of(((ScheduledExecutorIsDoneFromAddressCodec.RequestParameters)this.parameters).address, ((ScheduledExecutorIsDoneFromAddressCodec.RequestParameters)this.parameters).schedulerName, ((ScheduledExecutorIsDoneFromAddressCodec.RequestParameters)this.parameters).taskName);
        return new IsDoneOperation(handler);
    }

    @Override
    protected Address getAddress() {
        return ((ScheduledExecutorIsDoneFromAddressCodec.RequestParameters)this.parameters).address;
    }

    @Override
    protected ScheduledExecutorIsDoneFromAddressCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        this.parameters = ScheduledExecutorIsDoneFromAddressCodec.decodeRequest(clientMessage);
        ((ScheduledExecutorIsDoneFromAddressCodec.RequestParameters)this.parameters).address = this.clientEngine.memberAddressOf(((ScheduledExecutorIsDoneFromAddressCodec.RequestParameters)this.parameters).address);
        return (ScheduledExecutorIsDoneFromAddressCodec.RequestParameters)this.parameters;
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return ScheduledExecutorIsDoneFromAddressCodec.encodeResponse((Boolean)response);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:scheduledExecutorService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new ScheduledExecutorPermission(((ScheduledExecutorIsDoneFromAddressCodec.RequestParameters)this.parameters).schedulerName, "read");
    }

    @Override
    public String getDistributedObjectName() {
        return ((ScheduledExecutorIsDoneFromAddressCodec.RequestParameters)this.parameters).schedulerName;
    }

    @Override
    public String getMethodName() {
        return "isDone";
    }

    @Override
    public Object[] getParameters() {
        return null;
    }
}

