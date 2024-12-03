/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm.api.license.event;

import com.atlassian.upm.api.license.entity.PluginLicense;
import com.atlassian.upm.api.license.event.PluginLicenseEvent;
import com.atlassian.upm.api.util.Option;

public final class PluginLicenseCheckEvent
extends PluginLicenseEvent {
    private final Option<PluginLicense> license;

    public PluginLicenseCheckEvent(String pluginKey, Option<PluginLicense> license) {
        super(pluginKey);
        this.license = license;
    }

    public Option<PluginLicense> getLicense() {
        return this.license;
    }

    public String toString() {
        return "License Check: " + this.getPluginKey();
    }
}

