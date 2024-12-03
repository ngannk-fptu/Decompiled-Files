/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.multimap;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.MultiMapUnlockCodec;
import com.hazelcast.client.impl.protocol.task.AbstractPartitionMessageTask;
import com.hazelcast.concurrent.lock.operations.UnlockOperation;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.MultiMapPermission;
import com.hazelcast.spi.DistributedObjectNamespace;
import com.hazelcast.spi.ObjectNamespace;
import com.hazelcast.spi.Operation;
import java.security.Permission;

public class MultiMapUnlockMessageTask
extends AbstractPartitionMessageTask<MultiMapUnlockCodec.RequestParameters> {
    public MultiMapUnlockMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        DistributedObjectNamespace namespace = new DistributedObjectNamespace("hz:impl:multiMapService", ((MultiMapUnlockCodec.RequestParameters)this.parameters).name);
        return new UnlockOperation((ObjectNamespace)namespace, ((MultiMapUnlockCodec.RequestParameters)this.parameters).key, ((MultiMapUnlockCodec.RequestParameters)this.parameters).threadId, false, ((MultiMapUnlockCodec.RequestParameters)this.parameters).referenceId);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return MultiMapUnlockCodec.encodeResponse();
    }

    @Override
    protected MultiMapUnlockCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return MultiMapUnlockCodec.decodeRequest(clientMessage);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:lockService";
    }

    @Override
    public String getDistributedObjectType() {
        return "hz:impl:multiMapService";
    }

    @Override
    public String getMethodName() {
        return "unlock";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((MultiMapUnlockCodec.RequestParameters)this.parameters).key};
    }

    @Override
    public Permission getRequiredPermission() {
        return new MultiMapPermission(((MultiMapUnlockCodec.RequestParameters)this.parameters).name, "lock");
    }

    @Override
    public String getDistributedObjectName() {
        return ((MultiMapUnlockCodec.RequestParameters)this.parameters).name;
    }
}

