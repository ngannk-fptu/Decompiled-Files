/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bamboo.license.BambooLicenseManager
 *  com.atlassian.extras.api.bamboo.BambooLicense
 */
package com.atlassian.analytics.client.license;

import com.atlassian.analytics.client.exception.NoLicenseException;
import com.atlassian.analytics.client.license.LicenseProvider;
import com.atlassian.bamboo.license.BambooLicenseManager;
import com.atlassian.extras.api.bamboo.BambooLicense;
import java.util.Date;

public class BambooLicenseProvider
implements LicenseProvider {
    private final BambooLicenseManager bambooLicenseManager;

    public BambooLicenseProvider(BambooLicenseManager bambooLicenseManager) {
        this.bambooLicenseManager = bambooLicenseManager;
    }

    private BambooLicense getLicense() throws NoLicenseException {
        BambooLicense license = this.bambooLicenseManager.getLicense();
        if (license == null) {
            throw new NoLicenseException();
        }
        return license;
    }

    @Override
    public Date getLicenseCreationDate() throws NoLicenseException {
        return this.getLicense().getCreationDate();
    }
}

