/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.map;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.ContinuousQueryPublisherCreateCodec;
import com.hazelcast.client.impl.protocol.task.AbstractCallableMessageTask;
import com.hazelcast.client.impl.protocol.task.BlockingMessageTask;
import com.hazelcast.instance.MemberImpl;
import com.hazelcast.instance.Node;
import com.hazelcast.internal.cluster.ClusterService;
import com.hazelcast.map.impl.query.QueryResult;
import com.hazelcast.map.impl.query.QueryResultRow;
import com.hazelcast.map.impl.querycache.accumulator.AccumulatorInfo;
import com.hazelcast.map.impl.querycache.subscriber.operation.PublisherCreateOperation;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.Connection;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.query.Predicate;
import com.hazelcast.spi.InternalCompletableFuture;
import com.hazelcast.spi.InvocationBuilder;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.impl.operationservice.InternalOperationService;
import com.hazelcast.util.ExceptionUtil;
import com.hazelcast.util.collection.InflatableSet;
import java.security.Permission;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;

public class MapPublisherCreateMessageTask
extends AbstractCallableMessageTask<ContinuousQueryPublisherCreateCodec.RequestParameters>
implements BlockingMessageTask {
    public MapPublisherCreateMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Object call() throws Exception {
        ClusterService clusterService = this.clientEngine.getClusterService();
        Collection<MemberImpl> members = clusterService.getMemberImpls();
        ArrayList<Future> futures = new ArrayList<Future>(members.size());
        this.createInvocations(members, futures);
        return MapPublisherCreateMessageTask.fetchMapSnapshotFrom(futures);
    }

    private void createInvocations(Collection<MemberImpl> members, List<Future> futures) {
        InternalOperationService operationService = this.nodeEngine.getOperationService();
        for (MemberImpl member : members) {
            Predicate predicate = (Predicate)this.serializationService.toObject(((ContinuousQueryPublisherCreateCodec.RequestParameters)this.parameters).predicate);
            AccumulatorInfo accumulatorInfo = AccumulatorInfo.toAccumulatorInfo(((ContinuousQueryPublisherCreateCodec.RequestParameters)this.parameters).mapName, ((ContinuousQueryPublisherCreateCodec.RequestParameters)this.parameters).cacheName, predicate, ((ContinuousQueryPublisherCreateCodec.RequestParameters)this.parameters).batchSize, ((ContinuousQueryPublisherCreateCodec.RequestParameters)this.parameters).bufferSize, ((ContinuousQueryPublisherCreateCodec.RequestParameters)this.parameters).delaySeconds, false, ((ContinuousQueryPublisherCreateCodec.RequestParameters)this.parameters).populate, ((ContinuousQueryPublisherCreateCodec.RequestParameters)this.parameters).coalesce);
            PublisherCreateOperation operation = new PublisherCreateOperation(accumulatorInfo);
            operation.setCallerUuid(this.endpoint.getUuid());
            Address address = member.getAddress();
            InvocationBuilder invocationBuilder = operationService.createInvocationBuilder("hz:impl:mapService", (Operation)operation, address);
            InternalCompletableFuture future = invocationBuilder.invoke();
            futures.add(future);
        }
    }

    private static Set<Data> fetchMapSnapshotFrom(List<Future> futures) {
        ArrayList<Object> queryResults = new ArrayList<Object>(futures.size());
        int queryResultSize = 0;
        for (Future future : futures) {
            Object result;
            try {
                result = future.get();
            }
            catch (Throwable t) {
                throw ExceptionUtil.rethrow(t);
            }
            if (result == null) continue;
            queryResults.add(result);
            queryResultSize += ((QueryResult)result).size();
        }
        return MapPublisherCreateMessageTask.unpackResults(queryResults, queryResultSize);
    }

    private static Set<Data> unpackResults(List<Object> results, int numOfEntries) {
        InflatableSet.Builder<Data> builder = InflatableSet.newBuilder(numOfEntries);
        for (Object result : results) {
            for (QueryResultRow row : (QueryResult)result) {
                builder.add(row.getKey());
            }
        }
        return builder.build();
    }

    @Override
    protected ContinuousQueryPublisherCreateCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return ContinuousQueryPublisherCreateCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return ContinuousQueryPublisherCreateCodec.encodeResponse((Set)response);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:mapService";
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

