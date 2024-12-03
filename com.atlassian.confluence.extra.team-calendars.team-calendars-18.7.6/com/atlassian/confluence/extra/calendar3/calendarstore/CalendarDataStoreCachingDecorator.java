/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.Cache
 *  com.atlassian.cache.CacheLoader
 *  com.atlassian.cache.CacheManager
 *  com.atlassian.cache.CacheSettings
 *  com.atlassian.cache.CacheSettingsBuilder
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.fugue.Option
 *  com.atlassian.sal.api.message.Message
 *  com.atlassian.util.profiling.UtilTimerStack
 *  com.google.common.base.Function
 *  com.google.common.base.Predicate
 *  com.google.common.base.Predicates
 *  com.google.common.collect.Collections2
 *  com.google.common.collect.Lists
 *  org.joda.time.DateTime
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.InitializingBean
 */
package com.atlassian.confluence.extra.calendar3.calendarstore;

import com.atlassian.cache.Cache;
import com.atlassian.cache.CacheLoader;
import com.atlassian.cache.CacheManager;
import com.atlassian.cache.CacheSettings;
import com.atlassian.cache.CacheSettingsBuilder;
import com.atlassian.confluence.extra.calendar3.CalendarSettingsManager;
import com.atlassian.confluence.extra.calendar3.GenericMessage;
import com.atlassian.confluence.extra.calendar3.caldav.filter.FilterBase;
import com.atlassian.confluence.extra.calendar3.caldav.filter.RecurrenceRetrieval;
import com.atlassian.confluence.extra.calendar3.calendarstore.BaseCacheableCalendarDataStore;
import com.atlassian.confluence.extra.calendar3.calendarstore.CalendarContentCacheLoader;
import com.atlassian.confluence.extra.calendar3.calendarstore.DelegatableCalendarDataStore;
import com.atlassian.confluence.extra.calendar3.calendarstore.MemoryCalendarComponentPredicate;
import com.atlassian.confluence.extra.calendar3.calendarstore.RefreshableCalendarDataStore;
import com.atlassian.confluence.extra.calendar3.calendarstore.ReminderSettingCallback;
import com.atlassian.confluence.extra.calendar3.model.EventTypeReminder;
import com.atlassian.confluence.extra.calendar3.model.NonExistCalendar;
import com.atlassian.confluence.extra.calendar3.model.PersistedSubCalendar;
import com.atlassian.confluence.extra.calendar3.model.ReminderEvent;
import com.atlassian.confluence.extra.calendar3.model.SubCalendar;
import com.atlassian.confluence.extra.calendar3.model.SubCalendarEvent;
import com.atlassian.confluence.extra.calendar3.model.SubCalendarSummary;
import com.atlassian.confluence.extra.calendar3.model.persistence.CustomEventTypeEntity;
import com.atlassian.confluence.extra.calendar3.model.persistence.ReminderSettingEntity;
import com.atlassian.confluence.extra.calendar3.model.persistence.SubCalendarEntity;
import com.atlassian.confluence.extra.calendar3.util.CalendarUtil;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.fugue.Option;
import com.atlassian.sal.api.message.Message;
import com.atlassian.util.profiling.UtilTimerStack;
import com.google.common.base.Function;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.Content;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.DtEnd;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.model.property.Uid;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

