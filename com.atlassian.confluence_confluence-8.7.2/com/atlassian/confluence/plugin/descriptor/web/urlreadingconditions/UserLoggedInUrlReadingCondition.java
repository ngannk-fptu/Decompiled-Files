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
package com.atlassian.confluence.plugin.descriptor.web.urlreadingconditions;

import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.webresource.QueryParams;
import com.atlassian.plugin.webresource.url.UrlBuilder;
import com.atlassian.webresource.api.prebake.Coordinate;
import com.atlassian.webresource.api.prebake.DimensionAwareUrlReadingCondition;
import com.atlassian.webresource.api.prebake.Dimensions;
import java.util.Map;

public class UserLoggedInUrlReadingCondition
implements DimensionAwareUrlReadingCondition {
    private static final String USER_LOGGED_IN_QUERY_PARAM = "user-logged-in";

    public void init(Map<String, String> params) throws PluginParseException {
    }

    public void addToUrl(UrlBuilder urlBuilder) {
        if (this.isUserLoggedIn()) {
            urlBuilder.addToQueryString(USER_LOGGED_IN_QUERY_PARAM, String.valueOf(true));
        }
    }

    public boolean shouldDisplay(QueryParams params) {
        return Boolean.valueOf(params.get(USER_LOGGED_IN_QUERY_PARAM));
    }

    private boolean isUserLoggedIn() {
        return !AuthenticatedUserThreadLocal.isAnonymousUser();
    }

    public Dimensions computeDimensions() {
        return Dimensions.empty().andExactly(USER_LOGGED_IN_QUERY_PARAM, new String[]{String.valueOf(true)}).andAbsent(USER_LOGGED_IN_QUERY_PARAM);
    }

    public void addToUrl(UrlBuilder urlBuilder, Coordinate coordinate) {
        coordinate.copyTo(urlBuilder, USER_LOGGED_IN_QUERY_PARAM);
    }
}

