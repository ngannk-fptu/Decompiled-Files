/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.config;

import com.hazelcast.cache.impl.merge.policy.CacheMergePolicyProvider;
import com.hazelcast.config.AbstractBasicConfig;
import com.hazelcast.config.CacheConfig;
import com.hazelcast.config.CacheSimpleConfig;
import com.hazelcast.config.CollectionConfig;
import com.hazelcast.config.Config;
import com.hazelcast.config.ConfigurationException;
import com.hazelcast.config.EndpointConfig;
import com.hazelcast.config.EvictionConfig;
import com.hazelcast.config.EvictionPolicy;
import com.hazelcast.config.InMemoryFormat;
import com.hazelcast.config.InvalidConfigurationException;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.MultiMapConfig;
import com.hazelcast.config.NativeMemoryConfig;
import com.hazelcast.config.NearCacheConfig;
import com.hazelcast.config.QueueConfig;
import com.hazelcast.config.ReplicatedMapConfig;
import com.hazelcast.config.RingbufferConfig;
import com.hazelcast.config.ScheduledExecutorConfig;
import com.hazelcast.config.ServerSocketEndpointConfig;
import com.hazelcast.config.WanPublisherConfig;
import com.hazelcast.config.WanReplicationConfig;
import com.hazelcast.config.cp.CPSubsystemConfig;
import com.hazelcast.instance.BuildInfoProvider;
import com.hazelcast.instance.EndpointQualifier;
import com.hazelcast.instance.ProtocolType;
import com.hazelcast.internal.config.MergePolicyValidator;
import com.hazelcast.internal.eviction.EvictionPolicyComparator;
import com.hazelcast.logging.ILogger;
import com.hazelcast.logging.Logger;
import com.hazelcast.replicatedmap.merge.MergePolicyProvider;
import com.hazelcast.spi.merge.SplitBrainMergePolicyProvider;
import com.hazelcast.spi.merge.SplitBrainMergeTypeProvider;
import com.hazelcast.spi.properties.GroupProperty;
import com.hazelcast.spi.properties.HazelcastProperties;
import com.hazelcast.spi.properties.HazelcastProperty;
import com.hazelcast.util.MutableInteger;
import com.hazelcast.util.Preconditions;
import com.hazelcast.util.StringUtil;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;

public final class ConfigValidator {
    private static final ILogger LOGGER = Logger.getLogger(ConfigValidator.class);
    private static final EnumSet<EvictionConfig.MaxSizePolicy> SUPPORTED_ON_HEAP_NEAR_CACHE_MAXSIZE_POLICIES = EnumSet.of(EvictionConfig.MaxSizePolicy.ENTRY_COUNT);
    private static final EnumSet<EvictionPolicy> SUPPORTED_EVICTION_POLICIES = EnumSet.of(EvictionPolicy.LRU, EvictionPolicy.LFU);

    private ConfigValidator() {
    }

    public static void checkMapConfig(MapConfig mapConfig, com.hazelcast.map.merge.MergePolicyProvider mergePolicyProvider) {
        ConfigValidator.checkNotNativeWhenOpenSource(mapConfig.getInMemoryFormat());
        MergePolicyValidator.checkMapMergePolicy(mapConfig, mergePolicyProvider);
        ConfigValidator.logIgnoredConfig(mapConfig);
    }

    private static void logIgnoredConfig(MapConfig mapConfig) {
        if (mapConfig.getMinEvictionCheckMillis() != 100L || mapConfig.getEvictionPercentage() != 25) {
            LOGGER.warning("As of Hazelcast version 3.7 `minEvictionCheckMillis` and `evictionPercentage` are deprecated due to a change of the eviction mechanism. The new eviction mechanism uses a probabilistic algorithm based on sampling. Please see documentation for further details.");
        }
    }

