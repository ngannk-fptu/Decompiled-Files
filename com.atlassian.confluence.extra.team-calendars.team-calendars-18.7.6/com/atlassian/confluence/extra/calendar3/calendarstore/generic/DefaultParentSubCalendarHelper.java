/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.ConfluenceUser
 */
package com.atlassian.confluence.extra.calendar3.calendarstore.generic;

import com.atlassian.confluence.extra.calendar3.calendarstore.CalendarDataStore;
import com.atlassian.confluence.extra.calendar3.calendarstore.generic.ParentSubCalendarHelper;
import com.atlassian.confluence.extra.calendar3.model.PersistedSubCalendar;
import com.atlassian.confluence.extra.calendar3.model.SubscribingSubCalendar;
import com.atlassian.confluence.user.ConfluenceUser;

public class DefaultParentSubCalendarHelper
implements ParentSubCalendarHelper {
    private final CalendarDataStore<PersistedSubCalendar> parentSubCalendarDataStore;

    public DefaultParentSubCalendarHelper(CalendarDataStore<PersistedSubCalendar> parentSubCalendarDataStore) {
        this.parentSubCalendarDataStore = parentSubCalendarDataStore;
    }

    @Override
    public PersistedSubCalendar getParentSubCalendar(String parentSubCalendarId) {
        return this.parentSubCalendarDataStore.getSubCalendar(parentSubCalendarId);
    }

    @Override
    public boolean canEditParentSubCalendarEvents(PersistedSubCalendar parentSubCalendar, ConfluenceUser user) {
        PersistedSubCalendar parent = this.getParentSubCalendar(parentSubCalendar instanceof SubscribingSubCalendar ? ((SubscribingSubCalendar)parentSubCalendar).getSubscriptionId() : parentSubCalendar.getId());
        return parent != null && this.parentSubCalendarDataStore.hasEditEventPrivilege(parent, user);
    }

    @Override
    public boolean hasViewEventPrivilege(String parentSubCalendarId, ConfluenceUser user) {
        return this.parentSubCalendarDataStore.hasViewEventPrivilege(parentSubCalendarId, user);
    }

    @Override
    public boolean hasEditEventPrivilege(PersistedSubCalendar parentSubCalendar, ConfluenceUser user) {
        return this.parentSubCalendarDataStore.hasEditEventPrivilege(parentSubCalendar, user);
    }

    @Override
    public boolean hasDeletePrivilege(PersistedSubCalendar parentSubCalendar, ConfluenceUser user) {
        return this.parentSubCalendarDataStore.hasDeletePrivilege(parentSubCalendar, user);
    }

    @Override
    public boolean hasAdminPrivilege(PersistedSubCalendar parentSubCalendar, ConfluenceUser user) {
        return this.parentSubCalendarDataStore.hasAdminPrivilege(parentSubCalendar, user);
    }
}

