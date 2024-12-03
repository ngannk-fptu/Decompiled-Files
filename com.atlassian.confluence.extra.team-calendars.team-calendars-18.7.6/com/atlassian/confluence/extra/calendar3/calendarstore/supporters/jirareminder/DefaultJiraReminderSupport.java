/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.external.ActiveObjects
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.sal.api.rdbms.TransactionalExecutorFactory
 *  com.google.common.collect.Collections2
 *  com.google.common.collect.Lists
 *  org.joda.time.DateTime
 *  org.joda.time.DateTimeZone
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.extra.calendar3.calendarstore.supporters.jirareminder;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.confluence.extra.calendar3.calendarstore.AbstractJiraSubCalendarDataStore;
import com.atlassian.confluence.extra.calendar3.calendarstore.supporters.jirareminder.JiraReminderEventDTOConverter;
import com.atlassian.confluence.extra.calendar3.calendarstore.supporters.jirareminder.JiraReminderPersister;
import com.atlassian.confluence.extra.calendar3.model.AbstractJiraSubCalendar;
import com.atlassian.confluence.extra.calendar3.querydsl.DTO.JiraReminderEventDTO;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.sal.api.rdbms.TransactionalExecutorFactory;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.property.DtStart;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultJiraReminderSupport<T extends AbstractJiraSubCalendar> {
    private static Logger logger = LoggerFactory.getLogger(DefaultJiraReminderSupport.class);
    private final AbstractJiraSubCalendarDataStore<T> jiraSubCalendarDataStore;
    private final JiraReminderPersister jiraReminderPersister;
    private final JiraReminderEventDTOConverter convertJiraVEventToDTO;

    public DefaultJiraReminderSupport(AbstractJiraSubCalendarDataStore<T> jiraSubCalendarDataStore, ActiveObjects activeObjects, TransactionalExecutorFactory transactionalExecutorFactory, Supplier<ConfluenceUser> loginUserSupplier) {
        this(jiraSubCalendarDataStore, new JiraReminderPersister(activeObjects, transactionalExecutorFactory), new JiraReminderEventDTOConverter<T>(jiraSubCalendarDataStore, loginUserSupplier));
    }

    @VisibleForTesting
    public DefaultJiraReminderSupport(AbstractJiraSubCalendarDataStore<T> jiraSubCalendarDataStore, JiraReminderPersister jiraReminderPersister, JiraReminderEventDTOConverter convertJiraVEventToDTO) {
        this.jiraSubCalendarDataStore = jiraSubCalendarDataStore;
        this.jiraReminderPersister = jiraReminderPersister;
        this.convertJiraVEventToDTO = convertJiraVEventToDTO;
    }

    public void updateJiraReminderNewEvents(T subCalendar, Calendar subCalendarContent) {
        ArrayList vEventFilters;
        ComponentList vEvents;
        long currentSystemUTC = new DateTime(System.currentTimeMillis(), DateTimeZone.UTC).getMillis();
        if (this.jiraSubCalendarDataStore.getEventTypeReminder(subCalendar) != null && (vEvents = subCalendarContent.getComponents("VEVENT")) != null && (vEventFilters = Lists.newArrayList((Iterable)Collections2.filter(vEvents, vEvent -> {
            DtStart eventStart = vEvent.getStartDate();
            if (eventStart != null) {
                long eventStartUTC = new DateTime(eventStart.getDate().getTime(), DateTimeZone.UTC).getMillis();
                return eventStartUTC > currentSystemUTC;
            }
            return false;
        }))).size() > 0) {
            List<JiraReminderEventDTO> jiraReminderEventDTOs = this.convertJiraVEventToDTO.convertJiraVEventToDTO(subCalendar, vEventFilters);
            this.jiraReminderPersister.insertMultiRecordWithSingleStatement(jiraReminderEventDTOs);
        }
    }
}

