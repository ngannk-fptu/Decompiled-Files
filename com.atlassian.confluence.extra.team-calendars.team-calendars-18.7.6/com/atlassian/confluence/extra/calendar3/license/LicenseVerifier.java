/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.calendar3.license;

public interface LicenseVerifier {
    public boolean isLicenseExpired();

    public boolean isLicenseInvalidated();

    @Deprecated
    public boolean isOnDemandLicense();
}

