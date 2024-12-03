/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginArtifact
 *  com.atlassian.plugin.PluginController
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.sal.api.transaction.TransactionCallback
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 */
package com.atlassian.upm.core.install;

import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginArtifact;
import com.atlassian.plugin.PluginController;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.upm.Iterables;
import com.atlassian.upm.UpmPluginAccessor;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.core.DefaultHostApplicationInformation;
import com.atlassian.upm.core.install.JarHelper;
import com.atlassian.upm.core.install.LegacyPluginsUnsupportedException;
import com.atlassian.upm.core.install.PluginDescriptor;
import com.atlassian.upm.core.install.UnknownPluginTypeException;
import com.atlassian.upm.core.install.UnrecognisedPluginVersionException;
import com.atlassian.upm.core.rest.resources.permission.PermissionEnforcer;
import com.atlassian.upm.spi.PluginInstallException;
import com.atlassian.upm.spi.PluginInstallHandler;
import com.atlassian.upm.spi.PluginInstallResult;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class AbstractPluginInstallHandler
implements PluginInstallHandler {
    private static final List<String> SUPPORTED_VERSIONS = Collections.unmodifiableList(Arrays.asList("1", "2", "3"));
    protected final DefaultHostApplicationInformation hostApplicationInformation;
    private final PermissionEnforcer permissionEnforcer;
    private final UpmPluginAccessor pluginAccessor;
    private final PluginController pluginController;
    private final TransactionTemplate txTemplate;

    protected AbstractPluginInstallHandler(DefaultHostApplicationInformation hostApplicationInformation, PermissionEnforcer permissionEnforcer, UpmPluginAccessor pluginAccessor, PluginController pluginController, TransactionTemplate txTemplate) {
        this.hostApplicationInformation = Objects.requireNonNull(hostApplicationInformation, "hostApplicationInformation");
        this.permissionEnforcer = Objects.requireNonNull(permissionEnforcer, "permissionEnforcer");
        this.pluginAccessor = Objects.requireNonNull(pluginAccessor, "pluginAccessor");
        this.pluginController = Objects.requireNonNull(pluginController, "pluginController");
        this.txTemplate = Objects.requireNonNull(txTemplate, "txTemplate");
    }

    @Override
    public final PluginInstallResult installPlugin(File pluginFile, Option<String> contentType) throws PluginInstallException {
        return this.installPluginInternal(pluginFile, contentType);
    }

    protected abstract PluginInstallResult installPluginInternal(File var1, Option<String> var2) throws PluginInstallException;

    protected void validateDescriptorIsInstallable(PluginDescriptor descriptor) {
        if (descriptor.hasRemotePluginContainer()) {
            throw new PluginInstallException("Cannot install XML plugin with remote-plugin-container module", false);
        }
        String version = descriptor.getPluginsVersion();
        if (version.equals("1") && !this.hostApplicationInformation.canInstallLegacyPlugins()) {
            throw new LegacyPluginsUnsupportedException();
        }
        if (!SUPPORTED_VERSIONS.contains(version)) {
            throw new UnrecognisedPluginVersionException(version);
        }
    }

    protected boolean isDescriptorInstallable(PluginDescriptor descriptor) {
        try {
            this.validateDescriptorIsInstallable(descriptor);
            return true;
        }
        catch (PluginInstallException e) {
            return false;
        }
    }

    protected Option<PluginDescriptor> validateJarIsInstallable(JarHelper jar) throws PluginInstallException {
        try {
            Option<PluginDescriptor> descriptor = jar.getPluginDescriptor();
            Option<String> bundleName = jar.getBundleSymbolicName();
            if (!descriptor.isDefined() && !bundleName.isDefined()) {
                throw new UnknownPluginTypeException("Jar contained neither an atlassian-plugin.xml nor a Bundle-SymbolicName");
            }
            for (PluginDescriptor d : descriptor) {
                this.validateDescriptorIsInstallable(d);
            }
            return descriptor;
        }
        catch (IOException e) {
            throw new PluginInstallException("Unreadable jar file", false);
        }
    }

    protected Plugin installArtifact(PluginArtifact artifact) throws PluginInstallException {
        List<Plugin> installed = this.installArtifacts(Collections.singletonList(artifact));
        if (installed.isEmpty()) {
            throw new PluginInstallException("Unknown error, plugin not installed");
        }
        if (installed.size() > 1) {
            throw new PluginInstallException("Unknown error, installer returned multiple plugins");
        }
        return Iterables.getOnlyElement(installed);
    }

    protected List<Plugin> installArtifacts(final List<PluginArtifact> artifacts) throws PluginInstallException {
        return (List)this.txTemplate.execute((TransactionCallback)new TransactionCallback<List<Plugin>>(){

            public List<Plugin> doInTransaction() {
                try {
                    Set pluginKeys = AbstractPluginInstallHandler.this.pluginController.installPlugins((PluginArtifact[])artifacts.stream().toArray(PluginArtifact[]::new));
                    return pluginKeys.stream().map(AbstractPluginInstallHandler.this.pluginAccessor::getPlugin).filter(Option::isDefined).map(Option::get).collect(Collectors.toList());
                }
                catch (PluginParseException e) {
                    throw new PluginInstallException("Plugin installation failed", e);
                }
            }
        });
    }
}

