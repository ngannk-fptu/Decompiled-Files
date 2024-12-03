/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.ContentQuery
 *  com.atlassian.confluence.content.CustomContentEntityObject
 */
package com.atlassian.confluence.extra.calendar3.contenttype.hibernatequery;

import com.atlassian.confluence.content.ContentQuery;
import com.atlassian.confluence.content.CustomContentEntityObject;
import java.util.List;

public class CalendarContentQueryFactory {
    public static ContentQuery<CustomContentEntityObject> findCalendarById(String calendarId) {
        return new ContentQuery("calendar.findCalendarById", new Object[]{calendarId});
    }

    public static ContentQuery<CustomContentEntityObject> findAllCalendarsById(List<String> calendarIds) {
        return new ContentQuery("calendar.findAllCalendarsById", new Object[]{calendarIds});
    }

    public static ContentQuery<CustomContentEntityObject> findCalendarBySpaceKey(String spaceKey) {
        return new ContentQuery("calendar.findCalendarBySpaceKey", new Object[]{spaceKey});
    }

    public static ContentQuery<CustomContentEntityObject> findAllCalendarsBySpaceKeys(List<String> spaceKeys) {
        return new ContentQuery("calendar.findAllCalendarsBySpaceKeys", new Object[]{spaceKeys});
    }

    public static ContentQuery<CustomContentEntityObject> getAllSpaceCalendarViewContent() {
        return new ContentQuery("calendar.getAllSpaceCalendarsContent", null);
    }

    public static ContentQuery<CustomContentEntityObject> getAllCalendars() {
        return new ContentQuery("calendar.getAllCalendarContents", null);
    }
}

