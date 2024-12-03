/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bitbucket.license.LicenseService
 *  com.atlassian.extras.api.bitbucket.BitbucketServerLicense
 */
package com.atlassian.analytics.client.license;

import com.atlassian.analytics.client.license.LicenseProvider;
import com.atlassian.bitbucket.license.LicenseService;
import com.atlassian.extras.api.bitbucket.BitbucketServerLicense;
import java.util.Date;

public class BitbucketLicenseProvider
implements LicenseProvider {
    private final LicenseService licenseService;

    public BitbucketLicenseProvider(LicenseService licenseService) {
        this.licenseService = licenseService;
    }

    @Override
    public Date getLicenseCreationDate() {
        BitbucketServerLicense license = this.licenseService.get();
        return license == null ? null : license.getCreationDate();
    }
}

