/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.atlassian.sal.api.license;

import com.atlassian.sal.api.license.BaseLicenseDetails;
import javax.annotation.Nullable;

public interface LicenseChangedEvent {
    @Nullable
    public BaseLicenseDetails getPreviousLicense();

    @Nullable
    public BaseLicenseDetails getNewLicense();
}

