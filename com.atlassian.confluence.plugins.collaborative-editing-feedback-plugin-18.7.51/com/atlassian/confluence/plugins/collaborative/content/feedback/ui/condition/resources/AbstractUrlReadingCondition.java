/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.webresource.QueryParams
 *  com.atlassian.plugin.webresource.condition.UrlReadingCondition
 *  com.atlassian.plugin.webresource.url.UrlBuilder
 */
package com.atlassian.confluence.plugins.collaborative.content.feedback.ui.condition.resources;

import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.webresource.QueryParams;
import com.atlassian.plugin.webresource.condition.UrlReadingCondition;
import com.atlassian.plugin.webresource.url.UrlBuilder;
import java.util.Map;

public abstract class AbstractUrlReadingCondition
implements UrlReadingCondition {
    public void init(Map<String, String> params) throws PluginParseException {
    }

    public void addToUrl(UrlBuilder urlBuilder) {
        urlBuilder.addToQueryString(this.getQueryParamName(), String.valueOf(this.getQueryParamValue()));
    }

    public boolean shouldDisplay(QueryParams params) {
        return Boolean.parseBoolean(params.get(this.getQueryParamName()));
    }

    protected abstract String getQueryParamName();

    protected abstract boolean getQueryParamValue();
}

