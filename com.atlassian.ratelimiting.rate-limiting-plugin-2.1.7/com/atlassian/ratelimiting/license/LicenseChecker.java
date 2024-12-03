/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.license.LicenseChangedEvent
 */
package com.atlassian.ratelimiting.license;

import com.atlassian.sal.api.license.LicenseChangedEvent;

public interface LicenseChecker {
    public boolean isDataCenterLicensed();

    public void onLicenseChanged(LicenseChangedEvent var1);
}

