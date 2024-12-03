/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.setup.settings.GlobalSettingsManager
 *  com.atlassian.soy.renderer.SoyServerFunction
 *  com.google.common.collect.ImmutableSet
 */
package com.atlassian.confluence.plugins.soy;

import com.atlassian.confluence.setup.settings.GlobalSettingsManager;
import com.atlassian.soy.renderer.SoyServerFunction;
import com.google.common.collect.ImmutableSet;
import java.util.Set;

public class SiteTitleFunction
implements SoyServerFunction<String> {
    private final GlobalSettingsManager settingsManager;

    public SiteTitleFunction(GlobalSettingsManager settingsManager) {
        this.settingsManager = settingsManager;
    }

    public String apply(Object ... objects) {
        return this.settingsManager.getGlobalSettings().getSiteTitle();
    }

    public String getName() {
        return "siteTitle";
    }

    public Set<Integer> validArgSizes() {
        return ImmutableSet.of((Object)0);
    }
}

