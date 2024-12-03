/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.mail.notification.Notification
 *  com.atlassian.confluence.mail.notification.NotificationManager
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.search.service.ContentTypeEnum
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.user.User
 *  com.atlassian.util.profiling.UtilTimerStack
 *  net.java.ao.EntityStreamCallback
 *  net.java.ao.Query
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.extra.calendar3;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.extra.calendar3.ActiveObjectsServiceWrapper;
import com.atlassian.confluence.extra.calendar3.EmbeddedSubCalendarsTracker;
import com.atlassian.confluence.extra.calendar3.SubCalendarSubscriptionStatisticsAccessor;
import com.atlassian.confluence.extra.calendar3.calendarstore.CalendarDataStore;
import com.atlassian.confluence.extra.calendar3.calendarstore.SubscribingCalendarDataStore;
import com.atlassian.confluence.extra.calendar3.model.PersistedSubCalendar;
import com.atlassian.confluence.extra.calendar3.model.SubscribingSubCalendar;
import com.atlassian.confluence.extra.calendar3.model.persistence.SubCalendarEntity;
import com.atlassian.confluence.mail.notification.Notification;
import com.atlassian.confluence.mail.notification.NotificationManager;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.search.service.ContentTypeEnum;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.user.User;
import com.atlassian.util.profiling.UtilTimerStack;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;
import net.java.ao.EntityStreamCallback;
import net.java.ao.Query;
import org.apache.commons.lang3.StringUtils;

