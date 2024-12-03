/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.mapreduce;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.MapReduceForListCodec;
import com.hazelcast.client.impl.protocol.task.mapreduce.AbstractMapReduceTask;
import com.hazelcast.instance.Node;
import com.hazelcast.mapreduce.CombinerFactory;
import com.hazelcast.mapreduce.KeyPredicate;
import com.hazelcast.mapreduce.KeyValueSource;
import com.hazelcast.mapreduce.Mapper;
import com.hazelcast.mapreduce.ReducerFactory;
import com.hazelcast.mapreduce.impl.ListKeyValueSource;
import com.hazelcast.nio.Connection;
import java.util.Collection;
import java.util.List;

public class MapReduceForListMessageTask
extends AbstractMapReduceTask<MapReduceForListCodec.RequestParameters> {
    public MapReduceForListMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected String getJobId() {
        return ((MapReduceForListCodec.RequestParameters)this.parameters).jobId;
    }

    @Override
    protected int getChunkSize() {
        return ((MapReduceForListCodec.RequestParameters)this.parameters).chunkSize;
    }

    @Override
    protected String getTopologyChangedStrategy() {
        return ((MapReduceForListCodec.RequestParameters)this.parameters).topologyChangedStrategy;
    }

    @Override
    protected KeyValueSource getKeyValueSource() {
        return new ListKeyValueSource(((MapReduceForListCodec.RequestParameters)this.parameters).listName);
    }

    @Override
    protected Mapper getMapper() {
        return (Mapper)this.serializationService.toObject(((MapReduceForListCodec.RequestParameters)this.parameters).mapper);
    }

    @Override
    protected CombinerFactory getCombinerFactory() {
        return (CombinerFactory)this.serializationService.toObject(((MapReduceForListCodec.RequestParameters)this.parameters).combinerFactory);
    }

    @Override
    protected ReducerFactory getReducerFactory() {
        return (ReducerFactory)this.serializationService.toObject(((MapReduceForListCodec.RequestParameters)this.parameters).reducerFactory);
    }

    @Override
    protected Collection getKeys() {
        return ((MapReduceForListCodec.RequestParameters)this.parameters).keys;
    }

    @Override
    protected KeyPredicate getPredicate() {
        return (KeyPredicate)this.serializationService.toObject(((MapReduceForListCodec.RequestParameters)this.parameters).predicate);
    }

    @Override
    protected MapReduceForListCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return MapReduceForListCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return MapReduceForListCodec.encodeResponse((List)response);
    }

    @Override
    public String getDistributedObjectName() {
        return ((MapReduceForListCodec.RequestParameters)this.parameters).name;
    }
}

