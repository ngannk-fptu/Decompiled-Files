/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.fugue.Iterables
 *  com.atlassian.fugue.Option
 *  com.atlassian.sal.api.message.Message
 *  com.atlassian.util.profiling.UtilTimerStack
 *  com.google.common.base.Function
 *  com.google.common.base.Predicate
 *  com.google.common.base.Predicates
 *  com.google.common.collect.Collections2
 *  com.google.common.collect.Lists
 *  org.joda.time.DateTime
 */
package com.atlassian.confluence.extra.calendar3.calendarstore;

import com.atlassian.confluence.extra.calendar3.ActiveObjectsServiceWrapper;
import com.atlassian.confluence.extra.calendar3.caldav.filter.FilterBase;
import com.atlassian.confluence.extra.calendar3.caldav.filter.RecurrenceRetrieval;
import com.atlassian.confluence.extra.calendar3.calendarstore.AbstractCalendarDataStore;
import com.atlassian.confluence.extra.calendar3.calendarstore.CalendarDataStore;
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
import com.atlassian.fugue.Iterables;
import com.atlassian.fugue.Option;
import com.atlassian.sal.api.message.Message;
import com.atlassian.util.profiling.UtilTimerStack;
import com.google.common.base.Function;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.component.VEvent;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;

public class DelegatingCalendarDataStore<T extends PersistedSubCalendar>
implements RefreshableCalendarDataStore<T> {
    private final List<DelegatableCalendarDataStore<T>> calendarDataStores;
    private final ActiveObjectsServiceWrapper activeObjectsServiceWrapper;

    public DelegatingCalendarDataStore(List<DelegatableCalendarDataStore<T>> calendarDataStores, ActiveObjectsServiceWrapper activeObjectsServiceWrapper) {
        this.calendarDataStores = calendarDataStores;
        this.activeObjectsServiceWrapper = activeObjectsServiceWrapper;
    }

    private Collection<DelegatableCalendarDataStore<T>> getStores() {
        return this.calendarDataStores;
    }

    private boolean hasSuitableHandler(SubCalendar subCalendar) {
        return null != this.getFirstFoundHandler(subCalendar);
    }

    private DelegatableCalendarDataStore<T> getFirstFoundHandler(SubCalendar subCalendar) {
        Collection calendarDataStores = this.getStores().stream().filter(aCalendarDataStore -> aCalendarDataStore.handles(subCalendar)).collect(Collectors.toList());
        return calendarDataStores.isEmpty() ? null : (DelegatableCalendarDataStore)calendarDataStores.iterator().next();
    }

    @Override
    public void validate(SubCalendar subCalendar, Map<String, List<String>> fieldErrors) {
        Collection handledStores = this.getStores().stream().filter(aCalendarDataStore -> aCalendarDataStore.handles(subCalendar)).collect(Collectors.toList());
        Iterator iterator = handledStores.iterator();
        if (iterator.hasNext()) {
            DelegatableCalendarDataStore calendarDataStore = (DelegatableCalendarDataStore)iterator.next();
            calendarDataStore.validate(subCalendar, fieldErrors);
            return;
        }
    }

    @Override
    public T save(SubCalendar subCalendar) throws Exception {
        if (!this.hasSuitableHandler(subCalendar)) {
            throw new UnsupportedOperationException("Unable to find a delegate to handle the operation");
        }
        return this.getFirstFoundHandler(subCalendar).save(subCalendar);
    }

    @Override
    public void remove(T subCalendar) {
        if (!this.hasSuitableHandler((SubCalendar)subCalendar)) {
            throw new UnsupportedOperationException("Unable to find a delegate to handle the operation");
        }
        this.getFirstFoundHandler((SubCalendar)subCalendar).remove(subCalendar);
    }

    @Override
    public T getSubCalendar(String subCalendarId) {
        CalendarDataStore existCalendarDataStoreFromCache = this.getCalendarDataStoreFollowSubCalendarIdFromCache(subCalendarId);
        if (existCalendarDataStoreFromCache != null) {
            return existCalendarDataStoreFromCache.getSubCalendar(subCalendarId);
        }
        for (CalendarDataStore calendarDataStore : this.getStores()) {
            if (!calendarDataStore.hasSubCalendar(subCalendarId)) continue;
            return calendarDataStore.getSubCalendar(subCalendarId);
        }
        return null;
    }

    @Override
    public List<T> getSubCalendarsWithRestriction(String ... subCalendarIds) {
        ArrayList subCalendarEntityList = Lists.newArrayList((Object[])((SubCalendarEntity[])this.activeObjectsServiceWrapper.getActiveObjects().get(SubCalendarEntity.class, (Object[])subCalendarIds)));
        ArrayList returnSubCalendarList = Lists.newArrayList((Iterable)Collections2.filter((Collection)Collections2.transform((Collection)subCalendarEntityList, subCalendarEntity -> {
            for (CalendarDataStore calendarDataStore : this.getStores()) {
                if (!calendarDataStore.hasSubCalendar((SubCalendarEntity)subCalendarEntity)) continue;
                return calendarDataStore.getSubCalendar((SubCalendarEntity)subCalendarEntity);
            }
            return null;
        }), (com.google.common.base.Predicate)Predicates.notNull()));
        return this.loadRestrictions(returnSubCalendarList);
    }

    @Override
    public List<T> loadRestrictions(List<T> persistedEntities) {
        for (CalendarDataStore calendarDataStore : this.calendarDataStores) {
            if (!(calendarDataStore instanceof AbstractCalendarDataStore)) continue;
            return ((AbstractCalendarDataStore)calendarDataStore).loadRestrictions(persistedEntities);
        }
        return Lists.newArrayList();
    }

    @Override
    public SubCalendarSummary getSubCalendarSummary(String subCalendarId) {
        CalendarDataStore existCalendarDataStoreFromCache = this.getCalendarDataStoreFollowSubCalendarIdFromCache(subCalendarId);
        if (existCalendarDataStoreFromCache != null) {
            return existCalendarDataStoreFromCache.getSubCalendarSummary(subCalendarId);
        }
        for (CalendarDataStore calendarDataStore : this.getStores()) {
            if (!calendarDataStore.hasSubCalendar(subCalendarId)) continue;
            return calendarDataStore.getSubCalendarSummary(subCalendarId);
        }
        return null;
    }

    @Override
    public List<SubCalendarSummary> getSubCalendarSummariesByStoreKey(String storeKey, int limit, int offset) {
        return this.getDefaultCalendarDataStore().getSubCalendarSummariesByStoreKey(storeKey, limit, offset);
    }

    @Override
    public Set<String> getAllParentSubCalendarIds() {
        for (CalendarDataStore calendarDataStore : this.calendarDataStores) {
            if (!(calendarDataStore instanceof AbstractCalendarDataStore)) continue;
            return ((AbstractCalendarDataStore)calendarDataStore).getAllParentSubCalendarIds();
        }
        return null;
    }

    @Override
    public Set<String> getAllParentSubCalendarIds(String spaceKey, int limit, int offset) {
        for (CalendarDataStore calendarDataStore : this.calendarDataStores) {
            if (!(calendarDataStore instanceof AbstractCalendarDataStore)) continue;
            return ((AbstractCalendarDataStore)calendarDataStore).getAllParentSubCalendarIds(spaceKey, limit, offset);
        }
        return null;
    }

    @Override
    public Calendar createEmptyCalendarForSubCalendar(T subCalendar) throws Exception {
        return this.getFirstFoundHandler((SubCalendar)subCalendar).createEmptyCalendarForSubCalendar(subCalendar);
    }

    @Override
    public Calendar getSubCalendarContent(T subCalendar) throws Exception {
        String subCalendarId = ((PersistedSubCalendar)subCalendar).getId();
        CalendarDataStore existCalendarDataStoreFromCache = this.getCalendarDataStoreFollowSubCalendarIdFromCache(subCalendarId);
        if (existCalendarDataStoreFromCache != null) {
            return existCalendarDataStoreFromCache.getSubCalendarContent(existCalendarDataStoreFromCache.getSubCalendar(subCalendarId));
        }
        for (CalendarDataStore calendarDataStore : this.getStores()) {
            if (!calendarDataStore.hasSubCalendar((PersistedSubCalendar)subCalendar)) continue;
            return calendarDataStore.getSubCalendarContent(calendarDataStore.getSubCalendar(subCalendarId));
        }
        return null;
    }

    @Override
    public void setSubCalendarContent(T subCalendar, Calendar subCalendarContent) throws Exception {
        if (!this.hasSuitableHandler((SubCalendar)subCalendar)) {
            throw new UnsupportedOperationException("Unable to find a delegate to handle the operation");
        }
        this.getFirstFoundHandler((SubCalendar)subCalendar).setSubCalendarContent(subCalendar, subCalendarContent);
    }

    @Override
    public List<Message> getSubCalendarWarnings(T subCalendar) {
        if (!this.hasSuitableHandler((SubCalendar)subCalendar)) {
            throw new UnsupportedOperationException("Unable to find a delegate to handle the operation");
        }
        return this.getFirstFoundHandler((SubCalendar)subCalendar).getSubCalendarWarnings(subCalendar);
    }

    @Override
    public boolean hasSubCalendar(String subCalendarId) {
        CalendarDataStore existCalendarDataStoreFromCache = this.getCalendarDataStoreFollowSubCalendarIdFromCache(subCalendarId);
        if (existCalendarDataStoreFromCache != null) {
            return true;
        }
        for (CalendarDataStore calendarDataStore : this.getStores()) {
            if (!calendarDataStore.hasSubCalendar(subCalendarId)) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean checkExistCalendarDataStoreFromCache(String subCalendarId) {
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

    private CalendarDataStore getCalendarDataStoreFollowSubCalendarIdFromCache(String subCalendarId) {
        for (CalendarDataStore calendarDataStore : this.getStores()) {
            if (!calendarDataStore.checkExistCalendarDataStoreFromCache(subCalendarId)) continue;
            return calendarDataStore;
        }
        return null;
    }

    @Override
    public boolean hasSubCalendar(PersistedSubCalendar subCalendar) {
        CalendarDataStore existCalendarDataStoreFromCache = this.getCalendarDataStoreFollowSubCalendarIdFromCache(subCalendar.getId());
        if (existCalendarDataStoreFromCache != null) {
            return true;
        }
        for (CalendarDataStore calendarDataStore : this.getStores()) {
            if (!calendarDataStore.hasSubCalendar(subCalendar)) continue;
            return true;
        }
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public T getSubCalendar(SubCalendarEntity subCalendarEntity) {
        UtilTimerStack.push((String)"DelegatingCalendarDataStore.getSubCalendar()");
        try {
            for (CalendarDataStore calendarDataStore : this.getStores()) {
                if (!calendarDataStore.hasSubCalendar(subCalendarEntity)) continue;
                Object t = calendarDataStore.getSubCalendar(subCalendarEntity);
                return t;
            }
            Iterator<DelegatableCalendarDataStore<T>> iterator = null;
            return (T)iterator;
        }
        finally {
            UtilTimerStack.pop((String)"DelegatingCalendarDataStore.getSubCalendar()");
        }
    }

    @Override
    public boolean hasSubCalendar(SubCalendarEntity subCalendarEntity) {
        for (CalendarDataStore calendarDataStore : this.getStores()) {
            if (!calendarDataStore.hasSubCalendar(subCalendarEntity)) continue;
            return true;
        }
        return false;
    }

    @Override
    public List<String> filterSubCalendarIds(String ... subCalendarIds) {
        return this.getStores().iterator().next().filterSubCalendarIds(subCalendarIds);
    }

    @Override
    public int getSubCalendarsCount() {
        int nSubCalendars = 0;
        for (CalendarDataStore calendarDataStore : this.getStores()) {
            nSubCalendars += calendarDataStore.getSubCalendarsCount();
        }
        return nSubCalendars;
    }

    @Override
    public boolean hasReloadEventsPrivilege(T subCalendar, ConfluenceUser user) {
        DelegatableCalendarDataStore<T> delegatableCalendarDataStore = this.getFirstFoundHandler((SubCalendar)subCalendar);
        return null != delegatableCalendarDataStore && delegatableCalendarDataStore instanceof RefreshableCalendarDataStore && ((RefreshableCalendarDataStore)((Object)delegatableCalendarDataStore)).hasReloadEventsPrivilege(subCalendar, user);
    }

    @Override
    public void refresh(T subCalendar) {
        DelegatableCalendarDataStore<T> delegatableCalendarDataStore = this.getFirstFoundHandler((SubCalendar)subCalendar);
        if (null != delegatableCalendarDataStore && delegatableCalendarDataStore instanceof RefreshableCalendarDataStore) {
            ((RefreshableCalendarDataStore)((Object)delegatableCalendarDataStore)).refresh(subCalendar);
        }
    }

    @Override
    public Set<ConfluenceUser> getEventEditUserRestrictions(String subCalendarId) {
        CalendarDataStore existCalendarDataStoreFromCache = this.getCalendarDataStoreFollowSubCalendarIdFromCache(subCalendarId);
        if (existCalendarDataStoreFromCache != null) {
            return existCalendarDataStoreFromCache.getEventEditUserRestrictions(subCalendarId);
        }
        for (CalendarDataStore calendarDataStore : this.getStores()) {
            if (!calendarDataStore.hasSubCalendar(subCalendarId)) continue;
            return calendarDataStore.getEventEditUserRestrictions(subCalendarId);
        }
        return Collections.emptySet();
    }

    @Override
    public Set<ConfluenceUser> getEventEditUserRestrictions(T subCalendar) {
        CalendarDataStore existCalendarDataStoreFromCache = this.getCalendarDataStoreFollowSubCalendarIdFromCache(((PersistedSubCalendar)subCalendar).getId());
        if (existCalendarDataStoreFromCache != null) {
            return existCalendarDataStoreFromCache.getEventEditUserRestrictions(subCalendar);
        }
        for (CalendarDataStore calendarDataStore : this.getStores()) {
            if (!calendarDataStore.hasSubCalendar((PersistedSubCalendar)subCalendar)) continue;
            return calendarDataStore.getEventEditUserRestrictions(subCalendar);
        }
        return Collections.emptySet();
    }

    @Override
    public Set<String> getEventEditGroupRestrictions(String subCalendarId) {
        CalendarDataStore existCalendarDataStoreFromCache = this.getCalendarDataStoreFollowSubCalendarIdFromCache(subCalendarId);
        if (existCalendarDataStoreFromCache != null) {
            return existCalendarDataStoreFromCache.getEventEditGroupRestrictions(subCalendarId);
        }
        for (CalendarDataStore calendarDataStore : this.getStores()) {
            if (!calendarDataStore.hasSubCalendar(subCalendarId)) continue;
            return calendarDataStore.getEventEditGroupRestrictions(subCalendarId);
        }
        return Collections.emptySet();
    }

    @Override
    public Set<String> getEventEditGroupRestrictions(T subCalendar) {
        CalendarDataStore existCalendarDataStoreFromCache = this.getCalendarDataStoreFollowSubCalendarIdFromCache(((PersistedSubCalendar)subCalendar).getId());
        if (existCalendarDataStoreFromCache != null) {
            return existCalendarDataStoreFromCache.getEventEditGroupRestrictions(subCalendar);
        }
        for (CalendarDataStore calendarDataStore : this.getStores()) {
            if (!calendarDataStore.hasSubCalendar((PersistedSubCalendar)subCalendar)) continue;
            return calendarDataStore.getEventEditGroupRestrictions(subCalendar);
        }
        return Collections.emptySet();
    }

    @Override
    public Set<ConfluenceUser> getEventViewUserRestrictions(String subCalendarId) {
        CalendarDataStore existCalendarDataStoreFromCache = this.getCalendarDataStoreFollowSubCalendarIdFromCache(subCalendarId);
        if (existCalendarDataStoreFromCache != null) {
            return existCalendarDataStoreFromCache.getEventViewUserRestrictions(subCalendarId);
        }
        for (CalendarDataStore calendarDataStore : this.getStores()) {
            if (!calendarDataStore.hasSubCalendar(subCalendarId)) continue;
            return calendarDataStore.getEventViewUserRestrictions(subCalendarId);
        }
        return Collections.emptySet();
    }

    @Override
    public Set<ConfluenceUser> getEventViewUserRestrictions(T subCalendar) {
        CalendarDataStore existCalendarDataStoreFromCache = this.getCalendarDataStoreFollowSubCalendarIdFromCache(((PersistedSubCalendar)subCalendar).getId());
        if (existCalendarDataStoreFromCache != null) {
            return existCalendarDataStoreFromCache.getEventViewUserRestrictions(subCalendar);
        }
        for (CalendarDataStore calendarDataStore : this.getStores()) {
            if (!calendarDataStore.hasSubCalendar((PersistedSubCalendar)subCalendar)) continue;
            return calendarDataStore.getEventViewUserRestrictions(subCalendar);
        }
        return Collections.emptySet();
    }

    @Override
    public Set<String> getEventViewGroupRestrictions(String subCalendarId) {
        CalendarDataStore existCalendarDataStoreFromCache = this.getCalendarDataStoreFollowSubCalendarIdFromCache(subCalendarId);
        if (existCalendarDataStoreFromCache != null) {
            return existCalendarDataStoreFromCache.getEventViewGroupRestrictions(subCalendarId);
        }
        for (CalendarDataStore calendarDataStore : this.getStores()) {
            if (!calendarDataStore.hasSubCalendar(subCalendarId)) continue;
            return calendarDataStore.getEventViewGroupRestrictions(subCalendarId);
        }
        return Collections.emptySet();
    }

    @Override
    public Set<String> getEventViewGroupRestrictions(T subCalendar) {
        CalendarDataStore existCalendarDataStoreFromCache = this.getCalendarDataStoreFollowSubCalendarIdFromCache(((PersistedSubCalendar)subCalendar).getId());
        if (existCalendarDataStoreFromCache != null) {
            return existCalendarDataStoreFromCache.getEventViewGroupRestrictions(subCalendar);
        }
        for (CalendarDataStore calendarDataStore : this.getStores()) {
            if (!calendarDataStore.hasSubCalendar((PersistedSubCalendar)subCalendar)) continue;
            return calendarDataStore.getEventViewGroupRestrictions(subCalendar);
        }
        return Collections.emptySet();
    }

    @Override
    public void restrictEventEditToUsers(String subCalendarId, Set<ConfluenceUser> users) {
        boolean privsSet = false;
        CalendarDataStore existCalendarDataStoreFromCache = this.getCalendarDataStoreFollowSubCalendarIdFromCache(subCalendarId);
        if (existCalendarDataStoreFromCache != null) {
            existCalendarDataStoreFromCache.restrictEventEditToUsers(subCalendarId, users);
            return;
        }
        for (CalendarDataStore calendarDataStore : this.getStores()) {
            if (!calendarDataStore.hasSubCalendar(subCalendarId)) continue;
            calendarDataStore.restrictEventEditToUsers(subCalendarId, users);
            privsSet = true;
            break;
        }
        if (!privsSet) {
            throw new UnsupportedOperationException("Unable to find a delegate to handle the operation");
        }
    }

    @Override
    public void restrictEventEditToGroups(String subCalendarId, Set<String> groupNames) {
        boolean privsSet = false;
        CalendarDataStore existCalendarDataStoreFromCache = this.getCalendarDataStoreFollowSubCalendarIdFromCache(subCalendarId);
        if (existCalendarDataStoreFromCache != null) {
            existCalendarDataStoreFromCache.restrictEventEditToGroups(subCalendarId, groupNames);
            return;
        }
        for (CalendarDataStore calendarDataStore : this.getStores()) {
            if (!calendarDataStore.hasSubCalendar(subCalendarId)) continue;
            calendarDataStore.restrictEventEditToGroups(subCalendarId, groupNames);
            privsSet = true;
            break;
        }
        if (!privsSet) {
            throw new UnsupportedOperationException("Unable to find a delegate to handle the operation");
        }
    }

    @Override
    public void restrictEventViewToUsers(String subCalendarId, Set<ConfluenceUser> users) {
        boolean privsSet = false;
        CalendarDataStore existCalendarDataStoreFromCache = this.getCalendarDataStoreFollowSubCalendarIdFromCache(subCalendarId);
        if (existCalendarDataStoreFromCache != null) {
            existCalendarDataStoreFromCache.restrictEventViewToUsers(subCalendarId, users);
            return;
        }
        for (CalendarDataStore calendarDataStore : this.getStores()) {
            if (!calendarDataStore.hasSubCalendar(subCalendarId)) continue;
            calendarDataStore.restrictEventViewToUsers(subCalendarId, users);
            privsSet = true;
            break;
        }
        if (!privsSet) {
            throw new UnsupportedOperationException("Unable to find a delegate to handle the operation");
        }
    }

    @Override
    public void restrictEventViewToGroups(String subCalendarId, Set<String> groupNames) {
        boolean privsSet = false;
        CalendarDataStore existCalendarDataStoreFromCache = this.getCalendarDataStoreFollowSubCalendarIdFromCache(subCalendarId);
        if (existCalendarDataStoreFromCache != null) {
            existCalendarDataStoreFromCache.restrictEventViewToGroups(subCalendarId, groupNames);
            return;
        }
        for (CalendarDataStore calendarDataStore : this.getStores()) {
            if (!calendarDataStore.hasSubCalendar(subCalendarId)) continue;
            calendarDataStore.restrictEventViewToGroups(subCalendarId, groupNames);
            privsSet = true;
            break;
        }
        if (!privsSet) {
            throw new UnsupportedOperationException("Unable to find a delegate to handle the operation");
        }
    }

    @Override
    public boolean hasViewEventPrivilege(String subCalendarId, ConfluenceUser user) {
        CalendarDataStore existCalendarDataStoreFromCache = this.getCalendarDataStoreFollowSubCalendarIdFromCache(subCalendarId);
        if (existCalendarDataStoreFromCache != null) {
            return existCalendarDataStoreFromCache.hasViewEventPrivilege(subCalendarId, user);
        }
        for (CalendarDataStore calendarDataStore : this.getStores()) {
            if (!calendarDataStore.hasSubCalendar(subCalendarId)) continue;
            return calendarDataStore.hasViewEventPrivilege(subCalendarId, user);
        }
        return false;
    }

    @Override
    public boolean hasViewEventPrivilege(T subCalendar, ConfluenceUser user) {
        CalendarDataStore existCalendarDataStoreFromCache = this.getCalendarDataStoreFollowSubCalendarIdFromCache(((PersistedSubCalendar)subCalendar).getId());
        if (existCalendarDataStoreFromCache != null) {
            if (StringUtils.isEmpty(((SubCalendar)subCalendar).getStoreKey())) {
                return existCalendarDataStoreFromCache.hasViewEventPrivilege(((PersistedSubCalendar)subCalendar).getId(), user);
            }
            return existCalendarDataStoreFromCache.hasViewEventPrivilege(subCalendar, user);
        }
        for (CalendarDataStore calendarDataStore : this.getStores()) {
            if (!calendarDataStore.hasSubCalendar((PersistedSubCalendar)subCalendar)) continue;
            if (StringUtils.isEmpty(((SubCalendar)subCalendar).getStoreKey())) {
                return calendarDataStore.hasViewEventPrivilege(((PersistedSubCalendar)subCalendar).getId(), user);
            }
            return calendarDataStore.hasViewEventPrivilege(subCalendar, user);
        }
        return false;
    }

    @Override
    public boolean hasEditEventPrivilege(T subCalendar, ConfluenceUser user) {
        DelegatableCalendarDataStore<T> handler = this.getFirstFoundHandler((SubCalendar)subCalendar);
        return null != handler && handler.hasEditEventPrivilege(subCalendar, user);
    }

    @Override
    public boolean hasDeletePrivilege(T subCalendar, ConfluenceUser user) {
        DelegatableCalendarDataStore<T> handler = this.getFirstFoundHandler((SubCalendar)subCalendar);
        return null != handler && handler.hasDeletePrivilege(subCalendar, user);
    }

    @Override
    public boolean hasAdminPrivilege(T subCalendar, ConfluenceUser user) {
        DelegatableCalendarDataStore<T> handler = this.getFirstFoundHandler((SubCalendar)subCalendar);
        return null != handler && handler.hasAdminPrivilege(subCalendar, user);
    }

    @Override
    public SubCalendarEvent transform(SubCalendarEvent toBeTransformed, VEvent raw) {
        DelegatableCalendarDataStore<T> handler = this.getFirstFoundHandler(toBeTransformed.getSubCalendar());
        return null == handler ? toBeTransformed : handler.transform(toBeTransformed, raw);
    }

    @Override
    public Message getTypeSpecificText(T subCalendar, Message originalMessage) {
        DelegatableCalendarDataStore<T> handler = this.getFirstFoundHandler((SubCalendar)subCalendar);
        return null != handler ? handler.getTypeSpecificText(subCalendar, originalMessage) : null;
    }

    @Override
    public Collection<VEvent> query(T subCalendar, FilterBase filter, RecurrenceRetrieval recurrenceRetrieval) throws Exception {
        return this.getFirstFoundHandler((SubCalendar)subCalendar).query(subCalendar, filter, recurrenceRetrieval);
    }

    @Override
    public List<VEvent> getEvents(T subCalendar, DateTime startTime, DateTime endTime) throws Exception {
        return this.getFirstFoundHandler((SubCalendar)subCalendar).getEvents(subCalendar, startTime, endTime);
    }

    @Override
    public List<VEvent> getEvents(T subCalendar, Predicate<VEvent> vEventPredicate, String ... vEventUids) throws Exception {
        return this.getFirstFoundHandler((SubCalendar)subCalendar).getEvents(subCalendar, vEventPredicate, vEventUids);
    }

    @Override
    public List<VEvent> getEvents(T subCalendar) throws Exception {
        return this.getFirstFoundHandler((SubCalendar)subCalendar).getEvents(subCalendar);
    }

    @Override
    public VEvent getEvent(T subCalendar, String vEventUid, String recurrenceId) throws Exception {
        return this.getFirstFoundHandler((SubCalendar)subCalendar).getEvent(subCalendar, vEventUid, recurrenceId);
    }

    @Override
    public VEvent addEvent(T subCalendar, VEvent newEventDetails) {
        return this.getFirstFoundHandler((SubCalendar)subCalendar).addEvent(subCalendar, newEventDetails);
    }

    @Override
    public VEvent updateEvent(T subCalendar, VEvent newEventDetails) {
        return this.getFirstFoundHandler((SubCalendar)subCalendar).updateEvent(subCalendar, newEventDetails);
    }

    @Override
    public void deleteEvent(T subCalendar, String vEventUid, String recurrenceId) {
        this.getFirstFoundHandler((SubCalendar)subCalendar).deleteEvent(subCalendar, vEventUid, recurrenceId);
    }

    @Override
    public void moveEvent(T subCalendar, String vEventUid, PersistedSubCalendar destinationSubCalendar) {
        this.getFirstFoundHandler((SubCalendar)subCalendar).moveEvent(subCalendar, vEventUid, destinationSubCalendar);
    }

    @Override
    public void changeEvent(T subCalendar, String vEventUid, PersistedSubCalendar destinationSubCalendar) {
        this.getFirstFoundHandler((SubCalendar)subCalendar).changeEvent(subCalendar, vEventUid, destinationSubCalendar);
    }

    @Override
    public boolean setReminderFor(T subCalendar, ConfluenceUser user, boolean isReminder) {
        return this.getFirstFoundHandler((SubCalendar)subCalendar).setReminderFor(subCalendar, user, isReminder);
    }

    @Override
    public boolean hasReminderFor(T subCalendar, ConfluenceUser user) {
        DelegatableCalendarDataStore<T> handler = this.getFirstFoundHandler((SubCalendar)subCalendar);
        return handler != null ? handler.hasReminderFor(subCalendar, user) : false;
    }

    @Override
    public void disableEventTypes(T subCalendar, List<String> disableEventTypes) {
        this.getFirstFoundHandler((SubCalendar)subCalendar).disableEventTypes(subCalendar, disableEventTypes);
    }

    @Override
    public void deleteDisableEventType(String subCalendarId, String eventType) {
        T subCalendar = this.getSubCalendar(subCalendarId);
        this.getFirstFoundHandler((SubCalendar)subCalendar).deleteDisableEventType(subCalendarId, eventType);
    }

    @Override
    public CustomEventTypeEntity updateCustomEventType(Option<ReminderSettingCallback> reminderSettingCallbacks, T subCalendar, String customEventTypeId, String title, String icon, int periodInMins) {
        return this.getFirstFoundHandler((SubCalendar)subCalendar).updateCustomEventType(reminderSettingCallbacks, subCalendar, customEventTypeId, title, icon, periodInMins);
    }

    @Override
    public ReminderSettingEntity updateReminderForSanboxEventType(Option<ReminderSettingCallback> reminderSettingCallbacks, T subCalendar, String eventTypeId, int periodInMins) {
        return this.getFirstFoundHandler((SubCalendar)subCalendar).updateReminderForSanboxEventType(reminderSettingCallbacks, subCalendar, eventTypeId, periodInMins);
    }

    @Override
    public ReminderSettingEntity getReminderSetting(String subCalendarId, String storeKey, String customEventTypeId) {
        return this.getDefaultCalendarDataStore().getReminderSetting(subCalendarId, storeKey, customEventTypeId);
    }

    @Override
    public Map<String, Set<String>> getVEventUidsForUserBySubCalendar(ConfluenceUser confluenceUser) {
        return this.getDefaultCalendarDataStore().getVEventUidsForUserBySubCalendar(confluenceUser);
    }

    @Override
    public CustomEventTypeEntity getCustomEventType(T subCalendar, String customEventTypeId) {
        return this.getFirstFoundHandler((SubCalendar)subCalendar).getCustomEventType(subCalendar, customEventTypeId);
    }

    @Override
    public List<CustomEventTypeEntity> getCustomEventTypes(String ... customEventTypeId) {
        return this.getDefaultCalendarDataStore().getCustomEventTypes(customEventTypeId);
    }

    @Override
    public void deleteCustomEventType(String subCalendarId, String customEventTypeId) {
        T subCalendar = this.getSubCalendar(subCalendarId);
        this.getFirstFoundHandler((SubCalendar)subCalendar).deleteCustomEventType(subCalendarId, customEventTypeId);
    }

    @Override
    public List<ReminderEvent> getSingleEventUpComingReminder(long startDateSystemUTC, long schedulerTime) {
        return this.getDefaultCalendarDataStore().getSingleEventUpComingReminder(startDateSystemUTC, schedulerTime);
    }

    @Override
    public List<VEvent> getRepeatEventUpComingReminder() {
        return this.getDefaultCalendarDataStore().getRepeatEventUpComingReminder();
    }

    @Override
    public List<ReminderEvent> getJiraEventUpComingReminder(long startDateSystemUTC, long schedulerTime) {
        return this.getDefaultCalendarDataStore().getJiraEventUpComingReminder(startDateSystemUTC, schedulerTime);
    }

    @Override
    public <T1> Option<T1> getReminderListFor(Function<Map<String, Collection<String>>, T1> callback, String ... subCalendarIds) {
        return this.getDefaultCalendarDataStore().getReminderListFor(callback, subCalendarIds);
    }

    @Override
    public Map<Integer, Collection<String>> getInviteesFor(Integer ... eventIds) {
        return this.getDefaultCalendarDataStore().getInviteesFor(eventIds);
    }

    @Override
    public List<String> getInviteesFor(String eventUid) {
        return this.getDefaultCalendarDataStore().getInviteesFor(eventUid);
    }

    @Override
    public Set<String> getChildSubCalendarHasReminders(ConfluenceUser user, String ... childSubCalendars) {
        return this.getDefaultCalendarDataStore().getChildSubCalendarHasReminders(user, childSubCalendars);
    }

    @Override
    public Set<String> getAllSubCalendarIdHasReminders(ConfluenceUser user) {
        return this.getDefaultCalendarDataStore().getAllSubCalendarIdHasReminders(user);
    }

    @Override
    public Option<T> getChildSubCalendarByStoreKey(T parentSubCalendar, String storeKey) {
        return this.getDefaultCalendarDataStore().getChildSubCalendarByStoreKey(parentSubCalendar, storeKey);
    }

    @Override
    public Option<T> getChildSubCalendarByCustomEventTypeId(T parentSubCalendar, String customEventTypeId) {
        return this.getDefaultCalendarDataStore().getChildSubCalendarByCustomEventTypeId(parentSubCalendar, customEventTypeId);
    }

    @Override
    public Set<String> filterExistSubCalendarIds(String ... subCalendarIds) {
        return this.getDefaultCalendarDataStore().filterExistSubCalendarIds(subCalendarIds);
    }

    protected AbstractCalendarDataStore getDefaultCalendarDataStore() {
        return (AbstractCalendarDataStore)Iterables.findFirst(this.calendarDataStores, (com.google.common.base.Predicate)Predicates.instanceOf(AbstractCalendarDataStore.class)).get();
    }

    @Override
    public Message getSubCalendarEventWarning(T subCalendar) throws Exception {
        return this.getFirstFoundHandler((SubCalendar)subCalendar).getSubCalendarEventWarning(subCalendar);
    }

    @Override
    public void updateJiraReminderEvents(T subCalendar, Calendar subCalendarContent) {
    }

    @Override
    public Set<String> getSubCalendarIdsOnSpace(String spaceKey) {
        return this.getDefaultCalendarDataStore().getSubCalendarIdsOnSpace(spaceKey);
    }

    @Override
    public void removeSubCalendarFromSpaceView(T subCalendar, String spaceKey) {
        this.getDefaultCalendarDataStore().removeSubCalendarFromSpaceView(subCalendar, spaceKey);
    }

    @Override
    public void addCalendarsToSpaceView(Set<String> calendarIds, String spaceKey) {
        this.getDefaultCalendarDataStore().addCalendarsToSpaceView(calendarIds, spaceKey);
    }

    @Override
    public void removeSubCalendarRestrictions(String userKey) {
        this.getDefaultCalendarDataStore().removeSubCalendarRestrictions(userKey);
    }

    @Override
    public void deleteInviteeFromAllEvents(String userKey) {
        this.getDefaultCalendarDataStore().deleteInviteeFromAllEvents(userKey);
    }
}