    public static void checkAdvancedNetworkConfig(Config config) {
        if (!config.getAdvancedNetworkConfig().isEnabled()) {
            return;
        }
        EnumMap<ProtocolType, MutableInteger> serverSocketsPerProtocolType = new EnumMap<ProtocolType, MutableInteger>(ProtocolType.class);
        for (ProtocolType protocolType : ProtocolType.values()) {
            serverSocketsPerProtocolType.put(protocolType, new MutableInteger());
        }
        Map<EndpointQualifier, EndpointConfig> endpointConfigs = config.getAdvancedNetworkConfig().getEndpointConfigs();
        for (EndpointConfig endpointConfig : endpointConfigs.values()) {
            if (!(endpointConfig instanceof ServerSocketEndpointConfig)) continue;
            ((MutableInteger)serverSocketsPerProtocolType.get((Object)endpointConfig.getProtocolType())).getAndInc();
        }
        ProtocolType[] protocolTypeArray = ProtocolType.values();
        int n = protocolTypeArray.length;
        for (int protocolType = 0; protocolType < n; ++protocolType) {
            ProtocolType protocolType2;
            int serverSocketCount = ((MutableInteger)serverSocketsPerProtocolType.get((Object)((Object)protocolType2))).value;
            protocolType2 = protocolTypeArray[protocolType];
            if (serverSocketCount <= protocolType2.getServerSocketCardinality()) continue;
            throw new InvalidConfigurationException(String.format("Protocol type %s allows definition of up to %d server sockets but %d were configured", new Object[]{protocolType2, protocolType2.getServerSocketCardinality(), serverSocketCount}));
        }
        if (((MutableInteger)serverSocketsPerProtocolType.get((Object)((Object)ProtocolType.MEMBER))).value != 1) {
            throw new InvalidConfigurationException("A member-server-socket-endpoint configuration is required for the clusterto form.");
        }
        HazelcastProperties props = new HazelcastProperties(config);
        if ((props.getBoolean(GroupProperty.REST_ENABLED) || props.getBoolean(GroupProperty.HTTP_HEALTHCHECK_ENABLED)) && ((MutableInteger)serverSocketsPerProtocolType.get((Object)((Object)ProtocolType.REST))).value != 1) {
            throw new InvalidConfigurationException("`hazelcast.rest.enabled` and/or `hazelcast.http.healthcheck.enabled` properties are enabled, without a rest-server-socket-endpoint");
        }
        if (props.getBoolean(GroupProperty.MEMCACHE_ENABLED) && ((MutableInteger)serverSocketsPerProtocolType.get((Object)((Object)ProtocolType.REST))).value != 1) {
            throw new InvalidConfigurationException("`hazelcast.memcache.enabled` property is enabled, without a memcache-server-socket-endpoint");
        }
        for (WanReplicationConfig wanReplicationConfig : config.getWanReplicationConfigs().values()) {
            for (WanPublisherConfig wanPublisherConfig : wanReplicationConfig.getWanPublisherConfigs()) {
                EndpointQualifier qualifier;
                if (wanPublisherConfig.getEndpoint() == null || endpointConfigs.get(qualifier = EndpointQualifier.resolve(ProtocolType.WAN, wanPublisherConfig.getEndpoint())) != null) continue;
                throw new InvalidConfigurationException(String.format("WAN publisher config for group name '%s' requires an wan-endpoint config with identifier '%s' but none was found", wanPublisherConfig.getGroupName(), wanPublisherConfig.getEndpoint()));
            }
        }
    }

    public static void checkNearCacheConfig(String mapName, NearCacheConfig nearCacheConfig, NativeMemoryConfig nativeMemoryConfig, boolean isClient) {
        ConfigValidator.checkNotNativeWhenOpenSource(nearCacheConfig.getInMemoryFormat());
        ConfigValidator.checkLocalUpdatePolicy(mapName, nearCacheConfig.getLocalUpdatePolicy());
        ConfigValidator.checkEvictionConfig(nearCacheConfig.getEvictionConfig(), true);
        ConfigValidator.checkOnHeapNearCacheMaxSizePolicy(nearCacheConfig);
        ConfigValidator.checkNearCacheNativeMemoryConfig(nearCacheConfig.getInMemoryFormat(), nativeMemoryConfig, BuildInfoProvider.getBuildInfo().isEnterprise());
        if (isClient && nearCacheConfig.isCacheLocalEntries()) {
            throw new IllegalArgumentException("The Near Cache option `cache-local-entries` is not supported in client configurations.");
        }
        ConfigValidator.checkPreloaderConfig(nearCacheConfig, isClient);
    }

    private static void checkLocalUpdatePolicy(String mapName, NearCacheConfig.LocalUpdatePolicy localUpdatePolicy) {
        if (localUpdatePolicy != NearCacheConfig.LocalUpdatePolicy.INVALIDATE) {
            throw new IllegalArgumentException(String.format("Wrong `local-update-policy` option is selected for `%s` map Near Cache. Only `%s` option is supported but found `%s`", new Object[]{mapName, NearCacheConfig.LocalUpdatePolicy.INVALIDATE, localUpdatePolicy}));
        }
    }

    public static void checkEvictionConfig(EvictionConfig evictionConfig, boolean isNearCache) {
        if (evictionConfig == null) {
            throw new IllegalArgumentException("Eviction config cannot be null!");
        }
        EvictionPolicy evictionPolicy = evictionConfig.getEvictionPolicy();
        String comparatorClassName = evictionConfig.getComparatorClassName();
        EvictionPolicyComparator comparator = evictionConfig.getComparator();
        ConfigValidator.checkEvictionConfig(evictionPolicy, comparatorClassName, comparator, isNearCache);
    }

