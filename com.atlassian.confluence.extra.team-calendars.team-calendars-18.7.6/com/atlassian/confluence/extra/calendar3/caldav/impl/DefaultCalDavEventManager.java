/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.google.common.collect.Lists
 *  org.joda.time.DateTime
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.calendar3.caldav.impl;

import com.atlassian.confluence.extra.calendar3.CalendarManager;
import com.atlassian.confluence.extra.calendar3.CalendarPermissionManager;
import com.atlassian.confluence.extra.calendar3.JodaIcal4jTimeZoneMapper;
import com.atlassian.confluence.extra.calendar3.SubCalendarEventConverter;
import com.atlassian.confluence.extra.calendar3.caldav.CalDavCalendarManager;
import com.atlassian.confluence.extra.calendar3.caldav.CalDavEventManager;
import com.atlassian.confluence.extra.calendar3.caldav.filter.BedeworkFilterConverter;
import com.atlassian.confluence.extra.calendar3.caldav.filter.RecurrenceRetrieval;
import com.atlassian.confluence.extra.calendar3.caldav.filter.RecurrenceRetrievalMode;
import com.atlassian.confluence.extra.calendar3.caldav.filter.SupportedPropertyFilterBaseTransformer;
import com.atlassian.confluence.extra.calendar3.caldav.node.CalendarCalDAVCollection;
import com.atlassian.confluence.extra.calendar3.caldav.node.CalendarCalDAVEvent;
import com.atlassian.confluence.extra.calendar3.caldav.node.HomeCalDAVCollection;
import com.atlassian.confluence.extra.calendar3.calendarstore.InternalSubscriptionCalendarDataStore;
import com.atlassian.confluence.extra.calendar3.calendarstore.SubscriptionCalendarDataStore;
import com.atlassian.confluence.extra.calendar3.events.CalDavEventAdded;
import com.atlassian.confluence.extra.calendar3.events.CalDavEventDeleted;
import com.atlassian.confluence.extra.calendar3.exception.ForbiddenCalendarException;
import com.atlassian.confluence.extra.calendar3.exception.RescheduleAllDayException;
import com.atlassian.confluence.extra.calendar3.model.PersistedSubCalendar;
import com.atlassian.confluence.extra.calendar3.model.SubCalendarEvent;
import com.atlassian.confluence.extra.calendar3.util.CalendarUtil;
import com.atlassian.confluence.extra.calendar3.util.RecurrenceIdPredicate;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.google.common.collect.Lists;
import ietf.params.xml.ns.caldav.ExpandType;
import ietf.params.xml.ns.caldav.LimitRecurrenceSetType;
import java.io.Writer;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.fortuna.ical4j.model.TimeZone;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.Status;
import org.apache.commons.lang.StringUtils;
import org.bedework.access.Acl;
import org.bedework.access.PrivilegeSet;
import org.bedework.caldav.server.CalDAVCollection;
import org.bedework.caldav.server.CalDAVEvent;
import org.bedework.caldav.server.sysinterface.RetrievalMode;
import org.bedework.caldav.server.sysinterface.SysIntf;
import org.bedework.caldav.util.TimeRange;
import org.bedework.caldav.util.filter.FilterBase;
import org.bedework.webdav.servlet.shared.WdEntity;
import org.bedework.webdav.servlet.shared.WebdavException;
import org.bedework.webdav.servlet.shared.WebdavForbidden;
import org.joda.time.DateTime;
import org.oasis_open.docs.ws_calendar.ns.soap.ComponentSelectionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value="calDavEventManager")
public final class DefaultCalDavEventManager
implements CalDavEventManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultCalDavEventManager.class);
    private static final Pattern EVENT_ID_PATTERN = Pattern.compile("(?<id>[a-zA-z0-9\\-]+(@[a-zA-Z0-9\\-.]+)?)(\\.ics)?");
    private final CalendarManager calendarManager;
    private final CalendarPermissionManager calendarPermissionManager;
    private final CalDavCalendarManager calDavCalendarManager;
    private final SubCalendarEventConverter<PersistedSubCalendar> eventConverter;
    private final JodaIcal4jTimeZoneMapper jodaIcal4jTimeZoneMapper;
    private final BedeworkFilterConverter bedeworkFilterConverter;
    private final SupportedPropertyFilterBaseTransformer supportedPropertyFilterBaseTransformer;
    private final EventPublisher eventPublisher;

    @Autowired
    public DefaultCalDavEventManager(CalendarManager calendarManager, CalendarPermissionManager calendarPermissionManager, SubCalendarEventConverter<PersistedSubCalendar> eventConverter, CalDavCalendarManager calDavCalendarManager, JodaIcal4jTimeZoneMapper jodaIcal4jTimeZoneMapper, BedeworkFilterConverter bedeworkFilterConverter, SupportedPropertyFilterBaseTransformer supportedPropertyFilterBaseTransformer, @ComponentImport EventPublisher eventPublisher) {
        this.calendarManager = calendarManager;
        this.calendarPermissionManager = calendarPermissionManager;
        this.eventConverter = eventConverter;
        this.calDavCalendarManager = calDavCalendarManager;
        this.jodaIcal4jTimeZoneMapper = jodaIcal4jTimeZoneMapper;
        this.bedeworkFilterConverter = bedeworkFilterConverter;
        this.supportedPropertyFilterBaseTransformer = supportedPropertyFilterBaseTransformer;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Collection<CalDAVEvent> addEvent(CalDAVEvent event, boolean noInvites, boolean rollbackOnError) throws WebdavException {
        try {
            CalendarCalDAVEvent calendarCalDAVEvent = (CalendarCalDAVEvent)event;
            ArrayList<SubCalendarEvent> addedEventList = new ArrayList<SubCalendarEvent>();
            Set originalEventSet = calendarCalDAVEvent.getSubCalendarEvents().stream().filter(subCalendarEvent -> StringUtils.isBlank(subCalendarEvent.getRecurrenceId())).collect(Collectors.toSet());
            Set rescheduleEventSet = calendarCalDAVEvent.getSubCalendarEvents().stream().filter(subCalendarEvent -> StringUtils.isNotBlank(subCalendarEvent.getRecurrenceId())).collect(Collectors.toSet());
            LOGGER.info("Number of original event to insert is {}", (Object)originalEventSet.size());
            for (SubCalendarEvent newEvent : originalEventSet) {
                addedEventList.add(this.calendarManager.addEvent(newEvent));
            }
            LOGGER.info("Number of reschedule event to insert is {}", (Object)rescheduleEventSet.size());
            for (SubCalendarEvent newEvent : rescheduleEventSet) {
                addedEventList.add(this.calendarManager.addEvent(newEvent));
            }
            List<CalDAVEvent> addEventList = Collections.singletonList(new CalendarCalDAVEvent(calendarCalDAVEvent.getParentPath(), event.getOwner(), true, addedEventList));
            this.eventPublisher.publish((Object)new CalDavEventAdded(this, AuthenticatedUserThreadLocal.get(), true, false));
            return addEventList;
        }
        catch (ForbiddenCalendarException forbiddenCalendarException) {
            throw new WebdavForbidden(forbiddenCalendarException.getMessage());
        }
        catch (Exception exception) {
            throw new WebdavException(exception.getMessage());
        }
    }

    @Override
    public void updateEvent(CalDAVEvent event) throws WebdavException {
        try {
            CalDavEventInformation calDavEventInformation = new CalDavEventInformation();
            CalendarCalDAVEvent calendarCalDAVEvent = (CalendarCalDAVEvent)event;
            Set<SubCalendarEvent> requestUpdateEvents = calendarCalDAVEvent.getSubCalendarEvents();
            SubCalendarEvent firstRequestUpdateEvent = calendarCalDAVEvent.getSubCalendarEvent();
            Set<SubCalendarEvent> dbSubCalendarEvents = this.calendarManager.getEvents(firstRequestUpdateEvent.getSubCalendar(), (VEvent vEvent) -> true, firstRequestUpdateEvent.getUid());
            ArrayList<Exception> lastExceptions = new ArrayList<Exception>();
            if (dbSubCalendarEvents.size() <= requestUpdateEvents.size()) {
                lastExceptions.addAll(this.processAddOrUpdate(requestUpdateEvents, dbSubCalendarEvents));
            } else {
                lastExceptions.addAll(this.processDeleteEvent(requestUpdateEvents, dbSubCalendarEvents));
            }
            if (lastExceptions.size() > 0) {
                LOGGER.error("Exception happens during update event process via CalDAV");
                throw new WebdavException((Throwable)lastExceptions.get(0));
            }
        }
        catch (Exception exception) {
            throw new WebdavException(exception);
        }
    }

    private List<Exception> processAddOrUpdate(Set<SubCalendarEvent> requestUpdateEvents, Set<SubCalendarEvent> dbSubCalendarEvents) {
        LOGGER.debug("requestUpdateEvents is >= dbSubCalendarEvents");
        CalDavEventInformation calDavEventInformation = new CalDavEventInformation();
        ArrayList<Exception> lastExceptions = new ArrayList<Exception>();
        List<DateTime> excludeDates = requestUpdateEvents.stream().filter(requestUpdateEvent -> Status.VEVENT_CANCELLED.getValue().equals(requestUpdateEvent.getStatus())).map(SubCalendarEvent::getStartTime).collect(Collectors.toList());
        if (excludeDates.size() > 0) {
            lastExceptions.addAll(this.processExcludeEventByStatus(requestUpdateEvents, excludeDates));
        } else {
            requestUpdateEvents.stream().forEach(requestUpdateEvent -> {
                try {
                    if (StringUtils.isBlank(requestUpdateEvent.getRecurrenceId())) {
                        LOGGER.debug("Updating original event");
                        this.calendarManager.updateEvent((SubCalendarEvent)requestUpdateEvent);
                    } else {
                        VEvent vEvent = this.eventConverter.getEvent(requestUpdateEvent.getSubCalendar(), requestUpdateEvent.getUid(), requestUpdateEvent.getRecurrenceId());
                        SubCalendarEvent originalRecurrenceEvent = dbSubCalendarEvents.stream().filter(dbEvent -> StringUtils.isBlank(dbEvent.getRecurrenceId())).findFirst().get();
                        if (requestUpdateEvent.isAllDay() != originalRecurrenceEvent.isAllDay()) {
                            throw new RescheduleAllDayException("Cannot reschedule event and change allday status. Must change allday status for all events in series");
                        }
                        if (vEvent != null) {
                            LOGGER.debug("Updating reschedule event");
                            this.calendarManager.updateEvent((SubCalendarEvent)requestUpdateEvent);
                            calDavEventInformation.isUpdateRescheduleEvent = true;
                        } else {
                            LOGGER.debug("Adding reschedule event");
                            this.calendarManager.addEvent((SubCalendarEvent)requestUpdateEvent);
                            calDavEventInformation.isUpdateRescheduleEvent = true;
                            calDavEventInformation.isAddNewRescheduleEvent = true;
                        }
                    }
                }
                catch (Exception e) {
                    lastExceptions.add(e);
                }
            });
            this.eventPublisher.publish((Object)new CalDavEventAdded(this, AuthenticatedUserThreadLocal.get(), calDavEventInformation.isNew(), calDavEventInformation.isReschedule()));
        }
        return lastExceptions;
    }

    private List<Exception> processExcludeEventByStatus(Set<SubCalendarEvent> requestUpdateEvents, List<DateTime> excludeDates) {
        LOGGER.debug("Exclude an instance from recurrence events on recurrence id {}", excludeDates);
        ArrayList<Exception> lastExceptions = new ArrayList<Exception>();
        CalDavEventInformation calDavEventInformation = new CalDavEventInformation();
        requestUpdateEvents.stream().filter(requestUpdateEvent -> StringUtils.isEmpty(requestUpdateEvent.getRecurrenceId())).findFirst().ifPresent(originalEvent -> excludeDates.forEach(excludeDate -> {
            try {
                this.calendarManager.excludeEventOnHierarchy(originalEvent.getSubCalendar(), originalEvent.getUid(), (DateTime)excludeDate);
                calDavEventInformation.isUpdateRescheduleEvent = true;
                this.eventPublisher.publish((Object)new CalDavEventAdded(this, AuthenticatedUserThreadLocal.get(), calDavEventInformation.isNew(), calDavEventInformation.isReschedule()));
            }
            catch (Exception e) {
                lastExceptions.add(e);
            }
        }));
        return lastExceptions;
    }

    private List<Exception> processDeleteEvent(Set<SubCalendarEvent> requestUpdateEvents, Set<SubCalendarEvent> dbSubCalendarEvents) {
        LOGGER.debug("requestUpdateEvents is < dbSubCalendarEvents");
        ArrayList<Exception> lastExceptions = new ArrayList<Exception>();
        dbSubCalendarEvents.stream().filter(dbSubCalendarEvent -> !requestUpdateEvents.stream().anyMatch(requestUpdateEvent -> {
            if (dbSubCalendarEvent.getRecurrenceId() == null) {
                return dbSubCalendarEvent.getUid().equals(requestUpdateEvent.getUid());
            }
            RecurrenceIdPredicate recurrenceIdPredicate = new RecurrenceIdPredicate(dbSubCalendarEvent.getRecurrenceId());
            return dbSubCalendarEvent.getUid().equals(requestUpdateEvent.getUid()) && recurrenceIdPredicate.test(requestUpdateEvent.getRecurrenceId());
        })).forEach(deleteSubCalendarEvent -> {
            LOGGER.debug("removing event name {}: {}", (Object)deleteSubCalendarEvent.getName(), (Object)deleteSubCalendarEvent.getUid());
            try {
                this.calendarManager.removeEventOnHierarchy(deleteSubCalendarEvent.getSubCalendar(), deleteSubCalendarEvent.getUid(), deleteSubCalendarEvent.getRecurrenceId());
            }
            catch (Exception e) {
                lastExceptions.add(e);
            }
        });
        this.eventPublisher.publish((Object)new CalDavEventDeleted(this, AuthenticatedUserThreadLocal.get(), true));
        return lastExceptions;
    }

    @Override
    public SysIntf.UpdateResult updateEvent(CalDAVEvent event, List<ComponentSelectionType> updates) throws WebdavException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<CalDAVEvent> getEvents(CalDAVCollection collection, FilterBase filter, List<String> retrieveList, RetrievalMode recurRetrieval) throws WebdavException {
        try {
            Collection<SubCalendarEvent> subCalendarEvents;
            CalendarCalDAVCollection calCollection = (CalendarCalDAVCollection)collection;
            PersistedSubCalendar persistedSubCalendar = calCollection.getPersistedSubCalendar();
            if (filter == null) {
                subCalendarEvents = this.calendarManager.getEvents(persistedSubCalendar);
            } else {
                RecurrenceRetrieval recurrenceRetrieval = this.getRecurrenceRetrievalMode(recurRetrieval);
                com.atlassian.confluence.extra.calendar3.caldav.filter.FilterBase tcFilterBase = this.bedeworkFilterConverter.transform(filter);
                Optional<com.atlassian.confluence.extra.calendar3.caldav.filter.FilterBase> supportedPropertiesFilterBase = this.supportedPropertyFilterBaseTransformer.transform(tcFilterBase);
                if (!supportedPropertiesFilterBase.isPresent()) {
                    LOGGER.warn("Could not transform filter to tcFilter due lacking supported properties");
                    return Collections.emptyList();
                }
                tcFilterBase = supportedPropertiesFilterBase.get();
                subCalendarEvents = this.calDavCalendarManager.query(persistedSubCalendar, tcFilterBase, recurrenceRetrieval);
            }
            ArrayList returnList = Lists.newArrayList();
            Map<String, List<SubCalendarEvent>> groupSubCalendarEventById = subCalendarEvents.stream().collect(Collectors.groupingBy(SubCalendarEvent::getUid));
            for (Map.Entry<String, List<SubCalendarEvent>> entry : groupSubCalendarEventById.entrySet()) {
                returnList.add(new CalendarCalDAVEvent(collection.getPath(), collection.getOwner(), true, (Collection<SubCalendarEvent>)entry.getValue()));
            }
            return returnList;
        }
        catch (Exception e) {
            throw new WebdavException(e);
        }
    }

    private RecurrenceRetrieval getRecurrenceRetrievalMode(@Nullable RetrievalMode recurRetrieval) throws ParseException {
        if (recurRetrieval == null) {
            return new RecurrenceRetrieval(RecurrenceRetrievalMode.OVERRIDE);
        }
        TimeRange timeRange = null;
        if (recurRetrieval.getExpand() != null) {
            ExpandType ex = recurRetrieval.getExpand();
            net.fortuna.ical4j.model.DateTime startTime = new net.fortuna.ical4j.model.DateTime(CalendarUtil.getIcalFormatDateTime(ex.getStart()));
            net.fortuna.ical4j.model.DateTime endTime = new net.fortuna.ical4j.model.DateTime(CalendarUtil.getIcalFormatDateTime(ex.getEnd()));
            timeRange = new TimeRange(startTime, endTime);
            return new RecurrenceRetrieval(RecurrenceRetrievalMode.EXPAND, timeRange);
        }
        if (recurRetrieval.getLimitRecurrenceSet() != null) {
            LimitRecurrenceSetType ex = recurRetrieval.getLimitRecurrenceSet();
            net.fortuna.ical4j.model.DateTime startTime = new net.fortuna.ical4j.model.DateTime(CalendarUtil.getIcalFormatDateTime(ex.getStart()));
            net.fortuna.ical4j.model.DateTime endTime = new net.fortuna.ical4j.model.DateTime(CalendarUtil.getIcalFormatDateTime(ex.getEnd()));
            timeRange = new TimeRange(startTime, endTime);
            return new RecurrenceRetrieval(RecurrenceRetrievalMode.OVERRIDE, timeRange);
        }
        return new RecurrenceRetrieval(RecurrenceRetrievalMode.OVERRIDE);
    }

    @Override
    public CalDAVEvent getEvent(CalDAVCollection collection, String eventId) throws WebdavException {
        try {
            Matcher matcher = EVENT_ID_PATTERN.matcher(eventId);
            if (!matcher.matches()) {
                LOGGER.warn("Event id pattern does not match");
                return null;
            }
            CalendarCalDAVCollection calCollection = (CalendarCalDAVCollection)collection;
            PersistedSubCalendar persistedSubCalendar = calCollection.getPersistedSubCalendar();
            TimeZone icalTimeZone = this.jodaIcal4jTimeZoneMapper.toIcal4jTimeZone(persistedSubCalendar.getTimeZoneId());
            Collection<SubCalendarEvent> vEvents = this.calDavCalendarManager.getEvents(persistedSubCalendar, (VEvent vEvent) -> true, matcher.group("id"));
            if (vEvents == null || vEvents.size() == 0) {
                return null;
            }
            CalendarCalDAVEvent calendarCalDAVEvent = new CalendarCalDAVEvent(calCollection.getPath(), calCollection.getOwner(), true, vEvents);
            return calendarCalDAVEvent;
        }
        catch (Exception exception) {
            throw new WebdavException(exception);
        }
    }

    @Override
    public void deleteEvent(CalDAVEvent event, boolean scheduleReply) throws WebdavException {
        CalendarCalDAVEvent calendarCalDAVEvent = (CalendarCalDAVEvent)event;
        SubCalendarEvent subCalendarEvent = calendarCalDAVEvent.getSubCalendarEvents().stream().filter(eventTmp -> StringUtils.isEmpty(eventTmp.getRecurrenceId())).findFirst().orElse(calendarCalDAVEvent.getSubCalendarEvent());
        if (!this.calendarPermissionManager.hasEditEventPrivilege(subCalendarEvent.getSubCalendar(), AuthenticatedUserThreadLocal.get())) {
            throw new WebdavForbidden();
        }
        try {
            this.calendarManager.removeEventOnHierarchy(subCalendarEvent.getSubCalendar(), subCalendarEvent.getUid(), subCalendarEvent.getRecurrenceId());
            this.eventPublisher.publish((Object)new CalDavEventDeleted(this, AuthenticatedUserThreadLocal.get(), false));
        }
        catch (Exception exception) {
            throw new WebdavException(exception);
        }
    }

    @Override
    public Collection<SysIntf.SchedRecipientResult> requestFreeBusy(CalDAVEvent event, boolean iSchedule) throws WebdavException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void getSpecialFreeBusy(String cua, Set<String> recipients, String originator, TimeRange timeRange, Writer writer) throws WebdavException {
        throw new UnsupportedOperationException();
    }

    @Override
    public CalDAVEvent getFreeBusy(CalDAVCollection collection, int depth, TimeRange timeRange) throws WebdavException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Acl.CurrentAccess checkAccess(WdEntity entity, int desiredAccess, boolean returnResult) {
        ArrayList readonlySubCalendar;
        ConfluenceUser currentUser = AuthenticatedUserThreadLocal.get();
        if (entity instanceof HomeCalDAVCollection || desiredAccess == 25) {
            return new Acl.CurrentAccess(PrivilegeSet.userHomeMaxPrivileges);
        }
        PersistedSubCalendar subCalendar = entity instanceof CalendarCalDAVEvent ? ((CalendarCalDAVEvent)entity).getSubCalendarEvent().getSubCalendar() : ((CalendarCalDAVCollection)entity).getPersistedSubCalendar();
        String storeKey = StringUtils.defaultIfEmpty(subCalendar.getStoreKey(), "");
        if (storeKey.equals("com.atlassian.confluence.extra.calendar3.calendarstore.InternalSubscriptionCalendarDataStore")) {
            subCalendar = ((InternalSubscriptionCalendarDataStore.InternalSubscriptionSubCalendar)subCalendar).getSourceSubCalendar();
        }
        if ((readonlySubCalendar = new ArrayList(){
            {
                this.add("JIRA_ISSUE_DATES_SUB_CALENDAR_STORE");
                this.add("AGILE_SPRINTS_SUB_CALENDAR_STORE");
                this.add("JIRA_PROJECT_RELEASES_SUB_CALENDAR_STORE");
                this.add(SubscriptionCalendarDataStore.class.getName());
            }
        }).contains(storeKey)) {
            LOGGER.debug("Return readonly permission for external calendar");
            return new Acl.CurrentAccess(PrivilegeSet.readOnlyPrivileges);
        }
        boolean allowed = desiredAccess == 8 || desiredAccess == 9 ? this.calendarPermissionManager.hasEditEventPrivilege(subCalendar, currentUser) : desiredAccess == 1 && this.calendarPermissionManager.hasViewEventPrivilege(subCalendar, currentUser);
        return new Acl.CurrentAccess(allowed);
    }

    @Override
    public void updateAccess(CalDAVEvent event, Acl acl) throws WebdavException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean copyMove(CalDAVEvent from, CalDAVCollection to, String name, boolean copy, boolean overwrite) throws WebdavException {
        throw new UnsupportedOperationException();
    }

    private class CalDavEventInformation {
        private boolean isUpdateRescheduleEvent = false;
        private boolean isAddNewRescheduleEvent = false;

        private CalDavEventInformation() {
        }

        private boolean isNew() {
            return this.isAddNewRescheduleEvent;
        }

        private boolean isReschedule() {
            return this.isUpdateRescheduleEvent;
        }
    }
}

