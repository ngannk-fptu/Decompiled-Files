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
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@SuppressFBWarnings(value={"URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD"})
public final class DynamicConfigAddMultiMapConfigCodec {
    public static final DynamicConfigMessageType REQUEST_TYPE = DynamicConfigMessageType.DYNAMICCONFIG_ADDMULTIMAPCONFIG;
    public static final int RESPONSE_TYPE = 100;

    public static ClientMessage encodeRequest(String name, String collectionType, Collection<ListenerConfigHolder> listenerConfigs, boolean binary, int backupCount, int asyncBackupCount, boolean statisticsEnabled) {
        int requiredDataSize = RequestParameters.calculateDataSize(name, collectionType, listenerConfigs, binary, backupCount, asyncBackupCount, statisticsEnabled);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(REQUEST_TYPE.id());
        clientMessage.setRetryable(false);
        clientMessage.setAcquiresResource(false);
        clientMessage.setOperationName("DynamicConfig.addMultiMapConfig");
        clientMessage.set(name);
        clientMessage.set(collectionType);
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
        clientMessage.set(binary);
        clientMessage.set(backupCount);
        clientMessage.set(asyncBackupCount);
        clientMessage.set(statisticsEnabled);
        clientMessage.updateFrameLength();
        return clientMessage;
    }

    public static ClientMessage encodeRequest(String name, String collectionType, Collection<ListenerConfigHolder> listenerConfigs, boolean binary, int backupCount, int asyncBackupCount, boolean statisticsEnabled, String quorumName, String mergePolicy, int mergeBatchSize) {
        int requiredDataSize = RequestParameters.calculateDataSize(name, collectionType, listenerConfigs, binary, backupCount, asyncBackupCount, statisticsEnabled, quorumName, mergePolicy, mergeBatchSize);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(REQUEST_TYPE.id());
        clientMessage.setRetryable(false);
        clientMessage.setAcquiresResource(false);
        clientMessage.setOperationName("DynamicConfig.addMultiMapConfig");
        clientMessage.set(name);
        clientMessage.set(collectionType);
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
        clientMessage.set(binary);
        clientMessage.set(backupCount);
        clientMessage.set(asyncBackupCount);
        clientMessage.set(statisticsEnabled);
        if (quorumName == null) {
            boolean quorumName_isNull = true;
            clientMessage.set(quorumName_isNull);
        } else {
            boolean quorumName_isNull = false;
            clientMessage.set(quorumName_isNull);
            clientMessage.set(quorumName);
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
        String collectionType = null;
        parameters.collectionType = collectionType = clientMessage.getStringUtf8();
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
        boolean binary = false;
        parameters.binary = binary = clientMessage.getBoolean();
        int backupCount = 0;
        parameters.backupCount = backupCount = clientMessage.getInt();
        int asyncBackupCount = 0;
        parameters.asyncBackupCount = asyncBackupCount = clientMessage.getInt();
        boolean statisticsEnabled = false;
        parameters.statisticsEnabled = statisticsEnabled = clientMessage.getBoolean();
        if (clientMessage.isComplete()) {
            return parameters;
        }
        String quorumName = null;
        boolean quorumName_isNull = clientMessage.getBoolean();
        if (!quorumName_isNull) {
            parameters.quorumName = quorumName = clientMessage.getStringUtf8();
        }
        parameters.quorumNameExist = true;
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
        public String collectionType;
        public List<ListenerConfigHolder> listenerConfigs;
        public boolean binary;
        public int backupCount;
        public int asyncBackupCount;
        public boolean statisticsEnabled;
        public boolean quorumNameExist = false;
        public String quorumName;
        public boolean mergePolicyExist = false;
        public String mergePolicy;
        public boolean mergeBatchSizeExist = false;
        public int mergeBatchSize;

        public static int calculateDataSize(String name, String collectionType, Collection<ListenerConfigHolder> listenerConfigs, boolean binary, int backupCount, int asyncBackupCount, boolean statisticsEnabled) {
            int dataSize = ClientMessage.HEADER_SIZE;
            dataSize += ParameterUtil.calculateDataSize(name);
            dataSize += ParameterUtil.calculateDataSize(collectionType);
            ++dataSize;
            if (listenerConfigs != null) {
                dataSize += 4;
                for (ListenerConfigHolder listenerConfigs_item : listenerConfigs) {
                    dataSize += ListenerConfigCodec.calculateDataSize(listenerConfigs_item);
                }
            }
            ++dataSize;
            dataSize += 4;
            dataSize += 4;
            return ++dataSize;
        }

        public static int calculateDataSize(String name, String collectionType, Collection<ListenerConfigHolder> listenerConfigs, boolean binary, int backupCount, int asyncBackupCount, boolean statisticsEnabled, String quorumName, String mergePolicy, int mergeBatchSize) {
            int dataSize = ClientMessage.HEADER_SIZE;
            dataSize += ParameterUtil.calculateDataSize(name);
            dataSize += ParameterUtil.calculateDataSize(collectionType);
            ++dataSize;
            if (listenerConfigs != null) {
                dataSize += 4;
                for (ListenerConfigHolder listenerConfigs_item : listenerConfigs) {
                    dataSize += ListenerConfigCodec.calculateDataSize(listenerConfigs_item);
                }
            }
            ++dataSize;
            dataSize += 4;
            dataSize += 4;
            ++dataSize;
            ++dataSize;
            if (quorumName != null) {
                dataSize += ParameterUtil.calculateDataSize(quorumName);
            }
            dataSize += ParameterUtil.calculateDataSize(mergePolicy);
            return dataSize += 4;
        }
    }
}

