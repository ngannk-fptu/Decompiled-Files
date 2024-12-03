/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.PluginController
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugin.manager;

import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.PluginController;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SafeModuleExtractor {
    private static final Logger log = LoggerFactory.getLogger(SafeModuleExtractor.class);
    private final PluginController pluginController;

    public SafeModuleExtractor(PluginController pluginController) {
        this.pluginController = pluginController;
    }

    public <M> List<M> getModules(Iterable<? extends ModuleDescriptor<M>> moduleDescriptors) {
        return StreamSupport.stream(moduleDescriptors.spliterator(), false).map(this::getModule).filter(Objects::nonNull).collect(Collectors.toList());
    }

    <M> M getModule(ModuleDescriptor<M> descriptor) {
        if (descriptor == null || descriptor.isBroken()) {
            return null;
        }
        try {
            return (M)descriptor.getModule();
        }
        catch (RuntimeException ex) {
            String pluginKey = descriptor.getPlugin().getKey();
            log.error("Exception when retrieving plugin module {}, disabling plugin {}", new Object[]{descriptor.getCompleteKey(), pluginKey, ex});
            descriptor.setBroken();
            this.pluginController.disablePluginWithoutPersisting(pluginKey);
            return null;
        }
    }
}

