/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm.api.license.event;

import com.atlassian.upm.api.license.entity.PluginLicense;
import com.atlassian.upm.api.license.event.PluginLicenseChangeEvent;

public final class PluginLicenseAddedEvent
extends PluginLicenseChangeEvent {
    public PluginLicenseAddedEvent(PluginLicense license) {
        super(license);
    }

    public String toString() {
        return "License Added: " + this.getPluginKey();
    }
}

