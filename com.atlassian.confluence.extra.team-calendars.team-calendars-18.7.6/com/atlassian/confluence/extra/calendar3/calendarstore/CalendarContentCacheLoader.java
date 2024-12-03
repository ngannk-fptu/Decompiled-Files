/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.CredentialsRequiredException
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.util.profiling.UtilTimerStack
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.extra.calendar3.calendarstore;

import com.atlassian.applinks.api.CredentialsRequiredException;
import com.atlassian.confluence.extra.calendar3.calendarstore.AbstractJiraSubCalendarDataStore;
import com.atlassian.confluence.extra.calendar3.calendarstore.BaseCacheableCalendarDataStore;
import com.atlassian.confluence.extra.calendar3.calendarstore.CalendarDataStoreCachingDecorator;
import com.atlassian.confluence.extra.calendar3.calendarstore.generic.AbstractChildJiraSubCalendarDataStore;
import com.atlassian.confluence.extra.calendar3.events.SubCalendarTrackChangeEvent;
import com.atlassian.confluence.extra.calendar3.exception.RuntimeCredentialsRequiredException;
import com.atlassian.confluence.extra.calendar3.model.AbstractJiraSubCalendar;
import com.atlassian.confluence.extra.calendar3.model.PersistedSubCalendar;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.util.profiling.UtilTimerStack;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import net.fortuna.ical4j.model.Calendar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CalendarContentCacheLoader<T extends PersistedSubCalendar> {
    private static final Logger logger = LoggerFactory.getLogger(CalendarContentCacheLoader.class);
    private final BaseCacheableCalendarDataStore<T> calendarDataStore;
    private final EventPublisher eventPublisher;

    public CalendarContentCacheLoader(EventPublisher eventPublisher, BaseCacheableCalendarDataStore<T> calendarDataStore) {
        this.eventPublisher = eventPublisher;
        this.calendarDataStore = calendarDataStore;
    }

    public Calendar load(String cacheKey, T subCalendar) {
        try {
            if (this.calendarDataStore instanceof AbstractJiraSubCalendarDataStore) {
                AbstractJiraSubCalendarDataStore.setQueryDateRangeFromCacheKey(cacheKey, (AbstractJiraSubCalendar)subCalendar);
            }
            Calendar cachedSubCalendarContent = this.getSubCalendarContentFromCalendarDataStore(subCalendar);
            this.reloadJiraReminder(subCalendar, cachedSubCalendarContent);
            return cachedSubCalendarContent;
        }
        catch (Exception e) {
            logger.warn("Could not load calendar content from cache key. Please enable DEBUG mode to see the error detail.");
            if (logger.isDebugEnabled()) {
                logger.debug("Could not load calendar content from cache key because of:", (Throwable)e);
            }
            if (e instanceof CredentialsRequiredException) {
                throw new RuntimeCredentialsRequiredException((CredentialsRequiredException)((Object)e));
            }
            return CalendarDataStoreCachingDecorator.CACHE_VALUE_NONE;
        }
    }

    private void reloadJiraReminder(T subCalendar, Calendar cachedSubCalendarContent) throws ParseException, IOException, URISyntaxException {
        String userId;
        StringBuilder keyIdBuilder;
        ConfluenceUser currentUser;
        if (subCalendar instanceof AbstractChildJiraSubCalendarDataStore.ChildJiraSubCalendar && (currentUser = AuthenticatedUserThreadLocal.get()) != null && this.calendarDataStore.getEventTypeReminder(subCalendar) != null && !this.calendarDataStore.checkExistJiraReminderEvent((keyIdBuilder = new StringBuilder(userId = currentUser.getKey().toString()).append(':').append(((PersistedSubCalendar)subCalendar).getId())).toString())) {
            this.calendarDataStore.updateJiraReminderEvents(subCalendar, new Calendar(cachedSubCalendarContent));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Calendar getSubCalendarContentFromCalendarDataStore(T subCalendar) throws Exception {
        String method = "getSubCalendarContentFromCalendarDataStore(T subCalendar)";
        UtilTimerStack.push((String)method);
        try {
            Calendar calendar = this.calendarDataStore.getSubCalendarContent(subCalendar);
            this.eventPublisher.publish((Object)new SubCalendarTrackChangeEvent((Object)this, AuthenticatedUserThreadLocal.get(), (PersistedSubCalendar)subCalendar));
            Calendar calendar2 = calendar;
            return calendar2;
        }
        finally {
            UtilTimerStack.pop((String)method);
        }
    }
}

