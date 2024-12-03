/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Plugin
 */
package com.atlassian.upm.license.internal.impl.role;

import com.atlassian.plugin.Plugin;
import com.atlassian.upm.license.internal.PluginLicenseGlobalEvent;

public class PluginLicensingRoleMembershipUpdatedEvent
implements PluginLicenseGlobalEvent {
    private final Plugin plugin;
    private final int newRoleCount;

    public PluginLicensingRoleMembershipUpdatedEvent(Plugin plugin, int newRoleCount) {
        this.plugin = plugin;
        this.newRoleCount = newRoleCount;
    }

    public Plugin getPlugin() {
        return this.plugin;
    }

    public int getNewRoleCount() {
        return this.newRoleCount;
    }
}

