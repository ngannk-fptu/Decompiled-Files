/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.ConfluenceUser
 */
package com.atlassian.confluence.extra.calendar3;

import com.atlassian.confluence.extra.calendar3.model.UserCalendarPreference;
import com.atlassian.confluence.user.ConfluenceUser;
import java.util.List;

public interface CalendarUserPreferenceStore {
    public List<String> list(long var1, long var3) throws Exception;

    public void setUserPreference(ConfluenceUser var1, UserCalendarPreference var2);

    public void clearUserPreferenceCache(ConfluenceUser var1);

    public UserCalendarPreference getUserPreference(ConfluenceUser var1);
}

