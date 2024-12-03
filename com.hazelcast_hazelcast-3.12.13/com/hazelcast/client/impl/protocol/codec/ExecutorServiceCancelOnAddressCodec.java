/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.client.impl.protocol.codec;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.AddressCodec;
import com.hazelcast.client.impl.protocol.codec.ExecutorServiceMessageType;
import com.hazelcast.client.impl.protocol.util.ParameterUtil;
import com.hazelcast.nio.Address;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressFBWarnings(value={"URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD"})
public final class ExecutorServiceCancelOnAddressCodec {
    public static final ExecutorServiceMessageType REQUEST_TYPE = ExecutorServiceMessageType.EXECUTORSERVICE_CANCELONADDRESS;
    public static final int RESPONSE_TYPE = 101;

    public static ClientMessage encodeRequest(String uuid, Address address, boolean interrupt) {
        int requiredDataSize = RequestParameters.calculateDataSize(uuid, address, interrupt);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(REQUEST_TYPE.id());
        clientMessage.setRetryable(false);
        clientMessage.setAcquiresResource(false);
        clientMessage.setOperationName("ExecutorService.cancelOnAddress");
        clientMessage.set(uuid);
        AddressCodec.encode(address, clientMessage);
        clientMessage.set(interrupt);
        clientMessage.updateFrameLength();
        return clientMessage;
    }

    public static RequestParameters decodeRequest(ClientMessage clientMessage) {
        RequestParameters parameters = new RequestParameters();
        String uuid = null;
        parameters.uuid = uuid = clientMessage.getStringUtf8();
        Address address = null;
        parameters.address = address = AddressCodec.decode(clientMessage);
        boolean interrupt = false;
        parameters.interrupt = interrupt = clientMessage.getBoolean();
        return parameters;
    }

    public static ClientMessage encodeResponse(boolean response) {
        int requiredDataSize = ResponseParameters.calculateDataSize(response);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(101);
        clientMessage.set(response);
        clientMessage.updateFrameLength();
        return clientMessage;
    }

    public static ResponseParameters decodeResponse(ClientMessage clientMessage) {
        ResponseParameters parameters = new ResponseParameters();
        boolean response = false;
        parameters.response = response = clientMessage.getBoolean();
        return parameters;
    }

    public static class ResponseParameters {
        public boolean response;

        public static int calculateDataSize(boolean response) {
            int dataSize = ClientMessage.HEADER_SIZE;
            return ++dataSize;
        }
    }

    public static class RequestParameters {
        public static final ExecutorServiceMessageType TYPE = REQUEST_TYPE;
        public String uuid;
        public Address address;
        public boolean interrupt;

        public static int calculateDataSize(String uuid, Address address, boolean interrupt) {
            int dataSize = ClientMessage.HEADER_SIZE;
            dataSize += ParameterUtil.calculateDataSize(uuid);
            dataSize += AddressCodec.calculateDataSize(address);
            return ++dataSize;
        }
    }
}

