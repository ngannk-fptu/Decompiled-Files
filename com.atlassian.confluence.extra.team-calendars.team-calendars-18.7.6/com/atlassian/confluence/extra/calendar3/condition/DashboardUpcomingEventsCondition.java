/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext
 *  com.atlassian.confluence.plugin.descriptor.web.conditions.user.UserLoggedInCondition
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.extra.calendar3.condition;

import com.atlassian.confluence.extra.calendar3.util.CalendarUtil;
import com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext;
import com.atlassian.confluence.plugin.descriptor.web.conditions.user.UserLoggedInCondition;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.user.User;
import java.util.Map;
import org.apache.commons.lang.StringUtils;

public class DashboardUpcomingEventsCondition
extends UserLoggedInCondition {
    private final UserAccessor userAccessor;
    private String location;

    public DashboardUpcomingEventsCondition(UserAccessor userAccessor) {
        this.userAccessor = userAccessor;
    }

    public void init(Map<String, String> params) throws PluginParseException {
        this.location = params.get("location");
    }

    public boolean shouldDisplay(WebInterfaceContext context) {
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        boolean checkLocation = StringUtils.equals(this.location, "atl.dashboard.secondary");
        boolean checkVersion = CalendarUtil.isNewDashBoard();
        return !this.userAccessor.isDeactivated((User)user) && user != null && checkLocation == checkVersion;
    }
}

