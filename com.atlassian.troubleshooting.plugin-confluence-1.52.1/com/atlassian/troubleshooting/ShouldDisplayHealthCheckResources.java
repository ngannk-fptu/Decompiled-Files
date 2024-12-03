/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.webresource.QueryParams
 *  com.atlassian.plugin.webresource.condition.UrlReadingCondition
 *  com.atlassian.plugin.webresource.url.UrlBuilder
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.sal.api.user.UserManager
 *  com.google.common.base.Preconditions
 */
package com.atlassian.troubleshooting;

import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.webresource.QueryParams;
import com.atlassian.plugin.webresource.condition.UrlReadingCondition;
import com.atlassian.plugin.webresource.url.UrlBuilder;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;
import com.google.common.base.Preconditions;
import java.util.Map;

public class ShouldDisplayHealthCheckResources
implements UrlReadingCondition {
    private static final String HEALTHCHECK_RESOURCES_QUERY_PARAM = "healthcheck-resources";
    private final UserManager userManager;

    public ShouldDisplayHealthCheckResources(UserManager userManager) {
        this.userManager = (UserManager)Preconditions.checkNotNull((Object)userManager);
    }

    public void init(Map<String, String> params) throws PluginParseException {
    }

    public void addToUrl(UrlBuilder urlBuilder) {
        if (this.isUserSysAdmin()) {
            urlBuilder.addToQueryString(HEALTHCHECK_RESOURCES_QUERY_PARAM, String.valueOf(true));
        }
    }

    public boolean shouldDisplay(QueryParams params) {
        return Boolean.valueOf(params.get(HEALTHCHECK_RESOURCES_QUERY_PARAM));
    }

    private boolean isUserSysAdmin() {
        UserKey userkey = this.userManager.getRemoteUserKey();
        return userkey != null && this.userManager.isSystemAdmin(userkey);
    }
}

