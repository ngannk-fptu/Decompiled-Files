/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.webresource.QueryParams
 *  com.atlassian.plugin.webresource.condition.UrlReadingCondition
 *  com.atlassian.plugin.webresource.url.UrlBuilder
 */
package com.atlassian.confluence.plugin.descriptor.web.urlreadingconditions;

import com.atlassian.confluence.security.SpacePermissionManager;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.webresource.QueryParams;
import com.atlassian.plugin.webresource.condition.UrlReadingCondition;
import com.atlassian.plugin.webresource.url.UrlBuilder;
import java.util.Map;

public class AnonymousAccessEnabledUrlReadingCondition
implements UrlReadingCondition {
    private final SpacePermissionManager spacePermissionManager;
    private static final String ANONYMOUS_ACCESS_ENABLED_QUERY_PARAM = "anonymous-access-enabled";

    public AnonymousAccessEnabledUrlReadingCondition(SpacePermissionManager spacePermissionManager) {
        this.spacePermissionManager = spacePermissionManager;
    }

    public void init(Map<String, String> params) throws PluginParseException {
    }

    public void addToUrl(UrlBuilder urlBuilder) {
        if (this.isAnonymousAccessEnabled()) {
            urlBuilder.addToQueryString(ANONYMOUS_ACCESS_ENABLED_QUERY_PARAM, String.valueOf(true));
        }
    }

    public boolean shouldDisplay(QueryParams params) {
        return Boolean.valueOf(params.get(ANONYMOUS_ACCESS_ENABLED_QUERY_PARAM));
    }

    private boolean isAnonymousAccessEnabled() {
        return this.spacePermissionManager.hasPermission("USECONFLUENCE", null, null);
    }
}

