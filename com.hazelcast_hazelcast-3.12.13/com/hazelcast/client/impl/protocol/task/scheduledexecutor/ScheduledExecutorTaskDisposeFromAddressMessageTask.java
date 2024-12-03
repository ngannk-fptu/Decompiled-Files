/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.scheduledexecutor;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.ScheduledExecutorDisposeFromAddressCodec;
import com.hazelcast.client.impl.protocol.task.AbstractAddressMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.Connection;
import com.hazelcast.scheduledexecutor.ScheduledTaskHandler;
import com.hazelcast.scheduledexecutor.impl.ScheduledTaskHandlerImpl;
import com.hazelcast.scheduledexecutor.impl.operations.DisposeTaskOperation;
import com.hazelcast.security.permission.ScheduledExecutorPermission;
import com.hazelcast.spi.Operation;
import java.security.Permission;

public class ScheduledExecutorTaskDisposeFromAddressMessageTask
extends AbstractAddressMessageTask<ScheduledExecutorDisposeFromAddressCodec.RequestParameters> {
    public ScheduledExecutorTaskDisposeFromAddressMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        ScheduledTaskHandler handler = ScheduledTaskHandlerImpl.of(((ScheduledExecutorDisposeFromAddressCodec.RequestParameters)this.parameters).address, ((ScheduledExecutorDisposeFromAddressCodec.RequestParameters)this.parameters).schedulerName, ((ScheduledExecutorDisposeFromAddressCodec.RequestParameters)this.parameters).taskName);
        return new DisposeTaskOperation(handler);
    }

    @Override
    protected Address getAddress() {
        return ((ScheduledExecutorDisposeFromAddressCodec.RequestParameters)this.parameters).address;
    }

    @Override
    protected ScheduledExecutorDisposeFromAddressCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        this.parameters = ScheduledExecutorDisposeFromAddressCodec.decodeRequest(clientMessage);
        ((ScheduledExecutorDisposeFromAddressCodec.RequestParameters)this.parameters).address = this.clientEngine.memberAddressOf(((ScheduledExecutorDisposeFromAddressCodec.RequestParameters)this.parameters).address);
        return (ScheduledExecutorDisposeFromAddressCodec.RequestParameters)this.parameters;
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return ScheduledExecutorDisposeFromAddressCodec.encodeResponse();
    }

    @Override
    public String getServiceName() {
        return "hz:impl:scheduledExecutorService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new ScheduledExecutorPermission(((ScheduledExecutorDisposeFromAddressCodec.RequestParameters)this.parameters).schedulerName, "modify");
    }

    @Override
    public String getDistributedObjectName() {
        return ((ScheduledExecutorDisposeFromAddressCodec.RequestParameters)this.parameters).schedulerName;
    }

    @Override
    public String getMethodName() {
        return "dispose";
    }

    @Override
    public Object[] getParameters() {
        return null;
    }
}

