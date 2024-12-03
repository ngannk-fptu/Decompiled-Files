/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.HomeLocator
 */
package com.atlassian.confluence.plugin;

import com.atlassian.config.HomeLocator;
import com.atlassian.confluence.plugin.ConfluencePluginUtils;
import com.atlassian.confluence.plugin.PluginParentDirectoryLocator;
import java.io.File;

class HomeLocatorPluginParentDirectoryLocator
implements PluginParentDirectoryLocator {
    private final HomeLocator homeLocator;

    public HomeLocatorPluginParentDirectoryLocator(HomeLocator homeLocator) {
        this.homeLocator = homeLocator;
    }

    @Override
    public File getDirectory() {
        return ConfluencePluginUtils.getPluginsBaseDirectory(this.homeLocator.getHomePath());
    }
}

