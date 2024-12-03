/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin;

import com.atlassian.plugin.PluginInstaller;

public interface RevertablePluginInstaller
extends PluginInstaller {
    public void revertInstalledPlugin(String var1);

    public void clearBackups();
}

