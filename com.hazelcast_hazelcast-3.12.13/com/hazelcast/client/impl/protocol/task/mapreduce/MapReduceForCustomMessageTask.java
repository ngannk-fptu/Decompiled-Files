/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.mapreduce;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.MapReduceForCustomCodec;
import com.hazelcast.client.impl.protocol.task.mapreduce.AbstractMapReduceTask;
import com.hazelcast.instance.Node;
import com.hazelcast.mapreduce.CombinerFactory;
import com.hazelcast.mapreduce.KeyPredicate;
import com.hazelcast.mapreduce.KeyValueSource;
import com.hazelcast.mapreduce.Mapper;
import com.hazelcast.mapreduce.ReducerFactory;
import com.hazelcast.nio.Connection;
import java.util.Collection;
import java.util.List;

public class MapReduceForCustomMessageTask
extends AbstractMapReduceTask<MapReduceForCustomCodec.RequestParameters> {
    public MapReduceForCustomMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected String getJobId() {
        return ((MapReduceForCustomCodec.RequestParameters)this.parameters).jobId;
    }

    @Override
    protected int getChunkSize() {
        return ((MapReduceForCustomCodec.RequestParameters)this.parameters).chunkSize;
    }

    @Override
    protected String getTopologyChangedStrategy() {
        return ((MapReduceForCustomCodec.RequestParameters)this.parameters).topologyChangedStrategy;
    }

    @Override
    protected KeyValueSource getKeyValueSource() {
        return (KeyValueSource)this.serializationService.toObject(((MapReduceForCustomCodec.RequestParameters)this.parameters).keyValueSource);
    }

    @Override
    protected Mapper getMapper() {
        return (Mapper)this.serializationService.toObject(((MapReduceForCustomCodec.RequestParameters)this.parameters).mapper);
    }

    @Override
    protected CombinerFactory getCombinerFactory() {
        return (CombinerFactory)this.serializationService.toObject(((MapReduceForCustomCodec.RequestParameters)this.parameters).combinerFactory);
    }

    @Override
    protected ReducerFactory getReducerFactory() {
        return (ReducerFactory)this.serializationService.toObject(((MapReduceForCustomCodec.RequestParameters)this.parameters).reducerFactory);
    }

    @Override
    protected Collection getKeys() {
        return ((MapReduceForCustomCodec.RequestParameters)this.parameters).keys;
    }

    @Override
    protected KeyPredicate getPredicate() {
        return (KeyPredicate)this.serializationService.toObject(((MapReduceForCustomCodec.RequestParameters)this.parameters).predicate);
    }

    @Override
    protected MapReduceForCustomCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return MapReduceForCustomCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return MapReduceForCustomCodec.encodeResponse((List)response);
    }

    @Override
    public String getDistributedObjectName() {
        return ((MapReduceForCustomCodec.RequestParameters)this.parameters).name;
    }
}

