/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.multimap;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.MultiMapTryLockCodec;
import com.hazelcast.client.impl.protocol.task.AbstractPartitionMessageTask;
import com.hazelcast.concurrent.lock.operations.LockOperation;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.MultiMapPermission;
import com.hazelcast.spi.DistributedObjectNamespace;
import com.hazelcast.spi.Operation;
import java.security.Permission;
import java.util.concurrent.TimeUnit;

public class MultiMapTryLockMessageTask
extends AbstractPartitionMessageTask<MultiMapTryLockCodec.RequestParameters> {
    public MultiMapTryLockMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        DistributedObjectNamespace namespace = new DistributedObjectNamespace("hz:impl:multiMapService", ((MultiMapTryLockCodec.RequestParameters)this.parameters).name);
        return new LockOperation(namespace, ((MultiMapTryLockCodec.RequestParameters)this.parameters).key, ((MultiMapTryLockCodec.RequestParameters)this.parameters).threadId, ((MultiMapTryLockCodec.RequestParameters)this.parameters).lease, ((MultiMapTryLockCodec.RequestParameters)this.parameters).timeout, ((MultiMapTryLockCodec.RequestParameters)this.parameters).referenceId);
    }

    @Override
    protected MultiMapTryLockCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return MultiMapTryLockCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return MultiMapTryLockCodec.encodeResponse((Boolean)response);
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
        return "tryLock";
    }

    @Override
    public Object[] getParameters() {
        if (((MultiMapTryLockCodec.RequestParameters)this.parameters).timeout == 0L) {
            return new Object[]{((MultiMapTryLockCodec.RequestParameters)this.parameters).key};
        }
        return new Object[]{((MultiMapTryLockCodec.RequestParameters)this.parameters).key, ((MultiMapTryLockCodec.RequestParameters)this.parameters).timeout, TimeUnit.MILLISECONDS};
    }

    @Override
    public Permission getRequiredPermission() {
        return new MultiMapPermission(((MultiMapTryLockCodec.RequestParameters)this.parameters).name, "lock");
    }

    @Override
    public String getDistributedObjectName() {
        return ((MultiMapTryLockCodec.RequestParameters)this.parameters).name;
    }
}

