/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.external.ActiveObjects
 *  com.atlassian.fugue.Effect
 *  com.atlassian.mywork.model.Item
 *  com.atlassian.mywork.model.Notification
 *  com.atlassian.mywork.model.NotificationFilter
 *  com.atlassian.mywork.model.Status
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.user.User
 *  com.google.common.base.Function
 *  com.google.common.base.Functions
 *  com.google.common.base.Joiner
 *  com.google.common.base.Supplier
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Lists
 *  javax.annotation.Nonnull
 *  net.java.ao.DBParam
 *  net.java.ao.EntityStreamCallback
 *  net.java.ao.Query
 *  net.java.ao.RawEntity
 *  org.apache.commons.lang3.StringUtils
 *  org.codehaus.jackson.JsonNode
 *  org.codehaus.jackson.node.ObjectNode
 *  org.springframework.stereotype.Component
 */
package com.atlassian.mywork.host.dao.ao;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.confluence.usercompatibility.UserCompatibilityHelper;
import com.atlassian.fugue.Effect;
import com.atlassian.mywork.host.dao.NotificationDao;
import com.atlassian.mywork.host.dao.ao.AONotification;
import com.atlassian.mywork.host.dao.ao.AbstractAODao;
import com.atlassian.mywork.host.dao.ao.ConditionUtil;
import com.atlassian.mywork.host.dao.ao.DateUtil;
import com.atlassian.mywork.model.Item;
import com.atlassian.mywork.model.Notification;
import com.atlassian.mywork.model.NotificationFilter;
import com.atlassian.mywork.model.Status;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.user.User;
import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Joiner;
import com.google.common.base.Supplier;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import net.java.ao.DBParam;
import net.java.ao.EntityStreamCallback;
import net.java.ao.Query;
import net.java.ao.RawEntity;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;
import org.springframework.stereotype.Component;

