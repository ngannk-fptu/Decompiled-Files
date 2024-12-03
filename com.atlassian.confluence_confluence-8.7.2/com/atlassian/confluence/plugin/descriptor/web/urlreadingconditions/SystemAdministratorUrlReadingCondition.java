/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.webresource.QueryParams
 *  com.atlassian.plugin.webresource.condition.UrlReadingCondition
 *  com.atlassian.plugin.webresource.url.UrlBuilder
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.plugin.descriptor.web.urlreadingconditions;

import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.webresource.QueryParams;
import com.atlassian.plugin.webresource.condition.UrlReadingCondition;
import com.atlassian.plugin.webresource.url.UrlBuilder;
import com.atlassian.user.User;
import java.util.Map;

public class SystemAdministratorUrlReadingCondition
implements UrlReadingCondition {
    private static final String SYSTEM_ADMINISTRATOR_QUERY_PARAM = "isSystemAdministrator";
    private final PermissionManager permissionManager;

    public SystemAdministratorUrlReadingCondition(PermissionManager permissionManager) {
        this.permissionManager = permissionManager;
    }

    public void init(Map<String, String> stringStringMap) throws PluginParseException {
    }

    public void addToUrl(UrlBuilder urlBuilder) {
        if (this.isSystemAdministrator()) {
            urlBuilder.addToQueryString(SYSTEM_ADMINISTRATOR_QUERY_PARAM, String.valueOf(true));
        }
    }

    public boolean shouldDisplay(QueryParams queryParams) {
        return Boolean.valueOf(queryParams.get(SYSTEM_ADMINISTRATOR_QUERY_PARAM));
    }

    private boolean isSystemAdministrator() {
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        return this.permissionManager.hasPermission((User)user, Permission.ADMINISTER, PermissionManager.TARGET_SYSTEM);
    }
}

