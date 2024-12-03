/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.lifecycle.LifecycleContext
 *  com.atlassian.config.lifecycle.LifecycleItem
 *  com.atlassian.dc.filestore.api.compat.FilesystemPath
 */
package com.atlassian.confluence.admin.actions;

import com.atlassian.config.lifecycle.LifecycleContext;
import com.atlassian.config.lifecycle.LifecycleItem;
import com.atlassian.confluence.plugin.ConfluencePluginUtils;
import com.atlassian.dc.filestore.api.compat.FilesystemPath;

public class HomeDirsSetupAction
implements LifecycleItem {
    private FilesystemPath confluenceHome;

    public void shutdown(LifecycleContext context) throws Exception {
    }

    public void startup(LifecycleContext context) throws Exception {
        this.createRestoreDirsIfNeeded();
    }

    public void setConfluenceHome(FilesystemPath confluenceHome) {
        this.confluenceHome = confluenceHome;
    }

    private void createRestoreDirsIfNeeded() {
        ConfluencePluginUtils.createDirectoryIfDoesntExist(this.confluenceHome.path(new String[]{"restore"}).path(new String[]{"site"}).asJavaFile());
        ConfluencePluginUtils.createDirectoryIfDoesntExist(this.confluenceHome.path(new String[]{"restore"}).path(new String[]{"space"}).asJavaFile());
    }
}

