/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 */
package com.atlassian.plugin.jmx;

import com.atlassian.annotations.PublicApi;

@PublicApi
public interface PluginManagerMXBean {
    public PluginData[] getPlugins();

    public int scanForNewPlugins();

    public static interface PluginData {
        public String getKey();

        public String getVersion();

        public String getLocation();

        public Long getDateLoaded();

        public Long getDateInstalled();

        public boolean isEnabled();

        public boolean isEnabledByDefault();

        public boolean isBundledPlugin();
    }
}

