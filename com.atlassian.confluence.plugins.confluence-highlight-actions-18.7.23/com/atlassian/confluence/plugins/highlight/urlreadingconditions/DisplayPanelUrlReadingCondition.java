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
 */
package com.atlassian.confluence.plugins.highlight.urlreadingconditions;

import com.atlassian.confluence.plugins.highlight.service.HighlightOptionPanelConfigService;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.webresource.QueryParams;
import com.atlassian.plugin.webresource.url.UrlBuilder;
import com.atlassian.webresource.api.prebake.Coordinate;
import com.atlassian.webresource.api.prebake.DimensionAwareUrlReadingCondition;
import com.atlassian.webresource.api.prebake.Dimensions;
import java.util.Map;

public class DisplayPanelUrlReadingCondition
implements DimensionAwareUrlReadingCondition {
    private static final String QUERY_PARAM = "highlightactions";
    private final HighlightOptionPanelConfigService highlightOptionPanelConfigService;
    private boolean highlightConfigSupported;

    public DisplayPanelUrlReadingCondition(HighlightOptionPanelConfigService highlightOptionPanelConfigService) {
        this.highlightOptionPanelConfigService = highlightOptionPanelConfigService;
    }

    public void init(Map<String, String> stringStringMap) throws PluginParseException {
        this.highlightConfigSupported = this.highlightOptionPanelConfigService.isSupported();
    }

    public void addToUrl(UrlBuilder urlBuilder) {
        if (this.isHighlightActionEnabled()) {
            urlBuilder.addToQueryString(QUERY_PARAM, String.valueOf(true));
        }
    }

    private boolean isHighlightActionEnabled() {
        return !this.highlightConfigSupported || this.highlightOptionPanelConfigService.isEnabled();
    }

    public boolean shouldDisplay(QueryParams queryParams) {
        return Boolean.valueOf(queryParams.get(QUERY_PARAM));
    }

    public Dimensions computeDimensions() {
        return Dimensions.empty().andExactly(QUERY_PARAM, new String[]{String.valueOf(true)}).andAbsent(QUERY_PARAM);
    }

    public void addToUrl(UrlBuilder urlBuilder, Coordinate coordinate) {
        coordinate.copyTo(urlBuilder, QUERY_PARAM);
    }
}

