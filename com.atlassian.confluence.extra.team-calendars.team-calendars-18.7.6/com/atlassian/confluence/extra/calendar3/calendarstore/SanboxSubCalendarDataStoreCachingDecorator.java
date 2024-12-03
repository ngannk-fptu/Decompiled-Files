/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.Cache
 *  com.atlassian.cache.CacheManager
 *  com.atlassian.cache.CacheSettingsBuilder
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.fugue.Option
 *  com.atlassian.sal.api.message.Message
 *  com.google.common.base.Function
 *  org.joda.time.DateTime
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.InitializingBean
 */
package com.atlassian.confluence.extra.calendar3.calendarstore;

import com.atlassian.cache.Cache;
import com.atlassian.cache.CacheManager;
import com.atlassian.cache.CacheSettingsBuilder;
import com.atlassian.confluence.extra.calendar3.caldav.filter.FilterBase;
import com.atlassian.confluence.extra.calendar3.caldav.filter.RecurrenceRetrieval;
import com.atlassian.confluence.extra.calendar3.calendarstore.BaseCacheableCalendarDataStore;
import com.atlassian.confluence.extra.calendar3.calendarstore.DelegatableCalendarDataStore;
import com.atlassian.confluence.extra.calendar3.calendarstore.RefreshableCalendarDataStore;
import com.atlassian.confluence.extra.calendar3.calendarstore.ReminderSettingCallback;
import com.atlassian.confluence.extra.calendar3.model.EventTypeReminder;
import com.atlassian.confluence.extra.calendar3.model.PersistedSubCalendar;
import com.atlassian.confluence.extra.calendar3.model.ReminderEvent;
import com.atlassian.confluence.extra.calendar3.model.SubCalendar;
import com.atlassian.confluence.extra.calendar3.model.SubCalendarEvent;
import com.atlassian.confluence.extra.calendar3.model.SubCalendarSummary;
import com.atlassian.confluence.extra.calendar3.model.persistence.CustomEventTypeEntity;
import com.atlassian.confluence.extra.calendar3.model.persistence.ReminderSettingEntity;
import com.atlassian.confluence.extra.calendar3.model.persistence.SubCalendarEntity;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.fugue.Option;
import com.atlassian.sal.api.message.Message;
import com.google.common.base.Function;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.component.VEvent;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

