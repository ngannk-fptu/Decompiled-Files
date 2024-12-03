/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.JarPluginArtifact
 *  com.atlassian.plugin.PluginArtifact
 *  com.atlassian.plugin.PluginController
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 */
package com.atlassian.upm.core.install;

import com.atlassian.plugin.JarPluginArtifact;
import com.atlassian.plugin.PluginArtifact;
import com.atlassian.plugin.PluginController;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.upm.UpmPluginAccessor;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.core.DefaultHostApplicationInformation;
import com.atlassian.upm.core.install.AbstractPluginInstallHandler;
import com.atlassian.upm.core.install.ContentTypes;
import com.atlassian.upm.core.install.JarHelper;
import com.atlassian.upm.core.rest.resources.permission.PermissionEnforcer;
import com.atlassian.upm.spi.PluginInstallException;
import com.atlassian.upm.spi.PluginInstallResult;
import java.io.File;
import java.util.Iterator;

public class JarPluginInstallHandler
extends AbstractPluginInstallHandler {
    public JarPluginInstallHandler(DefaultHostApplicationInformation hostApplicationInformation, PermissionEnforcer permissionEnforcer, UpmPluginAccessor pluginAccessor, PluginController pluginController, TransactionTemplate txTemplate) {
        super(hostApplicationInformation, permissionEnforcer, pluginAccessor, pluginController, txTemplate);
    }

    @Override
    public boolean canInstallPlugin(File pluginFile, Option<String> contentType) {
        for (String ct : contentType) {
            if (ContentTypes.matchContentType("application/java-archive", ct)) continue;
            return false;
        }
        Iterator<Object> iterator = JarHelper.fromFile(pluginFile).iterator();
        if (iterator.hasNext()) {
            JarHelper jar = (JarHelper)iterator.next();
            return !jar.isObr();
        }
        return false;
    }

    @Override
    protected PluginInstallResult installPluginInternal(File pluginFile, Option<String> contentType) throws PluginInstallException {
        for (JarHelper jar : JarHelper.fromFile(pluginFile)) {
            this.validateJarIsInstallable(jar);
        }
        return new PluginInstallResult(this.installArtifact((PluginArtifact)new JarPluginArtifact(pluginFile)));
    }
}

