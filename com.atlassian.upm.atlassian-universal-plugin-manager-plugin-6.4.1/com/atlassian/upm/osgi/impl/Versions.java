/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Plugin
 *  org.osgi.framework.Version
 */
package com.atlassian.upm.osgi.impl;

import com.atlassian.plugin.Plugin;
import com.atlassian.upm.osgi.Version;
import com.atlassian.upm.osgi.impl.VersionImpl;
import com.atlassian.upm.osgi.impl.Wrapper;

public class Versions {
    protected static final Wrapper<org.osgi.framework.Version, Version> wrap = new Wrapper<org.osgi.framework.Version, Version>("version"){

        @Override
        protected Version wrap(org.osgi.framework.Version version) {
            return new VersionImpl(version);
        }
    };

    public static Version fromString(String version) {
        return Versions.fromString(version, true);
    }

    public static Version fromString(String version, boolean cacheFunction) {
        if (cacheFunction) {
            return wrap.fromSingleton(Versions.parseVersion(version));
        }
        return new VersionImpl(Versions.parseVersion(version));
    }

    public static Version fromPlugin(Plugin plugin) {
        return Versions.fromPlugin(plugin, true);
    }

    public static Version fromPlugin(Plugin plugin, boolean cacheFunction) {
        return Versions.fromString(plugin.getPluginInformation().getVersion(), cacheFunction);
    }

    private static org.osgi.framework.Version parseVersion(String version) {
        if ((version = version.trim()).contains("-")) {
            String suffix = version.substring(version.indexOf("-"));
            String numericVersion = version.substring(0, version.length() - suffix.length());
            org.osgi.framework.Version osgiVersion = org.osgi.framework.Version.parseVersion((String)numericVersion);
            return new org.osgi.framework.Version(osgiVersion.getMajor(), osgiVersion.getMinor(), osgiVersion.getMicro(), suffix.substring(1));
        }
        return org.osgi.framework.Version.parseVersion((String)version);
    }
}

