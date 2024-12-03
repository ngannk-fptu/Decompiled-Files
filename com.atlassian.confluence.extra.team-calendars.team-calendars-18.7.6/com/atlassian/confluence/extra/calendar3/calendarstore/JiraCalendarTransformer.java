/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.calendar3.calendarstore;

import com.atlassian.confluence.extra.calendar3.model.SubCalendarEvent;
import net.fortuna.ical4j.model.component.VEvent;

public interface JiraCalendarTransformer {
    public SubCalendarEvent transformJiraEvent(SubCalendarEvent var1, VEvent var2);
}

