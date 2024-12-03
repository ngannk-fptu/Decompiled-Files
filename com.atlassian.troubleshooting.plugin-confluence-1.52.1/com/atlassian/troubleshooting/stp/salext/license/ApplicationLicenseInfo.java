/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.troubleshooting.stp.salext.license;

import java.util.Date;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public abstract class ApplicationLicenseInfo {
    private static final long DAY_IN_MS = TimeUnit.DAYS.toMillis(1L);

    public abstract Date getMaintenanceExpiryDate();

    public abstract boolean isEntitledToSupport();

    public abstract boolean isEvaluation();

    public abstract boolean isStarter();

    public abstract String getSEN();

    public abstract Set<Integer> getUserLimits();

    public int getDaysToExpiry() {
        Date expiryDate = this.getMaintenanceExpiryDate();
        int daysToExpiry = 365;
        if (expiryDate != null) {
            daysToExpiry = (int)((expiryDate.getTime() - System.currentTimeMillis()) / DAY_IN_MS);
        }
        return daysToExpiry;
    }
}

