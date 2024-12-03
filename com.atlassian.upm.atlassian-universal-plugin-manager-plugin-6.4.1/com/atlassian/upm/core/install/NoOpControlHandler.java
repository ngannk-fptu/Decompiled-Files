/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginException
 *  com.atlassian.plugin.PluginRestartState
 */
package com.atlassian.upm.core.install;

import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginException;
import com.atlassian.plugin.PluginRestartState;
import com.atlassian.upm.spi.PluginControlHandler;
import java.util.Collection;
import java.util.Collections;

public class NoOpControlHandler
implements PluginControlHandler {
    @Override
    public boolean canControl(String pluginKey) {
        return false;
    }

    @Override
    public void enablePlugins(String ... pluginKeys) {
    }

    @Override
    public boolean isPluginEnabled(String pluginKey) {
        return false;
    }

    @Override
    public void disablePlugin(String pluginKey) {
    }

    @Override
    public Plugin getPlugin(String pluginKey) {
        return null;
    }

    @Override
    public Collection<? extends Plugin> getPlugins() {
        return Collections.emptyList();
    }

    @Override
    public void uninstall(Plugin plugin) throws PluginException {
    }

    @Override
    public PluginRestartState getPluginRestartState(String key) {
        return PluginRestartState.NONE;
    }
}

