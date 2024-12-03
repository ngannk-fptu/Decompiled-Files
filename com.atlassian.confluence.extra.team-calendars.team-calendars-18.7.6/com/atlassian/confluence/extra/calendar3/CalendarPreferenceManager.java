/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.ConfluenceUser
 */
package com.atlassian.confluence.extra.calendar3;

import com.atlassian.confluence.extra.calendar3.model.PersistedSubCalendar;
import com.atlassian.confluence.extra.calendar3.model.UserCalendarPreference;
import com.atlassian.confluence.user.ConfluenceUser;
import java.util.Map;

public interface CalendarPreferenceManager {
    public void setUserPreference(ConfluenceUser var1, UserCalendarPreference var2);

    public UserCalendarPreference getUserPreference(ConfluenceUser var1);

    public void addToView(ConfluenceUser var1, PersistedSubCalendar var2);

    public void removeFromView(ConfluenceUser var1, PersistedSubCalendar var2);

    public void watch(ConfluenceUser var1, PersistedSubCalendar var2);

    public void unwatch(ConfluenceUser var1, PersistedSubCalendar var2);

    public boolean isWatching(ConfluenceUser var1, PersistedSubCalendar var2);

    public Map<String, Boolean> isWatching(ConfluenceUser var1, PersistedSubCalendar ... var2);

    public void hideEvents(ConfluenceUser var1, PersistedSubCalendar var2);

    public void unhideEvents(ConfluenceUser var1, PersistedSubCalendar var2);

    public boolean isEventsHidden(ConfluenceUser var1, PersistedSubCalendar var2);
}

