/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.client.impl.protocol.codec;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.DynamicConfigMessageType;
import com.hazelcast.client.impl.protocol.codec.HotRestartConfigCodec;
import com.hazelcast.client.impl.protocol.codec.ListenerConfigCodec;
import com.hazelcast.client.impl.protocol.codec.MapAttributeConfigCodec;
import com.hazelcast.client.impl.protocol.codec.MapIndexConfigCodec;
import com.hazelcast.client.impl.protocol.codec.MapStoreConfigCodec;
import com.hazelcast.client.impl.protocol.codec.NearCacheConfigCodec;
import com.hazelcast.client.impl.protocol.codec.QueryCacheConfigCodec;
import com.hazelcast.client.impl.protocol.codec.WanReplicationRefCodec;
import com.hazelcast.client.impl.protocol.task.dynamicconfig.ListenerConfigHolder;
import com.hazelcast.client.impl.protocol.task.dynamicconfig.MapStoreConfigHolder;
import com.hazelcast.client.impl.protocol.task.dynamicconfig.NearCacheConfigHolder;
import com.hazelcast.client.impl.protocol.task.dynamicconfig.QueryCacheConfigHolder;
import com.hazelcast.client.impl.protocol.util.ParameterUtil;
import com.hazelcast.config.HotRestartConfig;
import com.hazelcast.config.MapAttributeConfig;
import com.hazelcast.config.MapIndexConfig;
import com.hazelcast.config.WanReplicationRef;
import com.hazelcast.nio.serialization.Data;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@SuppressFBWarnings(value={"URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD"})
public final class DynamicConfigAddMapConfigCodec {
    public static final DynamicConfigMessageType REQUEST_TYPE = DynamicConfigMessageType.DYNAMICCONFIG_ADDMAPCONFIG;
    public static final int RESPONSE_TYPE = 100;

