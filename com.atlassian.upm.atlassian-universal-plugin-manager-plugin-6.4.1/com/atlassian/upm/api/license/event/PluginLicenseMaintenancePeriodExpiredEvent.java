/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joda.time.DateTime
 */
package com.atlassian.upm.api.license.event;

import com.atlassian.upm.api.license.entity.PluginLicense;
import com.atlassian.upm.api.license.event.PluginLicenseChangeEvent;
import java.util.Objects;
import org.joda.time.DateTime;

public final class PluginLicenseMaintenancePeriodExpiredEvent
extends PluginLicenseChangeEvent {
    private final DateTime expiryDate;

    public PluginLicenseMaintenancePeriodExpiredEvent(PluginLicense license, DateTime expiryDate) {
        super(license);
        this.expiryDate = Objects.requireNonNull(expiryDate, "expiryDate");
    }

    public String toString() {
        return "Plugin License Maintenance Period Expired: " + this.getPluginKey() + ", Date of Expiry: " + this.expiryDate;
    }

    public DateTime getExpiryDate() {
        return this.expiryDate;
    }
}

