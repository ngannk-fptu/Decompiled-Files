/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.webresource.QueryParams
 *  com.atlassian.plugin.webresource.condition.UrlReadingCondition
 *  com.atlassian.plugin.webresource.url.UrlBuilder
 */
package com.atlassian.confluence.user.extras.aggregation.impl;

import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.extras.aggregation.impl.AggregationWarningManager;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.webresource.QueryParams;
import com.atlassian.plugin.webresource.condition.UrlReadingCondition;
import com.atlassian.plugin.webresource.url.UrlBuilder;
import java.util.Map;

public class ShouldDisplayDirectoryAggregationWarningUrlReadingCondition
implements UrlReadingCondition {
    private final AggregationWarningManager warningManager;
    private static final String AGGREGATION_WARNING_QUERY_PARAM = "directory-aggregation-warning";

    public ShouldDisplayDirectoryAggregationWarningUrlReadingCondition(AggregationWarningManager aggregationWarningManager) {
        this.warningManager = aggregationWarningManager;
    }

    public void init(Map<String, String> map) throws PluginParseException {
    }

    public void addToUrl(UrlBuilder urlBuilder) {
        if (this.shouldShowDirectoryAggregationWarning()) {
            urlBuilder.addToQueryString(AGGREGATION_WARNING_QUERY_PARAM, String.valueOf(true));
        }
    }

    public boolean shouldDisplay(QueryParams queryParams) {
        return Boolean.valueOf(queryParams.get(AGGREGATION_WARNING_QUERY_PARAM));
    }

    public boolean shouldShowDirectoryAggregationWarning() {
        return this.warningManager.shouldShow(AuthenticatedUserThreadLocal.getUsername());
    }
}

