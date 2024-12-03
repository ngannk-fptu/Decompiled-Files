/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.client.impl.protocol.codec;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.CacheSimpleEntryListenerConfigCodec;
import com.hazelcast.client.impl.protocol.codec.DynamicConfigMessageType;
import com.hazelcast.client.impl.protocol.codec.EvictionConfigCodec;
import com.hazelcast.client.impl.protocol.codec.HotRestartConfigCodec;
import com.hazelcast.client.impl.protocol.codec.ListenerConfigCodec;
import com.hazelcast.client.impl.protocol.codec.TimedExpiryPolicyFactoryConfigCodec;
import com.hazelcast.client.impl.protocol.codec.WanReplicationRefCodec;
import com.hazelcast.client.impl.protocol.task.dynamicconfig.EvictionConfigHolder;
import com.hazelcast.client.impl.protocol.task.dynamicconfig.ListenerConfigHolder;
import com.hazelcast.client.impl.protocol.util.ParameterUtil;
import com.hazelcast.config.CacheSimpleConfig;
import com.hazelcast.config.CacheSimpleEntryListenerConfig;
import com.hazelcast.config.HotRestartConfig;
import com.hazelcast.config.WanReplicationRef;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@SuppressFBWarnings(value={"URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD"})
public final class DynamicConfigAddCacheConfigCodec {
    public static final DynamicConfigMessageType REQUEST_TYPE = DynamicConfigMessageType.DYNAMICCONFIG_ADDCACHECONFIG;
    public static final int RESPONSE_TYPE = 100;

