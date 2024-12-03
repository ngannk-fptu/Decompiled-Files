/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext
 *  com.atlassian.confluence.plugin.descriptor.web.conditions.user.UserLoggedInCondition
 */
package com.atlassian.confluence.extra.calendar3.condition;

import com.atlassian.confluence.extra.calendar3.CalendarPermissionManager;
import com.atlassian.confluence.extra.calendar3.license.LicenseAccessor;
import com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext;
import com.atlassian.confluence.plugin.descriptor.web.conditions.user.UserLoggedInCondition;

public class InsertTeamCalendarsFromEditorCondition
extends UserLoggedInCondition {
    private final LicenseAccessor licenseAccessor;
    private final CalendarPermissionManager calendarPermissionManager;

    public InsertTeamCalendarsFromEditorCondition(LicenseAccessor licenseAccessor, CalendarPermissionManager calendarPermissionManager) {
        this.licenseAccessor = licenseAccessor;
        this.calendarPermissionManager = calendarPermissionManager;
    }

    public boolean shouldDisplay(WebInterfaceContext context) {
        return super.shouldDisplay(context) && !this.licenseAccessor.isLicenseInvalidated() && this.calendarPermissionManager.hasEditSubCalendarPrivilege(context.getCurrentUser());
    }
}

