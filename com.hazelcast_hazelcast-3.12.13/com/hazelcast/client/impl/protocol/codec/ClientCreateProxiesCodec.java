/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.client.impl.protocol.codec;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.ClientMessageType;
import com.hazelcast.client.impl.protocol.util.ParameterUtil;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@SuppressFBWarnings(value={"URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD"})
public final class ClientCreateProxiesCodec {
    public static final ClientMessageType REQUEST_TYPE = ClientMessageType.CLIENT_CREATEPROXIES;
    public static final int RESPONSE_TYPE = 100;

    public static ClientMessage encodeRequest(Collection<Map.Entry<String, String>> proxies) {
        int requiredDataSize = RequestParameters.calculateDataSize(proxies);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(REQUEST_TYPE.id());
        clientMessage.setRetryable(false);
        clientMessage.setAcquiresResource(false);
        clientMessage.setOperationName("Client.createProxies");
        clientMessage.set(proxies.size());
        for (Map.Entry<String, String> proxies_item : proxies) {
            String proxies_itemKey = proxies_item.getKey();
            String proxies_itemVal = proxies_item.getValue();
            clientMessage.set(proxies_itemKey);
            clientMessage.set(proxies_itemVal);
        }
        clientMessage.updateFrameLength();
        return clientMessage;
    }

    public static RequestParameters decodeRequest(ClientMessage clientMessage) {
        RequestParameters parameters = new RequestParameters();
        if (clientMessage.isComplete()) {
            return parameters;
        }
        ArrayList<Map.Entry<String, String>> proxies = null;
        int proxies_size = clientMessage.getInt();
        proxies = new ArrayList<Map.Entry<String, String>>(proxies_size);
        for (int proxies_index = 0; proxies_index < proxies_size; ++proxies_index) {
            String proxies_item_key = clientMessage.getStringUtf8();
            String proxies_item_val = clientMessage.getStringUtf8();
            AbstractMap.SimpleEntry<String, String> proxies_item = new AbstractMap.SimpleEntry<String, String>(proxies_item_key, proxies_item_val);
            proxies.add(proxies_item);
        }
        parameters.proxies = proxies;
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
        public static final ClientMessageType TYPE = REQUEST_TYPE;
        public List<Map.Entry<String, String>> proxies;

        public static int calculateDataSize(Collection<Map.Entry<String, String>> proxies) {
            int dataSize = ClientMessage.HEADER_SIZE;
            dataSize += 4;
            for (Map.Entry<String, String> proxies_item : proxies) {
                String proxies_itemKey = proxies_item.getKey();
                String proxies_itemVal = proxies_item.getValue();
                dataSize += ParameterUtil.calculateDataSize(proxies_itemKey);
                dataSize += ParameterUtil.calculateDataSize(proxies_itemVal);
            }
            return dataSize;
        }
    }
}

