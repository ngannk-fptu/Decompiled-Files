/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.calendar3.caldav;

import com.atlassian.confluence.extra.calendar3.caldav.node.CalendarCalDAVCollection;
import com.atlassian.confluence.extra.calendar3.caldav.node.CalendarCalDAVEvent;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.TimeZone;
import org.apache.commons.lang.StringUtils;
import org.bedework.caldav.server.CalDAVEvent;
import org.bedework.caldav.server.Organizer;
import org.bedework.caldav.server.SysiIcalendar;
import org.bedework.util.calendar.IcalDefs;
import org.bedework.webdav.servlet.shared.WdEntity;
import org.bedework.webdav.servlet.shared.WebdavException;

public final class CalendarSysiIcalendar
extends SysiIcalendar {
    private final PropertyList<Property> calendarProperties;
    private final CalendarCalDAVCollection collection;
    private final Collection<CalendarCalDAVEvent> events;
    private final List<CalendarCalDAVEvent> iteratingList;

    public CalendarSysiIcalendar(PropertyList<Property> calendarProperties, CalendarCalDAVCollection collection, Collection<CalendarCalDAVEvent> events) {
        this.calendarProperties = calendarProperties;
        this.collection = collection;
        this.events = events;
        this.iteratingList = this.calculateIteratingList();
    }

    private List<CalendarCalDAVEvent> calculateIteratingList() {
        Map<String, List<CalendarCalDAVEvent>> groupByUid = this.events.stream().collect(Collectors.groupingBy(event -> event.getSubCalendarEvent().getUid()));
        return groupByUid.entrySet().stream().flatMap(entry -> {
            List sameEvents = (List)entry.getValue();
            return sameEvents.size() == 1 ? sameEvents.stream() : sameEvents.stream().filter(calendarCalDAVEvent -> StringUtils.isNotEmpty(calendarCalDAVEvent.getSubCalendarEvent().getRecurrenceId()));
        }).collect(Collectors.toList());
    }

    @Override
    public String getProdid() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getVersion() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getCalscale() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getMethod() {
        Property method = (Property)this.calendarProperties.getProperty("METHOD");
        return method != null ? method.getValue() : null;
    }

    @Override
    public Collection<TimeZone> getTimeZones() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<Object> getComponents() {
        throw new UnsupportedOperationException();
    }

    @Override
    public IcalDefs.IcalComponentType getComponentType() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getMethodType() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getMethodType(String val) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getMethodName(int mt) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Organizer getOrganizer() {
        throw new UnsupportedOperationException();
    }

    @Override
    public CalDAVEvent getEvent() throws WebdavException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<WdEntity> iterator() {
        return this;
    }

    @Override
    public int size() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean validItipMethodType() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean requestMethodType() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean replyMethodType() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean itipRequestMethodType(int mt) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean itipReplyMethodType(int mt) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean validItipMethodType(int val) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean hasNext() {
        return this.iteratingList.iterator().hasNext();
    }

    @Override
    public WdEntity next() {
        return this.iteratingList.iterator().next();
    }
}

