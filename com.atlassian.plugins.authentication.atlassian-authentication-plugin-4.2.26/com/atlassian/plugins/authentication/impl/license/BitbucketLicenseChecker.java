/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bitbucket.license.LicenseService
 *  com.atlassian.extras.api.bitbucket.BitbucketServerLicense
 *  com.atlassian.plugin.spring.scanner.annotation.component.BitbucketComponent
 *  com.atlassian.plugin.spring.scanner.annotation.imports.BitbucketImport
 *  javax.inject.Inject
 */
package com.atlassian.plugins.authentication.impl.license;

import com.atlassian.bitbucket.license.LicenseService;
import com.atlassian.extras.api.bitbucket.BitbucketServerLicense;
import com.atlassian.plugin.spring.scanner.annotation.component.BitbucketComponent;
import com.atlassian.plugin.spring.scanner.annotation.imports.BitbucketImport;
import com.atlassian.plugins.authentication.impl.license.ProductLicenseChecker;
import java.util.Set;
import javax.inject.Inject;

@BitbucketComponent
public class BitbucketLicenseChecker
implements ProductLicenseChecker {
    private final LicenseService licenseService;

    @Inject
    public BitbucketLicenseChecker(@BitbucketImport LicenseService licenseService) {
        this.licenseService = licenseService;
    }

    @Override
    public boolean areSlotsAvailable(Set<String> groupNames) {
        BitbucketServerLicense license = this.licenseService.get();
        if (license == null) {
            return false;
        }
        if (license.isUnlimitedNumberOfUsers()) {
            return true;
        }
        return this.licenseService.getLicensedUsersCount() < license.getMaximumNumberOfUsers();
    }
}

