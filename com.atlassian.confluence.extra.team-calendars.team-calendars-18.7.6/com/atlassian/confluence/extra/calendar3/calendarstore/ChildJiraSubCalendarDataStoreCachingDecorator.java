/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.Cache
 *  com.atlassian.cache.CacheLoader
 *  com.atlassian.cache.CacheManager
 *  com.atlassian.cache.CacheSettings
 *  com.atlassian.cache.CacheSettingsBuilder
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.google.common.collect.Collections2
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.InitializingBean
 */
package com.atlassian.confluence.extra.calendar3.calendarstore;

import com.atlassian.cache.Cache;
import com.atlassian.cache.CacheLoader;
import com.atlassian.cache.CacheManager;
import com.atlassian.cache.CacheSettings;
import com.atlassian.cache.CacheSettingsBuilder;
import com.atlassian.confluence.extra.calendar3.CalendarSettingsManager;
import com.atlassian.confluence.extra.calendar3.calendarstore.CalendarDataStoreCachingDecorator;
import com.atlassian.confluence.extra.calendar3.calendarstore.generic.AbstractChildJiraSubCalendarDataStore;
import com.atlassian.confluence.extra.calendar3.calendarstore.generic.ParentSubCalendarDataStore;
import com.atlassian.confluence.extra.calendar3.events.ParentSubCalendarRefreshed;
import com.atlassian.confluence.extra.calendar3.model.AbstractJiraSubCalendar;
import com.atlassian.confluence.extra.calendar3.model.persistence.SubCalendarEntity;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.google.common.collect.Collections2;
import java.util.Collection;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

public class ChildJiraSubCalendarDataStoreCachingDecorator<T extends AbstractChildJiraSubCalendarDataStore.ChildJiraSubCalendar>
extends CalendarDataStoreCachingDecorator<T>
implements InitializingBean,
DisposableBean {
    private static final Logger logger = LoggerFactory.getLogger(ChildJiraSubCalendarDataStoreCachingDecorator.class);
    private static final String JIRA_SUBCALENDAR_CACHE_KEY = "com.atlassian.confluence.extra.calendar3.calendarstore.ChildJiraSubCalendarDataStoreCachingDecorator:jira-subcalendar-cache.c711";
    private final EventPublisher eventPublisher;
    private final CacheManager cacheManager;
    private final AbstractChildJiraSubCalendarDataStore<T> calendarDataStore;
    private Cache<String, Object> subCalendarCache;

    public ChildJiraSubCalendarDataStoreCachingDecorator(CacheManager cacheManager, EventPublisher eventPublisher, CalendarSettingsManager calendarSettingsManager, AbstractChildJiraSubCalendarDataStore<T> calendarDataStore) {
        super(calendarDataStore, cacheManager, calendarSettingsManager, eventPublisher);
        this.calendarDataStore = calendarDataStore;
        this.eventPublisher = eventPublisher;
        this.cacheManager = cacheManager;
    }

    public void destroy() throws Exception {
        this.eventPublisher.unregister((Object)this);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();
        try {
            this.getSubCalendarCache().removeAll();
        }
        catch (RuntimeException re) {
            logger.warn("Error initializing cache ChildJiraSubCalendarDataStoreCachingDecorator. It's probably because of a race condition to get it initialized. If so, nothing to worry about");
            logger.debug("Error detail is:", (Throwable)re);
        }
        this.eventPublisher.register((Object)this);
    }

    @Override
    public T getSubCalendar(SubCalendarEntity subCalendarEntity) {
        Object cachedSubCalendar = this.getCachedSubCalendar(subCalendarEntity.getID());
        return CACHE_VALUE_NONE.equals(cachedSubCalendar) ? null : (T)this.calendarDataStore.createSubCalendarFrom(cachedSubCalendar.toString());
    }

    @Override
    public T getSubCalendar(String subCalendarId) {
        Object cachedSubCalendar = this.getCachedSubCalendar(subCalendarId);
        return CACHE_VALUE_NONE.equals(cachedSubCalendar) ? null : (T)this.calendarDataStore.createSubCalendarFrom(cachedSubCalendar.toString());
    }

    private Object getCachedSubCalendar(String subCalendarId) {
        Cache subCalendarCache = this.getSubCalendarCache();
        Object cachedObject = subCalendarCache.get((Object)subCalendarId);
        return cachedObject;
    }

    private Cache getSubCalendarCache() {
        if (this.subCalendarCache == null) {
            CacheSettings cacheSettings = new CacheSettingsBuilder().replicateViaInvalidation().replicateAsynchronously().flushable().build();
            SubCalendarCacheLoader cacheLoader = new SubCalendarCacheLoader();
            this.subCalendarCache = this.cacheManager.getCache(JIRA_SUBCALENDAR_CACHE_KEY, (CacheLoader)cacheLoader, cacheSettings);
        }
        return this.subCalendarCache;
    }

    @Override
    public void refresh(T subCalendar) {
        super.refresh(subCalendar);
        this.getSubCalendarCache().remove((Object)((AbstractJiraSubCalendar)subCalendar).getId());
    }

    @Override
    protected void uncacheSubCalendarContent(T subCalendar) {
        super.uncacheSubCalendarContent(subCalendar);
        this.getSubCalendarCache().remove((Object)((AbstractJiraSubCalendar)subCalendar).getId());
    }

    @Override
    public void remove(T subCalendar) {
        super.remove(subCalendar);
        this.getSubCalendarCache().remove((Object)((AbstractJiraSubCalendar)subCalendar).getId());
    }

    @EventListener
    public void handleParentSubCalendarRefreshed(ParentSubCalendarRefreshed parentSubCalendarRefreshed) {
        Set<String> childSubCalendarIds = ((ParentSubCalendarDataStore.ParentSubCalendar)parentSubCalendarRefreshed.getSubCalendar()).getChildSubCalendarIds();
        if (childSubCalendarIds != null) {
            Collection jiraSubCalendarsToRefresh = Collections2.transform((Collection)Collections2.filter(childSubCalendarIds, this::hasSubCalendar), string -> this.getSubCalendar((String)string));
            for (AbstractChildJiraSubCalendarDataStore.ChildJiraSubCalendar childJiraSubCalendar : jiraSubCalendarsToRefresh) {
                this.refresh((T)childJiraSubCalendar);
            }
        }
    }

    public class SubCalendarCacheLoader
    implements CacheLoader<String, Object> {
        public Object load(String subCalendarId) {
            Object cachedSubCalendar = null;
            AbstractJiraSubCalendar cachedSubCalendarObject = (AbstractJiraSubCalendar)ChildJiraSubCalendarDataStoreCachingDecorator.super.getSubCalendar(subCalendarId);
            cachedSubCalendar = cachedSubCalendarObject == null ? CalendarDataStoreCachingDecorator.CACHE_VALUE_NONE : cachedSubCalendarObject.toJson().toString();
            return cachedSubCalendar;
        }
    }
}