public class SanboxSubCalendarDataStoreCachingDecorator<T extends PersistedSubCalendar>
implements RefreshableCalendarDataStore<T>,
DelegatableCalendarDataStore<T>,
InitializingBean {
    private static final Logger LOG = LoggerFactory.getLogger(SanboxSubCalendarDataStoreCachingDecorator.class);
    private final BaseCacheableCalendarDataStore<T> calendarDataStore;
    private final CacheManager cacheManager;
    private Cache<String, String> subCalendarIdCache;

    public SanboxSubCalendarDataStoreCachingDecorator(BaseCacheableCalendarDataStore<T> calendarDataStore, CacheManager cacheManager) {
        this.calendarDataStore = calendarDataStore;
        this.cacheManager = cacheManager;
    }

    @Override
    public boolean handles(SubCalendar subCalendar) {
        return this.calendarDataStore instanceof DelegatableCalendarDataStore && ((DelegatableCalendarDataStore)((Object)this.calendarDataStore)).handles(subCalendar);
    }

    @Override
    public boolean hasReloadEventsPrivilege(T subCalendar, ConfluenceUser user) {
        return this.calendarDataStore instanceof RefreshableCalendarDataStore && ((RefreshableCalendarDataStore)((Object)this.calendarDataStore)).hasReloadEventsPrivilege(subCalendar, user);
    }

    @Override
    public void refresh(T subCalendar) {
        if (null != subCalendar && this.calendarDataStore instanceof RefreshableCalendarDataStore) {
            ((RefreshableCalendarDataStore)((Object)this.calendarDataStore)).refresh(subCalendar);
        }
    }

    @Override
    public void validate(SubCalendar subCalendar, Map<String, List<String>> fieldErrors) {
        this.calendarDataStore.validate(subCalendar, fieldErrors);
    }

    @Override
    public T save(SubCalendar subCalendar) throws Exception {
        return (T)((PersistedSubCalendar)((PersistedSubCalendar)this.calendarDataStore.save(subCalendar)).clone());
    }

    @Override
    public void remove(T subCalendar) {
        this.uncacheSubCalendarID(((PersistedSubCalendar)subCalendar).getId());
        this.calendarDataStore.remove(subCalendar);
    }

    @Override
    public T getSubCalendar(String subCalendarId) {
        return this.calendarDataStore.getSubCalendar(subCalendarId);
    }

    @Override
    public T getSubCalendar(SubCalendarEntity subCalendarEntity) {
        return this.calendarDataStore.getSubCalendar(subCalendarEntity);
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
    public List<String> filterSubCalendarIds(String ... subCalendarIds) {
        return this.calendarDataStore.filterSubCalendarIds(subCalendarIds);
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
    public Set<String> getAllParentSubCalendarIds(String spaceKey, int limit, int offset) {
        return this.calendarDataStore.getAllParentSubCalendarIds(spaceKey, limit, offset);
    }

    @Override
    public Calendar createEmptyCalendarForSubCalendar(T subCalendar) throws Exception {
        return this.calendarDataStore.createEmptyCalendarForSubCalendar(subCalendar);
    }

    @Override
    public Calendar getSubCalendarContent(T subCalendar) throws Exception {
        return this.calendarDataStore.getSubCalendarContent(subCalendar);
    }

    @Override
    public List<Message> getSubCalendarWarnings(T subCalendar) {
        return this.calendarDataStore.getSubCalendarWarnings(subCalendar);
    }

    @Override
    public void setSubCalendarContent(T subCalendar, Calendar subCalendarContent) throws Exception {
        this.calendarDataStore.setSubCalendarContent(subCalendar, subCalendarContent);
    }

    @Override
    public boolean hasSubCalendar(String subCalendarId) {
        return StringUtils.isNotEmpty((String)this.getSubCalendarIdCache().get((Object)subCalendarId));
    }

    @Override
    public boolean hasSubCalendar(PersistedSubCalendar subCalendar) {
        return this.calendarDataStore.hasSubCalendar(subCalendar);
    }

    @Override
    public boolean hasSubCalendar(SubCalendarEntity subCalendarEntity) {
        return this.calendarDataStore.hasSubCalendar(subCalendarEntity);
    }

    @Override
    public int getSubCalendarsCount() {
        return this.calendarDataStore.getSubCalendarsCount();
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
    public void restrictEventViewToGroups(String subCalendarId, Set<String> groupNames) {
        this.calendarDataStore.restrictEventViewToGroups(subCalendarId, groupNames);
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
        return this.calendarDataStore.query(subCalendar, filter, recurrenceRetrieval);
    }

    @Override
    public List<VEvent> getEvents(T subCalendar) throws Exception {
        return this.calendarDataStore.getEvents(subCalendar);
    }

    @Override
    public List<VEvent> getEvents(T subCalendar, DateTime startTime, DateTime endTime) throws Exception {
        return this.calendarDataStore.getEvents(subCalendar, startTime, endTime);
    }

    @Override
    public List<VEvent> getEvents(T subCalendar, Predicate<VEvent> vEventPredicate, String ... vEventUids) throws Exception {
        return this.calendarDataStore.getEvents(subCalendar, vEventPredicate, vEventUids);
    }

    @Override
    public VEvent getEvent(T subCalendar, String vEventUid, String recurrenceId) throws Exception {
        return this.calendarDataStore.getEvent(subCalendar, vEventUid, recurrenceId);
    }

    @Override
    public VEvent addEvent(T subCalendar, VEvent newEventDetails) {
        return this.calendarDataStore.addEvent(subCalendar, newEventDetails);
    }

    @Override
    public VEvent updateEvent(T subCalendar, VEvent newEventDetails) {
        return this.calendarDataStore.updateEvent(subCalendar, newEventDetails);
    }

    @Override
    public void deleteEvent(T subCalendar, String vEventUid, String recurrenceId) {
        this.calendarDataStore.deleteEvent(subCalendar, vEventUid, recurrenceId);
    }

    @Override
    public void moveEvent(T subCalendar, String vEventUid, PersistedSubCalendar destinationSubCalendar) {
        this.calendarDataStore.moveEvent(subCalendar, vEventUid, destinationSubCalendar);
    }

    @Override
    public void changeEvent(T subCalendar, String vEventUid, PersistedSubCalendar destinationSubCalendar) {
        this.calendarDataStore.changeEvent(subCalendar, vEventUid, destinationSubCalendar);
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
    }

    @Override
    public void deleteDisableEventType(String subCalendarId, String eventType) {
        this.calendarDataStore.deleteDisableEventType(subCalendarId, eventType);
    }

    @Override
    public CustomEventTypeEntity updateCustomEventType(Option<ReminderSettingCallback> reminderSettingCallbacks, T subCalendar, String customEventTypeId, String title, String icon, int periodInMins) {
        return this.calendarDataStore.updateCustomEventType(reminderSettingCallbacks, subCalendar, customEventTypeId, title, icon, periodInMins);
    }

    @Override
    public CustomEventTypeEntity getCustomEventType(T subCalendar, String customEventTypeId) {
        return this.calendarDataStore.getCustomEventType(subCalendar, customEventTypeId);
    }

    @Override
    public List<CustomEventTypeEntity> getCustomEventTypes(String ... customEventTypeId) {
        return this.calendarDataStore.getCustomEventTypes(customEventTypeId);
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
    public ReminderSettingEntity updateReminderForSanboxEventType(Option<ReminderSettingCallback> reminderSettingCallbacks, T subCalendar, String eventTypeId, int periodInMins) {
        return this.calendarDataStore.updateReminderForSanboxEventType(reminderSettingCallbacks, subCalendar, eventTypeId, periodInMins);
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
        return false;
    }

    @Override
    public EventTypeReminder getEventTypeReminder(T subCalendar) {
        return null;
    }

    public void afterPropertiesSet() throws Exception {
        try {
            this.getSubCalendarIdCache().removeAll();
        }
        catch (RuntimeException re) {
            LOG.warn("Error flush caches SanboxSubCalendarDataStoreCachingDecorator. It's probably because of a racing condition to get them initialized. If so, nothing to worry about");
            LOG.debug("Error detail is:", (Throwable)re);
        }
    }

    private Cache<String, String> getSubCalendarIdCache() {
        if (this.subCalendarIdCache == null) {
            this.subCalendarIdCache = this.cacheManager.getCache(this.calendarDataStore.getClass().getName() + ":subcalendar-id.c711", subCalendarId -> {
                boolean hasSubCalendarId = this.calendarDataStore.hasSubCalendar((String)subCalendarId);
                if (hasSubCalendarId) {
                    return this.calendarDataStore.getClass().getName();
                }
                return "";
            }, new CacheSettingsBuilder().replicateViaInvalidation().replicateAsynchronously().flushable().build());
        }
        return this.subCalendarIdCache;
    }

    protected void uncacheSubCalendarID(String subCalendarId) {
        this.getSubCalendarIdCache().remove((Object)subCalendarId);
    }

    @Override
    public Message getSubCalendarEventWarning(T subCalendar) throws Exception {
        return this.calendarDataStore.getSubCalendarEventWarning(subCalendar);
    }

    @Override
    public void updateJiraReminderEvents(T subCalendar, Calendar subCalendarContent) {
        this.calendarDataStore.updateJiraReminderEvents(subCalendar, subCalendarContent);
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
    public void removeSubCalendarRestrictions(String userKey) {
        this.calendarDataStore.removeSubCalendarRestrictions(userKey);
    }

    @Override
    public void deleteInviteeFromAllEvents(String userKey) {
        this.calendarDataStore.deleteInviteeFromAllEvents(userKey);
    }
}

