/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm.api.license.event;

import com.atlassian.upm.api.license.event.PluginLicenseEvent;

public class PluginLicenseRoleExceededEvent
extends PluginLicenseEvent {
    private final int currentRoleCount;
    private final int licensedRoleCount;

    public PluginLicenseRoleExceededEvent(String pluginKey, int currentRoleCount, int licensedRoleCount) {
        super(pluginKey);
        this.currentRoleCount = currentRoleCount;
        this.licensedRoleCount = licensedRoleCount;
    }

    public int getCurrentRoleCount() {
        return this.currentRoleCount;
    }

    public int getLicensedRoleCount() {
        return this.licensedRoleCount;
    }
}

