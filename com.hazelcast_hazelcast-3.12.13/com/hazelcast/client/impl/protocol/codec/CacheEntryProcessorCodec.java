/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.client.impl.protocol.codec;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.CacheMessageType;
import com.hazelcast.client.impl.protocol.util.ParameterUtil;
import com.hazelcast.nio.serialization.Data;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@SuppressFBWarnings(value={"URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD"})
public final class CacheEntryProcessorCodec {
    public static final CacheMessageType REQUEST_TYPE = CacheMessageType.CACHE_ENTRYPROCESSOR;
    public static final int RESPONSE_TYPE = 105;

    public static ClientMessage encodeRequest(String name, Data key, Data entryProcessor, Collection<Data> arguments, int completionId) {
        int requiredDataSize = RequestParameters.calculateDataSize(name, key, entryProcessor, arguments, completionId);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(REQUEST_TYPE.id());
        clientMessage.setRetryable(false);
        clientMessage.setAcquiresResource(false);
        clientMessage.setOperationName("Cache.entryProcessor");
        clientMessage.set(name);
        clientMessage.set(key);
        clientMessage.set(entryProcessor);
        clientMessage.set(arguments.size());
        for (Data arguments_item : arguments) {
            clientMessage.set(arguments_item);
        }
        clientMessage.set(completionId);
        clientMessage.updateFrameLength();
        return clientMessage;
    }

    public static RequestParameters decodeRequest(ClientMessage clientMessage) {
        RequestParameters parameters = new RequestParameters();
        String name = null;
        parameters.name = name = clientMessage.getStringUtf8();
        Data key = null;
        parameters.key = key = clientMessage.getData();
        Data entryProcessor = null;
        parameters.entryProcessor = entryProcessor = clientMessage.getData();
        ArrayList<Data> arguments = null;
        int arguments_size = clientMessage.getInt();
        arguments = new ArrayList<Data>(arguments_size);
        for (int arguments_index = 0; arguments_index < arguments_size; ++arguments_index) {
            Data arguments_item = clientMessage.getData();
            arguments.add(arguments_item);
        }
        parameters.arguments = arguments;
        int completionId = 0;
        parameters.completionId = completionId = clientMessage.getInt();
        return parameters;
    }

    public static ClientMessage encodeResponse(Data response) {
        int requiredDataSize = ResponseParameters.calculateDataSize(response);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(105);
        if (response == null) {
            boolean response_isNull = true;
            clientMessage.set(response_isNull);
        } else {
            boolean response_isNull = false;
            clientMessage.set(response_isNull);
            clientMessage.set(response);
        }
        clientMessage.updateFrameLength();
        return clientMessage;
    }

    public static ResponseParameters decodeResponse(ClientMessage clientMessage) {
        ResponseParameters parameters = new ResponseParameters();
        Data response = null;
        boolean response_isNull = clientMessage.getBoolean();
        if (!response_isNull) {
            parameters.response = response = clientMessage.getData();
        }
        return parameters;
    }

    public static class ResponseParameters {
        public Data response;

        public static int calculateDataSize(Data response) {
            int dataSize = ClientMessage.HEADER_SIZE;
            ++dataSize;
            if (response != null) {
                dataSize += ParameterUtil.calculateDataSize(response);
            }
            return dataSize;
        }
    }

    public static class RequestParameters {
        public static final CacheMessageType TYPE = REQUEST_TYPE;
        public String name;
        public Data key;
        public Data entryProcessor;
        public List<Data> arguments;
        public int completionId;

        public static int calculateDataSize(String name, Data key, Data entryProcessor, Collection<Data> arguments, int completionId) {
            int dataSize = ClientMessage.HEADER_SIZE;
            dataSize += ParameterUtil.calculateDataSize(name);
            dataSize += ParameterUtil.calculateDataSize(key);
            dataSize += ParameterUtil.calculateDataSize(entryProcessor);
            dataSize += 4;
            for (Data arguments_item : arguments) {
                dataSize += ParameterUtil.calculateDataSize(arguments_item);
            }
            return dataSize += 4;
        }
    }
}

