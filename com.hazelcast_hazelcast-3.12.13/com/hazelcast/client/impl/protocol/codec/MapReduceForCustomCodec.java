/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.client.impl.protocol.codec;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.MapReduceMessageType;
import com.hazelcast.client.impl.protocol.util.ParameterUtil;
import com.hazelcast.nio.serialization.Data;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@SuppressFBWarnings(value={"URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD"})
public final class MapReduceForCustomCodec {
    public static final MapReduceMessageType REQUEST_TYPE = MapReduceMessageType.MAPREDUCE_FORCUSTOM;
    public static final int RESPONSE_TYPE = 117;

    public static ClientMessage encodeRequest(String name, String jobId, Data predicate, Data mapper, Data combinerFactory, Data reducerFactory, Data keyValueSource, int chunkSize, Collection<Data> keys, String topologyChangedStrategy) {
        boolean reducerFactory_isNull;
        boolean combinerFactory_isNull;
        boolean predicate_isNull;
        int requiredDataSize = RequestParameters.calculateDataSize(name, jobId, predicate, mapper, combinerFactory, reducerFactory, keyValueSource, chunkSize, keys, topologyChangedStrategy);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(REQUEST_TYPE.id());
        clientMessage.setRetryable(false);
        clientMessage.setAcquiresResource(false);
        clientMessage.setOperationName("MapReduce.forCustom");
        clientMessage.set(name);
        clientMessage.set(jobId);
        if (predicate == null) {
            predicate_isNull = true;
            clientMessage.set(predicate_isNull);
        } else {
            predicate_isNull = false;
            clientMessage.set(predicate_isNull);
            clientMessage.set(predicate);
        }
        clientMessage.set(mapper);
        if (combinerFactory == null) {
            combinerFactory_isNull = true;
            clientMessage.set(combinerFactory_isNull);
        } else {
            combinerFactory_isNull = false;
            clientMessage.set(combinerFactory_isNull);
            clientMessage.set(combinerFactory);
        }
        if (reducerFactory == null) {
            reducerFactory_isNull = true;
            clientMessage.set(reducerFactory_isNull);
        } else {
            reducerFactory_isNull = false;
            clientMessage.set(reducerFactory_isNull);
            clientMessage.set(reducerFactory);
        }
        clientMessage.set(keyValueSource);
        clientMessage.set(chunkSize);
        if (keys == null) {
            boolean keys_isNull = true;
            clientMessage.set(keys_isNull);
        } else {
            boolean keys_isNull = false;
            clientMessage.set(keys_isNull);
            clientMessage.set(keys.size());
            for (Data keys_item : keys) {
                clientMessage.set(keys_item);
            }
        }
        if (topologyChangedStrategy == null) {
            boolean topologyChangedStrategy_isNull = true;
            clientMessage.set(topologyChangedStrategy_isNull);
        } else {
            boolean topologyChangedStrategy_isNull = false;
            clientMessage.set(topologyChangedStrategy_isNull);
            clientMessage.set(topologyChangedStrategy);
        }
        clientMessage.updateFrameLength();
        return clientMessage;
    }

    public static RequestParameters decodeRequest(ClientMessage clientMessage) {
        RequestParameters parameters = new RequestParameters();
        String name = null;
        parameters.name = name = clientMessage.getStringUtf8();
        String jobId = null;
        parameters.jobId = jobId = clientMessage.getStringUtf8();
        Data predicate = null;
        boolean predicate_isNull = clientMessage.getBoolean();
        if (!predicate_isNull) {
            parameters.predicate = predicate = clientMessage.getData();
        }
        Data mapper = null;
        parameters.mapper = mapper = clientMessage.getData();
        Data combinerFactory = null;
        boolean combinerFactory_isNull = clientMessage.getBoolean();
        if (!combinerFactory_isNull) {
            parameters.combinerFactory = combinerFactory = clientMessage.getData();
        }
        Data reducerFactory = null;
        boolean reducerFactory_isNull = clientMessage.getBoolean();
        if (!reducerFactory_isNull) {
            parameters.reducerFactory = reducerFactory = clientMessage.getData();
        }
        Data keyValueSource = null;
        parameters.keyValueSource = keyValueSource = clientMessage.getData();
        int chunkSize = 0;
        parameters.chunkSize = chunkSize = clientMessage.getInt();
        ArrayList<Data> keys = null;
        boolean keys_isNull = clientMessage.getBoolean();
        if (!keys_isNull) {
            int keys_size = clientMessage.getInt();
            keys = new ArrayList<Data>(keys_size);
            for (int keys_index = 0; keys_index < keys_size; ++keys_index) {
                Data keys_item = clientMessage.getData();
                keys.add(keys_item);
            }
            parameters.keys = keys;
        }
        String topologyChangedStrategy = null;
        boolean topologyChangedStrategy_isNull = clientMessage.getBoolean();
        if (!topologyChangedStrategy_isNull) {
            parameters.topologyChangedStrategy = topologyChangedStrategy = clientMessage.getStringUtf8();
        }
        return parameters;
    }

