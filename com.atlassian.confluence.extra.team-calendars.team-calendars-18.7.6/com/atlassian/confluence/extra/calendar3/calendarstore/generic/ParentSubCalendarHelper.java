/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.ConfluenceUser
 */
package com.atlassian.confluence.extra.calendar3.calendarstore.generic;

import com.atlassian.confluence.extra.calendar3.model.PersistedSubCalendar;
import com.atlassian.confluence.user.ConfluenceUser;

public interface ParentSubCalendarHelper {
    public PersistedSubCalendar getParentSubCalendar(String var1);

    public boolean canEditParentSubCalendarEvents(PersistedSubCalendar var1, ConfluenceUser var2);

    public boolean hasViewEventPrivilege(String var1, ConfluenceUser var2);

    public boolean hasEditEventPrivilege(PersistedSubCalendar var1, ConfluenceUser var2);

    public boolean hasDeletePrivilege(PersistedSubCalendar var1, ConfluenceUser var2);

    public boolean hasAdminPrivilege(PersistedSubCalendar var1, ConfluenceUser var2);
}

