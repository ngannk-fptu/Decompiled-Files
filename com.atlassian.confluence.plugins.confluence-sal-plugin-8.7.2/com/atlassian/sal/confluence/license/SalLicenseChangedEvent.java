/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.license.BaseLicenseDetails
 *  com.atlassian.sal.api.license.LicenseChangedEvent
 *  javax.annotation.Nullable
 */
package com.atlassian.sal.confluence.license;

import com.atlassian.sal.api.license.BaseLicenseDetails;
import com.atlassian.sal.api.license.LicenseChangedEvent;
import javax.annotation.Nullable;

public class SalLicenseChangedEvent
implements LicenseChangedEvent {
    @Nullable
    private final BaseLicenseDetails newLicense;
    @Nullable
    private final BaseLicenseDetails previousLicense;

    public SalLicenseChangedEvent(@Nullable BaseLicenseDetails newLicense, @Nullable BaseLicenseDetails previousLicense) {
        this.newLicense = newLicense;
        this.previousLicense = previousLicense;
    }

    @Nullable
    public BaseLicenseDetails getNewLicense() {
        return this.newLicense;
    }

    @Nullable
    public BaseLicenseDetails getPreviousLicense() {
        return this.previousLicense;
    }
}

