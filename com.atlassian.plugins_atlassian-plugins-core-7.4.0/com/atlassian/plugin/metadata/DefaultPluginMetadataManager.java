/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.metadata.PluginMetadataManager
 *  com.google.common.base.Preconditions
 *  javax.annotation.concurrent.Immutable
 */
package com.atlassian.plugin.metadata;

import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.descriptors.CannotDisable;
import com.atlassian.plugin.metadata.ClasspathFilePluginMetadata;
import com.atlassian.plugin.metadata.PluginMetadata;
import com.atlassian.plugin.metadata.PluginMetadataManager;
import com.google.common.base.Preconditions;
import javax.annotation.concurrent.Immutable;

@Immutable
public final class DefaultPluginMetadataManager
implements PluginMetadataManager {
    private final PluginMetadata metadata;

    public DefaultPluginMetadataManager() {
        this(new ClasspathFilePluginMetadata());
    }

    DefaultPluginMetadataManager(PluginMetadata metadata) {
        this.metadata = (PluginMetadata)Preconditions.checkNotNull((Object)metadata, (Object)"metadata");
    }

    public boolean isUserInstalled(Plugin plugin) {
        Preconditions.checkNotNull((Object)plugin, (Object)"plugin");
        return !plugin.isBundledPlugin() && !this.metadata.applicationProvided(plugin);
    }

    public boolean isSystemProvided(Plugin plugin) {
        return !this.isUserInstalled(plugin);
    }

    public boolean isOptional(Plugin plugin) {
        Preconditions.checkNotNull((Object)plugin, (Object)"plugin");
        if (!this.optionalAccordingToHostApplication(plugin)) {
            return false;
        }
        for (ModuleDescriptor moduleDescriptor : plugin.getModuleDescriptors()) {
            if (this.optionalAccordingToHostApplication(moduleDescriptor)) continue;
            return false;
        }
        return true;
    }

    public boolean isOptional(ModuleDescriptor<?> moduleDescriptor) {
        Preconditions.checkNotNull(moduleDescriptor, (Object)"moduleDescriptor");
        if (!this.optionalAccordingToHostApplication(moduleDescriptor)) {
            return false;
        }
        if (!this.optionalAccordingToModuleDescriptorType(moduleDescriptor)) {
            return false;
        }
        return this.optionalAccordingToHostApplication(moduleDescriptor.getPlugin());
    }

    private boolean optionalAccordingToHostApplication(Plugin plugin) {
        return !this.metadata.required(plugin);
    }

    private boolean optionalAccordingToHostApplication(ModuleDescriptor<?> moduleDescriptor) {
        return !this.metadata.required(moduleDescriptor);
    }

    private boolean optionalAccordingToModuleDescriptorType(ModuleDescriptor<?> moduleDescriptor) {
        return !moduleDescriptor.getClass().isAnnotationPresent(CannotDisable.class);
    }
}

