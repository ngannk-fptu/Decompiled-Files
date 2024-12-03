/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.mapreduce;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.MapReduceForMapCodec;
import com.hazelcast.client.impl.protocol.task.mapreduce.AbstractMapReduceTask;
import com.hazelcast.instance.Node;
import com.hazelcast.mapreduce.CombinerFactory;
import com.hazelcast.mapreduce.KeyPredicate;
import com.hazelcast.mapreduce.KeyValueSource;
import com.hazelcast.mapreduce.Mapper;
import com.hazelcast.mapreduce.ReducerFactory;
import com.hazelcast.mapreduce.impl.MapKeyValueSource;
import com.hazelcast.nio.Connection;
import java.util.Collection;
import java.util.List;

public class MapReduceForMapMessageTask
extends AbstractMapReduceTask<MapReduceForMapCodec.RequestParameters> {
    public MapReduceForMapMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected String getJobId() {
        return ((MapReduceForMapCodec.RequestParameters)this.parameters).jobId;
    }

    @Override
    protected int getChunkSize() {
        return ((MapReduceForMapCodec.RequestParameters)this.parameters).chunkSize;
    }

    @Override
    protected String getTopologyChangedStrategy() {
        return ((MapReduceForMapCodec.RequestParameters)this.parameters).topologyChangedStrategy;
    }

    @Override
    protected KeyValueSource getKeyValueSource() {
        return new MapKeyValueSource(((MapReduceForMapCodec.RequestParameters)this.parameters).mapName);
    }

    @Override
    protected Mapper getMapper() {
        return (Mapper)this.serializationService.toObject(((MapReduceForMapCodec.RequestParameters)this.parameters).mapper);
    }

    @Override
    protected CombinerFactory getCombinerFactory() {
        return (CombinerFactory)this.serializationService.toObject(((MapReduceForMapCodec.RequestParameters)this.parameters).combinerFactory);
    }

    @Override
    protected ReducerFactory getReducerFactory() {
        return (ReducerFactory)this.serializationService.toObject(((MapReduceForMapCodec.RequestParameters)this.parameters).reducerFactory);
    }

    @Override
    protected Collection getKeys() {
        return ((MapReduceForMapCodec.RequestParameters)this.parameters).keys;
    }

    @Override
    protected KeyPredicate getPredicate() {
        return (KeyPredicate)this.serializationService.toObject(((MapReduceForMapCodec.RequestParameters)this.parameters).predicate);
    }

    @Override
    protected MapReduceForMapCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return MapReduceForMapCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return MapReduceForMapCodec.encodeResponse((List)response);
    }

    @Override
    public String getDistributedObjectName() {
        return ((MapReduceForMapCodec.RequestParameters)this.parameters).name;
    }
}

