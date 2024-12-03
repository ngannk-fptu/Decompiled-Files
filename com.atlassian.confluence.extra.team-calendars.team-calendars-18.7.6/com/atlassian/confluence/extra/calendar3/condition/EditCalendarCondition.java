/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext
 *  com.atlassian.confluence.plugin.descriptor.web.conditions.BaseConfluenceCondition
 */
package com.atlassian.confluence.extra.calendar3.condition;

import com.atlassian.confluence.extra.calendar3.CalendarPermissionManager;
import com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext;
import com.atlassian.confluence.plugin.descriptor.web.conditions.BaseConfluenceCondition;

public class EditCalendarCondition
extends BaseConfluenceCondition {
    private final CalendarPermissionManager calendarPermissionManager;

    public EditCalendarCondition(CalendarPermissionManager calendarPermissionManager) {
        this.calendarPermissionManager = calendarPermissionManager;
    }

    protected boolean shouldDisplay(WebInterfaceContext webInterfaceContext) {
        return this.calendarPermissionManager.hasEditSubCalendarPrivilege(webInterfaceContext.getCurrentUser());
    }
}

