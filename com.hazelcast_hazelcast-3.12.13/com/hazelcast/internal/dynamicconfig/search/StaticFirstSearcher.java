/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.hazelcast.internal.dynamicconfig.search;

import com.hazelcast.config.Config;
import com.hazelcast.config.ConfigPatternMatcher;
import com.hazelcast.internal.config.ConfigUtils;
import com.hazelcast.internal.dynamicconfig.ConfigurationService;
import com.hazelcast.internal.dynamicconfig.search.ConfigSupplier;
import com.hazelcast.internal.dynamicconfig.search.Searcher;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.partition.strategy.StringPartitioningStrategy;
import java.util.Map;
import javax.annotation.Nonnull;

class StaticFirstSearcher<T extends IdentifiedDataSerializable>
implements Searcher<T> {
    private final ConfigurationService configurationService;
    private final Config staticConfig;
    private final ConfigPatternMatcher configPatternMatcher;

    StaticFirstSearcher(ConfigurationService configurationService, Config staticConfig, ConfigPatternMatcher configPatternMatcher) {
        this.configurationService = configurationService;
        this.staticConfig = staticConfig;
        this.configPatternMatcher = configPatternMatcher;
    }

    @Override
    public T getConfig(@Nonnull String name, String fallbackName, @Nonnull ConfigSupplier<T> configSupplier) {
        String baseName = StringPartitioningStrategy.getBaseName(name);
        Map<String, T> staticCacheConfigs = configSupplier.getStaticConfigs(this.staticConfig);
        IdentifiedDataSerializable config = (IdentifiedDataSerializable)ConfigUtils.lookupByPattern(this.configPatternMatcher, staticCacheConfigs, baseName);
        if (config == null) {
            config = configSupplier.getDynamicConfig(this.configurationService, baseName);
        }
        if (config == null && fallbackName != null) {
            config = configSupplier.getStaticConfig(this.staticConfig, fallbackName);
        }
        return (T)config;
    }
}

