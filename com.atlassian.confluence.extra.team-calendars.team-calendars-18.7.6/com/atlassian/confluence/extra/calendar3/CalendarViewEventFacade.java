/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.calendar3;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.confluence.extra.calendar3.CalendarRenderer;
import com.atlassian.confluence.extra.calendar3.events.CalendarEvent;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CalendarViewEventFacade {
    private static final Logger LOG = LoggerFactory.getLogger(CalendarViewEventFacade.class);
    private final EventPublisher eventPublisher;

    @Autowired
    public CalendarViewEventFacade(@ComponentImport EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public void publishEvent(Object eventSource, ConfluenceUser trigger, String view, String calendarContext) {
        CalendarEvent event = null;
        CalendarRenderer.CalendarView selectedView = null;
        try {
            selectedView = CalendarRenderer.CalendarView.valueOf(view);
        }
        catch (IllegalArgumentException ex) {
            LOG.debug("Unknown view type: " + view);
        }
        if (selectedView.equals((Object)CalendarRenderer.CalendarView.month)) {
            event = new SubCalendarMonthViewedEvent(eventSource, trigger, calendarContext);
        } else if (selectedView.equals((Object)CalendarRenderer.CalendarView.agendaWeek)) {
            event = new SubCalendarWeekViewedEvent(eventSource, trigger, calendarContext);
        } else if (selectedView.equals((Object)CalendarRenderer.CalendarView.basicDay)) {
            event = new SubCalendarListViewedEvent(eventSource, trigger, calendarContext);
        } else if (selectedView.equals((Object)CalendarRenderer.CalendarView.agendaDay)) {
            event = new SubCalendarUpcomingViewedEvent(eventSource, trigger, calendarContext);
        } else if (selectedView.equals((Object)CalendarRenderer.CalendarView.timeline)) {
            event = new SubCalendarTimelineViewedEvent(eventSource, trigger, calendarContext);
        }
        this.eventPublisher.publish((Object)event);
    }

    public static class SubCalendarTimelineViewedEvent
    extends CalendarEvent {
        private String calendarContext;

        public SubCalendarTimelineViewedEvent(Object eventSource, ConfluenceUser trigger, String calendarContext) {
            super(eventSource, trigger);
            this.calendarContext = calendarContext;
        }

        public String getContext() {
            return this.calendarContext;
        }

        public String getViewName() {
            return "timeline";
        }

        @EventName
        public String calculateEventName() {
            return "teamcalendars.view.render";
        }
    }

    public static class SubCalendarUpcomingViewedEvent
    extends CalendarEvent {
        private String calendarContext;

        public SubCalendarUpcomingViewedEvent(Object eventSource, ConfluenceUser trigger, String calendarContext) {
            super(eventSource, trigger);
            this.calendarContext = calendarContext;
        }

        public String getContext() {
            return this.calendarContext;
        }

        public String getViewName() {
            return "upcoming";
        }

        @EventName
        public String calculateEventName() {
            return "teamcalendars.view.render";
        }
    }

    public static class SubCalendarListViewedEvent
    extends CalendarEvent {
        private String calendarContext;

        public SubCalendarListViewedEvent(Object eventSource, ConfluenceUser trigger, String calendarContext) {
            super(eventSource, trigger);
            this.calendarContext = calendarContext;
        }

        public String getContext() {
            return this.calendarContext;
        }

        public String getViewName() {
            return "list";
        }

        @EventName
        public String calculateEventName() {
            return "teamcalendars.view.render";
        }
    }

    public static class SubCalendarWeekViewedEvent
    extends CalendarEvent {
        private String calendarContext;

        public SubCalendarWeekViewedEvent(Object eventSource, ConfluenceUser trigger, String calendarContext) {
            super(eventSource, trigger);
            this.calendarContext = calendarContext;
        }

        public String getContext() {
            return this.calendarContext;
        }

        public String getViewName() {
            return "week";
        }

        @EventName
        public String calculateEventName() {
            return "teamcalendars.view.render";
        }
    }

    public static class SubCalendarMonthViewedEvent
    extends CalendarEvent {
        private String calendarContext;

        public SubCalendarMonthViewedEvent(Object eventSource, ConfluenceUser trigger, String calendarContext) {
            super(eventSource, trigger);
            this.calendarContext = calendarContext;
        }

        public String getContext() {
            return this.calendarContext;
        }

        public String getViewName() {
            return "month";
        }

        @EventName
        public String calculateEventName() {
            return "teamcalendars.view.render";
        }
    }
}

