/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.terracotta.toolkit.cache.ToolkitCache
 *  org.terracotta.toolkit.config.Configuration
 *  org.terracotta.toolkit.events.ToolkitNotificationEvent
 *  org.terracotta.toolkit.events.ToolkitNotificationListener
 *  org.terracotta.toolkit.events.ToolkitNotifier
 *  org.terracotta.toolkit.internal.cache.ToolkitCacheInternal
 */
package org.terracotta.modules.ehcache.store;

import java.io.Serializable;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.CacheConfigurationListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terracotta.modules.ehcache.store.CacheConfigChangeNotificationMsg;
import org.terracotta.toolkit.cache.ToolkitCache;
import org.terracotta.toolkit.config.Configuration;
import org.terracotta.toolkit.events.ToolkitNotificationEvent;
import org.terracotta.toolkit.events.ToolkitNotificationListener;
import org.terracotta.toolkit.events.ToolkitNotifier;
import org.terracotta.toolkit.internal.cache.ToolkitCacheInternal;

public class CacheConfigChangeBridge
implements CacheConfigurationListener,
ToolkitNotificationListener {
    private static final Logger LOG = LoggerFactory.getLogger(CacheConfigChangeBridge.class);
    private final ToolkitNotifier notifier;
    private final ToolkitCache backend;
    private final CacheConfiguration cacheConfiguration;
    private final String fullyQualifiedEhcacheName;

    public CacheConfigChangeBridge(String fullyQualifiedEhcacheName, ToolkitCacheInternal backend, ToolkitNotifier<CacheConfigChangeNotificationMsg> notifier, CacheConfiguration cacheConfiguration) {
        this.cacheConfiguration = cacheConfiguration;
        this.fullyQualifiedEhcacheName = fullyQualifiedEhcacheName;
        this.backend = backend;
        this.notifier = notifier;
    }

    public void connectConfigs() {
        this.initializeFromCluster();
        this.cacheConfiguration.addConfigurationListener(this);
        this.notifier.addNotificationListener((ToolkitNotificationListener)this);
    }

    public void disconnectConfigs() {
        this.cacheConfiguration.removeConfigurationListener(this);
        this.notifier.removeNotificationListener((ToolkitNotificationListener)this);
    }

    private void initializeFromCluster() {
        Configuration clusterConfig = this.backend.getConfiguration();
        if (clusterConfig.hasField("maxTotalCount")) {
            this.cacheConfiguration.internalSetMaxEntriesInCache(CacheConfigChangeBridge.mapTotalCountToMaxEntriesInCache(clusterConfig.getInt("maxTotalCount")));
        }
        if (clusterConfig.hasField("maxTTLSeconds") || clusterConfig.hasField("maxTTISeconds")) {
            int ttl;
            int tti = clusterConfig.hasField("maxTTISeconds") ? clusterConfig.getInt("maxTTISeconds") : 0;
            int n = ttl = clusterConfig.hasField("maxTTLSeconds") ? clusterConfig.getInt("maxTTLSeconds") : 0;
            if (tti != 0 || ttl != 0) {
                this.cacheConfiguration.internalSetEternal(false);
                this.cacheConfiguration.internalSetTimeToIdle(tti);
                this.cacheConfiguration.internalSetTimeToLive(ttl);
            } else {
                this.cacheConfiguration.internalSetTimeToIdle(0L);
                this.cacheConfiguration.internalSetTimeToLive(0L);
            }
        }
        if (clusterConfig.hasField("maxCountLocalHeap")) {
            this.cacheConfiguration.internalSetMemCapacity(clusterConfig.getInt("maxCountLocalHeap"));
        }
        if (clusterConfig.hasField("maxBytesLocalHeap")) {
            this.cacheConfiguration.internalSetMemCapacityInBytes(clusterConfig.getLong("maxBytesLocalHeap"));
        }
        if (clusterConfig.hasField("offheapEnabled")) {
            this.cacheConfiguration.internalSetOverflowToOffheap(clusterConfig.getBoolean("offheapEnabled"));
        }
        if (clusterConfig.hasField("maxBytesLocalOffHeap")) {
            this.cacheConfiguration.internalSetMaxBytesLocalOffheap(clusterConfig.getLong("maxBytesLocalOffHeap"));
        }
    }

    private void change(DynamicConfigType type, Serializable newValue, boolean notifyRemote) {
        this.backend.setConfigField(type.getToolkitConfigName(), newValue);
        if (notifyRemote) {
            this.notifier.notifyListeners((Object)new CacheConfigChangeNotificationMsg(this.fullyQualifiedEhcacheName, type.getToolkitConfigName(), newValue));
        }
    }

    @Override
    public void timeToIdleChanged(long oldTimeToIdle, long newTimeToIdle) {
        this.change(DynamicConfigType.MAX_TTI_SECONDS, Integer.valueOf((int)newTimeToIdle), true);
    }

    @Override
    public void timeToLiveChanged(long oldTimeToLive, long newTimeToLive) {
        this.change(DynamicConfigType.MAX_TTL_SECONDS, Integer.valueOf((int)newTimeToLive), true);
    }

    @Override
    public void diskCapacityChanged(int oldCapacity, int newCapacity) {
        throw new IllegalStateException("Disk capacity should not change for terracotta clustered caches");
    }

    @Override
    public void maxEntriesInCacheChanged(long oldCapacity, long newCapacity) {
        if (newCapacity > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Values greater than Integer.MAX_VALUE are not currently supported.");
        }
        this.change(DynamicConfigType.MAX_TOTAL_COUNT, Integer.valueOf(CacheConfigChangeBridge.mapMaxEntriesInCacheToTotalCount((int)newCapacity)), true);
    }

    @Override
    public void memoryCapacityChanged(int oldCapacity, int newCapacity) {
        this.change(DynamicConfigType.MAX_COUNT_LOCAL_HEAP, Integer.valueOf(newCapacity), false);
    }

    @Override
    public void maxBytesLocalHeapChanged(long oldValue, long newValue) {
        this.change(DynamicConfigType.MAX_BYTES_LOCAL_HEAP, Long.valueOf(newValue), false);
    }

    @Override
    public void maxBytesLocalDiskChanged(long oldValue, long newValue) {
    }

    @Override
    public void loggingChanged(boolean oldValue, boolean newValue) {
    }

    @Override
    public void registered(CacheConfiguration config) {
    }

    @Override
    public void deregistered(CacheConfiguration config) {
    }

    public void onNotification(ToolkitNotificationEvent event) {
        if (this.shouldProcessNotification(event)) {
            this.processConfigChangeNotification((CacheConfigChangeNotificationMsg)event.getMessage());
        } else {
            LOG.warn("Ignoring uninterested notification - " + event);
        }
    }

    private void processConfigChangeNotification(CacheConfigChangeNotificationMsg notification) {
        try {
            DynamicConfigType type = DynamicConfigType.getTypeFromToolkitConfigName(notification.getToolkitConfigName());
            Serializable newValue = notification.getNewValue();
            switch (type) {
                case MAX_TTI_SECONDS: {
                    this.cacheConfiguration.internalSetTimeToIdle(CacheConfigChangeBridge.getLong(newValue));
                    break;
                }
                case MAX_TTL_SECONDS: {
                    this.cacheConfiguration.internalSetTimeToLive(CacheConfigChangeBridge.getLong(newValue));
                    break;
                }
                case MAX_TOTAL_COUNT: {
                    this.cacheConfiguration.internalSetMaxEntriesInCache(CacheConfigChangeBridge.mapTotalCountToMaxEntriesInCache(CacheConfigChangeBridge.getInt(newValue)));
                    break;
                }
                case MAX_COUNT_LOCAL_HEAP: {
                    this.cacheConfiguration.internalSetMemCapacity(CacheConfigChangeBridge.getInt(newValue));
                    break;
                }
                case MAX_BYTES_LOCAL_HEAP: {
                    this.cacheConfiguration.internalSetMemCapacityInBytes(CacheConfigChangeBridge.getLong(newValue));
                }
            }
        }
        catch (IllegalArgumentException e) {
            LOG.warn("Notification will be ignored. Caught IllegalArgumentException while processing notification: " + notification + ", exception: " + e.getMessage());
        }
    }

    private boolean shouldProcessNotification(ToolkitNotificationEvent event) {
        return event.getMessage() instanceof CacheConfigChangeNotificationMsg && ((CacheConfigChangeNotificationMsg)event.getMessage()).getFullyQualifiedEhcacheName().equals(this.fullyQualifiedEhcacheName);
    }

    private static long getLong(Object newValue) {
        if (newValue instanceof Integer) {
            return ((Integer)newValue).intValue();
        }
        if (newValue instanceof Long) {
            return (Long)newValue;
        }
        throw new IllegalArgumentException("Expected long value but got: " + newValue);
    }

    private static int getInt(Object newValue) {
        if (newValue instanceof Integer) {
            return (Integer)newValue;
        }
        throw new IllegalArgumentException("Expected int value but got: " + newValue);
    }

    public static int mapMaxEntriesInCacheToTotalCount(int maxEntriesInCache) {
        if (maxEntriesInCache == 0) {
            return -1;
        }
        return maxEntriesInCache;
    }

    private static int mapTotalCountToMaxEntriesInCache(int totalCount) {
        if (totalCount == -1) {
            return 0;
        }
        return totalCount;
    }

    private static enum DynamicConfigType {
        MAX_TOTAL_COUNT("maxTotalCount"),
        MAX_COUNT_LOCAL_HEAP("maxCountLocalHeap"),
        MAX_BYTES_LOCAL_HEAP("maxBytesLocalHeap"),
        MAX_TTI_SECONDS("maxTTISeconds"),
        MAX_TTL_SECONDS("maxTTLSeconds");

        private final String toolkitConfigName;

        private DynamicConfigType(String toolkitConfigName) {
            this.toolkitConfigName = toolkitConfigName;
        }

        public String getToolkitConfigName() {
            return this.toolkitConfigName;
        }

        public static DynamicConfigType getTypeFromToolkitConfigName(String toolkitName) {
            for (DynamicConfigType type : DynamicConfigType.values()) {
                if (!type.getToolkitConfigName().equals(toolkitName)) continue;
                return type;
            }
            throw new IllegalArgumentException("Unknown toolkit config name - " + toolkitName);
        }
    }
}