    private static void checkOnHeapNearCacheMaxSizePolicy(NearCacheConfig nearCacheConfig) {
        InMemoryFormat inMemoryFormat = nearCacheConfig.getInMemoryFormat();
        if (inMemoryFormat == InMemoryFormat.NATIVE) {
            return;
        }
        EvictionConfig.MaxSizePolicy maxSizePolicy = nearCacheConfig.getEvictionConfig().getMaximumSizePolicy();
        if (!SUPPORTED_ON_HEAP_NEAR_CACHE_MAXSIZE_POLICIES.contains((Object)maxSizePolicy)) {
            throw new IllegalArgumentException(String.format("Near Cache maximum size policy %s cannot be used with %s storage. Supported maximum size policies are: %s", new Object[]{maxSizePolicy, inMemoryFormat, SUPPORTED_ON_HEAP_NEAR_CACHE_MAXSIZE_POLICIES}));
        }
    }

    static void checkNearCacheNativeMemoryConfig(InMemoryFormat inMemoryFormat, NativeMemoryConfig nativeMemoryConfig, boolean isEnterprise) {
        if (!isEnterprise) {
            return;
        }
        if (inMemoryFormat != InMemoryFormat.NATIVE) {
            return;
        }
        if (nativeMemoryConfig != null && nativeMemoryConfig.isEnabled()) {
            return;
        }
        throw new IllegalArgumentException("Enable native memory config to use NATIVE in-memory-format for Near Cache");
    }

    public static void checkEvictionConfig(EvictionPolicy evictionPolicy, String comparatorClassName, Object comparator, boolean isNearCache) {
        if (comparatorClassName != null && comparator != null) {
            throw new IllegalArgumentException("Only one of the `comparator class name` and `comparator` can be configured in the eviction configuration!");
        }
        if (!isNearCache && !SUPPORTED_EVICTION_POLICIES.contains((Object)evictionPolicy)) {
            if (StringUtil.isNullOrEmpty(comparatorClassName) && comparator == null) {
                String msg = String.format("Eviction policy `%s` is not supported. Either you can provide a custom one or can use one of the supported: %s.", new Object[]{evictionPolicy, SUPPORTED_EVICTION_POLICIES});
                throw new IllegalArgumentException(msg);
            }
        } else if (evictionPolicy != EvictionConfig.DEFAULT_EVICTION_POLICY) {
            if (!StringUtil.isNullOrEmpty(comparatorClassName)) {
                throw new IllegalArgumentException("Only one of the `eviction policy` and `comparator class name` can be configured!");
            }
            if (comparator != null) {
                throw new IllegalArgumentException("Only one of the `eviction policy` and `comparator` can be configured!");
            }
        }
    }

    public static void checkCacheConfig(CacheSimpleConfig cacheSimpleConfig, CacheMergePolicyProvider mergePolicyProvider) {
        ConfigValidator.checkCacheConfig(cacheSimpleConfig.getInMemoryFormat(), cacheSimpleConfig.getEvictionConfig(), cacheSimpleConfig.getMergePolicy(), cacheSimpleConfig, mergePolicyProvider);
    }

    public static void checkCacheConfig(CacheConfig cacheConfig, CacheMergePolicyProvider mergePolicyProvider) {
        ConfigValidator.checkCacheConfig(cacheConfig.getInMemoryFormat(), cacheConfig.getEvictionConfig(), cacheConfig.getMergePolicy(), cacheConfig, mergePolicyProvider);
    }

    public static void checkCacheConfig(InMemoryFormat inMemoryFormat, EvictionConfig evictionConfig, String mergePolicyClassname, SplitBrainMergeTypeProvider mergeTypeProvider, CacheMergePolicyProvider mergePolicyProvider) {
        ConfigValidator.checkNotNativeWhenOpenSource(inMemoryFormat);
        ConfigValidator.checkEvictionConfig(inMemoryFormat, evictionConfig);
        MergePolicyValidator.checkCacheMergePolicy(mergePolicyClassname, mergeTypeProvider, mergePolicyProvider);
    }

    static void checkEvictionConfig(InMemoryFormat inMemoryFormat, EvictionConfig evictionConfig) {
        EvictionConfig.MaxSizePolicy maxSizePolicy;
        if (inMemoryFormat == InMemoryFormat.NATIVE && (maxSizePolicy = evictionConfig.getMaximumSizePolicy()) == EvictionConfig.MaxSizePolicy.ENTRY_COUNT) {
            throw new IllegalArgumentException("Invalid max-size policy (" + (Object)((Object)maxSizePolicy) + ") for NATIVE in-memory format! Only " + (Object)((Object)EvictionConfig.MaxSizePolicy.USED_NATIVE_MEMORY_SIZE) + ", " + (Object)((Object)EvictionConfig.MaxSizePolicy.USED_NATIVE_MEMORY_PERCENTAGE) + ", " + (Object)((Object)EvictionConfig.MaxSizePolicy.FREE_NATIVE_MEMORY_SIZE) + ", " + (Object)((Object)EvictionConfig.MaxSizePolicy.FREE_NATIVE_MEMORY_PERCENTAGE) + " are supported.");
        }
    }