    public static ClientMessage encodeResponse(Collection<Map.Entry<Data, Data>> response) {
        int requiredDataSize = ResponseParameters.calculateDataSize(response);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(117);
        clientMessage.set(response.size());
        for (Map.Entry<Data, Data> response_item : response) {
            Data response_itemKey = response_item.getKey();
            Data response_itemVal = response_item.getValue();
            clientMessage.set(response_itemKey);
            clientMessage.set(response_itemVal);
        }
        clientMessage.updateFrameLength();
        return clientMessage;
    }

    public static ResponseParameters decodeResponse(ClientMessage clientMessage) {
        ResponseParameters parameters = new ResponseParameters();
        ArrayList<Map.Entry<Data, Data>> response = null;
        int response_size = clientMessage.getInt();
        response = new ArrayList<Map.Entry<Data, Data>>(response_size);
        for (int response_index = 0; response_index < response_size; ++response_index) {
            Data response_item_key = clientMessage.getData();
            Data response_item_val = clientMessage.getData();
            AbstractMap.SimpleEntry<Data, Data> response_item = new AbstractMap.SimpleEntry<Data, Data>(response_item_key, response_item_val);
            response.add(response_item);
        }
        parameters.response = response;
        return parameters;
    }

    public static class ResponseParameters {
        public List<Map.Entry<Data, Data>> response;

        public static int calculateDataSize(Collection<Map.Entry<Data, Data>> response) {
            int dataSize = ClientMessage.HEADER_SIZE;
            dataSize += 4;
            for (Map.Entry<Data, Data> response_item : response) {
                Data response_itemKey = response_item.getKey();
                Data response_itemVal = response_item.getValue();
                dataSize += ParameterUtil.calculateDataSize(response_itemKey);
                dataSize += ParameterUtil.calculateDataSize(response_itemVal);
            }
            return dataSize;
        }
    }

    public static class RequestParameters {
        public static final MapReduceMessageType TYPE = REQUEST_TYPE;
        public String name;
        public String jobId;
        public Data predicate;
        public Data mapper;
        public Data combinerFactory;
        public Data reducerFactory;
        public Data keyValueSource;
        public int chunkSize;
        public List<Data> keys;
        public String topologyChangedStrategy;

        public static int calculateDataSize(String name, String jobId, Data predicate, Data mapper, Data combinerFactory, Data reducerFactory, Data keyValueSource, int chunkSize, Collection<Data> keys, String topologyChangedStrategy) {
            int dataSize = ClientMessage.HEADER_SIZE;
            dataSize += ParameterUtil.calculateDataSize(name);
            dataSize += ParameterUtil.calculateDataSize(jobId);
            ++dataSize;
            if (predicate != null) {
                dataSize += ParameterUtil.calculateDataSize(predicate);
            }
            dataSize += ParameterUtil.calculateDataSize(mapper);
            ++dataSize;
            if (combinerFactory != null) {
                dataSize += ParameterUtil.calculateDataSize(combinerFactory);
            }
            ++dataSize;
            if (reducerFactory != null) {
                dataSize += ParameterUtil.calculateDataSize(reducerFactory);
            }
            dataSize += ParameterUtil.calculateDataSize(keyValueSource);
            dataSize += 4;
            ++dataSize;
            if (keys != null) {
                dataSize += 4;
                for (Data keys_item : keys) {
                    dataSize += ParameterUtil.calculateDataSize(keys_item);
                }
            }
            ++dataSize;
            if (topologyChangedStrategy != null) {
                dataSize += ParameterUtil.calculateDataSize(topologyChangedStrategy);
            }
            return dataSize;
        }
    }
}

