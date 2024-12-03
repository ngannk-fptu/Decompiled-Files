/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.CredentialsRequiredException
 *  com.atlassian.confluence.core.FormatSettingsManager
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.message.Message
 *  com.atlassian.user.User
 *  com.atlassian.util.profiling.UtilTimerStack
 *  com.google.common.base.Function
 *  com.google.common.base.Predicate
 *  com.google.common.base.Predicates
 *  com.google.common.collect.Collections2
 *  com.google.common.util.concurrent.SettableFuture
 *  org.joda.time.DateTime
 *  org.joda.time.DateTimeZone
 *  org.joda.time.ReadableInstant
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.calendar3.upcomingevents;

import com.atlassian.applinks.api.CredentialsRequiredException;
import com.atlassian.confluence.core.FormatSettingsManager;
import com.atlassian.confluence.extra.calendar3.CalendarManager;
import com.atlassian.confluence.extra.calendar3.CalendarPermissionManager;
import com.atlassian.confluence.extra.calendar3.CalendarSettingsManager;
import com.atlassian.confluence.extra.calendar3.GenericMessage;
import com.atlassian.confluence.extra.calendar3.JodaIcal4jTimeZoneMapper;
import com.atlassian.confluence.extra.calendar3.exception.CalendarException;
import com.atlassian.confluence.extra.calendar3.model.LocalizedSubCalendarEvent;
import com.atlassian.confluence.extra.calendar3.model.PersistedSubCalendar;
import com.atlassian.confluence.extra.calendar3.model.SubCalendarEvent;
import com.atlassian.confluence.extra.calendar3.model.SubCalendarSummary;
import com.atlassian.confluence.extra.calendar3.model.SubscribingSubCalendarSummary;
import com.atlassian.confluence.extra.calendar3.upcomingevents.ExpandedLocalizedSubCalendarEvent;
import com.atlassian.confluence.extra.calendar3.util.AsynchronousTaskExecutor;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.message.Message;
import com.atlassian.user.User;
import com.atlassian.util.profiling.UtilTimerStack;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.util.concurrent.SettableFuture;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import net.fortuna.ical4j.data.ParserException;
import org.apache.commons.lang.BooleanUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.ReadableInstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UpcomingEventsHelper {
    private static final Logger LOG = LoggerFactory.getLogger(UpcomingEventsHelper.class);
    private static final boolean RETRIEVE_EVENTS_MULTITHREADED = BooleanUtils.toBoolean(System.getProperty("com.atlassian.confluence.extra.calendar3.concurrent.task.enabled", Boolean.FALSE.toString()));
    private final AsynchronousTaskExecutor asynchronousTaskExecutor;
    private final FormatSettingsManager formatSettingsManager;
    private final CalendarPermissionManager calendarPermissionManager;
    private final CalendarManager calendarManager;
    private final JodaIcal4jTimeZoneMapper jodaIcal4jTimeZoneMapper;
    private final LocaleManager localeManager;
    private final CalendarSettingsManager calendarSettingsManager;

    @Autowired
    public UpcomingEventsHelper(AsynchronousTaskExecutor asynchronousTaskExecutor, @ComponentImport FormatSettingsManager formatSettingsManager, CalendarPermissionManager calendarPermissionManager, CalendarManager calendarManager, JodaIcal4jTimeZoneMapper jodaIcal4jTimeZoneMapper, @ComponentImport LocaleManager localeManager, CalendarSettingsManager calendarSettingsManager) {
        this.asynchronousTaskExecutor = asynchronousTaskExecutor;
        this.formatSettingsManager = formatSettingsManager;
        this.calendarPermissionManager = calendarPermissionManager;
        this.calendarManager = calendarManager;
        this.jodaIcal4jTimeZoneMapper = jodaIcal4jTimeZoneMapper;
        this.localeManager = localeManager;
        this.calendarSettingsManager = calendarSettingsManager;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Map<DateTime, List<LocalizedSubCalendarEvent>> getEventsGroup(ConfluenceUser user, boolean hideEventEditLinks, DateTime rangeStart, DateTime rangeEnd, Set<String> subCalendarIds, Collection<Message> errorMessagesCollection) {
        String methodSignature = "getEventsGroup(boolean hideEventEditLinks, DateTime rangeStart, DateTime rangeEnd, Set<String> subCalendarIds, Collection<String> errorMessagesCollection)";
        UtilTimerStack.push((String)methodSignature);
        Locale userLocale = this.getUserLocale(user);
        DateTimeZone userTimeZone = this.getUserTimeZone(user);
        try {
            Collection subCalendarsInView = Collections2.filter((Collection)Collections2.transform(subCalendarIds, this.calendarManager::getSubCalendar), (Predicate)Predicates.and((Predicate)Predicates.notNull(), persistedSubCalendar -> this.calendarPermissionManager.hasViewEventPrivilege((PersistedSubCalendar)persistedSubCalendar, user)));
            List<LocalizedSubCalendarEvent> allEventsInView = this.loadEvents(this.getRetrieveEventTasks(rangeStart, rangeEnd, userLocale, userTimeZone, subCalendarsInView), errorMessagesCollection);
            HashSet<String> subCalendarIdsInPersonalCalendar = new HashSet<String>();
            for (SubCalendarSummary subCalendarSummary : Collections2.filter((Collection)Collections2.transform(this.calendarManager.getSubCalendarsInView(user), this.calendarManager::getSubCalendarSummary), (Predicate)Predicates.notNull())) {
                subCalendarIdsInPersonalCalendar.add(subCalendarSummary.getId());
                if (!(subCalendarSummary instanceof SubscribingSubCalendarSummary)) continue;
                subCalendarIdsInPersonalCalendar.add(((SubscribingSubCalendarSummary)subCalendarSummary).getSubscriptionId());
            }
            for (LocalizedSubCalendarEvent localizedSubCalendarEvent : allEventsInView) {
                if (!localizedSubCalendarEvent.isEditable()) continue;
                localizedSubCalendarEvent.setEditable(!hideEventEditLinks && subCalendarIdsInPersonalCalendar.contains(localizedSubCalendarEvent.getSubCalendarId()));
            }
            allEventsInView = this.expandMultiDayEvents(allEventsInView, userTimeZone, rangeEnd, userLocale, this.formatSettingsManager, this.calendarSettingsManager);
            Collections.sort(allEventsInView, (eventLeft, eventRight) -> {
                int compareResult;
                int n = eventLeft.getClass().equals(eventRight.getClass()) ? 0 : (compareResult = eventLeft instanceof ExpandedLocalizedSubCalendarEvent ? 1 : -1);
                if (0 == compareResult) {
                    compareResult = eventLeft.getStartTime().compareTo((ReadableInstant)eventRight.getStartTime());
                }
                if (0 == compareResult) {
                    compareResult = eventLeft.getName().compareTo(eventRight.getName());
                }
                if (0 == compareResult) {
                    compareResult = eventLeft.getSubCalendar().getName().compareTo(eventRight.getSubCalendar().getName());
                }
                return compareResult;
            });
            this.filterEventsWhichStartBeforeRange(rangeStart, allEventsInView);
            Map<DateTime, List<LocalizedSubCalendarEvent>> map = this.groupEvents(allEventsInView);
            return map;
        }
        finally {
            UtilTimerStack.pop((String)methodSignature);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Map<DateTime, List<LocalizedSubCalendarEvent>> groupEvents(List<LocalizedSubCalendarEvent> allEventsInView) {
        String methodSignature = "groupEvents(List<LocalizedSubCalendarEvent> allEventsInView)";
        UtilTimerStack.push((String)methodSignature);
        try {
            TreeMap eventsGrouped = new TreeMap();
            for (LocalizedSubCalendarEvent localizedSubCalendarEvent : allEventsInView) {
                List<LocalizedSubCalendarEvent> groupedEvents;
                DateTime eventStart = localizedSubCalendarEvent.getStartTime();
                DateTime eventsGroupKey = new DateTime(eventStart.getYear(), eventStart.getMonthOfYear(), eventStart.getDayOfMonth(), 0, 0, 0, 0, eventStart.getZone());
                if (eventsGrouped.containsKey(eventsGroupKey)) {
                    groupedEvents = (List)eventsGrouped.get(eventsGroupKey);
                } else {
                    groupedEvents = new ArrayList();
                    eventsGrouped.put(eventsGroupKey, groupedEvents);
                }
                groupedEvents.add(localizedSubCalendarEvent);
            }
            TreeMap treeMap = eventsGrouped;
            return treeMap;
        }
        finally {
            UtilTimerStack.pop((String)methodSignature);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void filterEventsWhichStartBeforeRange(DateTime rangeStart, List<LocalizedSubCalendarEvent> allEventsInView) {
        String methodSignature = "filterEventsWhichStartBeforeRange(DateTime rangeStart, List<LocalizedSubCalendarEvent> allEventsInView)";
        UtilTimerStack.push((String)methodSignature);
        try {
            allEventsInView.removeIf(upcomingEvent -> upcomingEvent.getStartTime().isBefore((ReadableInstant)rangeStart));
        }
        finally {
            UtilTimerStack.pop((String)methodSignature);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private List<LocalizedSubCalendarEvent> expandMultiDayEvents(List<LocalizedSubCalendarEvent> allEventsInView, DateTimeZone userTimeZone, DateTime rangeEnd, Locale userLocale, FormatSettingsManager formatSettingsManager, CalendarSettingsManager calendarSettingsManager) {
        String methodSignature = "expandMultiDayEvents(List<LocalizedSubCalendarEvent> allEventsInView, DateTimeZone userTimeZone, DateTime rangeEnd, Locale userLocale, FormatSettingsManager formatSettingsManager)";
        UtilTimerStack.push((String)methodSignature);
        try {
            LinkedList<ExpandedLocalizedSubCalendarEvent> expandedEvents = new LinkedList<ExpandedLocalizedSubCalendarEvent>();
            for (LocalizedSubCalendarEvent baseEvent : allEventsInView) {
                ExpandedLocalizedSubCalendarEvent expandedEvent;
                if (baseEvent.isAllDay()) {
                    ExpandedLocalizedSubCalendarEvent expandedEvent2;
                    DateTime expandedStart = baseEvent.getStartTime().plusDays(1);
                    if (!expandedStart.isBefore((ReadableInstant)rangeEnd) || baseEvent.getEndTime().getMillis() - expandedStart.getMillis() < 86400000L) continue;
                    do {
                        expandedEvent2 = new ExpandedLocalizedSubCalendarEvent(baseEvent, userTimeZone, userLocale, formatSettingsManager, calendarSettingsManager);
                        expandedEvent2.setStartTime(expandedStart);
                        expandedEvent2.setEndTime(expandedStart.plusDays(1));
                        expandedEvents.add(expandedEvent2);
                    } while ((expandedStart = expandedEvent2.getEndTime()).isBefore((ReadableInstant)baseEvent.getEndTime()) && expandedStart.isBefore((ReadableInstant)rangeEnd));
                    continue;
                }
                DateTime baseStart = baseEvent.getStartTime();
                DateTime expandedStart = new DateTime(baseStart.getYear(), baseStart.getMonthOfYear(), baseStart.getDayOfMonth(), 0, 0, 0, 0, baseStart.getZone()).plusDays(1);
                if (!expandedStart.isBefore((ReadableInstant)rangeEnd) || !baseEvent.getEndTime().isAfter((ReadableInstant)expandedStart)) continue;
                do {
                    expandedEvent = new ExpandedLocalizedSubCalendarEvent(baseEvent, userTimeZone, userLocale, formatSettingsManager, calendarSettingsManager);
                    expandedEvent.setStartTime(expandedStart);
                    if (baseEvent.getEndTime().getMillis() - expandedStart.getMillis() >= 86400000L) {
                        expandedEvent.setAllDay(true);
                        expandedEvent.setEndTime(expandedStart.plusDays(1));
                    } else {
                        expandedEvent.setEndTime(baseEvent.getEndTime());
                    }
                    expandedEvents.add(expandedEvent);
                } while ((expandedStart = expandedEvent.getEndTime()).isBefore((ReadableInstant)baseEvent.getEndTime()) && expandedStart.isBefore((ReadableInstant)rangeEnd));
                ((ExpandedLocalizedSubCalendarEvent)expandedEvents.get(expandedEvents.size() - 1)).setLast(true);
            }
            allEventsInView.addAll(expandedEvents);
            List<LocalizedSubCalendarEvent> list = allEventsInView;
            return list;
        }
        finally {
            UtilTimerStack.pop((String)methodSignature);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private List<LocalizedSubCalendarEvent> loadEvents(Collection<Future<Collection<LocalizedSubCalendarEvent>>> retrieveEventsTasks, Collection<Message> errorMessagesCollection) {
        String methodSignature = "loadEvents(Collection<Future<Collection<LocalizedSubCalendarEvent>>> retrieveEventsTasks, Collection<String> errorMessagesCollection)";
        UtilTimerStack.push((String)methodSignature);
        LinkedList<LocalizedSubCalendarEvent> allEvents = new LinkedList<LocalizedSubCalendarEvent>();
        try {
            for (Future<Collection<LocalizedSubCalendarEvent>> retrieveEventTask : retrieveEventsTasks) {
                try {
                    allEvents.addAll(retrieveEventTask.get(2L, TimeUnit.MINUTES));
                }
                catch (ExecutionException executionProblem) {
                    CalendarException calendarError = this.findError(executionProblem, CalendarException.class);
                    if (null != calendarError && null != this.findError(calendarError, ParserException.class)) {
                        errorMessagesCollection.add(this.getCalendarErrorMessage(calendarError));
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Unable to read events via FutureTask", (Throwable)executionProblem);
                            continue;
                        }
                        LOG.warn("Unable to read events via FutureTask. Log at DEBUG level for more info.");
                        continue;
                    }
                    if (null != this.findError(executionProblem, CredentialsRequiredException.class)) {
                        CredentialsRequiredException credentialsRequiredException = this.findError(executionProblem, CredentialsRequiredException.class);
                        errorMessagesCollection.add(new GenericMessage("calendar3.notification.recommended.error.credentialsrequired", new Serializable[]{credentialsRequiredException.getAuthorisationURI().getHost()}));
                        LOG.warn("Unable to read events via FutureTask", (Throwable)executionProblem);
                        continue;
                    }
                    LOG.error("Unable to read events via FutureTask", (Throwable)executionProblem);
                }
                catch (TimeoutException timeoutException) {
                    errorMessagesCollection.add(new GenericMessage("calendar3.error.upcoming-events.timeout", new Serializable[0]));
                    LOG.warn("Timeout reading events via FutureTask");
                }
                catch (Exception unableToReadEvent) {
                    LOG.error("Unable to read events via FutureTask", (Throwable)unableToReadEvent);
                }
            }
            LinkedList<LocalizedSubCalendarEvent> linkedList = allEvents;
            return linkedList;
        }
        finally {
            UtilTimerStack.pop((String)methodSignature);
        }
    }

    private Message getCalendarErrorMessage(CalendarException calendarError) {
        return new GenericMessage(calendarError.getErrorMessageKey(), this.getSubstitutionsAsSerializable(calendarError.getErrorMessageSubstitutions()));
    }

    private Serializable[] getSubstitutionsAsSerializable(List<?> substitutions) {
        if (null == substitutions) {
            return null;
        }
        Serializable[] serializableSubstitutions = new Serializable[substitutions.size()];
        int j = substitutions.size();
        for (int i = 0; i < j; ++i) {
            Object theSubstitution = substitutions.get(i);
            serializableSubstitutions[i] = theSubstitution instanceof Serializable ? (Serializable)theSubstitution : theSubstitution.toString();
        }
        return serializableSubstitutions;
    }

    private <T extends Throwable> T findError(Throwable error, Class<T> exceptionType) {
        return (T)(null == error ? null : (exceptionType.isAssignableFrom(error.getClass()) ? error : this.findError(error.getCause(), exceptionType)));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Collection<Future<Collection<LocalizedSubCalendarEvent>>> getRetrieveEventTasks(DateTime rangeStart, DateTime rangeEnd, Locale userLocale, DateTimeZone userTimeZone, Collection<PersistedSubCalendar> subCalendarsInView) {
        String methodSignature = "getRetrieveEventTasks(DateTime rangeStart, DateTime rangeEnd, Locale userLocale, DateTimeZone userTimeZone, Collection<PersistedSubCalendar> subCalendarsInView)";
        UtilTimerStack.push((String)methodSignature);
        try {
            List<PersistedSubCalendar> childSubCalendars = this.calendarManager.flattenSubCalendars(subCalendarsInView);
            ArrayList<Future<Collection<LocalizedSubCalendarEvent>>> retrieveEventsTasks = new ArrayList<Future<Collection<LocalizedSubCalendarEvent>>>(childSubCalendars.size());
            Function transformToLocalisedEventFunction = subCalendarEvent -> new LocalizedSubCalendarEvent((SubCalendarEvent)subCalendarEvent, userTimeZone, userLocale, this.formatSettingsManager, this.calendarSettingsManager);
            for (PersistedSubCalendar subCalendar : childSubCalendars) {
                if (RETRIEVE_EVENTS_MULTITHREADED) {
                    this.addRetrieveEventTaskMultiThreaded(rangeStart, rangeEnd, retrieveEventsTasks, (Function<SubCalendarEvent, LocalizedSubCalendarEvent>)transformToLocalisedEventFunction, subCalendar);
                    continue;
                }
                this.addRetrieveEventTaskSingleThreaded(rangeStart, rangeEnd, retrieveEventsTasks, (Function<SubCalendarEvent, LocalizedSubCalendarEvent>)transformToLocalisedEventFunction, subCalendar);
            }
            ArrayList<Future<Collection<LocalizedSubCalendarEvent>>> arrayList = retrieveEventsTasks;
            return arrayList;
        }
        finally {
            UtilTimerStack.pop((String)methodSignature);
        }
    }

    private void addRetrieveEventTaskMultiThreaded(DateTime rangeStart, DateTime rangeEnd, Collection<Future<Collection<LocalizedSubCalendarEvent>>> retrieveEventsTasks, Function<SubCalendarEvent, LocalizedSubCalendarEvent> transformToLocalisedEventFunction, PersistedSubCalendar subCalendar) {
        retrieveEventsTasks.add(this.asynchronousTaskExecutor.submit(() -> Collections2.transform(this.calendarManager.getEvents(subCalendar, rangeStart, rangeEnd), (Function)transformToLocalisedEventFunction)));
    }

    private void addRetrieveEventTaskSingleThreaded(DateTime rangeStart, DateTime rangeEnd, Collection<Future<Collection<LocalizedSubCalendarEvent>>> retrieveEventsTasks, Function<SubCalendarEvent, LocalizedSubCalendarEvent> transformToLocalisedEventFunction, PersistedSubCalendar subCalendar) {
        SettableFuture localEvents = SettableFuture.create();
        try {
            localEvents.set((Object)Collections2.transform(this.calendarManager.getEvents(subCalendar, rangeStart, rangeEnd), transformToLocalisedEventFunction));
            retrieveEventsTasks.add((Future<Collection<LocalizedSubCalendarEvent>>)localEvents);
        }
        catch (Exception errorGettingEvents) {
            if (LOG.isDebugEnabled()) {
                LOG.debug(String.format("Error getting events from sub-calendar %s", subCalendar.getId()), (Throwable)errorGettingEvents);
            }
            LOG.warn(String.format("Error getting events from sub-calendar %s with error %s", subCalendar.getId(), errorGettingEvents.getCause() != null ? errorGettingEvents.getCause().getMessage() : errorGettingEvents.getMessage()));
        }
    }

    private DateTimeZone getUserTimeZone(ConfluenceUser user) {
        return DateTimeZone.forID((String)this.jodaIcal4jTimeZoneMapper.getUserTimeZoneIdJoda(user));
    }

    private Locale getUserLocale(ConfluenceUser user) {
        return this.localeManager.getLocale((User)user);
    }
}

