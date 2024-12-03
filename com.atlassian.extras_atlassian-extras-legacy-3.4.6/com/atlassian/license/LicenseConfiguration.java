/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.license;

import com.atlassian.license.LicenseRegistry;
import com.atlassian.license.LicenseTypeStore;

@Deprecated
public class LicenseConfiguration {
    private LicenseRegistry licenseRegistry;
    private LicenseTypeStore licenseTypeStore;

    public LicenseConfiguration(LicenseRegistry licenseRegistry, LicenseTypeStore licenseTypeStore) {
        this.licenseRegistry = licenseRegistry;
        this.licenseTypeStore = licenseTypeStore;
    }

    public LicenseRegistry getLicenseRegistry() {
        return this.licenseRegistry;
    }

    public LicenseTypeStore getLicenseTypeStore() {
        return this.licenseTypeStore;
    }

    public void setLicenseRegistry(LicenseRegistry licenseRegistry) {
        this.licenseRegistry = licenseRegistry;
    }

    public void setLicenseTypeStore(LicenseTypeStore licenseTypeStore) {
        this.licenseTypeStore = licenseTypeStore;
    }
}

