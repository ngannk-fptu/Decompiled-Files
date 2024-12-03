/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.BiMap
 *  com.google.common.collect.ImmutableBiMap
 */
package com.atlassian.confluence.extra.calendar3.calendarstore.generic;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;

public class KeyStoreToEventTypeMapper {
    public static BiMap<String, String> mapper = ImmutableBiMap.builder().put((Object)"com.atlassian.confluence.extra.calendar3.calendarstore.generic.GenericLocalSubCalendarDataStore", (Object)"other").put((Object)"com.atlassian.confluence.extra.calendar3.calendarstore.generic.LeaveSubCalendarDataStore", (Object)"leaves").put((Object)"com.atlassian.confluence.extra.calendar3.calendarstore.generic.TravelSubCalendarDataStore", (Object)"travel").put((Object)"com.atlassian.confluence.extra.calendar3.calendarstore.generic.BirthdaySubCalendarDataStore", (Object)"birthdays").put((Object)"AGILE_SPRINTS_SUB_CALENDAR_STORE", (Object)"jira-agile-sprint").put((Object)"JIRA_ISSUE_DATES_SUB_CALENDAR_STORE", (Object)"jira").put((Object)"JIRA_PROJECT_RELEASES_SUB_CALENDAR_STORE", (Object)"jira-project-releases").build();

    private KeyStoreToEventTypeMapper() {
        throw new IllegalStateException("Utility class");
    }
}

