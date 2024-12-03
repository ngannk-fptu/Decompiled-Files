/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.ConfluenceUser
 */
package com.atlassian.confluence.extra.calendar3;

import com.atlassian.confluence.extra.calendar3.model.PersistedSubCalendar;
import com.atlassian.confluence.user.ConfluenceUser;
import java.util.Set;

public interface CalendarPermissionManager {
    public boolean hasEditSubCalendarPrivilege(ConfluenceUser var1);

    public boolean hasReloadEventsPrivilege(PersistedSubCalendar var1, ConfluenceUser var2);

    public boolean hasDeleteSubCalendarPrivilege(PersistedSubCalendar var1, ConfluenceUser var2);

    public boolean hasAdminSubCalendarPrivilege(PersistedSubCalendar var1, ConfluenceUser var2);

    public boolean hasViewEventPrivilege(PersistedSubCalendar var1, ConfluenceUser var2);

    public boolean hasEditEventPrivilege(PersistedSubCalendar var1, ConfluenceUser var2);

    public Set<ConfluenceUser> getEventEditUserRestrictions(PersistedSubCalendar var1);

    public Set<String> getEventEditGroupRestrictions(PersistedSubCalendar var1);

    public Set<ConfluenceUser> getEventViewUserRestrictions(PersistedSubCalendar var1);

    public Set<String> getEventViewGroupRestrictions(PersistedSubCalendar var1);

    public void restrictEventEditToUsers(PersistedSubCalendar var1, Set<ConfluenceUser> var2);

    public void restrictEventEditToGroups(PersistedSubCalendar var1, Set<String> var2);

    public void restrictEventViewToUsers(PersistedSubCalendar var1, Set<ConfluenceUser> var2);

    public void restrictEventViewToGroups(PersistedSubCalendar var1, Set<String> var2);
}

