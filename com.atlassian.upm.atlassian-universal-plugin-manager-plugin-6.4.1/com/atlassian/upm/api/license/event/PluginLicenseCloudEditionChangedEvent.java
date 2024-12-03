/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm.api.license.event;

import com.atlassian.upm.api.license.entity.PluginLicense;
import com.atlassian.upm.api.license.event.PluginLicenseChangeEvent;

public final class PluginLicenseCloudEditionChangedEvent
extends PluginLicenseChangeEvent {
    public PluginLicenseCloudEditionChangedEvent(PluginLicense license) {
        super(license);
    }

    public String toString() {
        return "Plugin License Cloud Edition Change: " + this.getPluginKey();
    }
}

