/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.IllegalPluginStateException
 *  com.atlassian.plugin.PluginArtifact
 *  com.google.common.base.Preconditions
 *  org.osgi.framework.Bundle
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugin.osgi.factory;

import com.atlassian.plugin.IllegalPluginStateException;
import com.atlassian.plugin.PluginArtifact;
import com.atlassian.plugin.osgi.container.OsgiContainerManager;
import com.atlassian.plugin.osgi.factory.OsgiPluginNotInstalledHelperBase;
import com.atlassian.plugin.osgi.factory.transform.JarUtils;
import com.atlassian.plugin.osgi.util.OsgiHeaderUtil;
import com.google.common.base.Preconditions;
import java.io.File;
import org.osgi.framework.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class OsgiPluginUninstalledHelper
extends OsgiPluginNotInstalledHelperBase {
    private static final Logger log = LoggerFactory.getLogger(OsgiPluginUninstalledHelper.class);
    private final OsgiContainerManager osgiContainerManager;
    private final PluginArtifact pluginArtifact;

    public OsgiPluginUninstalledHelper(String key, OsgiContainerManager mgr, PluginArtifact artifact) {
        super(key);
        this.pluginArtifact = (PluginArtifact)Preconditions.checkNotNull((Object)artifact);
        this.osgiContainerManager = (OsgiContainerManager)Preconditions.checkNotNull((Object)mgr);
    }

    @Override
    public <T> Class<T> loadClass(String clazz, Class<?> callingClass) {
        throw new IllegalPluginStateException(this.getNotInstalledMessage() + " This is probably because the module descriptor is trying to load classes in its init() method. Move all classloading into the enabled() method, and be sure to properly drop class and instance references in disabled().");
    }

    @Override
    public Bundle install() {
        File osgiPlugin = this.pluginArtifact.toFile();
        log.debug("Installing OSGi plugin '{}'", (Object)osgiPlugin);
        Bundle bundle = this.osgiContainerManager.installBundle(osgiPlugin, this.pluginArtifact.getReferenceMode());
        String key = this.getKey();
        if (!OsgiHeaderUtil.getPluginKey(bundle).equals(key)) {
            throw new IllegalArgumentException("The plugin key '" + key + "' must either match the OSGi bundle symbolic name (Bundle-SymbolicName) or be specified in the Atlassian-Plugin-Key manifest header");
        }
        return bundle;
    }

    @Override
    protected String getNotInstalledMessage() {
        return "This operation requires the plugin '" + this.getKey() + "' to be installed";
    }

    @Override
    public boolean isRemotePlugin() {
        return JarUtils.hasManifestEntry(JarUtils.getManifest(this.pluginArtifact.toFile()), "Remote-Plugin");
    }
}

