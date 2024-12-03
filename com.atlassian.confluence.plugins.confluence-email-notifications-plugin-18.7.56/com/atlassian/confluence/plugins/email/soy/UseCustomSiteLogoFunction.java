/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugins.lookandfeel.SiteLogoManager
 *  com.atlassian.soy.renderer.SoyServerFunction
 *  com.google.common.collect.ImmutableSet
 */
package com.atlassian.confluence.plugins.email.soy;

import com.atlassian.confluence.plugins.lookandfeel.SiteLogoManager;
import com.atlassian.soy.renderer.SoyServerFunction;
import com.google.common.collect.ImmutableSet;
import java.util.Set;

public class UseCustomSiteLogoFunction
implements SoyServerFunction<Boolean> {
    private static final Set<Integer> ARGS = ImmutableSet.builder().add((Object)0).build();
    private SiteLogoManager siteLogoManager;

    public UseCustomSiteLogoFunction(SiteLogoManager siteLogoManager) {
        this.siteLogoManager = siteLogoManager;
    }

    public String getName() {
        return "useCustomSiteLogo";
    }

    public Boolean apply(Object ... args) {
        return this.siteLogoManager.useCustomLogo();
    }

    public Set<Integer> validArgSizes() {
        return ARGS;
    }
}

