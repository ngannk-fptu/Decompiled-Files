/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.webresource.QueryParams
 *  com.atlassian.plugin.webresource.condition.UrlReadingCondition
 *  com.atlassian.plugin.webresource.url.UrlBuilder
 */
package com.atlassian.confluence.plugins.requestaccess.condition;

import com.atlassian.confluence.plugins.requestaccess.service.GrantAccessRequestValidator;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.webresource.QueryParams;
import com.atlassian.plugin.webresource.condition.UrlReadingCondition;
import com.atlassian.plugin.webresource.url.UrlBuilder;
import java.util.Map;

public class GrantAccessUrlReadingCondition
implements UrlReadingCondition {
    private static final String REQUEST_ACCESS_QUERY_PARAM_NAME = "request-access";
    private final GrantAccessRequestValidator grantAccessRequestValidator;

    public GrantAccessUrlReadingCondition(GrantAccessRequestValidator grantAccessRequestValidator) {
        this.grantAccessRequestValidator = grantAccessRequestValidator;
    }

    public void init(Map<String, String> stringStringMap) throws PluginParseException {
    }

    public void addToUrl(UrlBuilder urlBuilder) {
        if (this.grantAccessRequestValidator.isGrantAccessRequestValid()) {
            urlBuilder.addToQueryString(REQUEST_ACCESS_QUERY_PARAM_NAME, String.valueOf("true"));
        }
    }

    public boolean shouldDisplay(QueryParams queryParams) {
        return Boolean.parseBoolean(queryParams.get(REQUEST_ACCESS_QUERY_PARAM_NAME));
    }
}