    public static void checkReplicatedMapConfig(ReplicatedMapConfig replicatedMapConfig, MergePolicyProvider mergePolicyProvider) {
        MergePolicyValidator.checkReplicatedMapMergePolicy(replicatedMapConfig, mergePolicyProvider);
    }

    public static void checkMultiMapConfig(MultiMapConfig multiMapConfig, SplitBrainMergePolicyProvider mergePolicyProvider) {
        MergePolicyValidator.checkMergePolicy(multiMapConfig, mergePolicyProvider, multiMapConfig.getMergePolicyConfig().getPolicy());
    }

    public static void checkQueueConfig(QueueConfig queueConfig, SplitBrainMergePolicyProvider mergePolicyProvider) {
        MergePolicyValidator.checkMergePolicy(queueConfig, mergePolicyProvider, queueConfig.getMergePolicyConfig().getPolicy());
    }

    public static void checkCollectionConfig(CollectionConfig collectionConfig, SplitBrainMergePolicyProvider mergePolicyProvider) {
        MergePolicyValidator.checkMergePolicy(collectionConfig, mergePolicyProvider, collectionConfig.getMergePolicyConfig().getPolicy());
    }

    public static void checkRingbufferConfig(RingbufferConfig ringbufferConfig, SplitBrainMergePolicyProvider mergePolicyProvider) {
        MergePolicyValidator.checkMergePolicy(ringbufferConfig, mergePolicyProvider, ringbufferConfig.getMergePolicyConfig().getPolicy());
    }

    public static <C extends AbstractBasicConfig> void checkBasicConfig(C basicConfig, SplitBrainMergePolicyProvider mergePolicyProvider) {
        MergePolicyValidator.checkMergePolicy(basicConfig, mergePolicyProvider, basicConfig.getMergePolicyConfig().getPolicy());
    }

    public static void checkScheduledExecutorConfig(ScheduledExecutorConfig scheduledExecutorConfig, SplitBrainMergePolicyProvider mergePolicyProvider) {
        String mergePolicyClassName = scheduledExecutorConfig.getMergePolicyConfig().getPolicy();
        MergePolicyValidator.checkMergePolicy(scheduledExecutorConfig, mergePolicyProvider, mergePolicyClassName);
    }

    public static void checkCPSubsystemConfig(CPSubsystemConfig config) {
        Preconditions.checkTrue(config.getGroupSize() <= config.getCPMemberCount(), "The group size parameter cannot be bigger than the number of the CP member count");
        Preconditions.checkTrue(config.getSessionTimeToLiveSeconds() > config.getSessionHeartbeatIntervalSeconds(), "Session TTL must be greater than session heartbeat interval!");
        Preconditions.checkTrue(config.getMissingCPMemberAutoRemovalSeconds() == 0 || config.getSessionTimeToLiveSeconds() <= config.getMissingCPMemberAutoRemovalSeconds(), "Session TTL must be smaller than or equal to missing CP member auto-removal seconds!");
    }

    private static void checkNotNativeWhenOpenSource(InMemoryFormat inMemoryFormat) {
        if (inMemoryFormat == InMemoryFormat.NATIVE && !BuildInfoProvider.getBuildInfo().isEnterprise()) {
            throw new IllegalArgumentException("NATIVE storage format is supported in Hazelcast Enterprise only. Make sure you have Hazelcast Enterprise JARs on your classpath!");
        }
    }

    private static void checkPreloaderConfig(NearCacheConfig nearCacheConfig, boolean isClient) {
        if (!isClient && nearCacheConfig.getPreloaderConfig().isEnabled()) {
            throw new IllegalArgumentException("The Near Cache pre-loader is just available on Hazelcast clients!");
        }
    }

    public static void ensurePropertyNotConfigured(HazelcastProperties properties, HazelcastProperty hazelcastProperty) throws ConfigurationException {
        if (properties.containsKey(hazelcastProperty)) {
            throw new ConfigurationException("Service start failed. The legacy property " + hazelcastProperty.getName() + " is provided together with new Config object. Remove the property from your configuration to fix this issue.");
        }
    }

    public static boolean checkAndLogPropertyDeprecated(HazelcastProperties properties, HazelcastProperty hazelcastProperty) {
        if (properties.containsKey(hazelcastProperty)) {
            LOGGER.warning("Property " + hazelcastProperty.getName() + " is deprecated. Use configuration object/element instead.");
            return properties.getBoolean(hazelcastProperty);
        }
        return false;
    }
}

