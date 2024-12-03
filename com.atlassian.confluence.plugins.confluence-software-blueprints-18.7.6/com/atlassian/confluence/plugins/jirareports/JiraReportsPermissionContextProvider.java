/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.web.ContextProvider
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.plugins.jirareports;

import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.web.ContextProvider;
import com.atlassian.user.User;
import java.util.Map;

public class JiraReportsPermissionContextProvider
implements ContextProvider {
    private PermissionManager permissionManager;

    public void init(Map<String, String> paramMap) throws PluginParseException {
    }

    public Map<String, Object> getContextMap(Map<String, Object> contextMap) {
        contextMap.put("isAdmin", false);
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        if (user != null) {
            contextMap.put("isAdmin", this.permissionManager.isConfluenceAdministrator((User)user));
        }
        return contextMap;
    }

    public void setPermissionManager(PermissionManager permissionManager) {
        this.permissionManager = permissionManager;
    }
}

