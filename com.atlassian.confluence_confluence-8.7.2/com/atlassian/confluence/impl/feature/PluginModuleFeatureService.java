/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginAccessor
 *  com.google.common.collect.ImmutableSet
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.feature;

import com.atlassian.confluence.plugin.descriptor.FeatureModuleDescriptor;
import com.atlassian.confluence.setup.settings.FeatureService;
import com.atlassian.plugin.PluginAccessor;
import com.google.common.collect.ImmutableSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Deprecated(forRemoval=true)
public class PluginModuleFeatureService
implements FeatureService {
    private static final Logger log = LoggerFactory.getLogger(PluginModuleFeatureService.class);
    private final PluginAccessor pluginAccessor;

    public PluginModuleFeatureService(PluginAccessor pluginAccessor) {
        this.pluginAccessor = Objects.requireNonNull(pluginAccessor);
    }

    @Override
    public Set<String> getRegisteredFeatures() {
        return (Set)this.pluginAccessor.getEnabledModuleDescriptorsByClass(FeatureModuleDescriptor.class).stream().flatMap(this::getFeatureKeys).collect(ImmutableSet.toImmutableSet());
    }

    private Stream<String> getFeatureKeys(FeatureModuleDescriptor moduleDescriptor) {
        if (moduleDescriptor.getKey() == null) {
            log.warn("Plugin [{}] has a null feature key, skipping", (Object)moduleDescriptor.getPluginKey());
            return Stream.empty();
        }
        return Stream.of(moduleDescriptor.getKey());
    }
}

