/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.permission.PermissionEnforcer
 */
package com.atlassian.pats.rest;

import com.atlassian.pats.exception.InvalidLicenseException;
import com.atlassian.pats.utils.LicenseChecker;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.permission.PermissionEnforcer;

public class PermissionChecker {
    private final PermissionEnforcer permissionEnforcer;
    private final LicenseChecker licenseChecker;
    private final I18nResolver i18nResolver;

    public PermissionChecker(PermissionEnforcer permissionEnforcer, LicenseChecker licenseChecker, I18nResolver i18nResolver) {
        this.permissionEnforcer = permissionEnforcer;
        this.licenseChecker = licenseChecker;
        this.i18nResolver = i18nResolver;
    }

    public void verifyPermissions() {
        if (!this.licenseChecker.isDataCenterProduct()) {
            throw new InvalidLicenseException(this.i18nResolver.getText("personal.access.tokens.error.invalid.license"));
        }
        this.permissionEnforcer.enforceSystemAdmin();
    }
}

