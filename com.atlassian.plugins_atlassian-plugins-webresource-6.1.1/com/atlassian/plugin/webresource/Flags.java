/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.util.PluginUtils
 */
package com.atlassian.plugin.webresource;

import com.atlassian.plugin.util.PluginUtils;

public class Flags {
    public static boolean isDevMode() {
        return PluginUtils.isAtlassianDevMode();
    }

    public static boolean isFileCacheEnabled() {
        return !Boolean.getBoolean(PluginUtils.WEBRESOURCE_DISABLE_FILE_CACHE) && !Flags.isDevMode();
    }

    public static Integer getFileCacheSize(int defaultValue) {
        return Integer.getInteger(PluginUtils.WEBRESOURCE_FILE_CACHE_SIZE, defaultValue);
    }
}

