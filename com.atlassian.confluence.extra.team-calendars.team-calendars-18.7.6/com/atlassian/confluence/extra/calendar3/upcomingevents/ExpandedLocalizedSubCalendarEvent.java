/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.FormatSettingsManager
 *  org.joda.time.DateTimeZone
 */
package com.atlassian.confluence.extra.calendar3.upcomingevents;

import com.atlassian.confluence.core.FormatSettingsManager;
import com.atlassian.confluence.extra.calendar3.CalendarSettingsManager;
import com.atlassian.confluence.extra.calendar3.model.LocalizedSubCalendarEvent;
import com.atlassian.confluence.extra.calendar3.model.SubCalendarEvent;
import java.util.Locale;
import org.joda.time.DateTimeZone;

public class ExpandedLocalizedSubCalendarEvent
extends LocalizedSubCalendarEvent {
    private boolean last;

    public ExpandedLocalizedSubCalendarEvent(SubCalendarEvent toCopy, DateTimeZone userTimeZone, Locale locale, FormatSettingsManager formatSettingsManager, CalendarSettingsManager calendarSettingsManager) {
        super(toCopy, userTimeZone, locale, formatSettingsManager, calendarSettingsManager);
    }

    public boolean isLast() {
        return this.last;
    }

    public void setLast(boolean last) {
        this.last = last;
    }
}

