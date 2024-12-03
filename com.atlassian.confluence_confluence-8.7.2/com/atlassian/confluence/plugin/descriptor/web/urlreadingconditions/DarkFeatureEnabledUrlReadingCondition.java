/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.webresource.QueryParams
 *  com.atlassian.plugin.webresource.url.UrlBuilder
 *  com.atlassian.webresource.api.prebake.Coordinate
 *  com.atlassian.webresource.api.prebake.DimensionAwareUrlReadingCondition
 *  com.atlassian.webresource.api.prebake.Dimensions
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.plugin.descriptor.web.urlreadingconditions;

import com.atlassian.confluence.setup.settings.DarkFeaturesManager;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.webresource.QueryParams;
import com.atlassian.plugin.webresource.url.UrlBuilder;
import com.atlassian.webresource.api.prebake.Coordinate;
import com.atlassian.webresource.api.prebake.DimensionAwareUrlReadingCondition;
import com.atlassian.webresource.api.prebake.Dimensions;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

public class DarkFeatureEnabledUrlReadingCondition
implements DimensionAwareUrlReadingCondition {
    private final DarkFeaturesManager darkFeaturesManager;
    private String darkFeatureKey;

    public DarkFeatureEnabledUrlReadingCondition(DarkFeaturesManager darkFeaturesManager) {
        this.darkFeaturesManager = darkFeaturesManager;
    }

    public void init(Map<String, String> params) throws PluginParseException {
        String key = params.get("key");
        if (key == null || StringUtils.isBlank((CharSequence)key)) {
            throw new PluginParseException("Dark feature key must not be empty");
        }
        this.darkFeatureKey = key;
    }

    public void addToUrl(UrlBuilder urlBuilder) {
        if (this.darkFeaturesManager.getDarkFeatures().isFeatureEnabled(this.darkFeatureKey)) {
            urlBuilder.addToQueryString(this.darkFeatureKey, String.valueOf(true));
        }
    }

    public boolean shouldDisplay(QueryParams params) {
        return Boolean.valueOf(params.get(this.darkFeatureKey));
    }

    public Dimensions computeDimensions() {
        return Dimensions.empty().andExactly(this.darkFeatureKey, new String[]{String.valueOf(true)}).andAbsent(this.darkFeatureKey);
    }

    public void addToUrl(UrlBuilder urlBuilder, Coordinate coordinate) {
        coordinate.copyTo(urlBuilder, this.darkFeatureKey);
    }
}