    public static ClientMessage encodeRequest(String name, int backupCount, int asyncBackupCount, int timeToLiveSeconds, int maxIdleSeconds, String evictionPolicy, boolean readBackupData, String cacheDeserializedValues, String mergePolicy, String inMemoryFormat, Collection<ListenerConfigHolder> listenerConfigs, Collection<ListenerConfigHolder> partitionLostListenerConfigs, boolean statisticsEnabled, String quorumName, Data mapEvictionPolicy, String maxSizeConfigMaxSizePolicy, int maxSizeConfigSize, MapStoreConfigHolder mapStoreConfig, NearCacheConfigHolder nearCacheConfig, WanReplicationRef wanReplicationRef, Collection<MapIndexConfig> mapIndexConfigs, Collection<MapAttributeConfig> mapAttributeConfigs, Collection<QueryCacheConfigHolder> queryCacheConfigs, String partitioningStrategyClassName, Data partitioningStrategyImplementation, HotRestartConfig hotRestartConfig) {
        boolean mapAttributeConfigs_isNull;
        boolean mapIndexConfigs_isNull;
        boolean wanReplicationRef_isNull;
        boolean nearCacheConfig_isNull;
        boolean mapStoreConfig_isNull;
        boolean mapEvictionPolicy_isNull;
        boolean partitionLostListenerConfigs_isNull;
        boolean listenerConfigs_isNull;
        int requiredDataSize = RequestParameters.calculateDataSize(name, backupCount, asyncBackupCount, timeToLiveSeconds, maxIdleSeconds, evictionPolicy, readBackupData, cacheDeserializedValues, mergePolicy, inMemoryFormat, listenerConfigs, partitionLostListenerConfigs, statisticsEnabled, quorumName, mapEvictionPolicy, maxSizeConfigMaxSizePolicy, maxSizeConfigSize, mapStoreConfig, nearCacheConfig, wanReplicationRef, mapIndexConfigs, mapAttributeConfigs, queryCacheConfigs, partitioningStrategyClassName, partitioningStrategyImplementation, hotRestartConfig);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(REQUEST_TYPE.id());
        clientMessage.setRetryable(false);
        clientMessage.setAcquiresResource(false);
        clientMessage.setOperationName("DynamicConfig.addMapConfig");
        clientMessage.set(name);
        clientMessage.set(backupCount);
        clientMessage.set(asyncBackupCount);
        clientMessage.set(timeToLiveSeconds);
        clientMessage.set(maxIdleSeconds);
        clientMessage.set(evictionPolicy);
        clientMessage.set(readBackupData);
        clientMessage.set(cacheDeserializedValues);
        clientMessage.set(mergePolicy);
        clientMessage.set(inMemoryFormat);
        if (listenerConfigs == null) {
            listenerConfigs_isNull = true;
            clientMessage.set(listenerConfigs_isNull);
        } else {
            listenerConfigs_isNull = false;
            clientMessage.set(listenerConfigs_isNull);
            clientMessage.set(listenerConfigs.size());
            for (ListenerConfigHolder listenerConfigHolder : listenerConfigs) {
                ListenerConfigCodec.encode(listenerConfigHolder, clientMessage);
            }
        }
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
        clientMessage.set(statisticsEnabled);
        if (quorumName == null) {
            boolean bl = true;
            clientMessage.set(bl);
        } else {
            boolean bl = false;
            clientMessage.set(bl);
            clientMessage.set(quorumName);
        }
        if (mapEvictionPolicy == null) {
            mapEvictionPolicy_isNull = true;
            clientMessage.set(mapEvictionPolicy_isNull);
        } else {
            mapEvictionPolicy_isNull = false;
            clientMessage.set(mapEvictionPolicy_isNull);
            clientMessage.set(mapEvictionPolicy);
        }
        clientMessage.set(maxSizeConfigMaxSizePolicy);
        clientMessage.set(maxSizeConfigSize);
        if (mapStoreConfig == null) {
            mapStoreConfig_isNull = true;
            clientMessage.set(mapStoreConfig_isNull);
        } else {
            mapStoreConfig_isNull = false;
            clientMessage.set(mapStoreConfig_isNull);
            MapStoreConfigCodec.encode(mapStoreConfig, clientMessage);
        }
        if (nearCacheConfig == null) {
            nearCacheConfig_isNull = true;
            clientMessage.set(nearCacheConfig_isNull);
        } else {
            nearCacheConfig_isNull = false;
            clientMessage.set(nearCacheConfig_isNull);
            NearCacheConfigCodec.encode(nearCacheConfig, clientMessage);
        }
        if (wanReplicationRef == null) {
            wanReplicationRef_isNull = true;
            clientMessage.set(wanReplicationRef_isNull);
        } else {
            wanReplicationRef_isNull = false;
            clientMessage.set(wanReplicationRef_isNull);
            WanReplicationRefCodec.encode(wanReplicationRef, clientMessage);
        }
        if (mapIndexConfigs == null) {
            mapIndexConfigs_isNull = true;
            clientMessage.set(mapIndexConfigs_isNull);
        } else {
            mapIndexConfigs_isNull = false;
            clientMessage.set(mapIndexConfigs_isNull);
            clientMessage.set(mapIndexConfigs.size());
            for (MapIndexConfig mapIndexConfig : mapIndexConfigs) {
                MapIndexConfigCodec.encode(mapIndexConfig, clientMessage);
            }
        }
        if (mapAttributeConfigs == null) {
            mapAttributeConfigs_isNull = true;
            clientMessage.set(mapAttributeConfigs_isNull);
        } else {
            mapAttributeConfigs_isNull = false;
            clientMessage.set(mapAttributeConfigs_isNull);
            clientMessage.set(mapAttributeConfigs.size());
            for (MapAttributeConfig mapAttributeConfig : mapAttributeConfigs) {
                MapAttributeConfigCodec.encode(mapAttributeConfig, clientMessage);
            }
        }
        if (queryCacheConfigs == null) {
            boolean bl = true;
            clientMessage.set(bl);
        } else {
            boolean bl = false;
            clientMessage.set(bl);
            clientMessage.set(queryCacheConfigs.size());
            for (QueryCacheConfigHolder queryCacheConfigs_item : queryCacheConfigs) {
                QueryCacheConfigCodec.encode(queryCacheConfigs_item, clientMessage);
            }
        }
        if (partitioningStrategyClassName == null) {
            boolean bl = true;
            clientMessage.set(bl);
        } else {
            boolean bl = false;
            clientMessage.set(bl);
            clientMessage.set(partitioningStrategyClassName);
        }
        if (partitioningStrategyImplementation == null) {
            boolean partitioningStrategyImplementation_isNull = true;
            clientMessage.set(partitioningStrategyImplementation_isNull);
        } else {
            boolean partitioningStrategyImplementation_isNull = false;
            clientMessage.set(partitioningStrategyImplementation_isNull);
            clientMessage.set(partitioningStrategyImplementation);
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

    public static ClientMessage encodeRequest(String name, int backupCount, int asyncBackupCount, int timeToLiveSeconds, int maxIdleSeconds, String evictionPolicy, boolean readBackupData, String cacheDeserializedValues, String mergePolicy, String inMemoryFormat, Collection<ListenerConfigHolder> listenerConfigs, Collection<ListenerConfigHolder> partitionLostListenerConfigs, boolean statisticsEnabled, String quorumName, Data mapEvictionPolicy, String maxSizeConfigMaxSizePolicy, int maxSizeConfigSize, MapStoreConfigHolder mapStoreConfig, NearCacheConfigHolder nearCacheConfig, WanReplicationRef wanReplicationRef, Collection<MapIndexConfig> mapIndexConfigs, Collection<MapAttributeConfig> mapAttributeConfigs, Collection<QueryCacheConfigHolder> queryCacheConfigs, String partitioningStrategyClassName, Data partitioningStrategyImplementation, HotRestartConfig hotRestartConfig, int mergeBatchSize) {
        boolean mapAttributeConfigs_isNull;
        boolean mapIndexConfigs_isNull;
        boolean wanReplicationRef_isNull;
        boolean nearCacheConfig_isNull;
        boolean mapStoreConfig_isNull;
        boolean mapEvictionPolicy_isNull;
        boolean partitionLostListenerConfigs_isNull;
        boolean listenerConfigs_isNull;
        int requiredDataSize = RequestParameters.calculateDataSize(name, backupCount, asyncBackupCount, timeToLiveSeconds, maxIdleSeconds, evictionPolicy, readBackupData, cacheDeserializedValues, mergePolicy, inMemoryFormat, listenerConfigs, partitionLostListenerConfigs, statisticsEnabled, quorumName, mapEvictionPolicy, maxSizeConfigMaxSizePolicy, maxSizeConfigSize, mapStoreConfig, nearCacheConfig, wanReplicationRef, mapIndexConfigs, mapAttributeConfigs, queryCacheConfigs, partitioningStrategyClassName, partitioningStrategyImplementation, hotRestartConfig, mergeBatchSize);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(REQUEST_TYPE.id());
        clientMessage.setRetryable(false);
        clientMessage.setAcquiresResource(false);
        clientMessage.setOperationName("DynamicConfig.addMapConfig");
        clientMessage.set(name);
        clientMessage.set(backupCount);
        clientMessage.set(asyncBackupCount);
        clientMessage.set(timeToLiveSeconds);
        clientMessage.set(maxIdleSeconds);
        clientMessage.set(evictionPolicy);
        clientMessage.set(readBackupData);
        clientMessage.set(cacheDeserializedValues);
        clientMessage.set(mergePolicy);
        clientMessage.set(inMemoryFormat);
        if (listenerConfigs == null) {
            listenerConfigs_isNull = true;
            clientMessage.set(listenerConfigs_isNull);
        } else {
            listenerConfigs_isNull = false;
            clientMessage.set(listenerConfigs_isNull);
            clientMessage.set(listenerConfigs.size());
            for (ListenerConfigHolder listenerConfigHolder : listenerConfigs) {
                ListenerConfigCodec.encode(listenerConfigHolder, clientMessage);
            }
        }
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
        clientMessage.set(statisticsEnabled);
        if (quorumName == null) {
            boolean bl = true;
            clientMessage.set(bl);
        } else {
            boolean bl = false;
            clientMessage.set(bl);
            clientMessage.set(quorumName);
        }
        if (mapEvictionPolicy == null) {
            mapEvictionPolicy_isNull = true;
            clientMessage.set(mapEvictionPolicy_isNull);
        } else {
            mapEvictionPolicy_isNull = false;
            clientMessage.set(mapEvictionPolicy_isNull);
            clientMessage.set(mapEvictionPolicy);
        }
        clientMessage.set(maxSizeConfigMaxSizePolicy);
        clientMessage.set(maxSizeConfigSize);
        if (mapStoreConfig == null) {
            mapStoreConfig_isNull = true;
            clientMessage.set(mapStoreConfig_isNull);
        } else {
            mapStoreConfig_isNull = false;
            clientMessage.set(mapStoreConfig_isNull);
            MapStoreConfigCodec.encode(mapStoreConfig, clientMessage);
        }
        if (nearCacheConfig == null) {
            nearCacheConfig_isNull = true;
            clientMessage.set(nearCacheConfig_isNull);
        } else {
            nearCacheConfig_isNull = false;
            clientMessage.set(nearCacheConfig_isNull);
            NearCacheConfigCodec.encode(nearCacheConfig, clientMessage);
        }
        if (wanReplicationRef == null) {
            wanReplicationRef_isNull = true;
            clientMessage.set(wanReplicationRef_isNull);
        } else {
            wanReplicationRef_isNull = false;
            clientMessage.set(wanReplicationRef_isNull);
            WanReplicationRefCodec.encode(wanReplicationRef, clientMessage);
        }
        if (mapIndexConfigs == null) {
            mapIndexConfigs_isNull = true;
            clientMessage.set(mapIndexConfigs_isNull);
        } else {
            mapIndexConfigs_isNull = false;
            clientMessage.set(mapIndexConfigs_isNull);
            clientMessage.set(mapIndexConfigs.size());
            for (MapIndexConfig mapIndexConfig : mapIndexConfigs) {
                MapIndexConfigCodec.encode(mapIndexConfig, clientMessage);
            }
        }
        if (mapAttributeConfigs == null) {
            mapAttributeConfigs_isNull = true;
            clientMessage.set(mapAttributeConfigs_isNull);
        } else {
            mapAttributeConfigs_isNull = false;
            clientMessage.set(mapAttributeConfigs_isNull);
            clientMessage.set(mapAttributeConfigs.size());
            for (MapAttributeConfig mapAttributeConfig : mapAttributeConfigs) {
                MapAttributeConfigCodec.encode(mapAttributeConfig, clientMessage);
            }
        }
        if (queryCacheConfigs == null) {
            boolean bl = true;
            clientMessage.set(bl);
        } else {
            boolean bl = false;
            clientMessage.set(bl);
            clientMessage.set(queryCacheConfigs.size());
            for (QueryCacheConfigHolder queryCacheConfigs_item : queryCacheConfigs) {
                QueryCacheConfigCodec.encode(queryCacheConfigs_item, clientMessage);
            }
        }
        if (partitioningStrategyClassName == null) {
            boolean bl = true;
            clientMessage.set(bl);
        } else {
            boolean bl = false;
            clientMessage.set(bl);
            clientMessage.set(partitioningStrategyClassName);
        }
        if (partitioningStrategyImplementation == null) {
            boolean partitioningStrategyImplementation_isNull = true;
            clientMessage.set(partitioningStrategyImplementation_isNull);
        } else {
            boolean partitioningStrategyImplementation_isNull = false;
            clientMessage.set(partitioningStrategyImplementation_isNull);
            clientMessage.set(partitioningStrategyImplementation);
        }
        if (hotRestartConfig == null) {
            boolean hotRestartConfig_isNull = true;
            clientMessage.set(hotRestartConfig_isNull);
        } else {
            boolean hotRestartConfig_isNull = false;
            clientMessage.set(hotRestartConfig_isNull);
            HotRestartConfigCodec.encode(hotRestartConfig, clientMessage);
        }
        clientMessage.set(mergeBatchSize);
        clientMessage.updateFrameLength();
        return clientMessage;
    }

    public static ClientMessage encodeRequest(String name, int backupCount, int asyncBackupCount, int timeToLiveSeconds, int maxIdleSeconds, String evictionPolicy, boolean readBackupData, String cacheDeserializedValues, String mergePolicy, String inMemoryFormat, Collection<ListenerConfigHolder> listenerConfigs, Collection<ListenerConfigHolder> partitionLostListenerConfigs, boolean statisticsEnabled, String quorumName, Data mapEvictionPolicy, String maxSizeConfigMaxSizePolicy, int maxSizeConfigSize, MapStoreConfigHolder mapStoreConfig, NearCacheConfigHolder nearCacheConfig, WanReplicationRef wanReplicationRef, Collection<MapIndexConfig> mapIndexConfigs, Collection<MapAttributeConfig> mapAttributeConfigs, Collection<QueryCacheConfigHolder> queryCacheConfigs, String partitioningStrategyClassName, Data partitioningStrategyImplementation, HotRestartConfig hotRestartConfig, int mergeBatchSize, int metadataPolicy) {
        boolean mapAttributeConfigs_isNull;
        boolean mapIndexConfigs_isNull;
        boolean wanReplicationRef_isNull;
        boolean nearCacheConfig_isNull;
        boolean mapStoreConfig_isNull;
        boolean mapEvictionPolicy_isNull;
        boolean partitionLostListenerConfigs_isNull;
        boolean listenerConfigs_isNull;
        int requiredDataSize = RequestParameters.calculateDataSize(name, backupCount, asyncBackupCount, timeToLiveSeconds, maxIdleSeconds, evictionPolicy, readBackupData, cacheDeserializedValues, mergePolicy, inMemoryFormat, listenerConfigs, partitionLostListenerConfigs, statisticsEnabled, quorumName, mapEvictionPolicy, maxSizeConfigMaxSizePolicy, maxSizeConfigSize, mapStoreConfig, nearCacheConfig, wanReplicationRef, mapIndexConfigs, mapAttributeConfigs, queryCacheConfigs, partitioningStrategyClassName, partitioningStrategyImplementation, hotRestartConfig, mergeBatchSize, metadataPolicy);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(REQUEST_TYPE.id());
        clientMessage.setRetryable(false);
        clientMessage.setAcquiresResource(false);
        clientMessage.setOperationName("DynamicConfig.addMapConfig");
        clientMessage.set(name);
        clientMessage.set(backupCount);
        clientMessage.set(asyncBackupCount);
        clientMessage.set(timeToLiveSeconds);
        clientMessage.set(maxIdleSeconds);
        clientMessage.set(evictionPolicy);
        clientMessage.set(readBackupData);
        clientMessage.set(cacheDeserializedValues);
        clientMessage.set(mergePolicy);
        clientMessage.set(inMemoryFormat);
        if (listenerConfigs == null) {
            listenerConfigs_isNull = true;
            clientMessage.set(listenerConfigs_isNull);
        } else {
            listenerConfigs_isNull = false;
            clientMessage.set(listenerConfigs_isNull);
            clientMessage.set(listenerConfigs.size());
            for (ListenerConfigHolder listenerConfigHolder : listenerConfigs) {
                ListenerConfigCodec.encode(listenerConfigHolder, clientMessage);
            }
        }
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
        clientMessage.set(statisticsEnabled);
        if (quorumName == null) {
            boolean bl = true;
            clientMessage.set(bl);
        } else {
            boolean bl = false;
            clientMessage.set(bl);
            clientMessage.set(quorumName);
        }
        if (mapEvictionPolicy == null) {
            mapEvictionPolicy_isNull = true;
            clientMessage.set(mapEvictionPolicy_isNull);
        } else {
            mapEvictionPolicy_isNull = false;
            clientMessage.set(mapEvictionPolicy_isNull);
            clientMessage.set(mapEvictionPolicy);
        }
        clientMessage.set(maxSizeConfigMaxSizePolicy);
        clientMessage.set(maxSizeConfigSize);
        if (mapStoreConfig == null) {
            mapStoreConfig_isNull = true;
            clientMessage.set(mapStoreConfig_isNull);
        } else {
            mapStoreConfig_isNull = false;
            clientMessage.set(mapStoreConfig_isNull);
            MapStoreConfigCodec.encode(mapStoreConfig, clientMessage);
        }
        if (nearCacheConfig == null) {
            nearCacheConfig_isNull = true;
            clientMessage.set(nearCacheConfig_isNull);
        } else {
            nearCacheConfig_isNull = false;
            clientMessage.set(nearCacheConfig_isNull);
            NearCacheConfigCodec.encode(nearCacheConfig, clientMessage);
        }
        if (wanReplicationRef == null) {
            wanReplicationRef_isNull = true;
            clientMessage.set(wanReplicationRef_isNull);
        } else {
            wanReplicationRef_isNull = false;
            clientMessage.set(wanReplicationRef_isNull);
            WanReplicationRefCodec.encode(wanReplicationRef, clientMessage);
        }
        if (mapIndexConfigs == null) {
            mapIndexConfigs_isNull = true;
            clientMessage.set(mapIndexConfigs_isNull);
        } else {
            mapIndexConfigs_isNull = false;
            clientMessage.set(mapIndexConfigs_isNull);
            clientMessage.set(mapIndexConfigs.size());
            for (MapIndexConfig mapIndexConfig : mapIndexConfigs) {
                MapIndexConfigCodec.encode(mapIndexConfig, clientMessage);
            }
        }
        if (mapAttributeConfigs == null) {
            mapAttributeConfigs_isNull = true;
            clientMessage.set(mapAttributeConfigs_isNull);
        } else {
            mapAttributeConfigs_isNull = false;
            clientMessage.set(mapAttributeConfigs_isNull);
            clientMessage.set(mapAttributeConfigs.size());
            for (MapAttributeConfig mapAttributeConfig : mapAttributeConfigs) {
                MapAttributeConfigCodec.encode(mapAttributeConfig, clientMessage);
            }
        }
        if (queryCacheConfigs == null) {
            boolean bl = true;
            clientMessage.set(bl);
        } else {
            boolean bl = false;
            clientMessage.set(bl);
            clientMessage.set(queryCacheConfigs.size());
            for (QueryCacheConfigHolder queryCacheConfigs_item : queryCacheConfigs) {
                QueryCacheConfigCodec.encode(queryCacheConfigs_item, clientMessage);
            }
        }
        if (partitioningStrategyClassName == null) {
            boolean bl = true;
            clientMessage.set(bl);
        } else {
            boolean bl = false;
            clientMessage.set(bl);
            clientMessage.set(partitioningStrategyClassName);
        }
        if (partitioningStrategyImplementation == null) {
            boolean partitioningStrategyImplementation_isNull = true;
            clientMessage.set(partitioningStrategyImplementation_isNull);
        } else {
            boolean partitioningStrategyImplementation_isNull = false;
            clientMessage.set(partitioningStrategyImplementation_isNull);
            clientMessage.set(partitioningStrategyImplementation);
        }
        if (hotRestartConfig == null) {
            boolean hotRestartConfig_isNull = true;
            clientMessage.set(hotRestartConfig_isNull);
        } else {
            boolean hotRestartConfig_isNull = false;
            clientMessage.set(hotRestartConfig_isNull);
            HotRestartConfigCodec.encode(hotRestartConfig, clientMessage);
        }
        clientMessage.set(mergeBatchSize);
        clientMessage.set(metadataPolicy);
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
        int backupCount = 0;
        parameters.backupCount = backupCount = clientMessage.getInt();
        int asyncBackupCount = 0;
        parameters.asyncBackupCount = asyncBackupCount = clientMessage.getInt();
        int timeToLiveSeconds = 0;
        parameters.timeToLiveSeconds = timeToLiveSeconds = clientMessage.getInt();
        int maxIdleSeconds = 0;
        parameters.maxIdleSeconds = maxIdleSeconds = clientMessage.getInt();
        String evictionPolicy = null;
        parameters.evictionPolicy = evictionPolicy = clientMessage.getStringUtf8();
        boolean readBackupData = false;
        parameters.readBackupData = readBackupData = clientMessage.getBoolean();
        String cacheDeserializedValues = null;
        parameters.cacheDeserializedValues = cacheDeserializedValues = clientMessage.getStringUtf8();
        String mergePolicy = null;
        parameters.mergePolicy = mergePolicy = clientMessage.getStringUtf8();
        String inMemoryFormat = null;
        parameters.inMemoryFormat = inMemoryFormat = clientMessage.getStringUtf8();
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
        boolean statisticsEnabled = false;
        parameters.statisticsEnabled = statisticsEnabled = clientMessage.getBoolean();
        String quorumName = null;
        boolean quorumName_isNull = clientMessage.getBoolean();
        if (!quorumName_isNull) {
            parameters.quorumName = quorumName = clientMessage.getStringUtf8();
        }
        Data mapEvictionPolicy = null;
        boolean mapEvictionPolicy_isNull = clientMessage.getBoolean();
        if (!mapEvictionPolicy_isNull) {
            parameters.mapEvictionPolicy = mapEvictionPolicy = clientMessage.getData();
        }
        String maxSizeConfigMaxSizePolicy = null;
        parameters.maxSizeConfigMaxSizePolicy = maxSizeConfigMaxSizePolicy = clientMessage.getStringUtf8();
        int maxSizeConfigSize = 0;
        parameters.maxSizeConfigSize = maxSizeConfigSize = clientMessage.getInt();
        MapStoreConfigHolder mapStoreConfig = null;
        boolean mapStoreConfig_isNull = clientMessage.getBoolean();
        if (!mapStoreConfig_isNull) {
            parameters.mapStoreConfig = mapStoreConfig = MapStoreConfigCodec.decode(clientMessage);
        }
        NearCacheConfigHolder nearCacheConfig = null;
        boolean nearCacheConfig_isNull = clientMessage.getBoolean();
        if (!nearCacheConfig_isNull) {
            parameters.nearCacheConfig = nearCacheConfig = NearCacheConfigCodec.decode(clientMessage);
        }
        WanReplicationRef wanReplicationRef = null;
        boolean wanReplicationRef_isNull = clientMessage.getBoolean();
        if (!wanReplicationRef_isNull) {
            parameters.wanReplicationRef = wanReplicationRef = WanReplicationRefCodec.decode(clientMessage);
        }
        ArrayList<MapIndexConfig> mapIndexConfigs = null;
        boolean mapIndexConfigs_isNull = clientMessage.getBoolean();
        if (!mapIndexConfigs_isNull) {
            int mapIndexConfigs_size = clientMessage.getInt();
            mapIndexConfigs = new ArrayList<MapIndexConfig>(mapIndexConfigs_size);
            for (int mapIndexConfigs_index = 0; mapIndexConfigs_index < mapIndexConfigs_size; ++mapIndexConfigs_index) {
                MapIndexConfig mapIndexConfigs_item = MapIndexConfigCodec.decode(clientMessage);
                mapIndexConfigs.add(mapIndexConfigs_item);
            }
            parameters.mapIndexConfigs = mapIndexConfigs;
        }
        ArrayList<MapAttributeConfig> mapAttributeConfigs = null;
        boolean mapAttributeConfigs_isNull = clientMessage.getBoolean();
        if (!mapAttributeConfigs_isNull) {
            int mapAttributeConfigs_size = clientMessage.getInt();
            mapAttributeConfigs = new ArrayList<MapAttributeConfig>(mapAttributeConfigs_size);
            for (int mapAttributeConfigs_index = 0; mapAttributeConfigs_index < mapAttributeConfigs_size; ++mapAttributeConfigs_index) {
                MapAttributeConfig mapAttributeConfigs_item = MapAttributeConfigCodec.decode(clientMessage);
                mapAttributeConfigs.add(mapAttributeConfigs_item);
            }
            parameters.mapAttributeConfigs = mapAttributeConfigs;
        }
        ArrayList<QueryCacheConfigHolder> queryCacheConfigs = null;
        boolean queryCacheConfigs_isNull = clientMessage.getBoolean();
        if (!queryCacheConfigs_isNull) {
            int queryCacheConfigs_size = clientMessage.getInt();
            queryCacheConfigs = new ArrayList<QueryCacheConfigHolder>(queryCacheConfigs_size);
            for (int queryCacheConfigs_index = 0; queryCacheConfigs_index < queryCacheConfigs_size; ++queryCacheConfigs_index) {
                QueryCacheConfigHolder queryCacheConfigs_item = QueryCacheConfigCodec.decode(clientMessage);
                queryCacheConfigs.add(queryCacheConfigs_item);
            }
            parameters.queryCacheConfigs = queryCacheConfigs;
        }
        String partitioningStrategyClassName = null;
        boolean partitioningStrategyClassName_isNull = clientMessage.getBoolean();
        if (!partitioningStrategyClassName_isNull) {
            parameters.partitioningStrategyClassName = partitioningStrategyClassName = clientMessage.getStringUtf8();
        }
        Data partitioningStrategyImplementation = null;
        boolean partitioningStrategyImplementation_isNull = clientMessage.getBoolean();
        if (!partitioningStrategyImplementation_isNull) {
            parameters.partitioningStrategyImplementation = partitioningStrategyImplementation = clientMessage.getData();
        }
        HotRestartConfig hotRestartConfig = null;
        boolean hotRestartConfig_isNull = clientMessage.getBoolean();
        if (!hotRestartConfig_isNull) {
            parameters.hotRestartConfig = hotRestartConfig = HotRestartConfigCodec.decode(clientMessage);
        }
        if (clientMessage.isComplete()) {
            return parameters;
        }
        int mergeBatchSize = 0;
        parameters.mergeBatchSize = mergeBatchSize = clientMessage.getInt();
        parameters.mergeBatchSizeExist = true;
        if (clientMessage.isComplete()) {
            return parameters;
        }
        int metadataPolicy = 0;
        parameters.metadataPolicy = metadataPolicy = clientMessage.getInt();
        parameters.metadataPolicyExist = true;
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
        public int backupCount;
        public int asyncBackupCount;
        public int timeToLiveSeconds;
        public int maxIdleSeconds;
        public String evictionPolicy;
        public boolean readBackupData;
        public String cacheDeserializedValues;
        public String mergePolicy;
        public String inMemoryFormat;
        public List<ListenerConfigHolder> listenerConfigs;
        public List<ListenerConfigHolder> partitionLostListenerConfigs;
        public boolean statisticsEnabled;
        public String quorumName;
        public Data mapEvictionPolicy;
        public String maxSizeConfigMaxSizePolicy;
        public int maxSizeConfigSize;
        public MapStoreConfigHolder mapStoreConfig;
        public NearCacheConfigHolder nearCacheConfig;
        public WanReplicationRef wanReplicationRef;
        public List<MapIndexConfig> mapIndexConfigs;
        public List<MapAttributeConfig> mapAttributeConfigs;
        public List<QueryCacheConfigHolder> queryCacheConfigs;
        public String partitioningStrategyClassName;
        public Data partitioningStrategyImplementation;
        public HotRestartConfig hotRestartConfig;
        public boolean mergeBatchSizeExist = false;
        public int mergeBatchSize;
        public boolean metadataPolicyExist = false;
        public int metadataPolicy;

        public static int calculateDataSize(String name, int backupCount, int asyncBackupCount, int timeToLiveSeconds, int maxIdleSeconds, String evictionPolicy, boolean readBackupData, String cacheDeserializedValues, String mergePolicy, String inMemoryFormat, Collection<ListenerConfigHolder> listenerConfigs, Collection<ListenerConfigHolder> partitionLostListenerConfigs, boolean statisticsEnabled, String quorumName, Data mapEvictionPolicy, String maxSizeConfigMaxSizePolicy, int maxSizeConfigSize, MapStoreConfigHolder mapStoreConfig, NearCacheConfigHolder nearCacheConfig, WanReplicationRef wanReplicationRef, Collection<MapIndexConfig> mapIndexConfigs, Collection<MapAttributeConfig> mapAttributeConfigs, Collection<QueryCacheConfigHolder> queryCacheConfigs, String partitioningStrategyClassName, Data partitioningStrategyImplementation, HotRestartConfig hotRestartConfig) {
            int dataSize = ClientMessage.HEADER_SIZE;
            dataSize += ParameterUtil.calculateDataSize(name);
            dataSize += 4;
            dataSize += 4;
            dataSize += 4;
            dataSize += 4;
            dataSize += ParameterUtil.calculateDataSize(evictionPolicy);
            ++dataSize;
            dataSize += ParameterUtil.calculateDataSize(cacheDeserializedValues);
            dataSize += ParameterUtil.calculateDataSize(mergePolicy);
            dataSize += ParameterUtil.calculateDataSize(inMemoryFormat);
            ++dataSize;
            if (listenerConfigs != null) {
                dataSize += 4;
                for (ListenerConfigHolder listenerConfigs_item : listenerConfigs) {
                    dataSize += ListenerConfigCodec.calculateDataSize(listenerConfigs_item);
                }
            }
            ++dataSize;
            if (partitionLostListenerConfigs != null) {
                dataSize += 4;
                for (ListenerConfigHolder partitionLostListenerConfigs_item : partitionLostListenerConfigs) {
                    dataSize += ListenerConfigCodec.calculateDataSize(partitionLostListenerConfigs_item);
                }
            }
            ++dataSize;
            ++dataSize;
            if (quorumName != null) {
                dataSize += ParameterUtil.calculateDataSize(quorumName);
            }
            ++dataSize;
            if (mapEvictionPolicy != null) {
                dataSize += ParameterUtil.calculateDataSize(mapEvictionPolicy);
            }
            dataSize += ParameterUtil.calculateDataSize(maxSizeConfigMaxSizePolicy);
            dataSize += 4;
            ++dataSize;
            if (mapStoreConfig != null) {
                dataSize += MapStoreConfigCodec.calculateDataSize(mapStoreConfig);
            }
            ++dataSize;
            if (nearCacheConfig != null) {
                dataSize += NearCacheConfigCodec.calculateDataSize(nearCacheConfig);
            }
            ++dataSize;
            if (wanReplicationRef != null) {
                dataSize += WanReplicationRefCodec.calculateDataSize(wanReplicationRef);
            }
            ++dataSize;
            if (mapIndexConfigs != null) {
                dataSize += 4;
                for (MapIndexConfig mapIndexConfigs_item : mapIndexConfigs) {
                    dataSize += MapIndexConfigCodec.calculateDataSize(mapIndexConfigs_item);
                }
            }
            ++dataSize;
            if (mapAttributeConfigs != null) {
                dataSize += 4;
                for (MapAttributeConfig mapAttributeConfigs_item : mapAttributeConfigs) {
                    dataSize += MapAttributeConfigCodec.calculateDataSize(mapAttributeConfigs_item);
                }
            }
            ++dataSize;
            if (queryCacheConfigs != null) {
                dataSize += 4;
                for (QueryCacheConfigHolder queryCacheConfigs_item : queryCacheConfigs) {
                    dataSize += QueryCacheConfigCodec.calculateDataSize(queryCacheConfigs_item);
                }
            }
            ++dataSize;
            if (partitioningStrategyClassName != null) {
                dataSize += ParameterUtil.calculateDataSize(partitioningStrategyClassName);
            }
            ++dataSize;
            if (partitioningStrategyImplementation != null) {
                dataSize += ParameterUtil.calculateDataSize(partitioningStrategyImplementation);
            }
            ++dataSize;
            if (hotRestartConfig != null) {
                dataSize += HotRestartConfigCodec.calculateDataSize(hotRestartConfig);
            }
            return dataSize;
        }

        public static int calculateDataSize(String name, int backupCount, int asyncBackupCount, int timeToLiveSeconds, int maxIdleSeconds, String evictionPolicy, boolean readBackupData, String cacheDeserializedValues, String mergePolicy, String inMemoryFormat, Collection<ListenerConfigHolder> listenerConfigs, Collection<ListenerConfigHolder> partitionLostListenerConfigs, boolean statisticsEnabled, String quorumName, Data mapEvictionPolicy, String maxSizeConfigMaxSizePolicy, int maxSizeConfigSize, MapStoreConfigHolder mapStoreConfig, NearCacheConfigHolder nearCacheConfig, WanReplicationRef wanReplicationRef, Collection<MapIndexConfig> mapIndexConfigs, Collection<MapAttributeConfig> mapAttributeConfigs, Collection<QueryCacheConfigHolder> queryCacheConfigs, String partitioningStrategyClassName, Data partitioningStrategyImplementation, HotRestartConfig hotRestartConfig, int mergeBatchSize) {
            int dataSize = ClientMessage.HEADER_SIZE;
            dataSize += ParameterUtil.calculateDataSize(name);
            dataSize += 4;
            dataSize += 4;
            dataSize += 4;
            dataSize += 4;
            dataSize += ParameterUtil.calculateDataSize(evictionPolicy);
            ++dataSize;
            dataSize += ParameterUtil.calculateDataSize(cacheDeserializedValues);
            dataSize += ParameterUtil.calculateDataSize(mergePolicy);
            dataSize += ParameterUtil.calculateDataSize(inMemoryFormat);
            ++dataSize;
            if (listenerConfigs != null) {
                dataSize += 4;
                for (ListenerConfigHolder listenerConfigs_item : listenerConfigs) {
                    dataSize += ListenerConfigCodec.calculateDataSize(listenerConfigs_item);
                }
            }
            ++dataSize;
            if (partitionLostListenerConfigs != null) {
                dataSize += 4;
                for (ListenerConfigHolder partitionLostListenerConfigs_item : partitionLostListenerConfigs) {
                    dataSize += ListenerConfigCodec.calculateDataSize(partitionLostListenerConfigs_item);
                }
            }
            ++dataSize;
            ++dataSize;
            if (quorumName != null) {
                dataSize += ParameterUtil.calculateDataSize(quorumName);
            }
            ++dataSize;
            if (mapEvictionPolicy != null) {
                dataSize += ParameterUtil.calculateDataSize(mapEvictionPolicy);
            }
            dataSize += ParameterUtil.calculateDataSize(maxSizeConfigMaxSizePolicy);
            dataSize += 4;
            ++dataSize;
            if (mapStoreConfig != null) {
                dataSize += MapStoreConfigCodec.calculateDataSize(mapStoreConfig);
            }
            ++dataSize;
            if (nearCacheConfig != null) {
                dataSize += NearCacheConfigCodec.calculateDataSize(nearCacheConfig);
            }
            ++dataSize;
            if (wanReplicationRef != null) {
                dataSize += WanReplicationRefCodec.calculateDataSize(wanReplicationRef);
            }
            ++dataSize;
            if (mapIndexConfigs != null) {
                dataSize += 4;
                for (MapIndexConfig mapIndexConfigs_item : mapIndexConfigs) {
                    dataSize += MapIndexConfigCodec.calculateDataSize(mapIndexConfigs_item);
                }
            }
            ++dataSize;
            if (mapAttributeConfigs != null) {
                dataSize += 4;
                for (MapAttributeConfig mapAttributeConfigs_item : mapAttributeConfigs) {
                    dataSize += MapAttributeConfigCodec.calculateDataSize(mapAttributeConfigs_item);
                }
            }
            ++dataSize;
            if (queryCacheConfigs != null) {
                dataSize += 4;
                for (QueryCacheConfigHolder queryCacheConfigs_item : queryCacheConfigs) {
                    dataSize += QueryCacheConfigCodec.calculateDataSize(queryCacheConfigs_item);
                }
            }
            ++dataSize;
            if (partitioningStrategyClassName != null) {
                dataSize += ParameterUtil.calculateDataSize(partitioningStrategyClassName);
            }
            ++dataSize;
            if (partitioningStrategyImplementation != null) {
                dataSize += ParameterUtil.calculateDataSize(partitioningStrategyImplementation);
            }
            ++dataSize;
            if (hotRestartConfig != null) {
                dataSize += HotRestartConfigCodec.calculateDataSize(hotRestartConfig);
            }
            return dataSize += 4;
        }

        public static int calculateDataSize(String name, int backupCount, int asyncBackupCount, int timeToLiveSeconds, int maxIdleSeconds, String evictionPolicy, boolean readBackupData, String cacheDeserializedValues, String mergePolicy, String inMemoryFormat, Collection<ListenerConfigHolder> listenerConfigs, Collection<ListenerConfigHolder> partitionLostListenerConfigs, boolean statisticsEnabled, String quorumName, Data mapEvictionPolicy, String maxSizeConfigMaxSizePolicy, int maxSizeConfigSize, MapStoreConfigHolder mapStoreConfig, NearCacheConfigHolder nearCacheConfig, WanReplicationRef wanReplicationRef, Collection<MapIndexConfig> mapIndexConfigs, Collection<MapAttributeConfig> mapAttributeConfigs, Collection<QueryCacheConfigHolder> queryCacheConfigs, String partitioningStrategyClassName, Data partitioningStrategyImplementation, HotRestartConfig hotRestartConfig, int mergeBatchSize, int metadataPolicy) {
            int dataSize = ClientMessage.HEADER_SIZE;
            dataSize += ParameterUtil.calculateDataSize(name);
            dataSize += 4;
            dataSize += 4;
            dataSize += 4;
            dataSize += 4;
            dataSize += ParameterUtil.calculateDataSize(evictionPolicy);
            ++dataSize;
            dataSize += ParameterUtil.calculateDataSize(cacheDeserializedValues);
            dataSize += ParameterUtil.calculateDataSize(mergePolicy);
            dataSize += ParameterUtil.calculateDataSize(inMemoryFormat);
            ++dataSize;
            if (listenerConfigs != null) {
                dataSize += 4;
                for (ListenerConfigHolder listenerConfigs_item : listenerConfigs) {
                    dataSize += ListenerConfigCodec.calculateDataSize(listenerConfigs_item);
                }
            }
            ++dataSize;
            if (partitionLostListenerConfigs != null) {
                dataSize += 4;
                for (ListenerConfigHolder partitionLostListenerConfigs_item : partitionLostListenerConfigs) {
                    dataSize += ListenerConfigCodec.calculateDataSize(partitionLostListenerConfigs_item);
                }
            }
            ++dataSize;
            ++dataSize;
            if (quorumName != null) {
                dataSize += ParameterUtil.calculateDataSize(quorumName);
            }
            ++dataSize;
            if (mapEvictionPolicy != null) {
                dataSize += ParameterUtil.calculateDataSize(mapEvictionPolicy);
            }
            dataSize += ParameterUtil.calculateDataSize(maxSizeConfigMaxSizePolicy);
            dataSize += 4;
            ++dataSize;
            if (mapStoreConfig != null) {
                dataSize += MapStoreConfigCodec.calculateDataSize(mapStoreConfig);
            }
            ++dataSize;
            if (nearCacheConfig != null) {
                dataSize += NearCacheConfigCodec.calculateDataSize(nearCacheConfig);
            }
            ++dataSize;
            if (wanReplicationRef != null) {
                dataSize += WanReplicationRefCodec.calculateDataSize(wanReplicationRef);
            }
            ++dataSize;
            if (mapIndexConfigs != null) {
                dataSize += 4;
                for (MapIndexConfig mapIndexConfigs_item : mapIndexConfigs) {
                    dataSize += MapIndexConfigCodec.calculateDataSize(mapIndexConfigs_item);
                }
            }
            ++dataSize;
            if (mapAttributeConfigs != null) {
                dataSize += 4;
                for (MapAttributeConfig mapAttributeConfigs_item : mapAttributeConfigs) {
                    dataSize += MapAttributeConfigCodec.calculateDataSize(mapAttributeConfigs_item);
                }
            }
            ++dataSize;
            if (queryCacheConfigs != null) {
                dataSize += 4;
                for (QueryCacheConfigHolder queryCacheConfigs_item : queryCacheConfigs) {
                    dataSize += QueryCacheConfigCodec.calculateDataSize(queryCacheConfigs_item);
                }
            }
            ++dataSize;
            if (partitioningStrategyClassName != null) {
                dataSize += ParameterUtil.calculateDataSize(partitioningStrategyClassName);
            }
            ++dataSize;
            if (partitioningStrategyImplementation != null) {
                dataSize += ParameterUtil.calculateDataSize(partitioningStrategyImplementation);
            }
            ++dataSize;
            if (hotRestartConfig != null) {
                dataSize += HotRestartConfigCodec.calculateDataSize(hotRestartConfig);
            }
            dataSize += 4;
            return dataSize += 4;
        }
    }
}

