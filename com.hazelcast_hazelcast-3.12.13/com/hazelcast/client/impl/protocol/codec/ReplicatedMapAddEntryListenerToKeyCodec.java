/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.client.impl.protocol.codec;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.ReplicatedMapMessageType;
import com.hazelcast.client.impl.protocol.util.ParameterUtil;
import com.hazelcast.logging.Logger;
import com.hazelcast.nio.serialization.Data;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressFBWarnings(value={"URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD"})
public final class ReplicatedMapAddEntryListenerToKeyCodec {
    public static final ReplicatedMapMessageType REQUEST_TYPE = ReplicatedMapMessageType.REPLICATEDMAP_ADDENTRYLISTENERTOKEY;
    public static final int RESPONSE_TYPE = 104;

    public static ClientMessage encodeRequest(String name, Data key, boolean localOnly) {
        int requiredDataSize = RequestParameters.calculateDataSize(name, key, localOnly);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(REQUEST_TYPE.id());
        clientMessage.setRetryable(false);
        clientMessage.setAcquiresResource(false);
        clientMessage.setOperationName("ReplicatedMap.addEntryListenerToKey");
        clientMessage.set(name);
        clientMessage.set(key);
        clientMessage.set(localOnly);
        clientMessage.updateFrameLength();
        return clientMessage;
    }

    public static RequestParameters decodeRequest(ClientMessage clientMessage) {
        RequestParameters parameters = new RequestParameters();
        String name = null;
        parameters.name = name = clientMessage.getStringUtf8();
        Data key = null;
        parameters.key = key = clientMessage.getData();
        boolean localOnly = false;
        parameters.localOnly = localOnly = clientMessage.getBoolean();
        return parameters;
    }

    public static ClientMessage encodeResponse(String response) {
        int requiredDataSize = ResponseParameters.calculateDataSize(response);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(104);
        clientMessage.set(response);
        clientMessage.updateFrameLength();
        return clientMessage;
    }

    public static ResponseParameters decodeResponse(ClientMessage clientMessage) {
        ResponseParameters parameters = new ResponseParameters();
        String response = null;
        parameters.response = response = clientMessage.getStringUtf8();
        return parameters;
    }

    public static ClientMessage encodeEntryEvent(Data key, Data value, Data oldValue, Data mergingValue, int eventType, String uuid, int numberOfAffectedEntries) {
        int dataSize = ClientMessage.HEADER_SIZE;
        ++dataSize;
        if (key != null) {
            dataSize += ParameterUtil.calculateDataSize(key);
        }
        ++dataSize;
        if (value != null) {
            dataSize += ParameterUtil.calculateDataSize(value);
        }
        ++dataSize;
        if (oldValue != null) {
            dataSize += ParameterUtil.calculateDataSize(oldValue);
        }
        ++dataSize;
        if (mergingValue != null) {
            dataSize += ParameterUtil.calculateDataSize(mergingValue);
        }
        dataSize += 4;
        dataSize += ParameterUtil.calculateDataSize(uuid);
        ClientMessage clientMessage = ClientMessage.createForEncode(dataSize += 4);
        clientMessage.setMessageType(203);
        clientMessage.addFlag((short)1);
        if (key == null) {
            boolean key_isNull = true;
            clientMessage.set(key_isNull);
        } else {
            boolean key_isNull = false;
            clientMessage.set(key_isNull);
            clientMessage.set(key);
        }
        if (value == null) {
            boolean value_isNull = true;
            clientMessage.set(value_isNull);
        } else {
            boolean value_isNull = false;
            clientMessage.set(value_isNull);
            clientMessage.set(value);
        }
        if (oldValue == null) {
            boolean oldValue_isNull = true;
            clientMessage.set(oldValue_isNull);
        } else {
            boolean oldValue_isNull = false;
            clientMessage.set(oldValue_isNull);
            clientMessage.set(oldValue);
        }
        if (mergingValue == null) {
            boolean mergingValue_isNull = true;
            clientMessage.set(mergingValue_isNull);
        } else {
            boolean mergingValue_isNull = false;
            clientMessage.set(mergingValue_isNull);
            clientMessage.set(mergingValue);
        }
        clientMessage.set(eventType);
        clientMessage.set(uuid);
        clientMessage.set(numberOfAffectedEntries);
        clientMessage.updateFrameLength();
        return clientMessage;
    }

    public static abstract class AbstractEventHandler {
        public void handle(ClientMessage clientMessage) {
            int messageType = clientMessage.getMessageType();
            if (messageType == 203) {
                Data key = null;
                boolean key_isNull = clientMessage.getBoolean();
                if (!key_isNull) {
                    key = clientMessage.getData();
                }
                Data value = null;
                boolean value_isNull = clientMessage.getBoolean();
                if (!value_isNull) {
                    value = clientMessage.getData();
                }
                Data oldValue = null;
                boolean oldValue_isNull = clientMessage.getBoolean();
                if (!oldValue_isNull) {
                    oldValue = clientMessage.getData();
                }
                Data mergingValue = null;
                boolean mergingValue_isNull = clientMessage.getBoolean();
                if (!mergingValue_isNull) {
                    mergingValue = clientMessage.getData();
                }
                int eventType = 0;
                eventType = clientMessage.getInt();
                String uuid = null;
                uuid = clientMessage.getStringUtf8();
                int numberOfAffectedEntries = 0;
                numberOfAffectedEntries = clientMessage.getInt();
                this.handleEntryEventV10(key, value, oldValue, mergingValue, eventType, uuid, numberOfAffectedEntries);
                return;
            }
            Logger.getLogger(super.getClass()).warning("Unknown message type received on event handler :" + messageType);
        }

        public abstract void handleEntryEventV10(Data var1, Data var2, Data var3, Data var4, int var5, String var6, int var7);
    }

    public static class ResponseParameters {
        public String response;

        public static int calculateDataSize(String response) {
            int dataSize = ClientMessage.HEADER_SIZE;
            return dataSize += ParameterUtil.calculateDataSize(response);
        }
    }

    public static class RequestParameters {
        public static final ReplicatedMapMessageType TYPE = REQUEST_TYPE;
        public String name;
        public Data key;
        public boolean localOnly;

        public static int calculateDataSize(String name, Data key, boolean localOnly) {
            int dataSize = ClientMessage.HEADER_SIZE;
            dataSize += ParameterUtil.calculateDataSize(name);
            dataSize += ParameterUtil.calculateDataSize(key);
            return ++dataSize;
        }
    }
}