public class DefaultSubCalendarSubscriptionStatisticsAccessor
implements SubCalendarSubscriptionStatisticsAccessor {
    private final SubscribingCalendarDataStore<SubscribingSubCalendar> subscribingCalendarDataStore;
    private final ActiveObjectsServiceWrapper activeObjectsServiceWrapper;
    private final EmbeddedSubCalendarsTracker embeddedSubCalendarsTracker;
    private final NotificationManager notificationManager;
    private final PermissionManager permissionManager;
    private final SpaceManager spaceManager;
    private final UserAccessor userAccessor;
    private final CalendarDataStore<PersistedSubCalendar> calendarDataStore;

    public DefaultSubCalendarSubscriptionStatisticsAccessor(ActiveObjectsServiceWrapper activeObjectsServiceWrapper, SubscribingCalendarDataStore<SubscribingSubCalendar> subscribingCalendarDataStore, EmbeddedSubCalendarsTracker embeddedSubCalendarsTracker, NotificationManager notificationManager, UserAccessor userAccessor, CalendarDataStore<PersistedSubCalendar> calendarDataStore, SpaceManager spaceManager, PermissionManager permissionManager) {
        this.activeObjectsServiceWrapper = activeObjectsServiceWrapper;
        this.subscribingCalendarDataStore = subscribingCalendarDataStore;
        this.embeddedSubCalendarsTracker = embeddedSubCalendarsTracker;
        this.notificationManager = notificationManager;
        this.userAccessor = userAccessor;
        this.calendarDataStore = calendarDataStore;
        this.spaceManager = spaceManager;
        this.permissionManager = permissionManager;
    }

    @Override
    public Set<String> getSubscribingSubCalendarIds(PersistedSubCalendar persistedSubCalendar) {
        return this.getSubscribingSubCalendarIds(persistedSubCalendar.getId());
    }

    private Set<String> getSubscribingSubCalendarIds(String subCalendarId) {
        HashSet<String> subscribingSubCalendarIds = new HashSet<String>();
        this.collectSubscribingSubCalendarsData(subCalendarId, (EntityStreamCallback<SubCalendarEntity, String>)((EntityStreamCallback)subCalendarEntity -> subscribingSubCalendarIds.add(subCalendarEntity.getID())));
        return subscribingSubCalendarIds;
    }

    private void collectSubscribingSubCalendarsData(String sourceSubCalendarId, EntityStreamCallback<SubCalendarEntity, String> entityEntityStreamCallback) {
        this.activeObjectsServiceWrapper.getActiveObjects().stream(SubCalendarEntity.class, Query.select((String)"ID, CREATOR").where("SUBSCRIPTION_ID = ?", new Object[]{sourceSubCalendarId}), entityEntityStreamCallback);
    }

    @Override
    public int getSubscriberCount(PersistedSubCalendar subCalendar) {
        return this.getSubscriberCount(subCalendar.getId());
    }

    private int getSubscriberCount(String sourceSubCalendarId) {
        return this.getSubscribingSubCalendarIds(sourceSubCalendarId).size() + this.embeddedSubCalendarsTracker.getEmbedCount(sourceSubCalendarId);
    }

    @Override
    public Set<ConfluenceUser> getUsersSubscribingToSubCalendar(PersistedSubCalendar subCalendar, boolean includeSubscriptionsFromContent) {
        Objects.requireNonNull(subCalendar);
        String subCalendarId = subCalendar.getId();
        Set<ConfluenceUser> subscribers = this.getUsersSubscribingToSubCalendar(subCalendarId);
        if (includeSubscriptionsFromContent) {
            Space space;
            HashMap<Space, Set> contentTypesToQueryForSpaceWatches = new HashMap<Space, Set>();
            String spaceKey = subCalendar.getSpaceKey();
            if (StringUtils.isNotBlank((CharSequence)spaceKey) && (space = this.spaceManager.getSpace(subCalendar.getSpaceKey())) != null) {
                contentTypesToQueryForSpaceWatches.put(space, this.getNewContentTypesToQuery());
            }
            Collection contentsEmbeddedCalendars = this.embeddedSubCalendarsTracker.getContentEmbeddingSubCalendar(subCalendarId).stream().filter(AbstractPage.class::isInstance).map(watchableContent -> (AbstractPage)watchableContent).collect(Collectors.toList());
            for (AbstractPage abstractPage : contentsEmbeddedCalendars) {
                Set contentTypesToQuery;
                List notificationsByPage = this.notificationManager.getNotificationsByContent((ContentEntityObject)abstractPage);
                for (Notification notification : notificationsByPage) {
                    if (!this.permissionManager.hasPermission((User)notification.getReceiver(), Permission.VIEW, (Object)abstractPage)) continue;
                    subscribers.add(notification.getReceiver());
                }
                Space space2 = abstractPage.getSpace();
                if (contentTypesToQueryForSpaceWatches.containsKey(space2)) {
                    contentTypesToQuery = (Set)contentTypesToQueryForSpaceWatches.get(space2);
                } else {
                    contentTypesToQuery = this.getNewContentTypesToQuery();
                    contentTypesToQueryForSpaceWatches.put(space2, contentTypesToQuery);
                }
                contentTypesToQuery.add(abstractPage.getTypeEnum());
            }
            for (Map.Entry entry : contentTypesToQueryForSpaceWatches.entrySet()) {
                for (ContentTypeEnum typeToQuery : (Set)entry.getValue()) {
                    subscribers.addAll(this.notificationManager.getNotificationsBySpaceAndType((Space)entry.getKey(), typeToQuery).stream().map(Notification::getReceiver).collect(Collectors.toList()));
                }
            }
        }
        return subscribers.stream().filter(Objects::nonNull).collect(Collectors.toSet());
    }

    private Set<ContentTypeEnum> getNewContentTypesToQuery() {
        HashSet<ContentTypeEnum> contentTypesToQuery = new HashSet<ContentTypeEnum>(ContentTypeEnum.values().length);
        contentTypesToQuery.add(null);
        return contentTypesToQuery;
    }

    private Set<ConfluenceUser> getUsersSubscribingToSubCalendar(String sourceSubCalendarId) {
        HashSet userKeys = new HashSet();
        this.collectSubscribingSubCalendarsData(sourceSubCalendarId, (EntityStreamCallback<SubCalendarEntity, String>)((EntityStreamCallback)subCalendarEntity -> userKeys.add(subCalendarEntity.getCreator())));
        return userKeys.stream().map(userKey -> this.userAccessor.getUserByKey(new UserKey(userKey))).filter(Objects::nonNull).collect(Collectors.toSet());
    }

    @Override
    public Collection<ContentEntityObject> getContentEmbeddingSubCalendar(PersistedSubCalendar persistedSubCalendar) {
        return this.embeddedSubCalendarsTracker.getContentEmbeddingSubCalendar(persistedSubCalendar.getId());
    }

    @Override
    public boolean hasPopularSubscriptions(ConfluenceUser user) {
        return !this.getPopularSubscriptions(user, 0, 1).isEmpty();
    }

    @Override
    public List<SubCalendarSubscriptionStatisticsAccessor.PopularSubCalendarSubscription> getPopularSubscriptions(ConfluenceUser user, int starIndex, int pageSize) {
        UtilTimerStack.push((String)"getPopularSubscriptions");
        SubCalendarEntity[] subCalendarInternalSubscriptionEntities = (SubCalendarEntity[])this.activeObjectsServiceWrapper.getActiveObjects().find(SubCalendarEntity.class, Query.select().where("SUBSCRIPTION_ID IS NOT NULL AND PARENT_ID IS NULL", new Object[0]));
        UtilTimerStack.push((String)"getPopularSubscriptions => calculate for popular");
        Map<String, Integer> embeddedSubCalendarsMap = this.getEmbeddedSubCalendarMap();
        TreeMap<String, Integer> subCalendarIdsUserCanView = new TreeMap<String, Integer>();
        for (SubCalendarEntity popularSubCalendarEntity2 : subCalendarInternalSubscriptionEntities) {
            String subscriptionId = popularSubCalendarEntity2.getSubscription().getID();
            if (subCalendarIdsUserCanView.containsKey(subscriptionId)) {
                int count = (Integer)subCalendarIdsUserCanView.get(subscriptionId);
                if (count < 0) continue;
                subCalendarIdsUserCanView.put(subscriptionId, ++count);
                continue;
            }
            UtilTimerStack.push((String)"getPopularSubscriptions => permission check");
            boolean hasPermission = this.calendarDataStore.hasViewEventPrivilege(subscriptionId, user);
            UtilTimerStack.pop((String)"getPopularSubscriptions => permission check");
            if (hasPermission) {
                UtilTimerStack.push((String)"getPopularSubscriptions => calculate embedded calendar");
                Integer total = embeddedSubCalendarsMap.get(subscriptionId);
                if (total == null) {
                    total = 0;
                }
                UtilTimerStack.pop((String)"getPopularSubscriptions => calculate embedded calendar");
                subCalendarIdsUserCanView.put(subscriptionId, 1 + total);
                continue;
            }
            subCalendarIdsUserCanView.put(subscriptionId, -1);
        }
        UtilTimerStack.pop((String)"getPopularSubscriptions => calculate for popular");
        List popularSubCalendarEntitiesUserCanView = subCalendarIdsUserCanView.entrySet().stream().filter(entry -> (Integer)entry.getValue() >= 0).sorted(Comparator.comparing(Map.Entry::getValue, Comparator.reverseOrder())).collect(Collectors.toList());
        List<SubCalendarSubscriptionStatisticsAccessor.PopularSubCalendarSubscription> popularSubCalendarSubscriptions = popularSubCalendarEntitiesUserCanView.stream().skip(starIndex * pageSize).limit(pageSize).map(popularSubCalendarEntity -> new SubCalendarSubscriptionStatisticsAccessor.PopularSubCalendarSubscription(this.subscribingCalendarDataStore.getSourceSubCalendar((String)popularSubCalendarEntity.getKey()), (Integer)popularSubCalendarEntity.getValue())).collect(Collectors.toList());
        UtilTimerStack.pop((String)"getPopularSubscriptions");
        return popularSubCalendarSubscriptions;
    }

    private Map<String, Integer> getEmbeddedSubCalendarMap() {
        HashMap<String, Integer> embeddedSubCalendarMap = new HashMap<String, Integer>();
        UtilTimerStack.push((String)"getPopularSubscriptions => getEmbeddedSubCalendarMap");
        Collection<String> embededdSubCalendars = this.embeddedSubCalendarsTracker.getEmbedSubCalendars();
        UtilTimerStack.pop((String)"getPopularSubscriptions => getEmbeddedSubCalendarMap");
        for (String subCalendarId : embededdSubCalendars) {
            if (embeddedSubCalendarMap.containsKey(subCalendarId)) {
                int count = (Integer)embeddedSubCalendarMap.get(subCalendarId);
                embeddedSubCalendarMap.put(subCalendarId, ++count);
                continue;
            }
            embeddedSubCalendarMap.put(subCalendarId, 1);
        }
        return embeddedSubCalendarMap;
    }
}

