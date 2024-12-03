/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.velocity.htmlsafe.HtmlSafe
 *  com.atlassian.sal.api.message.Message
 *  com.google.common.base.Predicate
 *  com.google.common.base.Predicates
 *  com.google.common.collect.Collections2
 *  org.joda.time.DateTime
 *  org.joda.time.Days
 *  org.joda.time.ReadableInstant
 *  org.joda.time.format.DateTimeFormat
 *  org.joda.time.format.DateTimeFormatter
 */
package com.atlassian.confluence.extra.calendar3.upcomingevents;

import com.atlassian.confluence.extra.calendar3.CalendarManager;
import com.atlassian.confluence.extra.calendar3.CalendarSettingsManager;
import com.atlassian.confluence.extra.calendar3.model.LocalizedSubCalendarEvent;
import com.atlassian.confluence.extra.calendar3.model.PersistedSubCalendar;
import com.atlassian.confluence.extra.calendar3.model.SubCalendarEvent;
import com.atlassian.confluence.extra.calendar3.model.SubscribingSubCalendar;
import com.atlassian.confluence.extra.calendar3.upcomingevents.ExpandedLocalizedSubCalendarEvent;
import com.atlassian.confluence.velocity.htmlsafe.HtmlSafe;
import com.atlassian.sal.api.message.Message;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.ReadableInstant;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class UpcomingEventsCalendar {
    private final CalendarManager calendarManager;
    private final CalendarSettingsManager calendarSettingsManager;
    private final boolean showLinkToMyCalendars;
    private final DateTime today;
    private final DateTime rangeStart;
    private final DateTime rangeEnd;
    private final Map<DateTime, List<LocalizedSubCalendarEvent>> eventsGroup;
    private final int maxEventsPerGroup;
    private final boolean hasAuthenticationError;
    private final Collection<Message> errorMessages;
    private final DateTimeFormatter dayFormatter;
    private final DateTimeFormatter monthNameFormatter;
    private final DateTimeFormatter dayNameFormatter;
    private final DateTimeFormatter timeFormatter;

    public UpcomingEventsCalendar(CalendarManager calendarManager, CalendarSettingsManager calendarSettingsManager, Locale userLocale, boolean showLinkToMyCalendars, DateTime today, DateTime rangeStart, DateTime rangeEnd, Map<DateTime, List<LocalizedSubCalendarEvent>> eventsGroup, int maxEventsPerGroup, Collection<Message> errorMessages) {
        this.calendarManager = calendarManager;
        this.calendarSettingsManager = calendarSettingsManager;
        this.showLinkToMyCalendars = showLinkToMyCalendars;
        this.today = today;
        this.rangeStart = rangeStart;
        this.rangeEnd = rangeEnd;
        this.eventsGroup = eventsGroup;
        this.maxEventsPerGroup = maxEventsPerGroup;
        this.errorMessages = null == errorMessages ? Collections.emptySet() : Collections2.filter(errorMessages, (Predicate)Predicates.not(message -> StringUtils.equals("calendar3.notification.recommended.error.credentialsrequired", message.getKey())));
        this.hasAuthenticationError = null != errorMessages && errorMessages.size() != this.errorMessages.size();
        this.dayFormatter = DateTimeFormat.forPattern((String)"dd").withZone(null).withLocale(userLocale);
        this.monthNameFormatter = DateTimeFormat.forPattern((String)"MMM").withZone(null).withLocale(userLocale);
        this.dayNameFormatter = DateTimeFormat.forPattern((String)"EEEE").withZone(null).withLocale(userLocale);
        this.timeFormatter = DateTimeFormat.forPattern((String)(calendarSettingsManager.isTimeFormat24Hour() ? "H:mm" : "h:mm a")).withZone(null).withLocale(userLocale);
    }

    public boolean isShowLinkToMyCalendars() {
        return this.showLinkToMyCalendars;
    }

    public DateTime getToday() {
        return this.today;
    }

    public DateTime getRangeStart() {
        return this.rangeStart;
    }

    public DateTime getRangeEnd() {
        return this.rangeEnd;
    }

    public int getDays() {
        return Days.daysBetween((ReadableInstant)this.getRangeStart(), (ReadableInstant)this.getRangeEnd()).getDays();
    }

    public Map<DateTime, List<LocalizedSubCalendarEvent>> getEventsGroup() {
        return this.eventsGroup;
    }

    public int getMaxEventsPerGroup() {
        return this.maxEventsPerGroup;
    }

    public Collection<Message> getErrorMessages() {
        return this.errorMessages;
    }

    public boolean hasAuthenticationError() {
        return this.hasAuthenticationError;
    }

    public boolean isDateToday(DateTime aDate) {
        DateTime today = this.getToday();
        return today.getYear() == aDate.getYear() && today.getMonthOfYear() == aDate.getMonthOfYear() && today.getDayOfMonth() == aDate.getDayOfMonth();
    }

    @HtmlSafe
    public String getEventColorAsHex(SubCalendarEvent subCalendarEvent) {
        return "#" + this.calendarManager.getSubCalendarColorAsHexValue(subCalendarEvent.getSubCalendar().getColor());
    }

    @HtmlSafe
    public String getDayFormatted(DateTime dateTime) {
        return this.dayFormatter.print((ReadableInstant)dateTime);
    }

    @HtmlSafe
    public String getMonthNameFormatted(DateTime dateTime) {
        return this.monthNameFormatter.print((ReadableInstant)dateTime);
    }

    @HtmlSafe
    public String getDayNameFormatted(DateTime dateTime) {
        return this.dayNameFormatter.print((ReadableInstant)dateTime);
    }

    @HtmlSafe
    public String getTimeFormatted(DateTime dateTime) {
        return this.timeFormatter.print((ReadableInstant)dateTime);
    }

    @HtmlSafe
    public String getSubCalendarId(SubCalendarEvent subCalendarEvent) {
        PersistedSubCalendar subCalendar = subCalendarEvent.getSubCalendar();
        return subCalendar instanceof SubscribingSubCalendar ? ((SubscribingSubCalendar)subCalendar).getSubscriptionId() : subCalendar.getId();
    }

    public boolean isLastExpandedInstance(SubCalendarEvent subCalendarEvent) {
        return subCalendarEvent instanceof ExpandedLocalizedSubCalendarEvent && ((ExpandedLocalizedSubCalendarEvent)subCalendarEvent).isLast();
    }
}

