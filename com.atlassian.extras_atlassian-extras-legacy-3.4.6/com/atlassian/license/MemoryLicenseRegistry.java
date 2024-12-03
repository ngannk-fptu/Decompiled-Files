/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.license;

import com.atlassian.license.LicenseRegistry;

@Deprecated
public class MemoryLicenseRegistry
implements LicenseRegistry {
    private static String LICENSE;
    private static String HASH;

    @Override
    public void setLicenseMessage(String licenseMessage) {
        LICENSE = licenseMessage;
    }

    @Override
    public void setLicenseHash(String licenseHash) {
        HASH = licenseHash;
    }

    @Override
    public String getLicenseMessage() {
        return LICENSE;
    }

    @Override
    public String getLicenseHash() {
        return HASH;
    }
}