@Component
public class AONotificationDao
extends AbstractAODao<AONotification, Long>
implements NotificationDao {
    private static final String QUESTION_MARK_CHARACTER = "?";

    public AONotificationDao(ActiveObjects ao) {
        super(AONotification.class, ao);
    }

    @Override
    public Notification get(long id) {
        return this.asNotification((AONotification)this.getAO(id));
    }

    @Override
    public Notification create(Notification notification) {
        return this.create(notification, new Date());
    }

    protected Notification create(Notification notification, Date date) {
        AONotification aoNotification = (AONotification)this.ao.create(AONotification.class, new DBParam[0]);
        this.updateAO(aoNotification, notification, date);
        aoNotification.setCreated(aoNotification.getUpdated());
        aoNotification.setStatus(this.getItemStatus(notification));
        aoNotification.setRead(notification.isRead());
        aoNotification.save();
        return this.asNotification(aoNotification);
    }

    private Status getItemStatus(Notification notification) {
        AONotification[] aoNotificationList;
        String globalId = notification.getGlobalId();
        if (!StringUtils.isEmpty((CharSequence)globalId) && (aoNotificationList = (AONotification[])this.ao.find(AONotification.class, Query.select().where("USER = ? AND GLOBAL_ID = ?", new Object[]{UserCompatibilityHelper.getStringKeyForUsername(notification.getUser()), globalId}).limit(1))).length > 0) {
            return aoNotificationList[0].getStatus();
        }
        return notification.getStatus();
    }

    @Override
    public Notification update(Notification notification) {
        return this.update(notification, new Date());
    }

    protected Notification update(Notification notification, Date date) {
        AONotification aoNotification = (AONotification)this.getAO(notification.getId());
        this.updateAO(aoNotification, notification, date);
        aoNotification.save();
        return this.asNotification(aoNotification);
    }

    @Override
    public void updateMetadata(String username, String globalId, ObjectNode condition, ObjectNode newMetadata) {
        AONotification[] aoNotificationList;
        int BATCH_SIZE = 100;
        int offset = 0;
        Date updated = new Date();
        do {
            for (AONotification aoNotification : aoNotificationList = (AONotification[])this.ao.find(AONotification.class, Query.select().where("USER = ? AND GLOBAL_ID = ?", new Object[]{UserCompatibilityHelper.getStringKeyForUsername(username), globalId}).order("ID").offset(offset).limit(100))) {
                ObjectNode metadata = AONotificationDao.toObjectNode(aoNotification.getMetadata());
                if (!AONotificationDao.isMetadataMatch(metadata, condition)) continue;
                metadata.putAll(newMetadata);
                aoNotification.setMetadata(metadata.toString());
                aoNotification.setUpdated(updated);
                aoNotification.save();
            }
            offset += 100;
        } while (aoNotificationList.length == 100);
    }

    protected static boolean isMetadataMatch(ObjectNode metadata, ObjectNode condition) {
        Iterator i = condition.getFields();
        while (i.hasNext()) {
            Map.Entry e = (Map.Entry)i.next();
            JsonNode node = metadata.get((String)e.getKey());
            if (node != null && node.getValueAsText().equals(((JsonNode)e.getValue()).getValueAsText())) continue;
            return false;
        }
        return true;
    }

    @Override
    public Notification delete(long id) {
        AONotification aoNotification = (AONotification)this.getAO(id);
        this.ao.delete(new RawEntity[]{aoNotification});
        return this.asNotification(aoNotification);
    }

    @Override
    public Iterable<Notification> deleteByGlobalId(final String globalId) {
        final ArrayList<Notification> deletedNotifications = new ArrayList<Notification>();
        this.delete(new Supplier<Query>(){

            public Query get() {
                return Query.select().where("GLOBAL_ID = ?", new Object[]{globalId});
            }
        }, (Effect<T[]>)new Effect<AONotification[]>(){

            public void apply(AONotification[] aoNotifications) {
                for (AONotification aoNotification : aoNotifications) {
                    deletedNotifications.add(AONotificationDao.this.asNotification(aoNotification));
                }
            }
        });
        return deletedNotifications;
    }

    @Override
    public int deleteAll(final @Nonnull com.atlassian.sal.usercompatibility.UserKey userKey) {
        return this.delete(new Supplier<Query>(){

            public Query get() {
                return Query.select().where("USER = ?", new Object[]{userKey.getStringValue()});
            }
        });
    }

    @Override
    public void setStatusByGlobalId(String username, String globalId, Status status) {
        if (StringUtils.isEmpty((CharSequence)globalId)) {
            return;
        }
        for (AONotification notification : (AONotification[])this.ao.find(AONotification.class, Query.select().where("USER = ? AND GLOBAL_ID = ? AND STATUS <> ?", new Object[]{UserCompatibilityHelper.getStringKeyForUsername(username), globalId, status}))) {
            notification.setStatus(status);
            notification.save();
        }
    }

    @Override
    public Iterable<Notification> findAll(String username) {
        return this.queryNotifications(Query.select().where("USER = ?", new Object[]{UserCompatibilityHelper.getStringKeyForUsername(username)}));
    }

    @Override
    public Iterable<Notification> findAll(String userKey, String appId, List<String> actions, Date after) {
        String placeholderCommaList = Joiner.on((String)", ").join((Iterable)Lists.transform(actions, (Function)Functions.constant((Object)QUESTION_MARK_CHARACTER)));
        String query = "USER = ? AND GLOBAL_ID LIKE ? AND CREATED > ? AND ACTION IN (" + placeholderCommaList + ")";
        ArrayList params = Lists.newArrayList((Object[])new Serializable[]{userKey, appId + "%", after});
        params.addAll(actions);
        return this.queryNotifications(Query.select().where(query, params.toArray()));
    }

    @Override
    public List<Notification> findAll(String userKey, boolean onlyGetDirectedAction, int start, int limit) {
        Object query = "USER = ?";
        if (onlyGetDirectedAction) {
            query = (String)query + " AND (ACTION IN ('share', 'mentions.user', 'task.assign') OR (ACTION = 'comment' AND METADATA LIKE '%\"replyYourComment\":true%'))";
        }
        Query aoQuery = Query.select().where((String)query, new Object[]{userKey}).order("CREATED DESC").offset(start).limit(limit);
        return this.asNotifications((AONotification[])this.ao.find(AONotification.class, aoQuery));
    }

    @Override
    public List<Notification> findAll(NotificationFilter filter, int start, int limit) {
        AONotification[] aoNotifications = (AONotification[])this.ao.find(AONotification.class, ConditionUtil.buildQuery(filter).order("CREATED DESC").offset(start).limit(limit));
        return this.asNotifications(aoNotifications);
    }

    @Override
    public Iterable<Notification> findAllUnread(String username) {
        return this.queryNotifications(Query.select().where("USER = ? AND READ <> ?", new Object[]{UserCompatibilityHelper.getStringKeyForUsername(username), true}));
    }

    @Override
    public Iterable<Notification> findAllUnread(String username, String applicationLinkId, String application) {
        return this.queryNotifications(Query.select().where("USER = ? AND APPLICATION_LINK_ID = ? AND APPLICATION = ? AND READ <> ?", new Object[]{UserCompatibilityHelper.getStringKeyForUsername(username), applicationLinkId, application, true}));
    }

    @Override
    public int countAllUnreadAfterOnlyIdsAction(String username, long notificationId, final int upTo) {
        final HashSet keySet = new HashSet();
        Query query = Query.select((String)"GROUPING_ID, GLOBAL_ID, ID, ACTION").where("USER = ? AND ID > ? AND READ <> ?", new Object[]{UserCompatibilityHelper.getStringKeyForUsername(username), notificationId, true});
        final StringBuilder keyBuilder = new StringBuilder();
        try {
            this.ao.stream(AONotification.class, query, (EntityStreamCallback)new EntityStreamCallback<AONotification, Long>(){

                public void onRowRead(AONotification o) {
                    keyBuilder.setLength(0);
                    if (o.getGroupingId() != null) {
                        keyBuilder.append(o.getGroupingId());
                    } else {
                        keyBuilder.append("--, ");
                        if (o.getGlobalId() != null) {
                            keyBuilder.append(o.getGlobalId());
                        } else {
                            keyBuilder.append("--, ").append(o.getId());
                        }
                    }
                    keyBuilder.append(", ").append(o.getAction());
                    keySet.add(keyBuilder.toString());
                    if (keySet.size() >= upTo) {
                        throw new StopStreamingException();
                    }
                }
            });
        }
        catch (StopStreamingException stopStreamingException) {
            // empty catch block
        }
        return keySet.size();
    }

    @Override
    public Iterable<Notification> findAllAfter(String username, long lastReadNotificationId, long before, int limit) {
        return Iterables.limit(this.queryNotifications(Query.select().where("USER = ? AND ID > ? AND ID < ?", new Object[]{UserCompatibilityHelper.getStringKeyForUsername(username), lastReadNotificationId, before > 0L ? before : Long.MAX_VALUE})), (int)limit);
    }

    @Override
    public Iterable<Notification> findByGlobalId(String username, String globalId) {
        if (StringUtils.isEmpty((CharSequence)globalId)) {
            return Lists.newArrayList();
        }
        return this.queryNotifications(Query.select().where("USER = ? AND GLOBAL_ID = ?", new Object[]{UserCompatibilityHelper.getStringKeyForUsername(username), globalId}));
    }

    @Override
    public int countByGlobalId(String username, String globalId) {
        if (StringUtils.isEmpty((CharSequence)globalId)) {
            return 0;
        }
        return this.ao.count(AONotification.class, Query.select().where("USER = ? AND GLOBAL_ID = ?", new Object[]{UserCompatibilityHelper.getStringKeyForUsername(username), globalId}));
    }

    @Override
    public int deleteOldNotifications(int days, boolean read) {
        return this.ao.deleteWithSQL(AONotification.class, "UPDATED < ? AND READ = ? AND PINNED = ?", new Object[]{DateUtil.getNoDaysAgo(days), read, false});
    }

    @Override
    public void setRead(NotificationFilter filter) {
        AONotification[] aoNotifications;
        for (AONotification aoNotification : aoNotifications = (AONotification[])this.ao.find(AONotification.class, ConditionUtil.buildQuery(filter))) {
            aoNotification.setRead(true);
            aoNotification.save();
        }
    }

    @Override
    public void delete(NotificationFilter filter) {
        this.ao.delete(this.ao.find(AONotification.class, ConditionUtil.buildQuery(filter)));
    }

    private Iterable<Notification> queryNotifications(Query query) {
        ArrayList<Notification> notifications = new ArrayList<Notification>();
        for (AONotification aoNotification : (AONotification[])this.ao.find(AONotification.class, query)) {
            notifications.add(this.asNotification(aoNotification));
        }
        Collections.sort(notifications, new Comparator<Notification>(){

            @Override
            public int compare(Notification o1, Notification o2) {
                int c = Boolean.valueOf(o2.isPinned()).compareTo(o1.isPinned());
                if (c != 0) {
                    return c;
                }
                c = Long.valueOf(o2.getCreated()).compareTo(o1.getCreated());
                if (c != 0) {
                    return c;
                }
                return Long.valueOf(o2.getId()).compareTo(o1.getId());
            }
        });
        return notifications;
    }

    private List<Notification> asNotifications(AONotification[] aoNotifications) {
        ArrayList<Notification> notifications = new ArrayList<Notification>();
        for (AONotification aoNotification : aoNotifications) {
            notifications.add(this.asNotification(aoNotification));
        }
        return notifications;
    }

    private Notification asNotification(AONotification ao) {
        if (ao == null) {
            return null;
        }
        return new Notification(ao.getId(), ao.getApplicationLinkId(), this.getUsername(ao.getUserKey()), ao.getIconUrl(), ao.getTitle(), ao.getDescription(), ao.getUrl(), ao.getApplication(), ao.getEntity(), ao.getAction(), ao.getActionIconUrl(), ao.getCreated() != null ? ao.getCreated().getTime() : -1L, ao.getUpdated() != null ? ao.getUpdated().getTime() : -1L, ao.getStatus(), ao.isRead(), ao.isPinned(), ao.getGroupingId(), ao.getGlobalId(), AONotificationDao.toObjectNode(ao.getMetadata()), new Item(ao.getItemIconUrl(), ao.getItemTitle(), ao.getItemUrl()));
    }

    private void updateAO(AONotification aoNotification, Notification notification, Date date) {
        aoNotification.setApplicationLinkId(notification.getApplicationLinkId());
        aoNotification.setUserKey(UserCompatibilityHelper.getStringKeyForUsername(notification.getUser()));
        aoNotification.setIconUrl(notification.getIconUrl());
        aoNotification.setTitle(notification.getTitle());
        aoNotification.setApplication(notification.getApplication());
        aoNotification.setEntity(notification.getEntity());
        aoNotification.setAction(notification.getAction());
        aoNotification.setActionIconUrl(notification.getActionIconUrl());
        aoNotification.setDescription(notification.getDescription());
        aoNotification.setStatus(notification.getStatus());
        aoNotification.setPinned(notification.isPinned());
        aoNotification.setGroupingId(notification.getGroupingId());
        aoNotification.setGlobalId(notification.getGlobalId());
        aoNotification.setMetadata(notification.getMetadata().toString());
        aoNotification.setItemIconUrl(notification.getItem().getIconUrl());
        aoNotification.setItemTitle(notification.getItem().getTitle());
        aoNotification.setUrl(notification.getUrl());
        aoNotification.setItemUrl(notification.getItem().getUrl());
        aoNotification.setUpdated(date);
    }

    @Override
    public void markAllRead(String username, long before) {
        for (AONotification aoNotification : (AONotification[])this.ao.find(AONotification.class, Query.select().where("USER = ? AND READ <> ? AND ID <= ?", new Object[]{UserCompatibilityHelper.getStringKeyForUsername(username), true, before}))) {
            aoNotification.setRead(true);
            aoNotification.save();
        }
    }

    @Override
    public void setRead(String username, List<Long> notificationIds) {
        String userKey = UserCompatibilityHelper.getStringKeyForUsername(username);
        for (Long id : notificationIds) {
            AONotification notification = (AONotification)this.getAO(id);
            if (notification == null || !userKey.equals(notification.getUserKey())) continue;
            notification.setRead(true);
            notification.save();
        }
    }

    @Override
    public List<Long> setRead(UserKey userKey, String globalId, String action, ObjectNode condition) {
        Query query = Query.select().where("USER = ? AND GLOBAL_ID = ? AND READ <> ?", new Object[]{userKey.getStringValue(), globalId, true});
        if (StringUtils.isNotBlank((CharSequence)action)) {
            query = query.where("ACTION = ?", new Object[]{action});
        }
        AONotification[] aoNotifications = (AONotification[])this.ao.find(AONotification.class, query);
        ArrayList readIds = Lists.newArrayList();
        for (AONotification aoNotification : aoNotifications) {
            ObjectNode metadata = AONotificationDao.toObjectNode(aoNotification.getMetadata());
            if (!AONotificationDao.isMetadataMatch(metadata, condition)) continue;
            aoNotification.setRead(true);
            aoNotification.save();
            readIds.add(aoNotification.getId());
        }
        return readIds;
    }

    private String getUsername(String userKey) {
        User user = UserCompatibilityHelper.getUserForKey(userKey);
        return user != null ? user.getName() : null;
    }

    private static class StopStreamingException
    extends RuntimeException {
        private StopStreamingException() {
        }

        @Override
        public synchronized Throwable fillInStackTrace() {
            return this;
        }
    }
}

