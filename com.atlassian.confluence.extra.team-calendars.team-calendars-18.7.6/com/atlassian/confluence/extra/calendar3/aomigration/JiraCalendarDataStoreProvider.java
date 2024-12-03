/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.UserAccessor
 *  org.json.JSONObject
 */
package com.atlassian.confluence.extra.calendar3.aomigration;

import com.atlassian.confluence.extra.calendar3.ActiveObjectsServiceWrapper;
import com.atlassian.confluence.extra.calendar3.aomigration.AbstractBandanaSubCalendarProvider;
import com.atlassian.confluence.extra.calendar3.aomigration.AbstractLegacyDataStoreProvider;
import com.atlassian.confluence.extra.calendar3.aomigration.SubCalendarPropertyProvider;
import com.atlassian.confluence.extra.calendar3.model.persistence.SubCalendarEntity;
import com.atlassian.confluence.user.UserAccessor;
import org.json.JSONObject;

public class JiraCalendarDataStoreProvider
extends AbstractLegacyDataStoreProvider {
    public static final String STORE_KEY = "com.atlassian.confluence.extra.calendar3.calendarstore.JiraCalendarDataStore";
    private final SubCalendarPropertyProvider subCalendarPropertyProvider;

    public JiraCalendarDataStoreProvider(UserAccessor userAccessor, SubCalendarPropertyProvider subCalendarPropertyProvider) {
        super(userAccessor);
        this.subCalendarPropertyProvider = subCalendarPropertyProvider;
    }

    @Override
    public String getProviderKey() {
        return STORE_KEY;
    }

    @Override
    public boolean requiresNewParent() {
        return true;
    }

    @Override
    public SubCalendarEntity createSubCalendarEntity(ActiveObjectsServiceWrapper activeObjectsWrapper, boolean subCalendarMigratedForUserKey, JSONObject theSubCalendar) {
        SubCalendarEntity subCalendarEntity = super.createSubCalendarEntity(activeObjectsWrapper, subCalendarMigratedForUserKey, theSubCalendar);
        return this.subCalendarPropertyProvider.addProperties(activeObjectsWrapper, subCalendarEntity, theSubCalendar, new AbstractBandanaSubCalendarProvider.BandanaProviderJsonPropertyGetter(this));
    }

    @Override
    protected String getStoreKey() {
        return "JIRA_ISSUE_DATES_SUB_CALENDAR_STORE";
    }

    @Override
    public boolean requiresEventsMigration() {
        return false;
    }
}

