/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.client.impl.protocol.codec;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.DynamicConfigMessageType;
import com.hazelcast.client.impl.protocol.codec.ListenerConfigCodec;
import com.hazelcast.client.impl.protocol.task.dynamicconfig.ListenerConfigHolder;
import com.hazelcast.client.impl.protocol.util.ParameterUtil;
import com.hazelcast.nio.serialization.Data;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@SuppressFBWarnings(value={"URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD"})
public final class DynamicConfigAddReliableTopicConfigCodec {
    public static final DynamicConfigMessageType REQUEST_TYPE = DynamicConfigMessageType.DYNAMICCONFIG_ADDRELIABLETOPICCONFIG;
    public static final int RESPONSE_TYPE = 100;

    public static ClientMessage encodeRequest(String name, Collection<ListenerConfigHolder> listenerConfigs, int readBatchSize, boolean statisticsEnabled, String topicOverloadPolicy, Data executor) {
        int requiredDataSize = RequestParameters.calculateDataSize(name, listenerConfigs, readBatchSize, statisticsEnabled, topicOverloadPolicy, executor);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(REQUEST_TYPE.id());
        clientMessage.setRetryable(false);
        clientMessage.setAcquiresResource(false);
        clientMessage.setOperationName("DynamicConfig.addReliableTopicConfig");
        clientMessage.set(name);
        if (listenerConfigs == null) {
            boolean listenerConfigs_isNull = true;
            clientMessage.set(listenerConfigs_isNull);
        } else {
            boolean listenerConfigs_isNull = false;
            clientMessage.set(listenerConfigs_isNull);
            clientMessage.set(listenerConfigs.size());
            for (ListenerConfigHolder listenerConfigs_item : listenerConfigs) {
                ListenerConfigCodec.encode(listenerConfigs_item, clientMessage);
            }
        }
        clientMessage.set(readBatchSize);
        clientMessage.set(statisticsEnabled);
        clientMessage.set(topicOverloadPolicy);
        if (executor == null) {
            boolean executor_isNull = true;
            clientMessage.set(executor_isNull);
        } else {
            boolean executor_isNull = false;
            clientMessage.set(executor_isNull);
            clientMessage.set(executor);
        }
        clientMessage.updateFrameLength();
        return clientMessage;
    }

    public static RequestParameters decodeRequest(ClientMessage clientMessage) {
        RequestParameters parameters = new RequestParameters();
        if (clientMessage.isComplete()) {
            return parameters;
        }
        String name = null;
        parameters.name = name = clientMessage.getStringUtf8();
        ArrayList<ListenerConfigHolder> listenerConfigs = null;
        boolean listenerConfigs_isNull = clientMessage.getBoolean();
        if (!listenerConfigs_isNull) {
            int listenerConfigs_size = clientMessage.getInt();
            listenerConfigs = new ArrayList<ListenerConfigHolder>(listenerConfigs_size);
            for (int listenerConfigs_index = 0; listenerConfigs_index < listenerConfigs_size; ++listenerConfigs_index) {
                ListenerConfigHolder listenerConfigs_item = ListenerConfigCodec.decode(clientMessage);
                listenerConfigs.add(listenerConfigs_item);
            }
            parameters.listenerConfigs = listenerConfigs;
        }
        int readBatchSize = 0;
        parameters.readBatchSize = readBatchSize = clientMessage.getInt();
        boolean statisticsEnabled = false;
        parameters.statisticsEnabled = statisticsEnabled = clientMessage.getBoolean();
        String topicOverloadPolicy = null;
        parameters.topicOverloadPolicy = topicOverloadPolicy = clientMessage.getStringUtf8();
        Data executor = null;
        boolean executor_isNull = clientMessage.getBoolean();
        if (!executor_isNull) {
            parameters.executor = executor = clientMessage.getData();
        }
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
        public static final DynamicConfigMessageType TYPE = REQUEST_TYPE;
        public String name;
        public List<ListenerConfigHolder> listenerConfigs;
        public int readBatchSize;
        public boolean statisticsEnabled;
        public String topicOverloadPolicy;
        public Data executor;

        public static int calculateDataSize(String name, Collection<ListenerConfigHolder> listenerConfigs, int readBatchSize, boolean statisticsEnabled, String topicOverloadPolicy, Data executor) {
            int dataSize = ClientMessage.HEADER_SIZE;
            dataSize += ParameterUtil.calculateDataSize(name);
            ++dataSize;
            if (listenerConfigs != null) {
                dataSize += 4;
                for (ListenerConfigHolder listenerConfigs_item : listenerConfigs) {
                    dataSize += ListenerConfigCodec.calculateDataSize(listenerConfigs_item);
                }
            }
            dataSize += 4;
            ++dataSize;
            dataSize += ParameterUtil.calculateDataSize(topicOverloadPolicy);
            ++dataSize;
            if (executor != null) {
                dataSize += ParameterUtil.calculateDataSize(executor);
            }
            return dataSize;
        }
    }
}

