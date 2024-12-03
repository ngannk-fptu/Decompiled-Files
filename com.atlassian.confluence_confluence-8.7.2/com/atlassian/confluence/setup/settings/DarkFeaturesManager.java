/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.setup.settings;

import com.atlassian.confluence.setup.settings.DarkFeatures;
import com.atlassian.confluence.setup.settings.UnknownFeatureException;
import com.atlassian.confluence.user.ConfluenceUser;

public interface DarkFeaturesManager {
    public DarkFeatures getDarkFeatures();

    public DarkFeatures getDarkFeatures(ConfluenceUser var1);

    public DarkFeatures getSiteDarkFeatures();

    public void enableUserFeature(String var1) throws UnknownFeatureException;

    public void enableUserFeature(ConfluenceUser var1, String var2) throws UnknownFeatureException;

    public void disableUserFeature(String var1) throws UnknownFeatureException;

    public void disableUserFeature(ConfluenceUser var1, String var2) throws UnknownFeatureException;

    public void enableSiteFeature(String var1) throws UnknownFeatureException;

    public void disableSiteFeature(String var1) throws UnknownFeatureException;

    public DarkFeatures getDarkFeaturesAllUsers();
}

