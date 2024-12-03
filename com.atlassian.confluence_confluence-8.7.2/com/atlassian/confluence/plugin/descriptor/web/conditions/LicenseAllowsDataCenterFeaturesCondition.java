/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.atlassian.confluence.plugin.descriptor.web.conditions;

import com.atlassian.confluence.license.LicenseService;
import com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext;
import com.atlassian.confluence.plugin.descriptor.web.conditions.BaseConfluenceCondition;
import com.google.common.base.Preconditions;

public class LicenseAllowsDataCenterFeaturesCondition
extends BaseConfluenceCondition {
    private LicenseService licenseService;

    @Override
    protected boolean shouldDisplay(WebInterfaceContext context) {
        return this.licenseService.isLicensedForDataCenterOrExempt();
    }

    public void setLicenseService(LicenseService licenseService) {
        this.licenseService = (LicenseService)Preconditions.checkNotNull((Object)licenseService);
    }
}

