/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginParseException
 */
package com.atlassian.plugin.webresource.condition;

import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.webresource.QueryParams;
import com.atlassian.plugin.webresource.url.UrlBuilder;
import com.atlassian.webresource.api.prebake.Coordinate;
import com.atlassian.webresource.api.prebake.DimensionAwareUrlReadingCondition;
import com.atlassian.webresource.api.prebake.Dimensions;
import java.util.Map;

public abstract class SimpleUrlReadingCondition
implements DimensionAwareUrlReadingCondition {
    private static final String TRUE = String.valueOf(true);

    @Override
    public void init(Map<String, String> params) throws PluginParseException {
    }

    @Override
    public void addToUrl(UrlBuilder urlBuilder) {
        if (this.isConditionTrue()) {
            urlBuilder.addToQueryString(this.queryKey(), TRUE);
        }
    }

    @Override
    public Dimensions computeDimensions() {
        String key = this.queryKey();
        return Dimensions.empty().andExactly(key, TRUE).andAbsent(key);
    }

    @Override
    public void addToUrl(UrlBuilder urlBuilder, Coordinate coord) {
        coord.copyTo(urlBuilder, this.queryKey());
    }

    @Override
    public boolean shouldDisplay(QueryParams params) {
        return Boolean.valueOf(params.get(this.queryKey()));
    }

    protected abstract boolean isConditionTrue();

    protected abstract String queryKey();
}

