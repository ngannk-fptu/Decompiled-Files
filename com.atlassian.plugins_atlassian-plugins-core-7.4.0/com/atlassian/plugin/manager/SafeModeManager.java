/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicSpi
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.Plugin
 */
package com.atlassian.plugin.manager;

import com.atlassian.annotations.PublicSpi;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.Plugin;

@PublicSpi
public interface SafeModeManager {
    public static final SafeModeManager START_ALL_PLUGINS = new SafeModeManager(){

        @Override
        public boolean pluginShouldBeStarted(Plugin plugin, Iterable<ModuleDescriptor> descriptors) {
            return true;
        }

        @Override
        public boolean isInSafeMode() {
            return false;
        }
    };

    public boolean pluginShouldBeStarted(Plugin var1, Iterable<ModuleDescriptor> var2);

    public boolean isInSafeMode();
}

