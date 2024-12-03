/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.metadata;

import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.Plugin;

public interface PluginMetadataManager {
    public boolean isUserInstalled(Plugin var1);

    public boolean isSystemProvided(Plugin var1);

    public boolean isOptional(Plugin var1);

    public boolean isOptional(ModuleDescriptor<?> var1);
}

