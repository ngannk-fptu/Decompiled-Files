/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl;

import com.hazelcast.config.PartitioningStrategyConfig;
import com.hazelcast.core.PartitioningStrategy;
import com.hazelcast.nio.ClassLoaderUtil;
import com.hazelcast.util.ExceptionUtil;
import java.util.concurrent.ConcurrentHashMap;

public final class PartitioningStrategyFactory {
    final ConcurrentHashMap<String, PartitioningStrategy> cache = new ConcurrentHashMap();
    private final ClassLoader configClassLoader;

    public PartitioningStrategyFactory(ClassLoader configClassLoader) {
        this.configClassLoader = configClassLoader;
    }

    public PartitioningStrategy getPartitioningStrategy(String mapName, PartitioningStrategyConfig config) {
        PartitioningStrategy strategy = null;
        if (config != null && (strategy = config.getPartitioningStrategy()) == null) {
            if (this.cache.containsKey(mapName)) {
                strategy = this.cache.get(mapName);
            } else if (config.getPartitioningStrategyClass() != null) {
                try {
                    strategy = (PartitioningStrategy)ClassLoaderUtil.newInstance(this.configClassLoader, config.getPartitioningStrategyClass());
                    this.cache.put(mapName, strategy);
                }
                catch (Exception e) {
                    throw ExceptionUtil.rethrow(e);
                }
            }
        }
        return strategy;
    }

    public void removePartitioningStrategyFromCache(String mapName) {
        this.cache.remove(mapName);
    }
}