    public static ClientMessage encodeRequest(String name, String keyType, String valueType, boolean statisticsEnabled, boolean managementEnabled, boolean readThrough, boolean writeThrough, String cacheLoaderFactory, String cacheWriterFactory, String cacheLoader, String cacheWriter, int backupCount, int asyncBackupCount, String inMemoryFormat, String quorumName, String mergePolicy, boolean disablePerEntryInvalidationEvents, Collection<ListenerConfigHolder> partitionLostListenerConfigs, String expiryPolicyFactoryClassName, CacheSimpleConfig.ExpiryPolicyFactoryConfig.TimedExpiryPolicyFactoryConfig timedExpiryPolicyFactoryConfig, Collection<CacheSimpleEntryListenerConfig> cacheEntryListeners, EvictionConfigHolder evictionConfig, WanReplicationRef wanReplicationRef, HotRestartConfig hotRestartConfig) {
        boolean timedExpiryPolicyFactoryConfig_isNull;
        boolean expiryPolicyFactoryClassName_isNull;
        boolean partitionLostListenerConfigs_isNull;
        boolean mergePolicy_isNull;
        boolean quorumName_isNull;
        boolean cacheWriter_isNull;
        boolean cacheLoader_isNull;
        boolean cacheWriterFactory_isNull;
        boolean cacheLoaderFactory_isNull;
        boolean valueType_isNull;
        boolean keyType_isNull;
        int requiredDataSize = RequestParameters.calculateDataSize(name, keyType, valueType, statisticsEnabled, managementEnabled, readThrough, writeThrough, cacheLoaderFactory, cacheWriterFactory, cacheLoader, cacheWriter, backupCount, asyncBackupCount, inMemoryFormat, quorumName, mergePolicy, disablePerEntryInvalidationEvents, partitionLostListenerConfigs, expiryPolicyFactoryClassName, timedExpiryPolicyFactoryConfig, cacheEntryListeners, evictionConfig, wanReplicationRef, hotRestartConfig);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(REQUEST_TYPE.id());
        clientMessage.setRetryable(false);
        clientMessage.setAcquiresResource(false);
        clientMessage.setOperationName("DynamicConfig.addCacheConfig");
        clientMessage.set(name);
        if (keyType == null) {
            keyType_isNull = true;
            clientMessage.set(keyType_isNull);
        } else {
            keyType_isNull = false;
            clientMessage.set(keyType_isNull);
            clientMessage.set(keyType);
        }
        if (valueType == null) {
            valueType_isNull = true;
            clientMessage.set(valueType_isNull);
        } else {
            valueType_isNull = false;
            clientMessage.set(valueType_isNull);
            clientMessage.set(valueType);
        }
        clientMessage.set(statisticsEnabled);
        clientMessage.set(managementEnabled);
        clientMessage.set(readThrough);
        clientMessage.set(writeThrough);
        if (cacheLoaderFactory == null) {
            cacheLoaderFactory_isNull = true;
            clientMessage.set(cacheLoaderFactory_isNull);
        } else {
            cacheLoaderFactory_isNull = false;
            clientMessage.set(cacheLoaderFactory_isNull);
            clientMessage.set(cacheLoaderFactory);
        }
        if (cacheWriterFactory == null) {
            cacheWriterFactory_isNull = true;
            clientMessage.set(cacheWriterFactory_isNull);
        } else {
            cacheWriterFactory_isNull = false;
            clientMessage.set(cacheWriterFactory_isNull);
            clientMessage.set(cacheWriterFactory);
        }
        if (cacheLoader == null) {
            cacheLoader_isNull = true;
            clientMessage.set(cacheLoader_isNull);
        } else {
            cacheLoader_isNull = false;
            clientMessage.set(cacheLoader_isNull);
            clientMessage.set(cacheLoader);
        }
        if (cacheWriter == null) {
            cacheWriter_isNull = true;
            clientMessage.set(cacheWriter_isNull);
        } else {
            cacheWriter_isNull = false;
            clientMessage.set(cacheWriter_isNull);
            clientMessage.set(cacheWriter);
        }
        clientMessage.set(backupCount);
        clientMessage.set(asyncBackupCount);
        clientMessage.set(inMemoryFormat);
        if (quorumName == null) {
            quorumName_isNull = true;
            clientMessage.set(quorumName_isNull);
        } else {
            quorumName_isNull = false;
            clientMessage.set(quorumName_isNull);
            clientMessage.set(quorumName);
        }
        if (mergePolicy == null) {
            mergePolicy_isNull = true;
            clientMessage.set(mergePolicy_isNull);
        } else {
            mergePolicy_isNull = false;
            clientMessage.set(mergePolicy_isNull);
            clientMessage.set(mergePolicy);
        }
        clientMessage.set(disablePerEntryInvalidationEvents);
        if (partitionLostListenerConfigs == null) {
            partitionLostListenerConfigs_isNull = true;
            clientMessage.set(partitionLostListenerConfigs_isNull);
        } else {
            partitionLostListenerConfigs_isNull = false;
            clientMessage.set(partitionLostListenerConfigs_isNull);
            clientMessage.set(partitionLostListenerConfigs.size());
            for (ListenerConfigHolder partitionLostListenerConfigs_item : partitionLostListenerConfigs) {
                ListenerConfigCodec.encode(partitionLostListenerConfigs_item, clientMessage);
            }
        }
        if (expiryPolicyFactoryClassName == null) {
            expiryPolicyFactoryClassName_isNull = true;
            clientMessage.set(expiryPolicyFactoryClassName_isNull);
        } else {
            expiryPolicyFactoryClassName_isNull = false;
            clientMessage.set(expiryPolicyFactoryClassName_isNull);
            clientMessage.set(expiryPolicyFactoryClassName);
        }
        if (timedExpiryPolicyFactoryConfig == null) {
            timedExpiryPolicyFactoryConfig_isNull = true;
            clientMessage.set(timedExpiryPolicyFactoryConfig_isNull);
        } else {
            timedExpiryPolicyFactoryConfig_isNull = false;
            clientMessage.set(timedExpiryPolicyFactoryConfig_isNull);
            TimedExpiryPolicyFactoryConfigCodec.encode(timedExpiryPolicyFactoryConfig, clientMessage);
        }
        if (cacheEntryListeners == null) {
            boolean cacheEntryListeners_isNull = true;
            clientMessage.set(cacheEntryListeners_isNull);
        } else {
            boolean cacheEntryListeners_isNull = false;
            clientMessage.set(cacheEntryListeners_isNull);
            clientMessage.set(cacheEntryListeners.size());
            for (CacheSimpleEntryListenerConfig cacheEntryListeners_item : cacheEntryListeners) {
                CacheSimpleEntryListenerConfigCodec.encode(cacheEntryListeners_item, clientMessage);
            }
        }
        if (evictionConfig == null) {
            boolean evictionConfig_isNull = true;
            clientMessage.set(evictionConfig_isNull);
        } else {
            boolean evictionConfig_isNull = false;
            clientMessage.set(evictionConfig_isNull);
            EvictionConfigCodec.encode(evictionConfig, clientMessage);
        }
        if (wanReplicationRef == null) {
            boolean wanReplicationRef_isNull = true;
            clientMessage.set(wanReplicationRef_isNull);
        } else {
            boolean wanReplicationRef_isNull = false;
            clientMessage.set(wanReplicationRef_isNull);
            WanReplicationRefCodec.encode(wanReplicationRef, clientMessage);
        }
        if (hotRestartConfig == null) {
            boolean hotRestartConfig_isNull = true;
            clientMessage.set(hotRestartConfig_isNull);
        } else {
            boolean hotRestartConfig_isNull = false;
            clientMessage.set(hotRestartConfig_isNull);
            HotRestartConfigCodec.encode(hotRestartConfig, clientMessage);
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
        String keyType = null;
        boolean keyType_isNull = clientMessage.getBoolean();
        if (!keyType_isNull) {
            parameters.keyType = keyType = clientMessage.getStringUtf8();
        }
        String valueType = null;
        boolean valueType_isNull = clientMessage.getBoolean();
        if (!valueType_isNull) {
            parameters.valueType = valueType = clientMessage.getStringUtf8();
        }
        boolean statisticsEnabled = false;
        parameters.statisticsEnabled = statisticsEnabled = clientMessage.getBoolean();
        boolean managementEnabled = false;
        parameters.managementEnabled = managementEnabled = clientMessage.getBoolean();
        boolean readThrough = false;
        parameters.readThrough = readThrough = clientMessage.getBoolean();
        boolean writeThrough = false;
        parameters.writeThrough = writeThrough = clientMessage.getBoolean();
        String cacheLoaderFactory = null;
        boolean cacheLoaderFactory_isNull = clientMessage.getBoolean();
        if (!cacheLoaderFactory_isNull) {
            parameters.cacheLoaderFactory = cacheLoaderFactory = clientMessage.getStringUtf8();
        }
        String cacheWriterFactory = null;
        boolean cacheWriterFactory_isNull = clientMessage.getBoolean();
        if (!cacheWriterFactory_isNull) {
            parameters.cacheWriterFactory = cacheWriterFactory = clientMessage.getStringUtf8();
        }
        String cacheLoader = null;
        boolean cacheLoader_isNull = clientMessage.getBoolean();
        if (!cacheLoader_isNull) {
            parameters.cacheLoader = cacheLoader = clientMessage.getStringUtf8();
        }
        String cacheWriter = null;
        boolean cacheWriter_isNull = clientMessage.getBoolean();
        if (!cacheWriter_isNull) {
            parameters.cacheWriter = cacheWriter = clientMessage.getStringUtf8();
        }
        int backupCount = 0;
        parameters.backupCount = backupCount = clientMessage.getInt();
        int asyncBackupCount = 0;
        parameters.asyncBackupCount = asyncBackupCount = clientMessage.getInt();
        String inMemoryFormat = null;
        parameters.inMemoryFormat = inMemoryFormat = clientMessage.getStringUtf8();
        String quorumName = null;
        boolean quorumName_isNull = clientMessage.getBoolean();
        if (!quorumName_isNull) {
            parameters.quorumName = quorumName = clientMessage.getStringUtf8();
        }
        String mergePolicy = null;
        boolean mergePolicy_isNull = clientMessage.getBoolean();
        if (!mergePolicy_isNull) {
            parameters.mergePolicy = mergePolicy = clientMessage.getStringUtf8();
        }
        boolean disablePerEntryInvalidationEvents = false;
        parameters.disablePerEntryInvalidationEvents = disablePerEntryInvalidationEvents = clientMessage.getBoolean();
        ArrayList<ListenerConfigHolder> partitionLostListenerConfigs = null;
        boolean partitionLostListenerConfigs_isNull = clientMessage.getBoolean();
        if (!partitionLostListenerConfigs_isNull) {
            int partitionLostListenerConfigs_size = clientMessage.getInt();
            partitionLostListenerConfigs = new ArrayList<ListenerConfigHolder>(partitionLostListenerConfigs_size);
            for (int partitionLostListenerConfigs_index = 0; partitionLostListenerConfigs_index < partitionLostListenerConfigs_size; ++partitionLostListenerConfigs_index) {
                ListenerConfigHolder partitionLostListenerConfigs_item = ListenerConfigCodec.decode(clientMessage);
                partitionLostListenerConfigs.add(partitionLostListenerConfigs_item);
            }
            parameters.partitionLostListenerConfigs = partitionLostListenerConfigs;
        }
        String expiryPolicyFactoryClassName = null;
        boolean expiryPolicyFactoryClassName_isNull = clientMessage.getBoolean();
        if (!expiryPolicyFactoryClassName_isNull) {
            parameters.expiryPolicyFactoryClassName = expiryPolicyFactoryClassName = clientMessage.getStringUtf8();
        }
        CacheSimpleConfig.ExpiryPolicyFactoryConfig.TimedExpiryPolicyFactoryConfig timedExpiryPolicyFactoryConfig = null;
        boolean timedExpiryPolicyFactoryConfig_isNull = clientMessage.getBoolean();
        if (!timedExpiryPolicyFactoryConfig_isNull) {
            parameters.timedExpiryPolicyFactoryConfig = timedExpiryPolicyFactoryConfig = TimedExpiryPolicyFactoryConfigCodec.decode(clientMessage);
        }
        ArrayList<CacheSimpleEntryListenerConfig> cacheEntryListeners = null;
        boolean cacheEntryListeners_isNull = clientMessage.getBoolean();
        if (!cacheEntryListeners_isNull) {
            int cacheEntryListeners_size = clientMessage.getInt();
            cacheEntryListeners = new ArrayList<CacheSimpleEntryListenerConfig>(cacheEntryListeners_size);
            for (int cacheEntryListeners_index = 0; cacheEntryListeners_index < cacheEntryListeners_size; ++cacheEntryListeners_index) {
                CacheSimpleEntryListenerConfig cacheEntryListeners_item = CacheSimpleEntryListenerConfigCodec.decode(clientMessage);
                cacheEntryListeners.add(cacheEntryListeners_item);
            }
            parameters.cacheEntryListeners = cacheEntryListeners;
        }
        EvictionConfigHolder evictionConfig = null;
        boolean evictionConfig_isNull = clientMessage.getBoolean();
        if (!evictionConfig_isNull) {
            parameters.evictionConfig = evictionConfig = EvictionConfigCodec.decode(clientMessage);
        }
        WanReplicationRef wanReplicationRef = null;
        boolean wanReplicationRef_isNull = clientMessage.getBoolean();
        if (!wanReplicationRef_isNull) {
            parameters.wanReplicationRef = wanReplicationRef = WanReplicationRefCodec.decode(clientMessage);
        }
        HotRestartConfig hotRestartConfig = null;
        boolean hotRestartConfig_isNull = clientMessage.getBoolean();
        if (!hotRestartConfig_isNull) {
            parameters.hotRestartConfig = hotRestartConfig = HotRestartConfigCodec.decode(clientMessage);
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
        public String keyType;
        public String valueType;
        public boolean statisticsEnabled;
        public boolean managementEnabled;
        public boolean readThrough;
        public boolean writeThrough;
        public String cacheLoaderFactory;
        public String cacheWriterFactory;
        public String cacheLoader;
        public String cacheWriter;
        public int backupCount;
        public int asyncBackupCount;
        public String inMemoryFormat;
        public String quorumName;
        public String mergePolicy;
        public boolean disablePerEntryInvalidationEvents;
        public List<ListenerConfigHolder> partitionLostListenerConfigs;
        public String expiryPolicyFactoryClassName;
        public CacheSimpleConfig.ExpiryPolicyFactoryConfig.TimedExpiryPolicyFactoryConfig timedExpiryPolicyFactoryConfig;
        public List<CacheSimpleEntryListenerConfig> cacheEntryListeners;
        public EvictionConfigHolder evictionConfig;
        public WanReplicationRef wanReplicationRef;
        public HotRestartConfig hotRestartConfig;

        public static int calculateDataSize(String name, String keyType, String valueType, boolean statisticsEnabled, boolean managementEnabled, boolean readThrough, boolean writeThrough, String cacheLoaderFactory, String cacheWriterFactory, String cacheLoader, String cacheWriter, int backupCount, int asyncBackupCount, String inMemoryFormat, String quorumName, String mergePolicy, boolean disablePerEntryInvalidationEvents, Collection<ListenerConfigHolder> partitionLostListenerConfigs, String expiryPolicyFactoryClassName, CacheSimpleConfig.ExpiryPolicyFactoryConfig.TimedExpiryPolicyFactoryConfig timedExpiryPolicyFactoryConfig, Collection<CacheSimpleEntryListenerConfig> cacheEntryListeners, EvictionConfigHolder evictionConfig, WanReplicationRef wanReplicationRef, HotRestartConfig hotRestartConfig) {
            int dataSize = ClientMessage.HEADER_SIZE;
            dataSize += ParameterUtil.calculateDataSize(name);
            ++dataSize;
            if (keyType != null) {
                dataSize += ParameterUtil.calculateDataSize(keyType);
            }
            ++dataSize;
            if (valueType != null) {
                dataSize += ParameterUtil.calculateDataSize(valueType);
            }
            ++dataSize;
            ++dataSize;
            ++dataSize;
            ++dataSize;
            ++dataSize;
            if (cacheLoaderFactory != null) {
                dataSize += ParameterUtil.calculateDataSize(cacheLoaderFactory);
            }
            ++dataSize;
            if (cacheWriterFactory != null) {
                dataSize += ParameterUtil.calculateDataSize(cacheWriterFactory);
            }
            ++dataSize;
            if (cacheLoader != null) {
                dataSize += ParameterUtil.calculateDataSize(cacheLoader);
            }
            ++dataSize;
            if (cacheWriter != null) {
                dataSize += ParameterUtil.calculateDataSize(cacheWriter);
            }
            dataSize += 4;
            dataSize += 4;
            dataSize += ParameterUtil.calculateDataSize(inMemoryFormat);
            ++dataSize;
            if (quorumName != null) {
                dataSize += ParameterUtil.calculateDataSize(quorumName);
            }
            ++dataSize;
            if (mergePolicy != null) {
                dataSize += ParameterUtil.calculateDataSize(mergePolicy);
            }
            ++dataSize;
            ++dataSize;
            if (partitionLostListenerConfigs != null) {
                dataSize += 4;
                for (ListenerConfigHolder partitionLostListenerConfigs_item : partitionLostListenerConfigs) {
                    dataSize += ListenerConfigCodec.calculateDataSize(partitionLostListenerConfigs_item);
                }
            }
            ++dataSize;
            if (expiryPolicyFactoryClassName != null) {
                dataSize += ParameterUtil.calculateDataSize(expiryPolicyFactoryClassName);
            }
            ++dataSize;
            if (timedExpiryPolicyFactoryConfig != null) {
                dataSize += TimedExpiryPolicyFactoryConfigCodec.calculateDataSize(timedExpiryPolicyFactoryConfig);
            }
            ++dataSize;
            if (cacheEntryListeners != null) {
                dataSize += 4;
                for (CacheSimpleEntryListenerConfig cacheEntryListeners_item : cacheEntryListeners) {
                    dataSize += CacheSimpleEntryListenerConfigCodec.calculateDataSize(cacheEntryListeners_item);
                }
            }
            ++dataSize;
            if (evictionConfig != null) {
                dataSize += EvictionConfigCodec.calculateDataSize(evictionConfig);
            }
            ++dataSize;
            if (wanReplicationRef != null) {
                dataSize += WanReplicationRefCodec.calculateDataSize(wanReplicationRef);
            }
            ++dataSize;
            if (hotRestartConfig != null) {
                dataSize += HotRestartConfigCodec.calculateDataSize(hotRestartConfig);
            }
            return dataSize;
        }
    }
}

