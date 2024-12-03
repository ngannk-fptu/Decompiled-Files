/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginException
 *  com.atlassian.plugin.PluginRestartState
 */
package com.atlassian.upm.spi;

import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginException;
import com.atlassian.plugin.PluginRestartState;
import java.util.Collection;

public interface PluginControlHandler {
    public boolean canControl(String var1);

    public void enablePlugins(String ... var1);

    public boolean isPluginEnabled(String var1);

    public void disablePlugin(String var1);

    public Plugin getPlugin(String var1);

    public Collection<? extends Plugin> getPlugins();

    public void uninstall(Plugin var1) throws PluginException;

    public PluginRestartState getPluginRestartState(String var1);
}

