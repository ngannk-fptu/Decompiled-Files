/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bandana.BandanaManager
 *  com.atlassian.confluence.pages.BlogPost
 *  com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext
 *  com.atlassian.confluence.plugin.descriptor.web.conditions.BaseConfluenceCondition
 */
package com.atlassian.confluence.plugins.gatekeeper.conditions;

import com.atlassian.bandana.BandanaManager;
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext;
import com.atlassian.confluence.plugin.descriptor.web.conditions.BaseConfluenceCondition;
import com.atlassian.confluence.plugins.gatekeeper.license.AddonLicenseManager;
import com.atlassian.confluence.plugins.gatekeeper.service.Configuration;

public class WhoCanViewCondition
extends BaseConfluenceCondition {
    private final BandanaManager bandanaManager;
    private final AddonLicenseManager addonLicenseManager;

    public WhoCanViewCondition(BandanaManager bandanaManager, AddonLicenseManager addonLicenseManager) {
        this.bandanaManager = bandanaManager;
        this.addonLicenseManager = addonLicenseManager;
    }

    protected boolean shouldDisplay(WebInterfaceContext webInterfaceContext) {
        Configuration configuration = new Configuration(this.bandanaManager, this.addonLicenseManager);
        return configuration.isWhoCanViewButtonAllowed() && !(webInterfaceContext.getPage() instanceof BlogPost);
    }
}

