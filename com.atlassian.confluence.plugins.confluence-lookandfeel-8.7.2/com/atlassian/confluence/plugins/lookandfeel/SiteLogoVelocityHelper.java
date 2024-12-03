/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.lookandfeel;

import com.atlassian.confluence.plugins.lookandfeel.SiteLogoManager;
import java.util.Objects;

public class SiteLogoVelocityHelper {
    private final SiteLogoManager siteLogoManager;

    public SiteLogoVelocityHelper(SiteLogoManager siteLogoManager) {
        this.siteLogoManager = Objects.requireNonNull(siteLogoManager);
    }

    public boolean isUsesCustomLogo() {
        return this.siteLogoManager.useCustomLogo();
    }

    public String getHeaderLogoImageUrl() {
        return this.siteLogoManager.getSiteLogoUrl();
    }
}

