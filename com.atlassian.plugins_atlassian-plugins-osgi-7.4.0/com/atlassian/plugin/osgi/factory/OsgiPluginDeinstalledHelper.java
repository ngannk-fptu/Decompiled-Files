/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.IllegalPluginStateException
 *  org.osgi.framework.Bundle
 */
package com.atlassian.plugin.osgi.factory;

import com.atlassian.plugin.IllegalPluginStateException;
import com.atlassian.plugin.osgi.factory.OsgiPluginNotInstalledHelperBase;
import org.osgi.framework.Bundle;

final class OsgiPluginDeinstalledHelper
extends OsgiPluginNotInstalledHelperBase {
    private final boolean remotePlugin;

    public OsgiPluginDeinstalledHelper(String key, boolean remotePlugin) {
        super(key);
        this.remotePlugin = remotePlugin;
    }

    @Override
    public <T> Class<T> loadClass(String clazz, Class<?> callingClass) {
        String className = null != callingClass ? callingClass.getCanonicalName() : "null";
        throw new IllegalPluginStateException(" Cannot loadClass(" + clazz + ", " + className + "): " + this.getNotInstalledMessage() + ". This is probably because the module/plugin code is continuing to execute code after it has been shutdown, for example from a finalize() method, or in response to a timer. Ensure all code execution ceases after PluginDisabledEvent is received.");
    }

    @Override
    public Bundle install() {
        throw new IllegalPluginStateException("Cannot reuse Plugin instance for '" + this.getKey() + "'");
    }

    @Override
    protected String getNotInstalledMessage() {
        return "This operation must occur before the plugin '" + this.getKey() + "' is uninstalled";
    }

    @Override
    public boolean isRemotePlugin() {
        return this.remotePlugin;
    }
}

