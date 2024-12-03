/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.mapreduce;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.MapReduceForSetCodec;
import com.hazelcast.client.impl.protocol.task.mapreduce.AbstractMapReduceTask;
import com.hazelcast.instance.Node;
import com.hazelcast.mapreduce.CombinerFactory;
import com.hazelcast.mapreduce.KeyPredicate;
import com.hazelcast.mapreduce.KeyValueSource;
import com.hazelcast.mapreduce.Mapper;
import com.hazelcast.mapreduce.ReducerFactory;
import com.hazelcast.mapreduce.impl.SetKeyValueSource;
import com.hazelcast.nio.Connection;
import java.util.Collection;
import java.util.List;

public class MapReduceForSetMessageTask
extends AbstractMapReduceTask<MapReduceForSetCodec.RequestParameters> {
    public MapReduceForSetMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected String getJobId() {
        return ((MapReduceForSetCodec.RequestParameters)this.parameters).jobId;
    }

    @Override
    protected int getChunkSize() {
        return ((MapReduceForSetCodec.RequestParameters)this.parameters).chunkSize;
    }

    @Override
    protected String getTopologyChangedStrategy() {
        return ((MapReduceForSetCodec.RequestParameters)this.parameters).topologyChangedStrategy;
    }

    @Override
    protected KeyValueSource getKeyValueSource() {
        return new SetKeyValueSource(((MapReduceForSetCodec.RequestParameters)this.parameters).setName);
    }

    @Override
    protected Mapper getMapper() {
        return (Mapper)this.serializationService.toObject(((MapReduceForSetCodec.RequestParameters)this.parameters).mapper);
    }

    @Override
    protected CombinerFactory getCombinerFactory() {
        return (CombinerFactory)this.serializationService.toObject(((MapReduceForSetCodec.RequestParameters)this.parameters).combinerFactory);
    }

    @Override
    protected ReducerFactory getReducerFactory() {
        return (ReducerFactory)this.serializationService.toObject(((MapReduceForSetCodec.RequestParameters)this.parameters).reducerFactory);
    }

    @Override
    protected Collection getKeys() {
        return ((MapReduceForSetCodec.RequestParameters)this.parameters).keys;
    }

    @Override
    protected KeyPredicate getPredicate() {
        return (KeyPredicate)this.serializationService.toObject(((MapReduceForSetCodec.RequestParameters)this.parameters).predicate);
    }

    @Override
    protected MapReduceForSetCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return MapReduceForSetCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return MapReduceForSetCodec.encodeResponse((List)response);
    }

    @Override
    public String getDistributedObjectName() {
        return ((MapReduceForSetCodec.RequestParameters)this.parameters).name;
    }
}

