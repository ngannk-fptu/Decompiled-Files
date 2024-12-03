/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm.api.license.event;

import com.atlassian.upm.api.license.entity.PluginLicense;
import com.atlassian.upm.api.license.event.PluginLicenseEvent;
import java.util.Objects;

public abstract class PluginLicenseChangeEvent
extends PluginLicenseEvent {
    private final PluginLicense license;

    public PluginLicenseChangeEvent(PluginLicense license) {
        super(Objects.requireNonNull(license, "license").getPluginKey());
        this.license = license;
    }

    public PluginLicense getLicense() {
        return this.license;
    }
}

