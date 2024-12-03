/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.executorservice;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.ExecutorServiceCancelOnAddressCodec;
import com.hazelcast.client.impl.protocol.task.AbstractAddressMessageTask;
import com.hazelcast.executor.impl.operations.CancellationOperation;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.Connection;
import com.hazelcast.spi.Operation;
import java.security.Permission;

public class ExecutorServiceCancelOnAddressMessageTask
extends AbstractAddressMessageTask<ExecutorServiceCancelOnAddressCodec.RequestParameters> {
    public ExecutorServiceCancelOnAddressMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        return new CancellationOperation(((ExecutorServiceCancelOnAddressCodec.RequestParameters)this.parameters).uuid, ((ExecutorServiceCancelOnAddressCodec.RequestParameters)this.parameters).interrupt);
    }

    @Override
    protected Address getAddress() {
        return ((ExecutorServiceCancelOnAddressCodec.RequestParameters)this.parameters).address;
    }

    @Override
    protected ExecutorServiceCancelOnAddressCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        this.parameters = ExecutorServiceCancelOnAddressCodec.decodeRequest(clientMessage);
        ((ExecutorServiceCancelOnAddressCodec.RequestParameters)this.parameters).address = this.clientEngine.memberAddressOf(((ExecutorServiceCancelOnAddressCodec.RequestParameters)this.parameters).address);
        return (ExecutorServiceCancelOnAddressCodec.RequestParameters)this.parameters;
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return ExecutorServiceCancelOnAddressCodec.encodeResponse((Boolean)response);
    }

    @Override
    public String getDistributedObjectName() {
        return null;
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
    public String getMethodName() {
        return "cancel";
    }

    @Override
    public Object[] getParameters() {
        return null;
    }
}

