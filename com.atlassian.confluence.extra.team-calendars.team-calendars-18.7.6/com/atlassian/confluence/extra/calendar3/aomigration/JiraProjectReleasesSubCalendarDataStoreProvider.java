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

public class JiraProjectReleasesSubCalendarDataStoreProvider
extends AbstractJiraSubCalendarDataStoreProvider {
    private static final String STORE_KEY = "JIRA_PROJECT_RELEASES_SUB_CALENDAR_STORE";

    public JiraProjectReleasesSubCalendarDataStoreProvider(UserAccessor userAccessor, SubCalendarPropertyProvider subCalendarPropertyProvider) {
        super(userAccessor, subCalendarPropertyProvider);
    }

    @Override
    public String getProviderKey() {
        return STORE_KEY;
    }
}

