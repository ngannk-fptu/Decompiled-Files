/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugin;

import com.atlassian.confluence.plugin.ConfluencePluginUtils;
import com.atlassian.confluence.plugin.PluginParentDirectoryLocator;
import com.atlassian.confluence.setup.BootstrapManager;
import java.io.File;

class BootstrapPluginParentDirectoryLocator
implements PluginParentDirectoryLocator {
    private final BootstrapManager bootstrapManager;

    public BootstrapPluginParentDirectoryLocator(BootstrapManager bootstrapManager) {
        this.bootstrapManager = bootstrapManager;
    }

    @Override
    public File getDirectory() {
        return ConfluencePluginUtils.getPluginsBaseDirectory(this.bootstrapManager.getLocalHome().getPath());
    }
}