public class CalendarDataStoreCachingDecorator<T extends PersistedSubCalendar>
implements RefreshableCalendarDataStore<T>,
DelegatableCalendarDataStore<T>,
InitializingBean {
    private static final Logger LOG = LoggerFactory.getLogger(CalendarDataStoreCachingDecorator.class);
    public static final NonExistCalendar CACHE_VALUE_NONE = new NonExistCalendar();
    private final BaseCacheableCalendarDataStore<T> calendarDataStore;
    private final CacheManager cacheManager;
    private final CacheSettings calendarDataCacheSettings;
    private Cache<String, Calendar> subCalendarDataCache;
    private Cache<String, String> subCalendarIdCache;
    private final CalendarContentCacheLoader<T> calendarContentCacheLoader;

    public CalendarDataStoreCachingDecorator(BaseCacheableCalendarDataStore<T> calendarDataStore, CacheManager cacheManager, CalendarSettingsManager calendarSettingsManager, EventPublisher eventPublisher) {
        this.calendarDataStore = calendarDataStore;
        this.cacheManager = cacheManager;
        this.calendarContentCacheLoader = new CalendarContentCacheLoader<T>(eventPublisher, calendarDataStore);
        this.calendarDataCacheSettings = new CacheSettingsBuilder().statisticsEnabled().flushable().remote().replicateViaInvalidation().replicateAsynchronously().expireAfterWrite(calendarSettingsManager.getCacheExpireTime(), TimeUnit.MINUTES).build();
    }

    public void afterPropertiesSet() throws Exception {
        try {
            this.getSubCalendarDataCache().removeAll();
            this.getSubCalendarIdCache().removeAll();
        }
        catch (RuntimeException re) {
            LOG.warn("Error initializing cache CalendarDataStoreCachingDecorator. It's probably because of a race condition to get it initialized. If so, nothing to worry about");
            LOG.debug("Error detail is:", (Throwable)re);
        }
    }

    @Override
    public boolean handles(SubCalendar subCalendar) {
        return this.calendarDataStore instanceof DelegatableCalendarDataStore && ((DelegatableCalendarDataStore)((Object)this.calendarDataStore)).handles(subCalendar);
    }

    @Override
    public void validate(SubCalendar subCalendar, Map<String, List<String>> fieldErrors) {
        this.calendarDataStore.validate(subCalendar, fieldErrors);
    }

    @Override
    public T save(SubCalendar subCalendar) throws Exception {
        if (subCalendar instanceof PersistedSubCalendar) {
            this.uncacheSubCalendarContent(this.getSubCalendar(((PersistedSubCalendar)subCalendar).getId()));
        }
        return (T)((PersistedSubCalendar)((PersistedSubCalendar)this.calendarDataStore.save(subCalendar)).clone());
    }

    @Override
    public void remove(T subCalendar) {
        this.uncacheSubCalendarContent(subCalendar);
        this.uncacheSubCalendarID(((PersistedSubCalendar)subCalendar).getId());
        this.calendarDataStore.remove(subCalendar);
    }

    protected void uncacheSubCalendarContent(T subCalendar) {
        Collection keySubCalendarDataCaches = this.getSubCalendarDataCache().getKeys();
        String keySubCalendarDateCache = this.calendarDataStore.getSubCalendarDataCacheKey(subCalendar);
        for (String key : keySubCalendarDataCaches) {
            if (!key.contains(keySubCalendarDateCache)) continue;
            this.getSubCalendarDataCache().remove((Object)key);
        }
    }

    protected void uncacheSubCalendarID(String subCalendarId) {
        this.getSubCalendarIdCache().remove((Object)subCalendarId);
    }

    @Override
    public T getSubCalendar(String subCalendarId) {
        return this.calendarDataStore.getSubCalendar(subCalendarId);
    }

    @Override
    public List<T> getSubCalendarsWithRestriction(String ... subCalendarIds) {
        return this.calendarDataStore.getSubCalendarsWithRestriction(subCalendarIds);
    }

    @Override
    public List<T> loadRestrictions(List<T> persistedEntities) {
        return this.calendarDataStore.loadRestrictions(persistedEntities);
    }

    @Override
    public SubCalendarSummary getSubCalendarSummary(String subCalendarId) {
        return this.calendarDataStore.getSubCalendarSummary(subCalendarId);
    }

    @Override
    public List<SubCalendarSummary> getSubCalendarSummariesByStoreKey(String storeKey, int limit, int offset) {
        return this.calendarDataStore.getSubCalendarSummariesByStoreKey(storeKey, limit, offset);
    }

    @Override
    public Set<String> getAllParentSubCalendarIds() {
        return this.calendarDataStore.getAllParentSubCalendarIds();
    }

    @Override
    public Set<String> getAllParentSubCalendarIds(String storeKey, int limit, int offset) {
        return this.calendarDataStore.getAllParentSubCalendarIds(storeKey, limit, offset);
    }

    @Override
    public Calendar createEmptyCalendarForSubCalendar(T subCalendar) throws Exception {
        return this.calendarDataStore.createEmptyCalendarForSubCalendar(subCalendar);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Calendar getSubCalendarContent(T subCalendar) throws Exception {
        String method = "getSubCalendarContent(T subCalendar)";
        UtilTimerStack.push((String)method);
        try {
            Calendar cachedSubCalendarContent = this.getCachedSubCalendarContent(subCalendar);
            if (Objects.nonNull(cachedSubCalendarContent) && !CACHE_VALUE_NONE.equals(cachedSubCalendarContent)) {
                Calendar calendar = this.copyCalendar(cachedSubCalendarContent);
                return calendar;
            }
            LOG.debug("cachedSubCalendarContent is null or none for calendar id {} and name", (Object)((PersistedSubCalendar)subCalendar).getId(), (Object)((PersistedSubCalendar)subCalendar).getName());
            Calendar calendar = new Calendar();
            return calendar;
        }
        finally {
            UtilTimerStack.pop((String)method);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Calendar copyCalendar(Calendar cachedSubCalendarContent) {
        String method = "copyCalendar(Calendar cachedSubCalendarContent)";
        UtilTimerStack.push((String)method);
        try {
            PropertyList<Property> oldProperties = cachedSubCalendarContent.getProperties();
            PropertyList<Property> newProperties = new PropertyList<Property>(oldProperties.size());
            newProperties.addAll(oldProperties);
            ComponentList<CalendarComponent> oldComponents = cachedSubCalendarContent.getComponents();
            ComponentList<CalendarComponent> newComponents = new ComponentList<CalendarComponent>(oldComponents.size());
            newComponents.addAll(oldComponents);
            Calendar calendar = new Calendar(newProperties, newComponents);
            return calendar;
        }
        finally {
            UtilTimerStack.pop((String)method);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Calendar getCachedSubCalendarContent(T subCalendar) {
        String method = "getCachedSubCalendarContent(T subCalendar)";
        UtilTimerStack.push((String)method);
        try {
            Cache<String, Calendar> subCalendarDataCache = this.getSubCalendarDataCache();
            String subCalendarDataCacheKey = this.calendarDataStore.getSubCalendarDataCacheKey(subCalendar);
            Calendar cachedObject = (Calendar)subCalendarDataCache.get((Object)subCalendarDataCacheKey, () -> this.calendarContentCacheLoader.load(subCalendarDataCacheKey, (PersistedSubCalendar)subCalendar));
            if (null != cachedObject && !CACHE_VALUE_NONE.getClass().isAssignableFrom(cachedObject.getClass()) && !Calendar.class.isAssignableFrom(cachedObject.getClass())) {
                subCalendarDataCache.remove((Object)subCalendarDataCacheKey);
                cachedObject = null;
            }
            Calendar calendar = cachedObject;
            return calendar;
        }
        finally {
            UtilTimerStack.pop((String)method);
        }
    }

    Cache<String, Calendar> getSubCalendarDataCache() {
        if (this.subCalendarDataCache == null) {
            this.subCalendarDataCache = this.cacheManager.getCache(this.calendarDataStore.getClass().getName() + ":subcalendar-data.c711", null, this.calendarDataCacheSettings);
        }
        return this.subCalendarDataCache;
    }

    @Override
    public void setSubCalendarContent(T subCalendar, Calendar subCalendarContent) throws Exception {
        this.calendarDataStore.setSubCalendarContent(subCalendar, subCalendarContent);
        this.getSubCalendarDataCache().remove((Object)this.calendarDataStore.getSubCalendarDataCacheKey(subCalendar));
    }

    @Override
    public List<Message> getSubCalendarWarnings(T subCalendar) {
        return this.calendarDataStore.getSubCalendarWarnings(subCalendar);
    }

    @Override
    public boolean hasSubCalendar(String subCalendarId) {
        return StringUtils.isNotEmpty((String)this.getSubCalendarIdCache().get((Object)subCalendarId));
    }

    private Cache<String, String> getSubCalendarIdCache() {
        if (this.subCalendarIdCache == null) {
            this.subCalendarIdCache = this.cacheManager.getCache(this.calendarDataStore.getClass().getName() + ":subcalendar-id.c711", (CacheLoader)new SubCalendarIdCacheLoader(), new CacheSettingsBuilder().replicateViaInvalidation().replicateAsynchronously().flushable().build());
        }
        return this.subCalendarIdCache;
    }

    @Override
    public boolean hasSubCalendar(PersistedSubCalendar subCalendar) {
        return this.calendarDataStore.hasSubCalendar(subCalendar);
    }

    @Override
    public T getSubCalendar(SubCalendarEntity subCalendarEntity) {
        UtilTimerStack.push((String)"CalendarDataStoreCachingDecorator.getSubCalendar()");
        try {
            Object t = this.calendarDataStore.getSubCalendar(subCalendarEntity);
            return t;
        }
        finally {
            UtilTimerStack.pop((String)"CalendarDataStoreCachingDecorator.getSubCalendar()");
        }
    }

    @Override
    public boolean hasSubCalendar(SubCalendarEntity subCalendarEntity) {
        return this.calendarDataStore.hasSubCalendar(subCalendarEntity);
    }

    @Override
    public List<String> filterSubCalendarIds(String ... subCalendarIds) {
        return this.calendarDataStore.filterSubCalendarIds(subCalendarIds);
    }

    @Override
    public int getSubCalendarsCount() {
        return this.calendarDataStore.getSubCalendarsCount();
    }

    @Override
    public boolean hasReloadEventsPrivilege(T subCalendar, ConfluenceUser user) {
        return this.calendarDataStore instanceof RefreshableCalendarDataStore && ((RefreshableCalendarDataStore)((Object)this.calendarDataStore)).hasReloadEventsPrivilege(subCalendar, user);
    }

    @Override
    public void refresh(T subCalendar) {
        if (null != subCalendar && this.calendarDataStore instanceof RefreshableCalendarDataStore) {
            if (StringUtils.isNotBlank(((SubCalendar)subCalendar).getSourceLocation())) {
                this.uncacheSubCalendarContent(subCalendar);
            }
            ((RefreshableCalendarDataStore)((Object)this.calendarDataStore)).refresh(subCalendar);
        }
    }

    @Override
    public void restrictEventViewToGroups(String subCalendarId, Set<String> groupNames) {
        this.calendarDataStore.restrictEventViewToGroups(subCalendarId, groupNames);
    }

    @Override
    public Set<ConfluenceUser> getEventEditUserRestrictions(String subCalendarId) {
        return this.calendarDataStore.getEventEditUserRestrictions(subCalendarId);
    }

    @Override
    public Set<ConfluenceUser> getEventEditUserRestrictions(T subCalendar) {
        return this.calendarDataStore.getEventEditUserRestrictions(subCalendar);
    }

    @Override
    public Set<String> getEventEditGroupRestrictions(String subCalendarId) {
        return this.calendarDataStore.getEventEditGroupRestrictions(subCalendarId);
    }

    @Override
    public Set<String> getEventEditGroupRestrictions(T subCalendar) {
        return this.calendarDataStore.getEventEditGroupRestrictions(subCalendar);
    }

    @Override
    public Set<ConfluenceUser> getEventViewUserRestrictions(String subCalendarId) {
        return this.calendarDataStore.getEventViewUserRestrictions(subCalendarId);
    }

    @Override
    public Set<ConfluenceUser> getEventViewUserRestrictions(T subCalendar) {
        return this.calendarDataStore.getEventViewUserRestrictions(subCalendar);
    }

    @Override
    public Set<String> getEventViewGroupRestrictions(String subCalendarId) {
        return this.calendarDataStore.getEventViewGroupRestrictions(subCalendarId);
    }

    @Override
    public Set<String> getEventViewGroupRestrictions(T subCalendar) {
        return this.calendarDataStore.getEventViewGroupRestrictions(subCalendar);
    }

    @Override
    public void restrictEventEditToUsers(String subCalendarId, Set<ConfluenceUser> users) {
        this.calendarDataStore.restrictEventEditToUsers(subCalendarId, users);
    }

    @Override
    public void restrictEventEditToGroups(String subCalendarId, Set<String> groupNames) {
        this.calendarDataStore.restrictEventEditToGroups(subCalendarId, groupNames);
    }

    @Override
    public void restrictEventViewToUsers(String subCalendarId, Set<ConfluenceUser> users) {
        this.calendarDataStore.restrictEventViewToUsers(subCalendarId, users);
    }

    @Override
    public boolean hasViewEventPrivilege(String subCalendarId, ConfluenceUser user) {
        return this.calendarDataStore.hasViewEventPrivilege(subCalendarId, user);
    }

    @Override
    public boolean hasViewEventPrivilege(T subCalendar, ConfluenceUser user) {
        return this.calendarDataStore.hasViewEventPrivilege(subCalendar, user);
    }

    @Override
    public boolean hasEditEventPrivilege(T subCalendar, ConfluenceUser user) {
        return this.calendarDataStore.hasEditEventPrivilege(subCalendar, user);
    }

    @Override
    public boolean hasDeletePrivilege(T subCalendar, ConfluenceUser user) {
        return this.calendarDataStore.hasDeletePrivilege(subCalendar, user);
    }

    @Override
    public boolean hasAdminPrivilege(T subCalendar, ConfluenceUser user) {
        return this.calendarDataStore.hasAdminPrivilege(subCalendar, user);
    }

    @Override
    public SubCalendarEvent transform(SubCalendarEvent toBeTransformed, VEvent raw) {
        return this.calendarDataStore.transform(toBeTransformed, raw);
    }

    @Override
    public Message getTypeSpecificText(T subCalendar, Message originalMessage) {
        return this.calendarDataStore.getTypeSpecificText(subCalendar, originalMessage);
    }

    @Override
    public Collection<VEvent> query(T subCalendar, FilterBase filter, RecurrenceRetrieval recurrenceRetrieval) throws Exception {
        MemoryCalendarComponentPredicate memoryCalendarComponentPredicate = new MemoryCalendarComponentPredicate(filter);
        com.google.common.base.Predicate predicate = vEvent -> memoryCalendarComponentPredicate.test((CalendarComponent)vEvent);
        return this.findEvents(subCalendar, (com.google.common.base.Predicate<VEvent>)predicate);
    }

    @Override
    public List<VEvent> getEvents(T subCalendar) throws Exception {
        Calendar subCalendarContent = this.getSubCalendarContent(subCalendar);
        ComponentList<VEvent> vEvents = subCalendarContent.getComponents("VEVENT");
        return vEvents;
    }

    @Override
    public List<VEvent> getEvents(T subCalendar, DateTime startTime, DateTime endTime) throws Exception {
        final long startTimeMs = startTime.getMillis();
        final long endTimeMs = endTime.getMillis();
        ((SubCalendar)subCalendar).setStart(startTimeMs);
        ((SubCalendar)subCalendar).setEnd(endTimeMs);
        return this.findEvents(subCalendar, new com.google.common.base.Predicate<VEvent>(){

            public boolean apply(VEvent vEvent) {
                DtStart eventStart = vEvent.getStartDate();
                DtEnd eventEnd = vEvent.getEndDate();
                Object recurrenceRuleProperty = vEvent.getProperty("RRULE");
                boolean inWindow = false;
                if (eventStart != null) {
                    long eventStartMs = eventStart.getDate().getTime();
                    boolean bl = inWindow = eventStartMs >= startTimeMs && eventStartMs <= endTimeMs;
                }
                if (!inWindow && eventEnd != null) {
                    long eventEndMs = eventEnd.getDate().getTime();
                    boolean bl = inWindow = eventEndMs >= startTimeMs;
                }
                if (!inWindow && recurrenceRuleProperty != null && !StringUtils.isEmpty(((Content)recurrenceRuleProperty).getValue())) {
                    inWindow = true;
                }
                return inWindow;
            }

            public String toString() {
                return "Filter By Time Window";
            }
        });
    }

    private List<VEvent> findEvents(T subCalendar, com.google.common.base.Predicate<VEvent> vEventPredicate) throws Exception {
        Calendar subCalendarContent = this.getSubCalendarContent(subCalendar);
        if (subCalendarContent != null) {
            ComponentList vEvents = subCalendarContent.getComponents("VEVENT");
            return Lists.newArrayList((Iterable)Collections2.filter(vEvents, vEventPredicate));
        }
        return Collections.emptyList();
    }

    @Override
    public List<VEvent> getEvents(T subCalendar, Predicate<VEvent> vEventPredicate, String ... vEventUids) throws Exception {
        return this.findEvents(subCalendar, (com.google.common.base.Predicate<VEvent>)Predicates.and((com.google.common.base.Predicate)new ByUidPredicates(vEventUids), vEvent -> vEventPredicate == null || vEventPredicate.test((VEvent)vEvent)));
    }

    @Override
    public VEvent getEvent(T subCalendar, String vEventUid, String recurrenceId) throws Exception {
        List<VEvent> vEvents = this.findEvents(subCalendar, (com.google.common.base.Predicate<VEvent>)Predicates.and((com.google.common.base.Predicate)new ByUidPredicates(new String[]{vEventUid}), (com.google.common.base.Predicate)new ByRecurrenceIdPredicate(recurrenceId)));
        return vEvents.isEmpty() ? null : vEvents.iterator().next();
    }

    @Override
    public VEvent addEvent(T subCalendar, VEvent newEventDetails) {
        VEvent newEvent = this.calendarDataStore.addEvent(subCalendar, newEventDetails);
        this.uncacheSubCalendarContent(subCalendar);
        return newEvent;
    }

    @Override
    public VEvent updateEvent(T subCalendar, VEvent newEventDetails) {
        VEvent updatedEvent = this.calendarDataStore.updateEvent(subCalendar, newEventDetails);
        this.uncacheSubCalendarContent(subCalendar);
        return updatedEvent;
    }

    @Override
    public void deleteEvent(T subCalendar, String vEventUid, String recurrenceId) {
        this.calendarDataStore.deleteEvent(subCalendar, vEventUid, recurrenceId);
        this.uncacheSubCalendarContent(subCalendar);
    }

    @Override
    public void moveEvent(T subCalendar, String vEventUid, PersistedSubCalendar destinationSubCalendar) {
        this.calendarDataStore.moveEvent(subCalendar, vEventUid, destinationSubCalendar);
        this.uncacheSubCalendarContent(subCalendar);
    }

    @Override
    public void changeEvent(T subCalendar, String vEventUid, PersistedSubCalendar destinationSubCalendar) {
        this.calendarDataStore.changeEvent(subCalendar, vEventUid, destinationSubCalendar);
        this.uncacheSubCalendarContent(subCalendar);
    }

    @Override
    public boolean setReminderFor(T subCalendar, ConfluenceUser user, boolean isReminder) {
        return this.calendarDataStore.setReminderFor(subCalendar, user, isReminder);
    }

    @Override
    public boolean hasReminderFor(T subCalendar, ConfluenceUser user) {
        return this.calendarDataStore.hasReminderFor(subCalendar, user);
    }

    @Override
    public void disableEventTypes(T subCalendar, List<String> disableEventTypes) {
        this.calendarDataStore.disableEventTypes(subCalendar, disableEventTypes);
        this.uncacheSubCalendarContent(subCalendar);
    }

    @Override
    public CustomEventTypeEntity updateCustomEventType(Option<ReminderSettingCallback> reminderSettingCallbacks, T subCalendar, String customEventTypeId, String title, String icon, int periodInMins) {
        CustomEventTypeEntity customEventTypeEntity = this.calendarDataStore.updateCustomEventType(reminderSettingCallbacks, subCalendar, customEventTypeId, title, icon, periodInMins);
        this.uncacheSubCalendarContent(subCalendar);
        return customEventTypeEntity;
    }

    @Override
    public ReminderSettingEntity updateReminderForSanboxEventType(Option<ReminderSettingCallback> reminderSettingCallbacks, T subCalendar, String eventTypeId, int periodInMins) {
        ReminderSettingEntity reminderSettingEntity = this.calendarDataStore.updateReminderForSanboxEventType(reminderSettingCallbacks, subCalendar, eventTypeId, periodInMins);
        this.uncacheSubCalendarContent(subCalendar);
        return reminderSettingEntity;
    }

    @Override
    public ReminderSettingEntity getReminderSetting(String subCalendarId, String storeKey, String customEventTypeId) {
        return this.calendarDataStore.getReminderSetting(subCalendarId, storeKey, customEventTypeId);
    }

    @Override
    public Map<String, Set<String>> getVEventUidsForUserBySubCalendar(ConfluenceUser confluenceUser) {
        return this.calendarDataStore.getVEventUidsForUserBySubCalendar(confluenceUser);
    }

    @Override
    public CustomEventTypeEntity getCustomEventType(T subCalendar, String customEventTypeId) {
        CustomEventTypeEntity customEventTypeEntity = this.calendarDataStore.getCustomEventType(subCalendar, customEventTypeId);
        this.uncacheSubCalendarContent(subCalendar);
        return customEventTypeEntity;
    }

    @Override
    public List<CustomEventTypeEntity> getCustomEventTypes(String ... customEventTypeId) {
        return this.calendarDataStore.getCustomEventTypes(customEventTypeId);
    }

    @Override
    public void deleteDisableEventType(String subCalendarId, String eventType) {
        this.calendarDataStore.deleteDisableEventType(subCalendarId, eventType);
    }

    @Override
    public void deleteCustomEventType(String subCalendarId, String customEventTypeId) {
        this.calendarDataStore.deleteCustomEventType(subCalendarId, customEventTypeId);
    }

    @Override
    public List<ReminderEvent> getSingleEventUpComingReminder(long startDateSystemUTC, long schedulerTime) {
        return this.calendarDataStore.getSingleEventUpComingReminder(startDateSystemUTC, schedulerTime);
    }

    @Override
    public List<VEvent> getRepeatEventUpComingReminder() {
        return this.calendarDataStore.getRepeatEventUpComingReminder();
    }

    @Override
    public List<ReminderEvent> getJiraEventUpComingReminder(long startDateSystemUTC, long schedulerTime) {
        return this.calendarDataStore.getJiraEventUpComingReminder(startDateSystemUTC, schedulerTime);
    }

    @Override
    public <T1> Option<T1> getReminderListFor(Function<Map<String, Collection<String>>, T1> callback, String ... subCalendarIds) {
        return this.calendarDataStore.getReminderListFor(callback, subCalendarIds);
    }

    @Override
    public Map<Integer, Collection<String>> getInviteesFor(Integer ... eventIds) {
        return this.calendarDataStore.getInviteesFor(eventIds);
    }

    @Override
    public List<String> getInviteesFor(String eventUid) {
        return this.calendarDataStore.getInviteesFor(eventUid);
    }

    @Override
    public Set<String> getChildSubCalendarHasReminders(ConfluenceUser user, String ... childSubCalendars) {
        return this.calendarDataStore.getChildSubCalendarHasReminders(user, childSubCalendars);
    }

    @Override
    public Set<String> getAllSubCalendarIdHasReminders(ConfluenceUser user) {
        return this.calendarDataStore.getAllSubCalendarIdHasReminders(user);
    }

    @Override
    public Option<T> getChildSubCalendarByStoreKey(T parentSubCalendar, String storeKey) {
        return this.calendarDataStore.getChildSubCalendarByStoreKey(parentSubCalendar, storeKey);
    }

    @Override
    public Option<T> getChildSubCalendarByCustomEventTypeId(T parentSubCalendar, String customEventTypeId) {
        return this.calendarDataStore.getChildSubCalendarByCustomEventTypeId(parentSubCalendar, customEventTypeId);
    }

    @Override
    public Set<String> filterExistSubCalendarIds(String ... subCalendarIds) {
        return this.calendarDataStore.filterExistSubCalendarIds(subCalendarIds);
    }

    @Override
    public boolean checkExistCalendarDataStoreFromCache(String subCalendarId) {
        Cache<String, String> subCalendarIdCache = this.getSubCalendarIdCache();
        boolean existCalendarDataStoreCache = subCalendarIdCache.containsKey((Object)subCalendarId);
        if (existCalendarDataStoreCache) {
            return StringUtils.isNotEmpty((String)subCalendarIdCache.get((Object)subCalendarId));
        }
        return false;
    }

    @Override
    public boolean checkExistJiraReminderEvent(String keyId) {
        return this.calendarDataStore.checkExistJiraReminderEvent(keyId);
    }

    @Override
    public EventTypeReminder getEventTypeReminder(T subCalendar) {
        return this.calendarDataStore.getEventTypeReminder(subCalendar);
    }

    @Override
    public Set<String> getSubCalendarIdsOnSpace(String spaceKey) {
        return this.calendarDataStore.getSubCalendarIdsOnSpace(spaceKey);
    }

    @Override
    public void removeSubCalendarFromSpaceView(T subCalendar, String spaceKey) {
        this.calendarDataStore.removeSubCalendarFromSpaceView(subCalendar, spaceKey);
    }

    @Override
    public void addCalendarsToSpaceView(Set<String> calendarIds, String spaceKey) {
        this.calendarDataStore.addCalendarsToSpaceView(calendarIds, spaceKey);
    }

    @Override
    public Message getSubCalendarEventWarning(T subCalendar) throws Exception {
        GenericMessage warning = null;
        Calendar cacheSubCalendarEventContent = this.getCachedSubCalendarContent(subCalendar);
        if (null == cacheSubCalendarEventContent || CACHE_VALUE_NONE.equals(cacheSubCalendarEventContent)) {
            return this.calendarDataStore.getSubCalendarEventWarning(subCalendar);
        }
        Calendar subCalendarEventCalendar = this.copyCalendar(cacheSubCalendarEventContent);
        ComponentList vEvents = subCalendarEventCalendar.getComponents("VEVENT");
        if (vEvents != null && vEvents.size() >= CalendarUtil.MAX_JIRA_ISSUES_TO_DISPLAY) {
            warning = new GenericMessage("calendar3.jira.error.calendartruncated", new Serializable[]{((PersistedSubCalendar)subCalendar).getName(), Integer.valueOf(CalendarUtil.MAX_JIRA_ISSUES_TO_DISPLAY)});
        }
        return warning;
    }

    @Override
    public void updateJiraReminderEvents(T subCalendar, Calendar subCalendarContent) {
        this.calendarDataStore.updateJiraReminderEvents(subCalendar, subCalendarContent);
    }

    @Override
    public void removeSubCalendarRestrictions(String userKey) {
        this.calendarDataStore.removeSubCalendarRestrictions(userKey);
    }

    @Override
    public void deleteInviteeFromAllEvents(String userKey) {
        this.calendarDataStore.deleteInviteeFromAllEvents(userKey);
    }

    public class SubCalendarIdCacheLoader
    implements CacheLoader<String, String> {
        public String load(String subCalendarId) {
            boolean hasSubCalendarId = CalendarDataStoreCachingDecorator.this.calendarDataStore.hasSubCalendar(subCalendarId);
            if (hasSubCalendarId) {
                return CalendarDataStoreCachingDecorator.this.calendarDataStore.getClass().getName();
            }
            return "";
        }
    }

    private static class ByRecurrenceIdPredicate
    implements com.google.common.base.Predicate<VEvent> {
        private final String recurrenceId;

        private ByRecurrenceIdPredicate(String recurrenceId) {
            this.recurrenceId = recurrenceId;
        }

        public boolean apply(VEvent vEvent) {
            return StringUtils.isBlank(this.recurrenceId) && (vEvent.getRecurrenceId() == null || StringUtils.isBlank(vEvent.getRecurrenceId().getValue())) || StringUtils.isNotBlank(this.recurrenceId) && vEvent.getRecurrenceId() != null && StringUtils.equals(this.recurrenceId, vEvent.getRecurrenceId().getValue());
        }
    }

    private static class ByUidPredicates
    implements com.google.common.base.Predicate<VEvent> {
        private final String[] vEventUids;

        private ByUidPredicates(String[] vEventUids) {
            this.vEventUids = vEventUids;
        }

        public boolean apply(VEvent vEvent) {
            Uid uidProperty = vEvent.getUid();
            if (uidProperty == null) {
                return false;
            }
            long countMatch = Arrays.stream(this.vEventUids).filter(vEventUid -> StringUtils.equals(vEventUid, uidProperty.getValue())).count();
            return countMatch > 0L;
        }
    }
}

