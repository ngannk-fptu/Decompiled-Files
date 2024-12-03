/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bandana.BandanaContext
 *  com.atlassian.bandana.BandanaManager
 *  com.google.common.collect.Sets
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.impl.feature;

import com.atlassian.bandana.BandanaContext;
import com.atlassian.bandana.BandanaManager;
import com.atlassian.confluence.impl.feature.SiteDarkFeaturesDao;
import com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext;
import com.google.common.collect.Sets;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;

public class BandanaSiteDarkFeaturesDao
implements SiteDarkFeaturesDao {
    static final String SITE_DARKFEATURE_BANDANA_KEY = "confluence.darkfeature";
    private final BandanaManager bandanaManager;

    public BandanaSiteDarkFeaturesDao(BandanaManager bandanaManager) {
        this.bandanaManager = bandanaManager;
    }

    @Override
    public Set<String> getSiteEnabledFeatures() {
        String features = (String)this.bandanaManager.getValue((BandanaContext)ConfluenceBandanaContext.GLOBAL_CONTEXT, SITE_DARKFEATURE_BANDANA_KEY);
        return BandanaSiteDarkFeaturesDao.deserialize(features);
    }

    @Override
    public boolean enableSiteFeature(String featureKey) {
        Set<String> enabledFeatures = this.getSiteEnabledFeatures();
        if (enabledFeatures.contains(featureKey)) {
            return false;
        }
        enabledFeatures.add(featureKey);
        this.updateSiteEnabledDarkFeatures(enabledFeatures);
        return true;
    }

    @Override
    public boolean disableSiteFeature(String featureKey) {
        Set<String> enabledFeatures = this.getSiteEnabledFeatures();
        if (!enabledFeatures.contains(featureKey)) {
            return false;
        }
        enabledFeatures.remove(featureKey);
        this.updateSiteEnabledDarkFeatures(enabledFeatures);
        return true;
    }

    private void updateSiteEnabledDarkFeatures(Set<String> enabledFeatures) {
        this.bandanaManager.setValue((BandanaContext)ConfluenceBandanaContext.GLOBAL_CONTEXT, SITE_DARKFEATURE_BANDANA_KEY, (Object)BandanaSiteDarkFeaturesDao.serialize(enabledFeatures));
    }

    private static String serialize(Set<String> features) {
        return StringUtils.join(features, (String)",");
    }

    private static Set<String> deserialize(String features) {
        if (StringUtils.isBlank((CharSequence)features)) {
            return new HashSet<String>();
        }
        return Sets.newHashSet((Object[])features.split(","));
    }
}

