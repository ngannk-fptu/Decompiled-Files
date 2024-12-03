/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.message.I18nResolver
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.troubleshooting.confluence.pup;

import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.troubleshooting.preupgrade.accessors.LicenseCompatibilityChecker;
import com.atlassian.troubleshooting.preupgrade.accessors.PupEnvironmentAccessor;
import com.atlassian.troubleshooting.preupgrade.accessors.PupPlatformAccessor;
import com.atlassian.troubleshooting.preupgrade.checks.AbstractPlatformsChecker;
import org.springframework.beans.factory.annotation.Autowired;

public class ConfluencePlatformsChecker
extends AbstractPlatformsChecker {
    @Autowired
    public ConfluencePlatformsChecker(I18nResolver i18n, PupEnvironmentAccessor pupEnvironmentAccessor, PupPlatformAccessor pupPlatformAccessor, LicenseCompatibilityChecker licenseCompatibilityChecker) {
        super(i18n, pupEnvironmentAccessor, pupPlatformAccessor, licenseCompatibilityChecker);
    }
}

