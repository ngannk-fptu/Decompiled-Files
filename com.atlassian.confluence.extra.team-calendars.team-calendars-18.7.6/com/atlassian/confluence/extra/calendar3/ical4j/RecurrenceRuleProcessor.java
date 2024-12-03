/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.fugue.Pair
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.features.DarkFeatureManager
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.atlassian.util.profiling.UtilTimerStack
 *  com.google.common.base.Function
 *  com.google.common.collect.Lists
 *  org.joda.time.DateTime
 *  org.joda.time.DateTimeZone
 *  org.joda.time.ReadableInstant
 *  org.joda.time.ReadablePartial
 *  org.joda.time.format.DateTimeFormat
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.calendar3.ical4j;

import com.atlassian.confluence.extra.calendar3.JodaIcal4jDateTimeConverter;
import com.atlassian.confluence.extra.calendar3.JodaIcal4jTimeZoneMapper;
import com.atlassian.confluence.extra.calendar3.SubCalendarEventConverter;
import com.atlassian.confluence.extra.calendar3.model.PersistedSubCalendar;
import com.atlassian.confluence.extra.calendar3.model.ReminderEvent;
import com.atlassian.confluence.extra.calendar3.model.SubCalendarEvent;
import com.atlassian.confluence.extra.calendar3.util.RecurrenceIdJodaTimeHelper;
import com.atlassian.fugue.Pair;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.features.DarkFeatureManager;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.util.profiling.UtilTimerStack;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.TimeZone;
import java.util.function.Consumer;
import java.util.stream.Stream;
import net.fortuna.ical4j.model.Content;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.DateList;
import net.fortuna.ical4j.model.Period;
import net.fortuna.ical4j.model.PeriodList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.Recur;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.model.property.DtEnd;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.model.property.ExDate;
import net.fortuna.ical4j.model.property.ExRule;
import net.fortuna.ical4j.model.property.RDate;
import net.fortuna.ical4j.model.property.RRule;
import net.fortuna.ical4j.model.property.RecurrenceId;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.ReadableInstant;
import org.joda.time.ReadablePartial;
import org.joda.time.format.DateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RecurrenceRuleProcessor {
    private static final Logger LOG = LoggerFactory.getLogger(RecurrenceRuleProcessor.class);
    private static TransactionTemplate transactionTemplate;
    private DarkFeatureManager darkFeatureManager;

    @Autowired
    public RecurrenceRuleProcessor(@ComponentImport TransactionTemplate transactionTemplate, @ComponentImport DarkFeatureManager darkFeatureManager) {
        RecurrenceRuleProcessor.transactionTemplate = transactionTemplate;
        this.darkFeatureManager = darkFeatureManager;
    }

    public Collection<SubCalendarEvent> getRecurrenceEvents(SubCalendarEventConverter subCalendarEventConverter, List<VEvent> eventComponents, net.fortuna.ical4j.model.TimeZone subCalendarTimeZone, PersistedSubCalendar subCalendar, DateTime start, DateTime end, Function<Void, Boolean> eventPermissionChecker) throws Exception {
        String uid;
        JodaIcal4jDateTimeConverter jodaIcal4jDateTimeConverter = subCalendarEventConverter.getJodaIcal4jDateTimeConverter();
        HashMap<String, Collection<SubCalendarEvent>> subCalendarEventsMap = new HashMap<String, Collection<SubCalendarEvent>>();
        ArrayList rescheduledEventComponents = Lists.newArrayList();
        ArrayList eventComponentsWithExRule = Lists.newArrayList();
        ArrayList eventComponentsWithExDate = Lists.newArrayList();
        ArrayList<VEvent> listEventHaveRule = new ArrayList<VEvent>();
        for (VEvent event : eventComponents) {
            RRule rRule;
            if (null != event.getRecurrenceId() || null == (rRule = (RRule)event.getProperty("RRULE"))) continue;
            listEventHaveRule.add(event);
        }
        this.processRecurrenceRule(subCalendarEventConverter, subCalendarTimeZone, subCalendar, start, end, eventPermissionChecker, subCalendarEventsMap, listEventHaveRule);
        for (VEvent eventComponent : eventComponents) {
            Object exDateList;
            RecurrenceId recurrenceId = eventComponent.getRecurrenceId();
            if (null == recurrenceId) {
                SubCalendarEvent subCalendarEvent;
                RRule rRule = (RRule)eventComponent.getProperty("RRULE");
                if (null == rRule && RecurrenceRuleProcessor.isInRange(start, end, (subCalendarEvent = subCalendarEventConverter.toSubCalendarEvent(eventComponent, subCalendar, subCalendarTimeZone, eventPermissionChecker)).getStartTime(), subCalendarEvent.getEndTime())) {
                    this.addSubCalendarEvent(subCalendarEvent, subCalendarEventsMap);
                }
                this.processRDates(subCalendarEventConverter, subCalendarTimeZone, eventComponent, subCalendar, start, end, subCalendarEventsMap, eventPermissionChecker);
            } else {
                rescheduledEventComponents.add(eventComponent);
            }
            if (null != eventComponent.getProperty("EXRULE")) {
                eventComponentsWithExRule.add(eventComponent);
            }
            if (((ArrayList)(exDateList = eventComponent.getProperties("EXDATE"))).isEmpty()) continue;
            eventComponentsWithExDate.add(eventComponent);
        }
        for (VEvent rescheduledEventComponent : rescheduledEventComponents) {
            SubCalendarEvent subCalendarEvent = subCalendarEventConverter.toSubCalendarEvent(rescheduledEventComponent, subCalendar, subCalendarTimeZone, eventPermissionChecker);
            if (!listEventHaveRule.isEmpty()) {
                for (VEvent originalVEventItem : listEventHaveRule) {
                    if (!((Property)originalVEventItem.getProperty("UID")).equals(rescheduledEventComponent.getProperty("UID"))) continue;
                    this.transferOriginalStartAndEndDates(subCalendarTimeZone, jodaIcal4jDateTimeConverter, subCalendarEvent, originalVEventItem);
                    subCalendarEvent.setRepeat(RecurrenceRuleProcessor.getRepeatFromEventComponent(originalVEventItem));
                    break;
                }
            }
            if (!RecurrenceRuleProcessor.isInRange(start, end, subCalendarEvent.getStartTime(), subCalendarEvent.getEndTime())) continue;
            this.addSubCalendarEvent(subCalendarEvent, subCalendarEventsMap);
            LOG.debug("Reschedule for event {} with detail: \n {}", (Object)subCalendarEvent.getUid(), (Object)subCalendarEvent.toJson());
        }
        for (VEvent eventComponentWithExRule : eventComponentsWithExRule) {
            uid = eventComponentWithExRule.getUid().getValue();
            ExRule exRule = (ExRule)eventComponentWithExRule.getProperty("EXRULE");
            Recur exRecur = exRule.getRecur();
            DateList excludedDates = exRecur.getDates((Date)jodaIcal4jDateTimeConverter.toIcal4jDateTime(start), jodaIcal4jDateTimeConverter.toIcal4jDateTime(end), Value.DATE_TIME);
            for (Date excludedDate : excludedDates) {
                SubCalendarEvent subCalendarEvent = new SubCalendarEvent();
                subCalendarEvent.setUid(uid);
                subCalendarEvent.setOriginalStartTime(jodaIcal4jDateTimeConverter.toJodaTime(excludedDate, subCalendarTimeZone));
                RecurrenceRuleProcessor.removeSubCalendarEvent(subCalendarEvent, subCalendarEventsMap);
                LOG.debug("ExRule for event {} with detail: \n {}", (Object)subCalendarEvent.getUid(), (Object)subCalendarEvent.toJson().toString());
            }
        }
        for (VEvent eventComponentWithExDate : eventComponentsWithExDate) {
            uid = eventComponentWithExDate.getUid().getValue();
            PropertyList excludedExDatPropertiesList = eventComponentWithExDate.getProperties("EXDATE");
            for (ExDate exDate : excludedExDatPropertiesList) {
                DateList excludedDatesList = exDate.getDates();
                for (Date excludedDate : excludedDatesList) {
                    SubCalendarEvent subCalendarEvent = new SubCalendarEvent();
                    subCalendarEvent.setUid(uid);
                    subCalendarEvent.setOriginalStartTime(jodaIcal4jDateTimeConverter.toJodaTime(excludedDate instanceof net.fortuna.ical4j.model.DateTime ? new net.fortuna.ical4j.model.DateTime(excludedDate.toString(), subCalendarTimeZone) : excludedDate, subCalendarTimeZone));
                    RecurrenceRuleProcessor.removeSubCalendarEvent(subCalendarEvent, subCalendarEventsMap);
                    LOG.debug("ExDate for event {} with detail: \n {}", (Object)subCalendarEvent.getUid(), (Object)subCalendarEvent.toJson().toString());
                }
            }
        }
        ArrayList<SubCalendarEvent> subCalendarEvents = new ArrayList<SubCalendarEvent>();
        for (Collection subCalendarEventSet : subCalendarEventsMap.values()) {
            subCalendarEvents.addAll(subCalendarEventSet);
        }
        Collections.sort(subCalendarEvents);
        return subCalendarEvents;
    }

    private void transferOriginalStartAndEndDates(net.fortuna.ical4j.model.TimeZone subCalendarTimeZone, JodaIcal4jDateTimeConverter jodaIcal4jDateTimeConverter, SubCalendarEvent subCalendarEvent, VEvent originalVEvent) {
        DtStart originalDtStart = (DtStart)originalVEvent.getProperty("DTSTART");
        DateTime originalStartDate = jodaIcal4jDateTimeConverter.toJodaTime(originalDtStart.getDate(), subCalendarTimeZone);
        subCalendarEvent.setOriginalStartDate(originalStartDate);
        DtEnd originalDtEnd = (DtEnd)originalVEvent.getProperty("DTEND");
        DateTime originalEndDate = jodaIcal4jDateTimeConverter.toJodaTime(originalDtEnd.getDate(), subCalendarTimeZone);
        subCalendarEvent.setOriginalEndDate(originalEndDate);
    }

    private void processRecurrenceRule(SubCalendarEventConverter subCalendarEventConverter, net.fortuna.ical4j.model.TimeZone subCalendarTimeZone, PersistedSubCalendar subCalendar, DateTime start, DateTime end, Function<Void, Boolean> eventPermissionChecker, Map<String, Collection<SubCalendarEvent>> subCalendarEventsMap, List<VEvent> listEventHaveRule) {
        UtilTimerStack.push((String)"processRecurrentPeriods");
        Consumer<SubCalendarEvent> subscriber = subCalendarEvent -> {
            String uuid = subCalendarEvent.getUid();
            Collection existingRecurrenceEvents = (Collection)subCalendarEventsMap.get(uuid);
            if (existingRecurrenceEvents == null) {
                existingRecurrenceEvents = Lists.newArrayList();
            }
            existingRecurrenceEvents.add(subCalendarEvent);
            subCalendarEventsMap.put(uuid, existingRecurrenceEvents);
        };
        listEventHaveRule.stream().flatMap(eventComponent -> {
            String uuid = eventComponent.getUid().getValue();
            RRule rRule = (RRule)eventComponent.getProperty("RRULE");
            LOG.info("Processing event [{}] with RRULE [{}]", (Object)uuid, (Object)rRule.getValue());
            subCalendarEventsMap.computeIfAbsent(uuid, k -> Lists.newArrayList());
            JodaIcal4jDateTimeConverter jodaIcal4jDateTimeConverter = subCalendarEventConverter.getJodaIcal4jDateTimeConverter();
            Period recurrenceSearchPeriod = new Period(jodaIcal4jDateTimeConverter.toIcal4jDateTime(start), jodaIcal4jDateTimeConverter.toIcal4jDateTime(end));
            PeriodList recurrentPeriodList = eventComponent.calculateRecurrenceSet(recurrenceSearchPeriod);
            SubCalendarEvent.Repeat repeat = RecurrenceRuleProcessor.getRepeatFromEventComponent(eventComponent);
            Stream periodsObservation = recurrentPeriodList.stream();
            Stream subCalendarEventsObservation = periodsObservation.flatMap(recurrencePeriod -> {
                Optional<SubCalendarEvent> subCalendarEvent = this.periodToSubCalendarEvent(jodaIcal4jDateTimeConverter, repeat, (Period)recurrencePeriod, subCalendarTimeZone, start, end, subCalendarEventConverter, (VEvent)eventComponent, subCalendar, eventPermissionChecker);
                return subCalendarEvent.stream();
            });
            return subCalendarEventsObservation;
        }).forEach(subscriber);
        subCalendarEventsMap.entrySet().stream().forEach(entry -> LOG.debug("Total {} recurrence event for event [ {}] at period {}-{}", new Object[]{entry.getKey(), ((Collection)entry.getValue()).size(), start, end}));
        UtilTimerStack.pop((String)"processRecurrentPeriods");
    }

    private Optional<SubCalendarEvent> periodToSubCalendarEvent(JodaIcal4jDateTimeConverter jodaIcal4jDateTimeConverter, SubCalendarEvent.Repeat repeat, Period recurrencePeriod, net.fortuna.ical4j.model.TimeZone subCalendarTimeZone, DateTime start, DateTime end, SubCalendarEventConverter subCalendarEventConverter, VEvent eventComponent, PersistedSubCalendar subCalendar, Function<Void, Boolean> eventPermissionChecker) {
        DateTime recurrenceEnd;
        DateTime recurrenceStart = jodaIcal4jDateTimeConverter.toJodaTime(recurrencePeriod.getStart(), subCalendarTimeZone);
        if (RecurrenceRuleProcessor.isInRange(start, end, recurrenceStart, recurrenceEnd = jodaIcal4jDateTimeConverter.toJodaTime(recurrencePeriod.getEnd(), subCalendarTimeZone))) {
            SubCalendarEvent subCalendarEvent = subCalendarEventConverter.toSubCalendarEvent(eventComponent, subCalendar, subCalendarTimeZone, eventPermissionChecker);
            subCalendarEvent = this.toSubCalendarEvent(subCalendarEvent, subCalendarTimeZone, eventComponent, jodaIcal4jDateTimeConverter, repeat, recurrenceStart, recurrenceEnd);
            return Optional.of(subCalendarEvent);
        }
        return Optional.empty();
    }

    public List<ReminderEvent> getRecurrenceEventsForReminder(JodaIcal4jTimeZoneMapper jodaIcal4jTimeZoneMapper, JodaIcal4jDateTimeConverter jodaIcal4jDateTimeConverter, List<VEvent> eventComponents, long startSystemUTC, long schedular) {
        RecurrenceId recurrenceId;
        HashMap<String, Collection<ReminderEvent>> reminderEventsMap = new HashMap<String, Collection<ReminderEvent>>();
        ArrayList rescheduledEventComponents = Lists.newArrayList();
        ArrayList eventComponentsWithExRule = Lists.newArrayList();
        ArrayList eventComponentsWithExDate = Lists.newArrayList();
        for (VEvent eventComponent : eventComponents) {
            PropertyList exDateList;
            recurrenceId = eventComponent.getRecurrenceId();
            if (null == recurrenceId) {
                net.fortuna.ical4j.model.TimeZone subCalendarTimeZone = jodaIcal4jTimeZoneMapper.toIcal4jTimeZone(((Content)eventComponent.getProperty("SUBCALENDAR-TZ-ID")).getValue());
                long period = Long.valueOf(((Content)eventComponent.getProperty("PERIOD-REMINDER")).getValue());
                boolean isAllDay = Boolean.parseBoolean(((Content)eventComponent.getProperty("EVENT-ALLDAY")).getValue());
                Pair<DateTime, DateTime> periodQuery = this.getQueryPeriod(startSystemUTC + period, schedular, isAllDay, subCalendarTimeZone);
                DateTime start = (DateTime)periodQuery.left();
                DateTime end = (DateTime)periodQuery.right();
                this.processRRuleForReminder(jodaIcal4jDateTimeConverter, subCalendarTimeZone, eventComponent, start, end, reminderEventsMap);
                this.processRDatesForReminder(jodaIcal4jDateTimeConverter, subCalendarTimeZone, eventComponent, start, end, reminderEventsMap);
            } else {
                rescheduledEventComponents.add(eventComponent);
            }
            if (null != eventComponent.getProperty("EXRULE")) {
                eventComponentsWithExRule.add(eventComponent);
            }
            if ((exDateList = eventComponent.getProperties("EXDATE")).isEmpty()) continue;
            eventComponentsWithExDate.add(eventComponent);
        }
        for (VEvent rescheduledEventComponent : rescheduledEventComponents) {
            DateTime rescheduledEndDate;
            DateTime rescheduledStartDate;
            DateTime end;
            recurrenceId = rescheduledEventComponent.getRecurrenceId();
            Collection recurrenceInstances = reminderEventsMap.getOrDefault(rescheduledEventComponent.getUid().getValue(), new ArrayList());
            String ical4jTimezoneId = ((Content)rescheduledEventComponent.getProperty("SUBCALENDAR-TZ-ID")).getValue();
            DateTimeZone subCalendarTimezone = jodaIcal4jTimeZoneMapper.toJodaTimeZone(ical4jTimezoneId);
            LOG.debug("Number of recurrence instance {}", (Object)recurrenceInstances.size());
            for (ReminderEvent reminderEvent : recurrenceInstances) {
                DateTime requestedDate = RecurrenceIdJodaTimeHelper.getJodaDateTimeFromRecurrenceId(recurrenceId.getValue(), subCalendarTimezone);
                DateTime recurrenceDate = new DateTime(reminderEvent.getUtcStart(), subCalendarTimezone);
                LOG.debug("Recurrence Id date {} and Recurrence date {}", (Object)requestedDate, (Object)recurrenceDate);
                int comparedValue = requestedDate.toLocalDate().compareTo((ReadablePartial)recurrenceDate.toLocalDate());
                if (comparedValue != 0) continue;
                LOG.debug("Rematch reschedule event into recurrence instance");
                recurrenceInstances.remove(reminderEvent);
            }
            net.fortuna.ical4j.model.TimeZone subCalendarTimeZone = jodaIcal4jTimeZoneMapper.toIcal4jTimeZone(ical4jTimezoneId);
            boolean isAllDay = Boolean.parseBoolean(((Content)rescheduledEventComponent.getProperty("EVENT-ALLDAY")).getValue());
            long period = Long.valueOf(((Content)rescheduledEventComponent.getProperty("PERIOD-REMINDER")).getValue());
            Pair<DateTime, DateTime> periodQuery = this.getQueryPeriod(startSystemUTC + period, schedular, isAllDay, subCalendarTimeZone);
            DateTime start = (DateTime)periodQuery.left();
            if (!RecurrenceRuleProcessor.isInRangeReminder(start, end = (DateTime)periodQuery.right(), rescheduledStartDate = jodaIcal4jDateTimeConverter.toJodaTime(rescheduledEventComponent.getStartDate().getDate(), subCalendarTimeZone), rescheduledEndDate = jodaIcal4jDateTimeConverter.toJodaTime(rescheduledEventComponent.getEndDate().getDate(), subCalendarTimeZone))) continue;
            LOG.debug("Reschedule event is match with reminding period");
            ReminderEvent reminderEvent = this.toReminderEvent(rescheduledEventComponent, isAllDay, rescheduledStartDate.getMillis(), rescheduledEndDate.getMillis());
            this.addReminderEvent(reminderEvent, reminderEventsMap);
        }
        if (reminderEventsMap.size() > 0) {
            for (VEvent eventComponentWithExRule : eventComponentsWithExRule) {
                net.fortuna.ical4j.model.TimeZone subCalendarTimeZone = jodaIcal4jTimeZoneMapper.toIcal4jTimeZone(((Content)eventComponentWithExRule.getProperty("SUBCALENDAR-TZ-ID")).getValue());
                long period = Long.valueOf(((Content)eventComponentWithExRule.getProperty("PERIOD-REMINDER")).getValue());
                DateTime start = new DateTime(startSystemUTC + period - schedular, DateTimeZone.forTimeZone((TimeZone)subCalendarTimeZone));
                DateTime end = new DateTime(startSystemUTC + period, DateTimeZone.forTimeZone((TimeZone)subCalendarTimeZone));
                String uid = eventComponentWithExRule.getUid().getValue();
                ExRule exRule = (ExRule)eventComponentWithExRule.getProperty("EXRULE");
                Recur exRecur = exRule.getRecur();
                DateList excludedDates = exRecur.getDates((Date)jodaIcal4jDateTimeConverter.toIcal4jDateTime(start), jodaIcal4jDateTimeConverter.toIcal4jDateTime(end), Value.DATE_TIME);
                for (Date excludedDate : excludedDates) {
                    ReminderEvent reminderEvent = new ReminderEvent();
                    reminderEvent.setUidEvent(uid);
                    RecurrenceRuleProcessor.removeReminderEvent(reminderEvent, reminderEventsMap);
                }
            }
            for (VEvent eventComponentWihtExDate : eventComponentsWithExDate) {
                String uid = eventComponentWihtExDate.getUid().getValue();
                PropertyList excludedExDatPropertiesList = eventComponentWihtExDate.getProperties("EXDATE");
                for (ExDate exDate : excludedExDatPropertiesList) {
                    DateList excludedDatesList = exDate.getDates();
                    for (Date excludedDate : excludedDatesList) {
                        ReminderEvent reminderEvent = new ReminderEvent();
                        reminderEvent.setUidEvent(uid);
                        RecurrenceRuleProcessor.removeReminderEvent(reminderEvent, reminderEventsMap);
                    }
                }
            }
        }
        ArrayList<ReminderEvent> reminderEvents = new ArrayList<ReminderEvent>();
        for (Collection reminderEventSet : reminderEventsMap.values()) {
            reminderEvents.addAll(reminderEventSet);
        }
        return reminderEvents;
    }

    private Pair<DateTime, DateTime> getQueryPeriod(long startPeriod, long schedular, boolean isAllDay, net.fortuna.ical4j.model.TimeZone subCalendarTimeZone) {
        DateTimeZone timeZone = DateTimeZone.forTimeZone((TimeZone)subCalendarTimeZone);
        DateTime start = new DateTime(startPeriod - schedular).withZone(timeZone);
        DateTime end = new DateTime(startPeriod).withZone(timeZone);
        if (isAllDay) {
            LOG.debug("All day calculation");
            start = new DateTime(startPeriod - schedular, timeZone).withZoneRetainFields(DateTimeZone.UTC);
            end = new DateTime(startPeriod, timeZone).withZoneRetainFields(DateTimeZone.UTC);
        }
        LOG.debug("Query recurrence event for period " + DateTimeFormat.fullDateTime().print((ReadableInstant)start) + "-" + DateTimeFormat.fullDateTime().print((ReadableInstant)end));
        LOG.debug("Start date milis : " + start.getMillis());
        LOG.debug("End date milis : " + end.getMillis());
        return Pair.pair((Object)start, (Object)end);
    }

    private void processRRuleForReminder(JodaIcal4jDateTimeConverter jodaIcal4jDateTimeConverter, net.fortuna.ical4j.model.TimeZone subCalendarTimeZone, VEvent eventComponent, DateTime start, DateTime end, Map<String, Collection<ReminderEvent>> reminderEvensMap) {
        Period recurrenceSearchPeriod = new Period(jodaIcal4jDateTimeConverter.toIcal4jDateTime(start), jodaIcal4jDateTimeConverter.toIcal4jDateTime(end));
        LOG.debug("Process RRULE for reminder for period: {}", (Object)recurrenceSearchPeriod);
        PeriodList recurrentPeriodList = eventComponent.calculateRecurrenceSet(recurrenceSearchPeriod);
        if (recurrentPeriodList.isEmpty()) {
            LOG.debug("There is no recurrence instance for reminder");
            return;
        }
        this.processRecurrentPeriodsForReminder(jodaIcal4jDateTimeConverter, subCalendarTimeZone, eventComponent, recurrentPeriodList, start, end, reminderEvensMap);
    }

    private void processRecurrentPeriodsForReminder(JodaIcal4jDateTimeConverter jodaIcal4jDateTimeConverter, net.fortuna.ical4j.model.TimeZone subCalendarTimeZone, VEvent eventComponent, PeriodList recurrentPeriodList, DateTime startSearch, DateTime endSearch, Map<String, Collection<ReminderEvent>> reminderEvensMap) {
        Boolean isAllDayEvent = Boolean.parseBoolean(((Content)eventComponent.getProperty("EVENT-ALLDAY")).getValue());
        for (Period recurrencePeriod : recurrentPeriodList) {
            DateTime endDate;
            net.fortuna.ical4j.model.DateTime recurrenceStart = recurrencePeriod.getStart();
            net.fortuna.ical4j.model.DateTime recurrenceEnd = recurrencePeriod.getEnd();
            DateTime startRange = startSearch;
            DateTime endRange = endSearch;
            DateTime startDate = jodaIcal4jDateTimeConverter.toJodaTime(recurrenceStart, subCalendarTimeZone);
            if (!RecurrenceRuleProcessor.isInRangeReminder(startRange, endRange, startDate, endDate = jodaIcal4jDateTimeConverter.toJodaTime(recurrenceEnd, subCalendarTimeZone))) continue;
            ReminderEvent reminderEvent = this.toReminderEvent(eventComponent, isAllDayEvent, startDate.getMillis(), endDate.getMillis());
            this.addReminderEvent(reminderEvent, reminderEvensMap);
        }
    }

    private ReminderEvent toReminderEvent(VEvent eventComponent, boolean isAllDayEvent, long startDateMilis, long endDateMilis) {
        Object propertyCustomEventTyprId;
        Object propertyCalendarName;
        ReminderEvent reminderEvent = new ReminderEvent();
        reminderEvent.setUidEvent(eventComponent.getUid().getValue());
        Object eventIdProperty = eventComponent.getProperty("EVENT-ID");
        if (eventIdProperty != null) {
            reminderEvent.setEventId(Integer.parseInt(((Content)eventIdProperty).getValue()));
        }
        reminderEvent.setSubCalendarId(((Content)eventComponent.getProperty("SUBCALENDAR-ID")).getValue());
        reminderEvent.setParentCalendarId(((Content)eventComponent.getProperty("PARENT-CALENDAR-ID")).getValue());
        reminderEvent.setParentCalendarName(((Content)eventComponent.getProperty("PARENT-CALENDAR-NAME")).getValue());
        reminderEvent.setSubscriptionId(((Content)eventComponent.getProperty("SUBSCRIPTION-ID")).getValue());
        reminderEvent.setTitle(eventComponent.getSummary().getValue());
        reminderEvent.setDescription(eventComponent.getDescription().getValue());
        reminderEvent.setAllDay(isAllDayEvent);
        Object period = eventComponent.getProperty("PERIOD-REMINDER");
        if (period != null) {
            reminderEvent.setPeriod(Long.valueOf(((Content)period).getValue()));
        }
        if ((propertyCalendarName = eventComponent.getProperty("SUBCALENDAR-NAME")) != null) {
            reminderEvent.setCalendarName(((Content)propertyCalendarName).getValue());
        }
        if ((propertyCustomEventTyprId = eventComponent.getProperty("CUSTOM-EVENTTYPE-ID")) != null) {
            reminderEvent.setCustomEventTypeId(((Content)propertyCustomEventTyprId).getValue());
        }
        reminderEvent.setUtcStart(new DateTime(startDateMilis, DateTimeZone.UTC).getMillis());
        reminderEvent.setUtcEnd(new DateTime(endDateMilis, DateTimeZone.UTC).getMillis());
        Object storeKey = eventComponent.getProperty("STORE-KEY-REMINDER");
        if (storeKey != null) {
            reminderEvent.setStoreKey(((Content)storeKey).getValue());
        }
        if (eventComponent.getRecurrenceId() != null) {
            reminderEvent.setRecurrenceId(eventComponent.getRecurrenceId().getValue());
        }
        return reminderEvent;
    }

    private void processRecurrentPeriods(SubCalendarEventConverter subCalendarEventConverter, net.fortuna.ical4j.model.TimeZone subCalendarTimeZone, VEvent eventComponent, PeriodList recurrentPeriodList, PersistedSubCalendar subCalendar, DateTime startSearch, DateTime endSearch, Map<String, Collection<SubCalendarEvent>> subCalendarEventsMap, Function<Void, Boolean> permissionChecker) {
        JodaIcal4jDateTimeConverter jodaIcal4jDateTimeConverter = subCalendarEventConverter.getJodaIcal4jDateTimeConverter();
        SubCalendarEvent.Repeat repeat = RecurrenceRuleProcessor.getRepeatFromEventComponent(eventComponent);
        UtilTimerStack.push((String)"processRecurrentPeriods");
        for (Period recurrencePeriod : recurrentPeriodList) {
            DateTime recurrenceEnd;
            DateTime recurrenceStart = jodaIcal4jDateTimeConverter.toJodaTime(recurrencePeriod.getStart(), subCalendarTimeZone);
            if (!RecurrenceRuleProcessor.isInRange(startSearch, endSearch, recurrenceStart, recurrenceEnd = jodaIcal4jDateTimeConverter.toJodaTime(recurrencePeriod.getEnd(), subCalendarTimeZone))) continue;
            SubCalendarEvent subCalendarEvent = this.toSubCalendarEvent(subCalendarEventConverter.toSubCalendarEvent(eventComponent, subCalendar, subCalendarTimeZone, permissionChecker), subCalendarTimeZone, eventComponent, jodaIcal4jDateTimeConverter, repeat, recurrenceStart, recurrenceEnd);
            this.addSubCalendarEvent(subCalendarEvent, subCalendarEventsMap);
        }
        UtilTimerStack.pop((String)"processRecurrentPeriods");
    }

    private SubCalendarEvent toSubCalendarEvent(SubCalendarEvent subCalendarEvent, net.fortuna.ical4j.model.TimeZone subCalendarTimeZone, VEvent originalVEvent, JodaIcal4jDateTimeConverter jodaIcal4jDateTimeConverter, SubCalendarEvent.Repeat repeat, DateTime recurrenceStart, DateTime recurrenceEnd) {
        subCalendarEvent.setOriginalStartTime(recurrenceStart);
        subCalendarEvent.setStartTime(recurrenceStart);
        subCalendarEvent.setEndTime(recurrenceEnd);
        subCalendarEvent.setRepeat(repeat);
        this.transferOriginalStartAndEndDates(subCalendarTimeZone, jodaIcal4jDateTimeConverter, subCalendarEvent, originalVEvent);
        return subCalendarEvent;
    }

    /*
     * WARNING - void declaration
     */
    private void addSubCalendarEvent(SubCalendarEvent subCalendarEvent, Map<String, Collection<SubCalendarEvent>> subCalendarEventsMap) {
        Collection<Object> subCalendarEvents;
        if (subCalendarEventsMap.containsKey(subCalendarEvent.getUid())) {
            subCalendarEvents = subCalendarEventsMap.get(subCalendarEvent.getUid());
        } else {
            subCalendarEvents = new HashSet<SubCalendarEvent>();
            subCalendarEvents.add(subCalendarEvent);
            subCalendarEventsMap.put(subCalendarEvent.getUid(), subCalendarEvents);
        }
        if (StringUtils.isBlank(subCalendarEvent.getRecurrenceId())) {
            subCalendarEvents.add(subCalendarEvent);
            return;
        }
        Boolean useDayPeriodComparison = this.darkFeatureManager.isEnabledForCurrentUser("team-calendars.recurrence.period.match").orElse(false);
        if (!useDayPeriodComparison.booleanValue()) {
            SubCalendarEvent existingEventToReplace = null;
            for (SubCalendarEvent subCalendarEvent2 : subCalendarEvents) {
                if (!subCalendarEvent2.compareWithDateOnly(subCalendarEvent)) continue;
                existingEventToReplace = subCalendarEvent2;
                break;
            }
            if (Objects.nonNull(existingEventToReplace)) {
                subCalendarEvents.remove(existingEventToReplace);
                subCalendarEvents.add(subCalendarEvent);
            }
            return;
        }
        if (subCalendarEvents.stream().anyMatch(targetSubCalEvent -> targetSubCalEvent.compareWithDayRange(subCalendarEvent))) {
            void var7_12;
            ArrayList<Object> descEvents = new ArrayList<Object>(subCalendarEvents);
            new ArrayList<Object>(subCalendarEvents).sort((event1, event2) -> {
                DateTime e1OriginalStartTime = event1.getOriginalStartTime();
                DateTime e2OriginalStartTime = event2.getOriginalStartTime();
                return e1OriginalStartTime.compareTo((ReadableInstant)e2OriginalStartTime);
            });
            int length = descEvents.size() - 1;
            Object var7_10 = null;
            for (int index = length; index >= 0; --index) {
                SubCalendarEvent testSubCalendarEvent = (SubCalendarEvent)descEvents.get(index);
                if (!testSubCalendarEvent.compareWithDayRange(subCalendarEvent)) continue;
                SubCalendarEvent subCalendarEvent3 = testSubCalendarEvent;
                break;
            }
            if (var7_12 == null) {
                LOG.error("Could not replace reschedule event");
                return;
            }
            subCalendarEvent.setOriginalStartTime(var7_12.getOriginalStartTime());
            HashSet<SubCalendarEvent> newSubCalendarEvents = new HashSet<SubCalendarEvent>();
            newSubCalendarEvents.add(subCalendarEvent);
            for (SubCalendarEvent subCalendarEvent4 : subCalendarEvents) {
                if (subCalendarEvent4.getOriginalStartTime() == null || subCalendarEvent4.getOriginalStartTime().equals((Object)var7_12.getOriginalStartTime())) continue;
                newSubCalendarEvents.add(subCalendarEvent4);
            }
            subCalendarEventsMap.put(subCalendarEvent.getUid(), newSubCalendarEvents);
        }
    }

    private void addReminderEvent(ReminderEvent reminderEvent, Map<String, Collection<ReminderEvent>> reminderEventMap) {
        Collection<Object> reminderEvents;
        if (reminderEventMap.containsKey(reminderEvent.getUidEvent())) {
            reminderEvents = reminderEventMap.get(reminderEvent.getUidEvent());
        } else {
            reminderEvents = new HashSet();
            reminderEventMap.put(String.valueOf(reminderEvent.getUidEvent()), reminderEvents);
        }
        if (StringUtils.isBlank(reminderEvent.getRecurrenceId())) {
            reminderEvents.add(reminderEvent);
        } else if (reminderEvents.contains(reminderEvent)) {
            reminderEvents.remove(reminderEvent);
            reminderEvents.add(reminderEvent);
        } else {
            reminderEvents.add(reminderEvent);
        }
    }

    private void processRDates(SubCalendarEventConverter subCalendarEventConverter, net.fortuna.ical4j.model.TimeZone subCalendarTimeZone, VEvent eventComponent, PersistedSubCalendar subCalendar, DateTime start, DateTime end, Map<String, Collection<SubCalendarEvent>> subCalendarEventsMap, Function<Void, Boolean> permissionChecker) {
        PropertyList rDateProperties = eventComponent.getProperties("RDATE");
        for (RDate rDate : rDateProperties) {
            PeriodList recurrentPeriodList = rDate.getPeriods();
            if (null == recurrentPeriodList) continue;
            this.processRecurrentPeriods(subCalendarEventConverter, subCalendarTimeZone, eventComponent, recurrentPeriodList, subCalendar, start, end, subCalendarEventsMap, permissionChecker);
        }
    }

    private void processRDatesForReminder(JodaIcal4jDateTimeConverter jodaIcal4jDateTimeConverter, net.fortuna.ical4j.model.TimeZone subCalendarTimeZone, VEvent eventComponent, DateTime start, DateTime end, Map<String, Collection<ReminderEvent>> reminderEventList) {
        PropertyList rDateProperties = eventComponent.getProperties("RDATE");
        for (RDate rDate : rDateProperties) {
            PeriodList recurrentPeriodList = rDate.getPeriods();
            if (null == recurrentPeriodList) continue;
            this.processRecurrentPeriodsForReminder(jodaIcal4jDateTimeConverter, subCalendarTimeZone, eventComponent, recurrentPeriodList, start, end, reminderEventList);
        }
    }

    private static void removeSubCalendarEvent(SubCalendarEvent subCalendarEvent, Map<String, Collection<SubCalendarEvent>> subCalendarEventsMap) {
        Collection<SubCalendarEvent> subCalendarEvents = subCalendarEventsMap.get(subCalendarEvent.getUid());
        if (null != subCalendarEvents) {
            subCalendarEvents.remove(subCalendarEvent);
        }
    }

    private static void removeReminderEvent(ReminderEvent reminderEvent, Map<String, Collection<ReminderEvent>> reminderEventsMap) {
        Collection<ReminderEvent> reminderEvents = reminderEventsMap.get(reminderEvent.getUidEvent());
        if (null != reminderEvents) {
            reminderEvents.remove(reminderEvent);
        }
    }

    private static SubCalendarEvent.Repeat getRepeatFromEventComponent(VEvent vEvent) {
        RRule rRule = (RRule)vEvent.getProperty("RRULE");
        return null != rRule ? new SubCalendarEvent.Repeat(rRule.getValue()) : null;
    }

    private static boolean isInRange(DateTime startRange, DateTime endRange, DateTime startDate, DateTime endDate) {
        boolean isAllDay = (endDate.getMillis() - startDate.getMillis()) / 1000L % 86400L == 0L;
        DateTime _startDate = isAllDay ? startDate.toDateMidnight() : startDate;
        DateTime _endDate = isAllDay ? endDate.toDateMidnight() : endDate;
        return _startDate.isBefore((ReadableInstant)startRange) && _endDate.isAfter((ReadableInstant)startRange) || _startDate.isEqual((ReadableInstant)startRange) || _startDate.isAfter((ReadableInstant)startRange) && startDate.isBefore((ReadableInstant)endRange);
    }

    private static boolean isInRangeReminder(DateTime startRange, DateTime endRange, DateTime startDate, DateTime endDate) {
        boolean isAllDay = (endDate.getMillis() - startDate.getMillis()) / 1000L % 86400L == 0L;
        DateTime _startDate = isAllDay ? startDate.toDateMidnight() : startDate;
        return !(!_startDate.isEqual((ReadableInstant)startRange) && !_startDate.isAfter((ReadableInstant)startRange) || !_startDate.isBefore((ReadableInstant)endRange) && !_startDate.isEqual((ReadableInstant)endRange));
    }
}

