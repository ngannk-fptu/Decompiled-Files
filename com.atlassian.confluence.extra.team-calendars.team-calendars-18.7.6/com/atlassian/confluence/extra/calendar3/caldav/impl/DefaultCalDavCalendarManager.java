/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.calendar3.caldav.impl;

import com.atlassian.confluence.extra.calendar3.CalendarManager;
import com.atlassian.confluence.extra.calendar3.caldav.CalDavCalendarManager;
import com.atlassian.confluence.extra.calendar3.caldav.filter.FilterBase;
import com.atlassian.confluence.extra.calendar3.caldav.filter.RecurrenceRetrieval;
import com.atlassian.confluence.extra.calendar3.ical4j.VEventMapper;
import com.atlassian.confluence.extra.calendar3.model.PersistedSubCalendar;
import com.atlassian.confluence.extra.calendar3.model.SubCalendarEvent;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Predicate;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.Organizer;
import net.fortuna.ical4j.model.property.XProperty;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DefaultCalDavCalendarManager
implements CalDavCalendarManager {
    private final CalendarManager calendarManager;
    private final VEventMapper vEventMapper;

    @Autowired
    public DefaultCalDavCalendarManager(CalendarManager calendarManager, VEventMapper vEventMapper) {
        this.calendarManager = calendarManager;
        this.vEventMapper = vEventMapper;
    }

    @Override
    public Calendar toCalendar(PersistedSubCalendar subCalendar, Collection<SubCalendarEvent> events) throws Exception {
        Objects.requireNonNull(subCalendar);
        Objects.requireNonNull(events);
        Calendar newCalendar = this.calendarManager.createEmptyCalendarForSubCalendar(subCalendar);
        boolean useUTCForCalendar = false;
        ComponentList<CalendarComponent> subCalendarContentComponents = newCalendar.getComponents();
        for (SubCalendarEvent subCalendarEvent : events) {
            VEvent vEvent = this.vEventMapper.toVEvent(subCalendar, subCalendarEvent);
            boolean bl = useUTCForCalendar = null != subCalendarEvent.getExtraProperties() && null != subCalendarEvent.getExtraProperties().get("skipSubCalendarTimezone");
            if (StringUtils.isNotEmpty(subCalendarEvent.getCustomEventTypeId())) {
                vEvent.getProperties().add(new XProperty("X-CONFLUENCE-CUSTOM-TYPE-ID", subCalendarEvent.getCustomEventTypeId()));
            }
            subCalendarContentComponents.add(vEvent);
        }
        if (useUTCForCalendar) {
            CalendarComponent currentTimezone = newCalendar.getComponent("VTIMEZONE");
            newCalendar.getComponents().remove(currentTimezone);
        }
        return newCalendar;
    }

    @Override
    public Collection<SubCalendarEvent> query(PersistedSubCalendar subCalendar, FilterBase filter, RecurrenceRetrieval recurrenceRetrieval) throws Exception {
        return this.calendarManager.query(AuthenticatedUserThreadLocal.get(), subCalendar, filter, recurrenceRetrieval);
    }

    @Override
    public Collection<SubCalendarEvent> getEvents(PersistedSubCalendar subCalendar, Predicate<VEvent> vEventPredicate, String ... vEventUids) throws Exception {
        return this.calendarManager.getEvents(subCalendar, vEventPredicate, vEventUids);
    }

    @Override
    public Calendar transform(PersistedSubCalendar persistedSubCalendar, Calendar source) throws Exception {
        Objects.nonNull(persistedSubCalendar);
        Objects.nonNull(source);
        Calendar exportedCalendar = this.calendarManager.transform(persistedSubCalendar, source);
        ComponentList eventComponents = exportedCalendar.getComponents("VEVENT");
        eventComponents.stream().forEach(eventComponent -> {
            PropertyList<Property> eventPropertiesList = eventComponent.getProperties();
            Organizer organizer = (Organizer)eventPropertiesList.getProperty("ORGANIZER");
            if (organizer != null) {
                eventPropertiesList.remove(organizer);
            }
        });
        return exportedCalendar;
    }
}

