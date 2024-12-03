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

public class CanClusterCondition
extends BaseConfluenceCondition {
    private LicenseService licenseService;

    @Override
    public boolean shouldDisplay(WebInterfaceContext context) {
        return this.licenseService.isLicensedForDataCenter();
    }

    public void setLicenseService(LicenseService licenseService) {
        this.licenseService = (LicenseService)Preconditions.checkNotNull((Object)licenseService);
    }
}

