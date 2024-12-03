/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.client.impl.protocol.codec;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.AddressCodec;
import com.hazelcast.client.impl.protocol.codec.CacheMessageType;
import com.hazelcast.client.impl.protocol.util.ParameterUtil;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.serialization.Data;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressFBWarnings(value={"URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD"})
public final class CacheListenerRegistrationCodec {
    public static final CacheMessageType REQUEST_TYPE = CacheMessageType.CACHE_LISTENERREGISTRATION;
    public static final int RESPONSE_TYPE = 100;

    public static ClientMessage encodeRequest(String name, Data listenerConfig, boolean shouldRegister, Address address) {
        int requiredDataSize = RequestParameters.calculateDataSize(name, listenerConfig, shouldRegister, address);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(REQUEST_TYPE.id());
        clientMessage.setRetryable(false);
        clientMessage.setAcquiresResource(false);
        clientMessage.setOperationName("Cache.listenerRegistration");
        clientMessage.set(name);
        clientMessage.set(listenerConfig);
        clientMessage.set(shouldRegister);
        AddressCodec.encode(address, clientMessage);
        clientMessage.updateFrameLength();
        return clientMessage;
    }

    public static RequestParameters decodeRequest(ClientMessage clientMessage) {
        RequestParameters parameters = new RequestParameters();
        String name = null;
        parameters.name = name = clientMessage.getStringUtf8();
        Data listenerConfig = null;
        parameters.listenerConfig = listenerConfig = clientMessage.getData();
        boolean shouldRegister = false;
        parameters.shouldRegister = shouldRegister = clientMessage.getBoolean();
        Address address = null;
        parameters.address = address = AddressCodec.decode(clientMessage);
        return parameters;
    }

    public static ClientMessage encodeResponse() {
        int requiredDataSize = ResponseParameters.calculateDataSize();
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(100);
        clientMessage.updateFrameLength();
        return clientMessage;
    }

    public static ResponseParameters decodeResponse(ClientMessage clientMessage) {
        ResponseParameters parameters = new ResponseParameters();
        return parameters;
    }

    public static class ResponseParameters {
        public static int calculateDataSize() {
            int dataSize = ClientMessage.HEADER_SIZE;
            return dataSize;
        }
    }

    public static class RequestParameters {
        public static final CacheMessageType TYPE = REQUEST_TYPE;
        public String name;
        public Data listenerConfig;
        public boolean shouldRegister;
        public Address address;

        public static int calculateDataSize(String name, Data listenerConfig, boolean shouldRegister, Address address) {
            int dataSize = ClientMessage.HEADER_SIZE;
            dataSize += ParameterUtil.calculateDataSize(name);
            dataSize += ParameterUtil.calculateDataSize(listenerConfig);
            ++dataSize;
            return dataSize += AddressCodec.calculateDataSize(address);
        }
    }
}

