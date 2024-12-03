/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugin;

import com.atlassian.confluence.plugin.ConfluencePluginUtils;
import com.atlassian.confluence.plugin.PluginDirectoryProvider;
import com.atlassian.confluence.plugin.PluginParentDirectoryLocator;
import java.io.File;

public class SimplePluginDirectoryProvider
implements PluginDirectoryProvider {
    private final PluginParentDirectoryLocator directoryLocator;

    public SimplePluginDirectoryProvider(PluginParentDirectoryLocator directoryLocator) {
        this.directoryLocator = directoryLocator;
    }

    @Override
    public File getPluginDirectory() {
        return this.findOrCreateDirectoryUnderParent("plugins");
    }

    @Override
    public File getPluginTempDirectory() {
        return this.findOrCreateDirectoryUnderParent("plugins-temp");
    }

    @Override
    public File getBundledPluginDirectory() {
        return this.findOrCreateDirectoryUnderParent("bundled-plugins");
    }

    @Override
    public File getPluginsPersistentCacheDirectory() {
        return this.findOrCreateDirectoryUnderParent("plugins-osgi-cache");
    }

    @Override
    public File getPluginsCacheDirectory() {
        return this.findOrCreateDirectoryUnderParent("plugins-cache");
    }

    @Override
    public File getWebResourceIntegrationTempDirectory() {
        return this.findOrCreateDirectoryUnderParent("webresource-temp");
    }

    private File findOrCreateDirectoryUnderParent(String directoryName) {
        return ConfluencePluginUtils.createDirectoryIfDoesntExist(new File(this.directoryLocator.getDirectory(), directoryName));
    }
}

