/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.web.Condition
 */
package com.atlassian.confluence.plugins.lookandfeel;

import com.atlassian.confluence.plugins.lookandfeel.SiteLogoManager;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.web.Condition;
import java.util.Map;

public class UseCustomSiteLogoCondition
implements Condition {
    private final SiteLogoManager siteLogoManager;

    public UseCustomSiteLogoCondition(SiteLogoManager siteLogoManager) {
        this.siteLogoManager = siteLogoManager;
    }

    public void init(Map<String, String> params) throws PluginParseException {
    }

    public boolean shouldDisplay(Map<String, Object> context) {
        return this.siteLogoManager.useCustomLogo();
    }
}

