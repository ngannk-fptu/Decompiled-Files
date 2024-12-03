/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext
 *  com.atlassian.confluence.plugin.descriptor.web.conditions.BaseConfluenceCondition
 */
package com.atlassian.confluence.extra.calendar3.condition;

import com.atlassian.confluence.extra.calendar3.license.LicenseAccessor;
import com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext;
import com.atlassian.confluence.plugin.descriptor.web.conditions.BaseConfluenceCondition;

public class LicenseCondition
extends BaseConfluenceCondition {
    LicenseAccessor licenseAccessor;

    public LicenseCondition(LicenseAccessor licenseAccessor) {
        this.licenseAccessor = licenseAccessor;
    }

    protected boolean shouldDisplay(WebInterfaceContext webInterfaceContext) {
        return !this.licenseAccessor.isLicenseInvalidated();
    }
}

