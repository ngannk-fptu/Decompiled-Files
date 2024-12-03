/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.lifecycle.LifecycleAware
 */
package com.atlassian.analytics.client.detect;

import com.atlassian.analytics.client.UserPermissionsHelper;
import com.atlassian.analytics.client.configuration.AnalyticsConfig;
import com.atlassian.analytics.client.exception.NoLicenseException;
import com.atlassian.analytics.client.service.LicenseCreationDateService;
import com.atlassian.sal.api.lifecycle.LifecycleAware;

public class PrivacyPolicyUpdateDetector
implements LifecycleAware {
    private final AnalyticsConfig analyticsConfig;
    private final UserPermissionsHelper userPermissionsHelper;
    private final LicenseCreationDateService licenseCreationDateService;
    private boolean isLicenceOlderThanPolicyUpdate;

    public PrivacyPolicyUpdateDetector(AnalyticsConfig analyticsConfig, UserPermissionsHelper userPermissionsHelper, LicenseCreationDateService licenseCreationDateService) {
        this.analyticsConfig = analyticsConfig;
        this.userPermissionsHelper = userPermissionsHelper;
        this.licenseCreationDateService = licenseCreationDateService;
    }

    public void onStart() {
        try {
            this.isLicenceOlderThanPolicyUpdate = this.licenseCreationDateService.isLicenseOlderThanPolicyUpdate();
        }
        catch (NoLicenseException noLicenseException) {
            // empty catch block
        }
        if (!this.isLicenceOlderThanPolicyUpdate) {
            this.analyticsConfig.setPolicyUpdateAcknowledged(true);
        }
    }

    public boolean isPolicyUpdated() {
        if (this.shouldSkipPolicyUpdateDateCheck()) {
            return false;
        }
        return this.isLicenceOlderThanPolicyUpdate;
    }

    private boolean shouldSkipPolicyUpdateDateCheck() {
        return !this.userPermissionsHelper.isCurrentUserSystemAdmin() || this.analyticsConfig.isPolicyUpdateAcknowledged();
    }

    public void onStop() {
    }
}

