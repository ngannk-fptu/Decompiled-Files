/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm.api.license.event;

import com.atlassian.upm.api.license.entity.PluginLicense;
import com.atlassian.upm.api.license.event.PluginLicenseEvent;

public final class PluginLicenseRemovedEvent
extends PluginLicenseEvent {
    private final PluginLicense oldLicense;

    public PluginLicenseRemovedEvent(String pluginKey, PluginLicense oldLicense) {
        super(pluginKey);
        this.oldLicense = oldLicense;
    }

    public PluginLicense getOldLicense() {
        return this.oldLicense;
    }

    public String toString() {
        return "Plugin License Deleted: " + this.getPluginKey();
    }
}

