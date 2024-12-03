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
import com.hazelcast.client.impl.protocol.codec.QueueStoreConfigCodec;
import com.hazelcast.client.impl.protocol.task.dynamicconfig.ListenerConfigHolder;
import com.hazelcast.client.impl.protocol.task.dynamicconfig.QueueStoreConfigHolder;
import com.hazelcast.client.impl.protocol.util.ParameterUtil;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@SuppressFBWarnings(value={"URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD"})
public final class DynamicConfigAddQueueConfigCodec {
    public static final DynamicConfigMessageType REQUEST_TYPE = DynamicConfigMessageType.DYNAMICCONFIG_ADDQUEUECONFIG;
    public static final int RESPONSE_TYPE = 100;

    public static ClientMessage encodeRequest(String name, Collection<ListenerConfigHolder> listenerConfigs, int backupCount, int asyncBackupCount, int maxSize, int emptyQueueTtl, boolean statisticsEnabled, String quorumName, QueueStoreConfigHolder queueStoreConfig) {
        int requiredDataSize = RequestParameters.calculateDataSize(name, listenerConfigs, backupCount, asyncBackupCount, maxSize, emptyQueueTtl, statisticsEnabled, quorumName, queueStoreConfig);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(REQUEST_TYPE.id());
        clientMessage.setRetryable(false);
        clientMessage.setAcquiresResource(false);
        clientMessage.setOperationName("DynamicConfig.addQueueConfig");
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
        clientMessage.set(backupCount);
        clientMessage.set(asyncBackupCount);
        clientMessage.set(maxSize);
        clientMessage.set(emptyQueueTtl);
        clientMessage.set(statisticsEnabled);
        if (quorumName == null) {
            boolean quorumName_isNull = true;
            clientMessage.set(quorumName_isNull);
        } else {
            boolean quorumName_isNull = false;
            clientMessage.set(quorumName_isNull);
            clientMessage.set(quorumName);
        }
        if (queueStoreConfig == null) {
            boolean queueStoreConfig_isNull = true;
            clientMessage.set(queueStoreConfig_isNull);
        } else {
            boolean queueStoreConfig_isNull = false;
            clientMessage.set(queueStoreConfig_isNull);
            QueueStoreConfigCodec.encode(queueStoreConfig, clientMessage);
        }
        clientMessage.updateFrameLength();
        return clientMessage;
    }

    public static ClientMessage encodeRequest(String name, Collection<ListenerConfigHolder> listenerConfigs, int backupCount, int asyncBackupCount, int maxSize, int emptyQueueTtl, boolean statisticsEnabled, String quorumName, QueueStoreConfigHolder queueStoreConfig, String mergePolicy, int mergeBatchSize) {
        int requiredDataSize = RequestParameters.calculateDataSize(name, listenerConfigs, backupCount, asyncBackupCount, maxSize, emptyQueueTtl, statisticsEnabled, quorumName, queueStoreConfig, mergePolicy, mergeBatchSize);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(REQUEST_TYPE.id());
        clientMessage.setRetryable(false);
        clientMessage.setAcquiresResource(false);
        clientMessage.setOperationName("DynamicConfig.addQueueConfig");
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
        clientMessage.set(backupCount);
        clientMessage.set(asyncBackupCount);
        clientMessage.set(maxSize);
        clientMessage.set(emptyQueueTtl);
        clientMessage.set(statisticsEnabled);
        if (quorumName == null) {
            boolean quorumName_isNull = true;
            clientMessage.set(quorumName_isNull);
        } else {
            boolean quorumName_isNull = false;
            clientMessage.set(quorumName_isNull);
            clientMessage.set(quorumName);
        }
        if (queueStoreConfig == null) {
            boolean queueStoreConfig_isNull = true;
            clientMessage.set(queueStoreConfig_isNull);
        } else {
            boolean queueStoreConfig_isNull = false;
            clientMessage.set(queueStoreConfig_isNull);
            QueueStoreConfigCodec.encode(queueStoreConfig, clientMessage);
        }
        clientMessage.set(mergePolicy);
        clientMessage.set(mergeBatchSize);
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
        int backupCount = 0;
        parameters.backupCount = backupCount = clientMessage.getInt();
        int asyncBackupCount = 0;
        parameters.asyncBackupCount = asyncBackupCount = clientMessage.getInt();
        int maxSize = 0;
        parameters.maxSize = maxSize = clientMessage.getInt();
        int emptyQueueTtl = 0;
        parameters.emptyQueueTtl = emptyQueueTtl = clientMessage.getInt();
        boolean statisticsEnabled = false;
        parameters.statisticsEnabled = statisticsEnabled = clientMessage.getBoolean();
        String quorumName = null;
        boolean quorumName_isNull = clientMessage.getBoolean();
        if (!quorumName_isNull) {
            parameters.quorumName = quorumName = clientMessage.getStringUtf8();
        }
        QueueStoreConfigHolder queueStoreConfig = null;
        boolean queueStoreConfig_isNull = clientMessage.getBoolean();
        if (!queueStoreConfig_isNull) {
            parameters.queueStoreConfig = queueStoreConfig = QueueStoreConfigCodec.decode(clientMessage);
        }
        if (clientMessage.isComplete()) {
            return parameters;
        }
        String mergePolicy = null;
        parameters.mergePolicy = mergePolicy = clientMessage.getStringUtf8();
        parameters.mergePolicyExist = true;
        int mergeBatchSize = 0;
        parameters.mergeBatchSize = mergeBatchSize = clientMessage.getInt();
        parameters.mergeBatchSizeExist = true;
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
        public int backupCount;
        public int asyncBackupCount;
        public int maxSize;
        public int emptyQueueTtl;
        public boolean statisticsEnabled;
        public String quorumName;
        public QueueStoreConfigHolder queueStoreConfig;
        public boolean mergePolicyExist = false;
        public String mergePolicy;
        public boolean mergeBatchSizeExist = false;
        public int mergeBatchSize;

        public static int calculateDataSize(String name, Collection<ListenerConfigHolder> listenerConfigs, int backupCount, int asyncBackupCount, int maxSize, int emptyQueueTtl, boolean statisticsEnabled, String quorumName, QueueStoreConfigHolder queueStoreConfig) {
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
            dataSize += 4;
            dataSize += 4;
            dataSize += 4;
            ++dataSize;
            ++dataSize;
            if (quorumName != null) {
                dataSize += ParameterUtil.calculateDataSize(quorumName);
            }
            ++dataSize;
            if (queueStoreConfig != null) {
                dataSize += QueueStoreConfigCodec.calculateDataSize(queueStoreConfig);
            }
            return dataSize;
        }

        public static int calculateDataSize(String name, Collection<ListenerConfigHolder> listenerConfigs, int backupCount, int asyncBackupCount, int maxSize, int emptyQueueTtl, boolean statisticsEnabled, String quorumName, QueueStoreConfigHolder queueStoreConfig, String mergePolicy, int mergeBatchSize) {
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
            dataSize += 4;
            dataSize += 4;
            dataSize += 4;
            ++dataSize;
            ++dataSize;
            if (quorumName != null) {
                dataSize += ParameterUtil.calculateDataSize(quorumName);
            }
            ++dataSize;
            if (queueStoreConfig != null) {
                dataSize += QueueStoreConfigCodec.calculateDataSize(queueStoreConfig);
            }
            dataSize += ParameterUtil.calculateDataSize(mergePolicy);
            return dataSize += 4;
        }
    }
}

