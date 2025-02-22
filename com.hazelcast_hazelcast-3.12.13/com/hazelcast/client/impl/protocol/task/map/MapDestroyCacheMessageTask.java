/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.map;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.ContinuousQueryDestroyCacheCodec;
import com.hazelcast.client.impl.protocol.task.AbstractCallableMessageTask;
import com.hazelcast.client.impl.protocol.task.BlockingMessageTask;
import com.hazelcast.instance.MemberImpl;
import com.hazelcast.instance.Node;
import com.hazelcast.internal.cluster.ClusterService;
import com.hazelcast.map.impl.querycache.subscriber.operation.DestroyQueryCacheOperation;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.Connection;
import com.hazelcast.spi.InternalCompletableFuture;
import com.hazelcast.spi.InvocationBuilder;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.impl.operationservice.InternalOperationService;
import com.hazelcast.util.FutureUtil;
import java.security.Permission;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class MapDestroyCacheMessageTask
extends AbstractCallableMessageTask<ContinuousQueryDestroyCacheCodec.RequestParameters>
implements BlockingMessageTask {
    public MapDestroyCacheMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Object call() throws Exception {
        ClusterService clusterService = this.clientEngine.getClusterService();
        Collection<MemberImpl> members = clusterService.getMemberImpls();
        ArrayList futures = new ArrayList(members.size());
        this.createInvocations(members, futures);
        Collection<Boolean> results = FutureUtil.returnWithDeadline(futures, 1L, TimeUnit.MINUTES);
        return this.reduce(results);
    }

    private boolean reduce(Collection<Boolean> results) {
        return !results.contains(Boolean.FALSE);
    }

    private void createInvocations(Collection<MemberImpl> members, List<Future<Boolean>> futures) {
        InternalOperationService operationService = this.nodeEngine.getOperationService();
        for (MemberImpl member : members) {
            DestroyQueryCacheOperation operation = new DestroyQueryCacheOperation(((ContinuousQueryDestroyCacheCodec.RequestParameters)this.parameters).mapName, ((ContinuousQueryDestroyCacheCodec.RequestParameters)this.parameters).cacheName);
            operation.setCallerUuid(this.endpoint.getUuid());
            Address address = member.getAddress();
            InvocationBuilder invocationBuilder = operationService.createInvocationBuilder("hz:impl:mapService", (Operation)operation, address);
            InternalCompletableFuture future = invocationBuilder.invoke();
            futures.add(future);
        }
    }

    @Override
    protected ContinuousQueryDestroyCacheCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return ContinuousQueryDestroyCacheCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return ContinuousQueryDestroyCacheCodec.encodeResponse((Boolean)response);
    }

    @Override
    public String getServiceName() {
        return null;
    }

    @Override
    public Permission getRequiredPermission() {
        return null;
    }

    @Override
    public String getDistributedObjectName() {
        return null;
    }

    @Override
    public String getMethodName() {
        return null;
    }

    @Override
    public Object[] getParameters() {
        return null;
    }
}

