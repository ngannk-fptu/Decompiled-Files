/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm.api.license.event;

import com.atlassian.upm.api.license.entity.PluginLicense;
import com.atlassian.upm.api.license.event.PluginLicenseChangeEvent;

public final class PluginLicenseUpdatedEvent
extends PluginLicenseChangeEvent {
    private final PluginLicense oldLicense;

    public PluginLicenseUpdatedEvent(PluginLicense license, PluginLicense oldLicense) {
        super(license);
        this.oldLicense = oldLicense;
    }

    public PluginLicense getOldLicense() {
        return this.oldLicense;
    }

    public String toString() {
        return "Plugin License Updated: " + this.getPluginKey();
    }
}

