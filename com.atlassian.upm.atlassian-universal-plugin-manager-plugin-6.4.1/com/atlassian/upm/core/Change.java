/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginRestartState
 */
package com.atlassian.upm.core;

import com.atlassian.plugin.PluginRestartState;
import com.atlassian.upm.core.Plugin;
import com.atlassian.upm.core.permission.Permission;
import java.util.Objects;

public class Change {
    private final Plugin plugin;
    private final String action;
    private final Permission requiredPermission;

    public Change(Plugin plugin, PluginRestartState pluginRestartState) {
        this.plugin = Objects.requireNonNull(plugin, "plugin");
        this.action = Objects.requireNonNull(pluginRestartState, "pluginRestartState").toString().toLowerCase();
        this.requiredPermission = Change.getRequiredPermission(pluginRestartState);
    }

    private static Permission getRequiredPermission(PluginRestartState pluginRestartState) {
        switch (pluginRestartState) {
            case UPGRADE: 
            case INSTALL: {
                return Permission.MANAGE_IN_PROCESS_PLUGIN_INSTALL_FROM_URI;
            }
            case REMOVE: {
                return Permission.MANAGE_PLUGIN_UNINSTALL;
            }
            case NONE: {
                throw new IllegalArgumentException("No restart state");
            }
        }
        throw new IllegalArgumentException("Unknown restart state");
    }

    public Plugin getPlugin() {
        return this.plugin;
    }

    public String getAction() {
        return this.action;
    }

    public Permission getRequiredPermission() {
        return this.requiredPermission;
    }
}

