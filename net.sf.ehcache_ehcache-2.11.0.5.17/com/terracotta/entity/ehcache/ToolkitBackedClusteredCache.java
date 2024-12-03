/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.terracotta.toolkit.Toolkit
 *  org.terracotta.toolkit.ToolkitObjectType
 *  org.terracotta.toolkit.builder.ToolkitCacheConfigBuilder
 *  org.terracotta.toolkit.collections.ToolkitMap
 *  org.terracotta.toolkit.internal.cache.ToolkitCacheInternal
 */
package com.terracotta.entity.ehcache;

import com.terracotta.entity.ClusteredEntityState;
import com.terracotta.entity.ehcache.ClusteredCache;
import com.terracotta.entity.ehcache.ClusteredCacheConfiguration;
import com.terracotta.entity.internal.ToolkitAwareEntity;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.terracotta.toolkit.Toolkit;
import org.terracotta.toolkit.ToolkitObjectType;
import org.terracotta.toolkit.builder.ToolkitCacheConfigBuilder;
import org.terracotta.toolkit.collections.ToolkitMap;
import org.terracotta.toolkit.internal.cache.ToolkitCacheInternal;

public class ToolkitBackedClusteredCache
implements ToolkitAwareEntity,
ClusteredCache {
    private static final long serialVersionUID = 1L;
    private final String cacheName;
    private final String toolkitCacheName;
    private final ClusteredCacheConfiguration configuration;
    private final ConcurrentMap<ToolkitObjectType, Set<String>> toolkitDSInfo;
    private final ConcurrentMap<String, Set<String>> keyRemoveInfo;
    private volatile ClusteredEntityState state = ClusteredEntityState.LIVE;
    private volatile transient Toolkit toolkit;

    public ToolkitBackedClusteredCache(String cacheName, ClusteredCacheConfiguration configuration, String toolkitCacheName) {
        this.cacheName = cacheName;
        this.toolkitCacheName = toolkitCacheName;
        this.configuration = configuration;
        this.toolkitDSInfo = new ConcurrentHashMap<ToolkitObjectType, Set<String>>();
        this.keyRemoveInfo = new ConcurrentHashMap<String, Set<String>>();
    }

    @Override
    public ClusteredCacheConfiguration getConfiguration() {
        return this.configuration;
    }

    @Override
    public ClusteredEntityState getState() {
        return this.state;
    }

    @Override
    public void setToolkit(Toolkit toolkit) {
        this.toolkit = toolkit;
    }

    @Override
    public String getName() {
        return this.cacheName;
    }

    @Override
    public long getSize() {
        return ((ToolkitCacheInternal)this.toolkit.getCache(this.toolkitCacheName, Serializable.class)).quickSize();
    }

    public void destroy() {
        Set values;
        block6: for (Map.Entry entry : this.toolkitDSInfo.entrySet()) {
            ToolkitObjectType type = (ToolkitObjectType)entry.getKey();
            values = (Set)entry.getValue();
            switch (type) {
                case LIST: {
                    for (String name : values) {
                        this.toolkit.getList(name, Serializable.class).destroy();
                    }
                    continue block6;
                }
                case MAP: {
                    for (String name : values) {
                        this.toolkit.getMap(name, String.class, Serializable.class).destroy();
                    }
                    continue block6;
                }
                case CACHE: {
                    for (String name : values) {
                        this.toolkit.getCache(name, Serializable.class).destroy();
                    }
                    continue block6;
                }
                case NOTIFIER: {
                    for (String name : values) {
                        this.toolkit.getNotifier(name, Serializable.class).destroy();
                    }
                    continue block6;
                }
                default: {
                    throw new IllegalStateException("got wrong ToolkitObjectType " + type);
                }
            }
        }
        for (Map.Entry entry : this.keyRemoveInfo.entrySet()) {
            String toolkitMapName = (String)entry.getKey();
            values = (Set)entry.getValue();
            ToolkitMap toolkitMap = this.toolkit.getMap(toolkitMapName, String.class, Serializable.class);
            for (String key : values) {
                toolkitMap.remove((Object)key);
            }
        }
        this.toolkit.getCache(this.toolkitCacheName, new ToolkitCacheConfigBuilder().localCacheEnabled(false).offheapEnabled(false).build(), Serializable.class).destroy();
    }

    public boolean addToolkitDSMetaInfo(ToolkitObjectType type, String dsName) {
        this.assertCacheAlive();
        HashSet<String> tmpValues = new HashSet<String>();
        tmpValues.add(dsName);
        Set oldValues = this.toolkitDSInfo.putIfAbsent(type, tmpValues);
        if (oldValues != null) {
            return oldValues.add(dsName);
        }
        return true;
    }

    public boolean addKeyRemoveInfo(String toolkitMapName, String keytoBeRemoved) {
        this.assertCacheAlive();
        HashSet<String> tmpValues = new HashSet<String>();
        tmpValues.add(keytoBeRemoved);
        Set oldValues = this.keyRemoveInfo.putIfAbsent(toolkitMapName, tmpValues);
        if (oldValues != null) {
            return oldValues.add(keytoBeRemoved);
        }
        return true;
    }

    private void assertCacheAlive() {
        if (this.state != ClusteredEntityState.LIVE) {
            throw new IllegalStateException(String.format("cache %s state is %s", new Object[]{this.cacheName, this.state}));
        }
    }

    public void markDestroyInProgress() {
        this.state = ClusteredEntityState.DESTROY_IN_PROGRESS;
    }

    public void alive() {
        this.state = ClusteredEntityState.LIVE;
    }
}

