/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.calendar3.calendarstore.supporters;

import com.atlassian.confluence.extra.calendar3.model.AbstractJiraSubCalendar;
import net.fortuna.ical4j.model.Calendar;

public interface JiraReminderSupport<T extends AbstractJiraSubCalendar> {
    public void updateJiraReminderNewEvents(T var1, Calendar var2);
}

