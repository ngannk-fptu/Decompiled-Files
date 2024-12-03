/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext
 *  com.atlassian.confluence.plugin.descriptor.web.conditions.BaseConfluenceCondition
 *  com.atlassian.confluence.user.ConfluenceUser
 */
package com.atlassian.confluence.extra.calendar3.condition;

import com.atlassian.confluence.extra.calendar3.CalendarManager;
import com.atlassian.confluence.extra.calendar3.license.LicenseAccessor;
import com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext;
import com.atlassian.confluence.plugin.descriptor.web.conditions.BaseConfluenceCondition;
import com.atlassian.confluence.user.ConfluenceUser;

public class ShowTimezoneSelectCondition
extends BaseConfluenceCondition {
    private final CalendarManager calendarManager;
    private final LicenseAccessor licenseAccessor;

    public ShowTimezoneSelectCondition(CalendarManager calendarManager, LicenseAccessor licenseAccessor) {
        this.calendarManager = calendarManager;
        this.licenseAccessor = licenseAccessor;
    }

    protected boolean shouldDisplay(WebInterfaceContext webInterfaceContext) {
        ConfluenceUser user = webInterfaceContext.getCurrentUser();
        return this.licenseAccessor.isLicenseSetup() && user != null && !this.calendarManager.getUserPreference(user).getDisabledMessageKeys().contains("MESSAGE_KEY_TIMEZONE_SETUP");
    }
}

