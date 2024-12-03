/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.UserAccessor
 */
package com.atlassian.confluence.extra.calendar3.aomigration;

import com.atlassian.confluence.extra.calendar3.aomigration.AbstractJiraSubCalendarDataStoreProvider;
import com.atlassian.confluence.extra.calendar3.aomigration.SubCalendarPropertyProvider;
import com.atlassian.confluence.user.UserAccessor;

public class AgileSprintsSubCalendarDataStoreProvider
extends AbstractJiraSubCalendarDataStoreProvider {
    private static final String STORE_KEY = "AGILE_SPRINTS_SUB_CALENDAR_STORE";

    public AgileSprintsSubCalendarDataStoreProvider(UserAccessor userAccessor, SubCalendarPropertyProvider subCalendarPropertyProvider) {
        super(userAccessor, subCalendarPropertyProvider);
    }

    @Override
    public String getProviderKey() {
        return STORE_KEY;
    }
}

