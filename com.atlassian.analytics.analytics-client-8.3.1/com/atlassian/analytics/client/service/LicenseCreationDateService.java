/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.analytics.client.service;

import com.atlassian.analytics.client.configuration.LastPrivacyPolicyUpdateDateProvider;
import com.atlassian.analytics.client.exception.NoLicenseException;
import com.atlassian.analytics.client.license.LicenseProvider;
import java.util.Date;

public class LicenseCreationDateService {
    private final LastPrivacyPolicyUpdateDateProvider lastPrivacyPolicyUpdateDateProvider;
    private final LicenseProvider licenseProvider;

    public LicenseCreationDateService(LastPrivacyPolicyUpdateDateProvider lastPrivacyPolicyUpdateDateProvider, LicenseProvider licenseProvider) {
        this.lastPrivacyPolicyUpdateDateProvider = lastPrivacyPolicyUpdateDateProvider;
        this.licenseProvider = licenseProvider;
    }

    public boolean isLicenseOlderThanPolicyUpdate() throws NoLicenseException {
        Date licenseCreationDate = this.licenseProvider.getLicenseCreationDate();
        return licenseCreationDate != null && licenseCreationDate.before(this.lastPrivacyPolicyUpdateDateProvider.getLastPrivacyPolicyUpdateDate());
    }
}

