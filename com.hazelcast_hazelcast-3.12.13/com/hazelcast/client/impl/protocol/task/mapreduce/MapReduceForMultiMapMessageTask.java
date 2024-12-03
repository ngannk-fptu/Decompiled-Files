/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.mapreduce;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.MapReduceForMultiMapCodec;
import com.hazelcast.client.impl.protocol.task.mapreduce.AbstractMapReduceTask;
import com.hazelcast.instance.Node;
import com.hazelcast.mapreduce.CombinerFactory;
import com.hazelcast.mapreduce.KeyPredicate;
import com.hazelcast.mapreduce.KeyValueSource;
import com.hazelcast.mapreduce.Mapper;
import com.hazelcast.mapreduce.ReducerFactory;
import com.hazelcast.mapreduce.impl.MultiMapKeyValueSource;
import com.hazelcast.nio.Connection;
import java.util.Collection;
import java.util.List;

public class MapReduceForMultiMapMessageTask
extends AbstractMapReduceTask<MapReduceForMultiMapCodec.RequestParameters> {
    public MapReduceForMultiMapMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected String getJobId() {
        return ((MapReduceForMultiMapCodec.RequestParameters)this.parameters).jobId;
    }

    @Override
    protected int getChunkSize() {
        return ((MapReduceForMultiMapCodec.RequestParameters)this.parameters).chunkSize;
    }

    @Override
    protected String getTopologyChangedStrategy() {
        return ((MapReduceForMultiMapCodec.RequestParameters)this.parameters).topologyChangedStrategy;
    }

    @Override
    protected KeyValueSource getKeyValueSource() {
        return new MultiMapKeyValueSource(((MapReduceForMultiMapCodec.RequestParameters)this.parameters).multiMapName);
    }

    @Override
    protected Mapper getMapper() {
        return (Mapper)this.serializationService.toObject(((MapReduceForMultiMapCodec.RequestParameters)this.parameters).mapper);
    }

    @Override
    protected CombinerFactory getCombinerFactory() {
        return (CombinerFactory)this.serializationService.toObject(((MapReduceForMultiMapCodec.RequestParameters)this.parameters).combinerFactory);
    }

    @Override
    protected ReducerFactory getReducerFactory() {
        return (ReducerFactory)this.serializationService.toObject(((MapReduceForMultiMapCodec.RequestParameters)this.parameters).reducerFactory);
    }

    @Override
    protected Collection getKeys() {
        return ((MapReduceForMultiMapCodec.RequestParameters)this.parameters).keys;
    }

    @Override
    protected KeyPredicate getPredicate() {
        return (KeyPredicate)this.serializationService.toObject(((MapReduceForMultiMapCodec.RequestParameters)this.parameters).predicate);
    }

    @Override
    protected MapReduceForMultiMapCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return MapReduceForMultiMapCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return MapReduceForMultiMapCodec.encodeResponse((List)response);
    }

    @Override
    public String getDistributedObjectName() {
        return ((MapReduceForMultiMapCodec.RequestParameters)this.parameters).name;
    }
}

