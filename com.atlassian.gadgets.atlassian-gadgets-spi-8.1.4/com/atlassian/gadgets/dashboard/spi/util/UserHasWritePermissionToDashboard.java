/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.gadgets.dashboard.DashboardId
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.web.Condition
 */
package com.atlassian.gadgets.dashboard.spi.util;

import com.atlassian.gadgets.dashboard.DashboardId;
import com.atlassian.gadgets.dashboard.spi.DashboardPermissionService;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.web.Condition;
import java.util.Map;

public class UserHasWritePermissionToDashboard
implements Condition {
    private final DashboardPermissionService permissionService;

    public UserHasWritePermissionToDashboard(DashboardPermissionService permissionService) {
        this.permissionService = permissionService;
    }

    public boolean shouldDisplay(Map<String, Object> context) {
        DashboardId dashboardId = (DashboardId)context.get("dashboardId");
        String username = (String)context.get("username");
        return this.permissionService.isWritableBy(dashboardId, username);
    }

    public void init(Map<String, String> params) throws PluginParseException {
    }
}

