/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugin;

import java.io.File;

public interface PluginDirectoryProvider {
    public File getPluginDirectory();

    public File getPluginTempDirectory();

    public File getBundledPluginDirectory();

    public File getPluginsPersistentCacheDirectory();

    public File getPluginsCacheDirectory();

    public File getWebResourceIntegrationTempDirectory();
}

