/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.web.Condition
 *  javax.annotation.Nonnull
 */
package com.atlassian.business.insights.core.frontend.conditions;

import com.atlassian.business.insights.core.service.LicenseChecker;
import com.atlassian.plugin.web.Condition;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nonnull;

public class DcLicenseCondition
implements Condition {
    private final LicenseChecker licenseChecker;

    public DcLicenseCondition(@Nonnull LicenseChecker licenseChecker) {
        this.licenseChecker = Objects.requireNonNull(licenseChecker);
    }

    public void init(Map<String, String> map) {
    }

    public boolean shouldDisplay(Map<String, Object> context) {
        return this.licenseChecker.isDcLicense();
    }
}

