/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.plugin.services.VelocityHelperService
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.message.Message
 *  com.atlassian.user.User
 *  org.joda.time.DateTime
 *  org.joda.time.DateTimeZone
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.calendar3.upcomingevents;

import com.atlassian.confluence.extra.calendar3.CalendarManager;
import com.atlassian.confluence.extra.calendar3.CalendarSettingsManager;
import com.atlassian.confluence.extra.calendar3.JodaIcal4jTimeZoneMapper;
import com.atlassian.confluence.extra.calendar3.upcomingevents.MailSummaryRenderer;
import com.atlassian.confluence.extra.calendar3.upcomingevents.UpcomingEventsCalendar;
import com.atlassian.confluence.extra.calendar3.upcomingevents.UpcomingEventsHelper;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.plugin.services.VelocityHelperService;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.message.Message;
import com.atlassian.user.User;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DefaultMailSummaryRenderer
implements MailSummaryRenderer {
    private static final int MAX_EVENTS_PER_DAY = Integer.getInteger("com.atlassian.confluence.extra.calendar3.display.events.calendar.maxdailysummary", 3);
    private static final int MAX_EVENTS_DAILY = Integer.getInteger("com.atlassian.confluence.extra.calendar3.display.events.calendar.maxperdaysummary", 4);
    private static final int MAX_EVENTS_WEEKLY = Integer.getInteger("com.atlassian.confluence.extra.calendar3.display.events.calendar.maxweeklysummary", 8);
    private static final int MAX_DESCRIPTION_LENGTH = 100;
    private final UpcomingEventsHelper upcomingEventsHelper;
    private final JodaIcal4jTimeZoneMapper jodaIcal4jTimeZoneMapper;
    private final CalendarManager calendarManager;
    private final UserAccessor userAccessor;
    private final VelocityHelperService velocityHelperService;
    private final LocaleManager localeManager;
    private final CalendarSettingsManager calendarSettingsManager;

    @Autowired
    public DefaultMailSummaryRenderer(UpcomingEventsHelper upcomingEventsHelper, JodaIcal4jTimeZoneMapper jodaIcal4jTimeZoneMapper, CalendarManager calendarManager, @ComponentImport UserAccessor userAccessor, @ComponentImport VelocityHelperService velocityHelperService, @ComponentImport LocaleManager localeManager, CalendarSettingsManager calendarSettingsManager) {
        this.calendarManager = calendarManager;
        this.upcomingEventsHelper = upcomingEventsHelper;
        this.jodaIcal4jTimeZoneMapper = jodaIcal4jTimeZoneMapper;
        this.userAccessor = userAccessor;
        this.velocityHelperService = velocityHelperService;
        this.localeManager = localeManager;
        this.calendarSettingsManager = calendarSettingsManager;
    }

    @Override
    public String renderUpcomingEventsForMail(Collection<String> subCalendarIds, boolean daily, ConfluenceUser user) {
        int maxEvents;
        int numDays;
        Map velocityContext = this.velocityHelperService.createDefaultVelocityContext();
        if (daily) {
            velocityContext.put("showMoreEvents", false);
            numDays = 2;
            maxEvents = MAX_EVENTS_DAILY;
        } else {
            velocityContext.put("showMoreEvents", true);
            numDays = 7;
            maxEvents = MAX_EVENTS_WEEKLY;
        }
        Set<String> subCalendarsInView = this.calendarManager.getSubCalendarsInView(this.userAccessor.getUserByName(user.getName()));
        DateTime userNow = new DateTime(DateTimeZone.forID((String)this.jodaIcal4jTimeZoneMapper.getUserTimeZoneIdJoda(user)));
        DateTime rangeStart = new DateTime(userNow.getYear(), userNow.getMonthOfYear(), userNow.getDayOfMonth(), 0, 0, 0, 0, userNow.getZone()).plusDays(1);
        DateTime rangeEnd = rangeStart.plusDays(numDays);
        HashSet<Message> errorMessageCollection = new HashSet<Message>();
        UpcomingEventsCalendar upcomingEventsCalendar = new UpcomingEventsCalendar(this.calendarManager, this.calendarSettingsManager, this.localeManager.getLocale((User)user), false, rangeStart, rangeStart, rangeEnd, this.upcomingEventsHelper.getEventsGroup(this.userAccessor.getUserByName(user.getName()), true, rangeStart, rangeEnd, subCalendarsInView, errorMessageCollection), MAX_EVENTS_PER_DAY, errorMessageCollection);
        velocityContext.put("upcomingEventsCalendar", upcomingEventsCalendar);
        velocityContext.put("emptyPersonalCalendar", this.calendarManager.isPersonalCalendarEmpty(this.userAccessor.getUserByName(user.getName())));
        velocityContext.put("maxEvents", maxEvents);
        velocityContext.put("maxDescriptionLength", 100);
        return this.velocityHelperService.getRenderedTemplate("com/atlassian/confluence/extra/calendar3/templates/velocity/calendar-mail-panel.vm", velocityContext);
    }
}

