/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.feature;

import java.util.Set;

public interface SiteDarkFeaturesDao {
    public Set<String> getSiteEnabledFeatures();

    public boolean enableSiteFeature(String var1);

    public boolean disableSiteFeature(String var1);
}

