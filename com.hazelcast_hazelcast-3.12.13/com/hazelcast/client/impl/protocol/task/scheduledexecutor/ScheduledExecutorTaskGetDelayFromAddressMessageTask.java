/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.scheduledexecutor;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.ScheduledExecutorGetDelayFromAddressCodec;
import com.hazelcast.client.impl.protocol.task.AbstractAddressMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.Connection;
import com.hazelcast.scheduledexecutor.ScheduledTaskHandler;
import com.hazelcast.scheduledexecutor.impl.ScheduledTaskHandlerImpl;
import com.hazelcast.scheduledexecutor.impl.operations.GetDelayOperation;
import com.hazelcast.security.permission.ScheduledExecutorPermission;
import com.hazelcast.spi.Operation;
import java.security.Permission;
import java.util.concurrent.TimeUnit;

public class ScheduledExecutorTaskGetDelayFromAddressMessageTask
extends AbstractAddressMessageTask<ScheduledExecutorGetDelayFromAddressCodec.RequestParameters> {
    public ScheduledExecutorTaskGetDelayFromAddressMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        ScheduledTaskHandler handler = ScheduledTaskHandlerImpl.of(((ScheduledExecutorGetDelayFromAddressCodec.RequestParameters)this.parameters).address, ((ScheduledExecutorGetDelayFromAddressCodec.RequestParameters)this.parameters).schedulerName, ((ScheduledExecutorGetDelayFromAddressCodec.RequestParameters)this.parameters).taskName);
        return new GetDelayOperation(handler, TimeUnit.NANOSECONDS);
    }

    @Override
    protected Address getAddress() {
        return ((ScheduledExecutorGetDelayFromAddressCodec.RequestParameters)this.parameters).address;
    }

    @Override
    protected ScheduledExecutorGetDelayFromAddressCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        this.parameters = ScheduledExecutorGetDelayFromAddressCodec.decodeRequest(clientMessage);
        ((ScheduledExecutorGetDelayFromAddressCodec.RequestParameters)this.parameters).address = this.clientEngine.memberAddressOf(((ScheduledExecutorGetDelayFromAddressCodec.RequestParameters)this.parameters).address);
        return (ScheduledExecutorGetDelayFromAddressCodec.RequestParameters)this.parameters;
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return ScheduledExecutorGetDelayFromAddressCodec.encodeResponse((Long)response);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:scheduledExecutorService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new ScheduledExecutorPermission(((ScheduledExecutorGetDelayFromAddressCodec.RequestParameters)this.parameters).schedulerName, "read");
    }

    @Override
    public String getDistributedObjectName() {
        return ((ScheduledExecutorGetDelayFromAddressCodec.RequestParameters)this.parameters).schedulerName;
    }

    @Override
    public String getMethodName() {
        return "getDelay";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{TimeUnit.NANOSECONDS};
    }
}

