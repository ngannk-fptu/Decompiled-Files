/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginArtifact
 *  com.atlassian.plugin.PluginController
 *  com.atlassian.plugin.XmlPluginArtifact
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 */
package com.atlassian.upm.core.install;

import com.atlassian.plugin.PluginArtifact;
import com.atlassian.plugin.PluginController;
import com.atlassian.plugin.XmlPluginArtifact;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.upm.UpmPluginAccessor;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.core.DefaultHostApplicationInformation;
import com.atlassian.upm.core.install.AbstractPluginInstallHandler;
import com.atlassian.upm.core.install.ContentTypes;
import com.atlassian.upm.core.install.PluginDescriptor;
import com.atlassian.upm.core.install.XmlPluginsUnsupportedException;
import com.atlassian.upm.core.rest.resources.permission.PermissionEnforcer;
import com.atlassian.upm.spi.PluginInstallException;
import com.atlassian.upm.spi.PluginInstallResult;
import java.io.File;

public class XmlPluginInstallHandler
extends AbstractPluginInstallHandler {
    public XmlPluginInstallHandler(DefaultHostApplicationInformation hostApplicationInformation, PermissionEnforcer permissionEnforcer, UpmPluginAccessor pluginAccessor, PluginController pluginController, TransactionTemplate txTemplate) {
        super(hostApplicationInformation, permissionEnforcer, pluginAccessor, pluginController, txTemplate);
    }

    @Override
    public boolean canInstallPlugin(File pluginFile, Option<String> contentType) {
        try {
            return ContentTypes.isXml(pluginFile, contentType) && this.isDescriptorInstallable(PluginDescriptor.fromFile(pluginFile));
        }
        catch (Exception e) {
            return false;
        }
    }

    @Override
    protected PluginInstallResult installPluginInternal(File pluginFile, Option<String> contentType) throws PluginInstallException {
        if (!this.hostApplicationInformation.canInstallXmlPlugins()) {
            throw new XmlPluginsUnsupportedException();
        }
        this.validateDescriptorIsInstallable(PluginDescriptor.fromFile(pluginFile));
        return new PluginInstallResult(this.installArtifact((PluginArtifact)new XmlPluginArtifact(pluginFile)));
    }
}

