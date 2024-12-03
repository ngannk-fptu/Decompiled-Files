/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.filters.ServletContextThreadLocal
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.webresource.QueryParams
 *  com.atlassian.plugin.webresource.condition.UrlReadingCondition
 *  com.atlassian.plugin.webresource.url.UrlBuilder
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.confluence.plugins.mobile.condition;

import com.atlassian.core.filters.ServletContextThreadLocal;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.webresource.QueryParams;
import com.atlassian.plugin.webresource.condition.UrlReadingCondition;
import com.atlassian.plugin.webresource.url.UrlBuilder;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

public class MobileAppResourceCondition
implements UrlReadingCondition {
    private static final String MOBILE_APP_LOGIN_RESOURCE_QUERY_PARAM_KEY = "mobile-app-login-resource";

    public void init(Map<String, String> map) throws PluginParseException {
    }

    public void addToUrl(UrlBuilder urlBuilder) {
        if (this.isMobileAppLogin()) {
            urlBuilder.addToQueryString(MOBILE_APP_LOGIN_RESOURCE_QUERY_PARAM_KEY, "true");
        }
    }

    public boolean shouldDisplay(QueryParams queryParams) {
        return Boolean.valueOf(queryParams.get(MOBILE_APP_LOGIN_RESOURCE_QUERY_PARAM_KEY));
    }

    private boolean isMobileAppLogin() {
        HttpServletRequest request = ServletContextThreadLocal.getRequest();
        if (request == null) {
            return false;
        }
        String userAgent = request.getHeader("User-Agent");
        return userAgent != null && userAgent.contains("AtlassianMobileApp");
    }
}

