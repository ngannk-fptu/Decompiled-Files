/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.google.common.base.Predicate
 *  com.google.common.base.Predicates
 *  com.google.common.collect.Collections2
 *  com.google.common.collect.Lists
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.extra.calendar3.calendarstore;

import com.atlassian.confluence.extra.calendar3.JodaIcal4jTimeZoneMapper;
import com.atlassian.confluence.extra.calendar3.calendarstore.BaseCacheableCalendarDataStore;
import com.atlassian.confluence.extra.calendar3.calendarstore.DataStoreCommonPropertyAccessor;
import com.atlassian.confluence.extra.calendar3.calendarstore.RefreshableCalendarDataStore;
import com.atlassian.confluence.extra.calendar3.model.PersistedSubCalendar;
import com.atlassian.confluence.extra.calendar3.model.SubCalendar;
import com.atlassian.confluence.extra.calendar3.model.persistence.SubCalendarEntity;
import com.atlassian.confluence.user.ConfluenceUser;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Content;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VTimeZone;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ExternalCalendarDataStore<T extends PersistedSubCalendar>
extends BaseCacheableCalendarDataStore<T>
implements RefreshableCalendarDataStore<T> {
    private static final Logger LOG = LoggerFactory.getLogger(ExternalCalendarDataStore.class);
    protected static final String delimiter = "::";

    protected ExternalCalendarDataStore(DataStoreCommonPropertyAccessor dataStoreCommonPropertyAccessor) {
        super(dataStoreCommonPropertyAccessor);
    }

    @Override
    public void setSubCalendarContent(T subCalendar, Calendar subCalendarData) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("External sub-calendars are read-only");
    }

    @Override
    public String getSubCalendarDataCacheKey(T subCalendar) {
        return ((PersistedSubCalendar)subCalendar).getId() + delimiter + ((SubCalendar)subCalendar).getSourceLocation();
    }

    @Override
    public Calendar getSubCalendarContent(T subCalendar) throws Exception {
        Calendar subCalendarContent = this.getSubCalendarContentInternal(subCalendar);
        SubCalendarEntity subCalendarEntity = this.getSubCalendarEntity(((PersistedSubCalendar)subCalendar).getId());
        String timezoneId = this.getSubCalendarTimeZoneId(subCalendarContent);
        if (!timezoneId.equalsIgnoreCase(subCalendarEntity.getTimeZoneId())) {
            subCalendarEntity.setTimeZoneId(timezoneId);
            subCalendarEntity.save();
        }
        return subCalendarContent;
    }

    protected abstract Calendar getSubCalendarContentInternal(T var1) throws Exception;

    protected String getSubCalendarTimeZoneId(Calendar subCalendarContent) throws Exception {
        String subCalendarTimeZoneId = null;
        JodaIcal4jTimeZoneMapper jodaIcal4jTimeZoneMapper = this.getJodaIcal4jTimeZoneMapper();
        if (null != subCalendarContent) {
            VTimeZone subCalendarTimeZoneComponent = (VTimeZone)subCalendarContent.getComponent("VTIMEZONE");
            if (null != subCalendarTimeZoneComponent) {
                subCalendarTimeZoneId = jodaIcal4jTimeZoneMapper.toJodaTimeZone(subCalendarTimeZoneComponent.getTimeZoneId().getValue()).getID();
            } else {
                Object xWrTimeZone = subCalendarContent.getProperty("X-WR-TIMEZONE");
                if (null != xWrTimeZone) {
                    subCalendarTimeZoneId = jodaIcal4jTimeZoneMapper.toJodaTimeZone(((Content)xWrTimeZone).getValue()).getID();
                }
            }
        }
        if (StringUtils.isNotEmpty(subCalendarTimeZoneId)) {
            return subCalendarTimeZoneId;
        }
        return StringUtils.defaultIfEmpty(jodaIcal4jTimeZoneMapper.getSystemTimeZoneIdJoda(), jodaIcal4jTimeZoneMapper.getSupportedTimeZoneIds().iterator().next());
    }

    @Override
    public boolean hasEditEventPrivilege(T subCalendar, ConfluenceUser user) {
        return false;
    }

    @Override
    public boolean hasAdminPrivilege(T subCalendar, ConfluenceUser user) {
        return super.hasEditEventPrivilege(subCalendar, user);
    }

    @Override
    public boolean hasReloadEventsPrivilege(T subCalendar, ConfluenceUser user) {
        return this.hasViewEventPrivilege(((PersistedSubCalendar)subCalendar).getId(), user);
    }

    @Override
    public void refresh(T subCalendar) {
    }

    @Override
    public List<VEvent> getEvents(T subCalendar, Predicate<VEvent> vEventPredicate, String ... vEventUids) {
        com.google.common.base.Predicate filter = vEvent -> {
            long match = Arrays.stream(vEventUids).filter(vEventUid -> StringUtils.equals(vEventUid, vEvent.getUid().getValue())).count();
            return match > 0L;
        };
        if (vEventPredicate != null) {
            filter = Predicates.and((com.google.common.base.Predicate)filter, vEventPredicate::test);
        }
        try {
            return Lists.newArrayList((Iterable)Collections2.filter(this.getSubCalendarContent(subCalendar).getComponents("VEVENT"), (com.google.common.base.Predicate)filter));
        }
        catch (Exception unableToReadEvents) {
            LOG.error(String.format("Unable to find events from sub-calendar %s", ((PersistedSubCalendar)subCalendar).getId()), (Throwable)unableToReadEvents);
            return Collections.emptyList();
        }
    }

    @Override
    public VEvent getEvent(T subCalendar, String vEventUid, String recurrenceId) {
        return super.getEvent(subCalendar, vEventUid, recurrenceId);
    }

    @Override
    public VEvent addEvent(T subCalendar, VEvent newEventDetails) {
        throw new UnsupportedOperationException("External sub-calendars are read-only.");
    }

    @Override
    public VEvent updateEvent(T subCalendar, VEvent newEventDetails) {
        throw new UnsupportedOperationException("External sub-calendars are read-only.");
    }

    @Override
    public void deleteEvent(T subCalendar, String vEventUid, String recurrenceId) {
        throw new UnsupportedOperationException("External sub-calendars are read-only.");
    }

    @Override
    public void moveEvent(T subCalendar, String vEventUid, PersistedSubCalendar destinationSubCalendar) {
        throw new UnsupportedOperationException("External sub-calendars are read-only.");
    }
}

