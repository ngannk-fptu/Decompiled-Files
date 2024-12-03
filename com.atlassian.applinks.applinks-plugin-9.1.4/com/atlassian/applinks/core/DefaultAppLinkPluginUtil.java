/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.osgi.util.OsgiHeaderUtil
 *  javax.annotation.Nonnull
 *  org.osgi.framework.Bundle
 *  org.osgi.framework.BundleContext
 *  org.osgi.framework.Version
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.applinks.core;

import com.atlassian.applinks.core.AppLinkPluginUtil;
import com.atlassian.plugin.osgi.util.OsgiHeaderUtil;
import java.util.Objects;
import javax.annotation.Nonnull;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class DefaultAppLinkPluginUtil
implements AppLinkPluginUtil {
    private static final String VERSION_OVERRIDE_PROPERTY = "applinks.version.override";
    private static final Logger LOG = LoggerFactory.getLogger((String)DefaultAppLinkPluginUtil.class.getName());
    private final String pluginKey;
    private final Version version;

    @Autowired
    public DefaultAppLinkPluginUtil(BundleContext bundleContext) {
        Bundle bundle = bundleContext.getBundle();
        this.pluginKey = OsgiHeaderUtil.getPluginKey((Bundle)bundle);
        this.version = bundle.getVersion();
    }

    @Override
    @Nonnull
    public String getPluginKey() {
        return this.pluginKey;
    }

    @Override
    @Nonnull
    public String completeModuleKey(@Nonnull String moduleKey) {
        Objects.requireNonNull(moduleKey, "moduleKey");
        return this.pluginKey + ":" + moduleKey;
    }

    @Override
    @Nonnull
    public Version getVersion() {
        String versionOverride = System.getProperty(VERSION_OVERRIDE_PROPERTY);
        if (versionOverride != null) {
            try {
                return new Version(versionOverride);
            }
            catch (Exception e) {
                LOG.debug("System Property '{}' contains an invalid version string, using version from OSGi", (Object)VERSION_OVERRIDE_PROPERTY);
            }
        }
        return this.version;
    }
}

