/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.web.Condition
 *  com.atlassian.sal.api.permission.PermissionEnforcer
 */
package com.atlassian.ratelimiting.internal.frontend;

import com.atlassian.plugin.web.Condition;
import com.atlassian.ratelimiting.license.LicenseChecker;
import com.atlassian.sal.api.permission.PermissionEnforcer;
import java.util.Map;

public class RateLimitingEnabledCondition
implements Condition {
    private final LicenseChecker licenseChecker;
    private final PermissionEnforcer permissionEnforcer;

    public RateLimitingEnabledCondition(LicenseChecker licenseChecker, PermissionEnforcer permissionEnforcer) {
        this.licenseChecker = licenseChecker;
        this.permissionEnforcer = permissionEnforcer;
    }

    public void init(Map<String, String> map) {
    }

    public boolean shouldDisplay(Map<String, Object> map) {
        return this.licenseChecker.isDataCenterLicensed() && this.permissionEnforcer.isSystemAdmin();
    }
}

