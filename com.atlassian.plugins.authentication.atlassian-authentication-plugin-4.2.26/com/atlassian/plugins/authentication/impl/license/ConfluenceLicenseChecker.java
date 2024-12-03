/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.license.LicenseService
 *  com.atlassian.confluence.license.exception.LicenseException
 *  com.atlassian.confluence.util.UserChecker
 *  com.atlassian.extras.api.confluence.ConfluenceLicense
 *  com.atlassian.plugin.spring.scanner.annotation.component.ConfluenceComponent
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ConfluenceImport
 *  javax.inject.Inject
 */
package com.atlassian.plugins.authentication.impl.license;

import com.atlassian.confluence.license.LicenseService;
import com.atlassian.confluence.license.exception.LicenseException;
import com.atlassian.confluence.util.UserChecker;
import com.atlassian.extras.api.confluence.ConfluenceLicense;
import com.atlassian.plugin.spring.scanner.annotation.component.ConfluenceComponent;
import com.atlassian.plugin.spring.scanner.annotation.imports.ConfluenceImport;
import com.atlassian.plugins.authentication.impl.license.ProductLicenseChecker;
import java.util.Set;
import javax.inject.Inject;

@ConfluenceComponent
public class ConfluenceLicenseChecker
implements ProductLicenseChecker {
    private final LicenseService licenseService;
    private final UserChecker userChecker;

    @Inject
    public ConfluenceLicenseChecker(@ConfluenceImport LicenseService licenseService, @ConfluenceImport UserChecker userChecker) {
        this.licenseService = licenseService;
        this.userChecker = userChecker;
    }

    @Override
    public boolean areSlotsAvailable(Set<String> groupNames) {
        try {
            ConfluenceLicense license = this.licenseService.retrieve();
            if (license.isUnlimitedNumberOfUsers()) {
                return true;
            }
            return this.userChecker.getNumberOfRegisteredUsers() < license.getMaximumNumberOfUsers();
        }
        catch (LicenseException e) {
            return false;
        }
    }
}

