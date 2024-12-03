/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.bean.EntityObject
 *  com.atlassian.user.User
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.Iterables
 *  org.apache.commons.collections4.CollectionUtils
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.hibernate.query.Query
 */
package com.atlassian.confluence.mail.notification.persistence.dao.hibernate;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.persistence.hibernate.ConfluenceHibernateObjectDao;
import com.atlassian.confluence.core.persistence.hibernate.HibernateObjectDao;
import com.atlassian.confluence.internal.notification.persistence.NotificationDaoInternal;
import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.mail.notification.Notification;
import com.atlassian.confluence.search.service.ContentTypeEnum;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.persistence.dao.compatibility.FindUserHelper;
import com.atlassian.core.bean.EntityObject;
import com.atlassian.user.User;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.hibernate.query.Query;

public class HibernateNotificationDao
extends ConfluenceHibernateObjectDao<Notification>
implements NotificationDaoInternal {
    @Override
    public List<Notification> findNotificationsByUser(User user) {
        ConfluenceUser confluenceUser = HibernateNotificationDao.getConfluenceUser(user);
        return this.findNamedQueryStringParam("confluence.notifications_findNotificationsByUser", "user", confluenceUser);
    }

    @Override
    public List<Notification> findAllNotificationsByUser(User user) {
        ConfluenceUser confluenceUser = HibernateNotificationDao.getConfluenceUser(user);
        return this.findNamedQueryStringParam("confluence.notifications_findAllNotificationsByUser", "user", confluenceUser);
    }

    @Override
    public List<Notification> findAllNotificationsBySpace(Space space) {
        return this.findNamedQueryStringParam("confluence.notifications_findNotificationsBySpace", "spaceId", space.getId());
    }

    @Override
    public Iterable<Long> findPageAndSpaceNotificationIdsFromSpace(Space space) {
        List spaceNotificationIds = this.findNamedQueryStringParam("confluence.notifications_findSpaceNotificationIdsBySpace", "spaceId", space.getId(), HibernateObjectDao.Cacheability.NOT_CACHEABLE);
        List pageNotificationIds = this.findNamedQueryStringParam("confluence.notifications_findPageNotificationIdsBySpace", "spaceId", space.getId(), HibernateObjectDao.Cacheability.NOT_CACHEABLE);
        return Iterables.concat((Iterable)spaceNotificationIds, (Iterable)pageNotificationIds);
    }

    @Override
    public List<Notification> findNotificationsBySpaceAndType(Space space, ContentTypeEnum type) {
        if (type == null) {
            return this.findNamedQueryStringParam("confluence.notifications_findNotificationsBySpaceWithNoType", "spaceId", space.getId());
        }
        return this.findNamedQueryStringParams("confluence.notifications_findNotificationsBySpaceAndType", "spaceId", space.getId(), "type", (Object)type.getRepresentation());
    }

    @Override
    public List<Notification> findNotificationsBySpacesAndType(List<Space> spaces, ContentTypeEnum type) {
        if (CollectionUtils.isEmpty(spaces)) {
            return Collections.emptyList();
        }
        Set spaceIds = spaces.stream().map(EntityObject::getId).collect(Collectors.toSet());
        if (type == null) {
            return this.findNamedQueryStringParam("confluence.notifications_findNotificationsBySpacesWithNoType", "spaceIds", spaceIds);
        }
        return this.findNamedQueryStringParams("confluence.notifications_findNotificationsBySpacesAndType", "spaceIds", spaceIds, "type", (Object)type.getRepresentation());
    }

    @Override
    public Notification findNotificationByUserAndSpace(User user, Space space) {
        return this.findNotificationByUserAndSpace(user, space.getKey());
    }

    @Override
    public Notification findNotificationByUserAndContent(User user, ContentEntityObject content) {
        if (user == null || content == null) {
            return null;
        }
        ConfluenceUser confluenceUser = HibernateNotificationDao.getConfluenceUser(user);
        return (Notification)this.getHibernateTemplate().execute(session -> {
            Query query = session.createQuery("from Notification n where n.receiver = :user and n.content.id = :contentId");
            query.setParameter("user", (Object)confluenceUser);
            query.setParameter("contentId", (Object)content.getId());
            List result = query.list();
            return result.size() == 0 ? null : result.get(0);
        });
    }

    @Override
    public List<Notification> findNotificationsByContent(ContentEntityObject content) {
        if (content == null) {
            return Collections.emptyList();
        }
        return this.findNamedQueryStringParam("confluence.notifications_findNotificationsByContent", "contentId", content.getId());
    }

    @Override
    public List<Notification> findNotificationsByContents(List<ContentEntityObject> contents) {
        if (CollectionUtils.isEmpty(contents)) {
            return Collections.emptyList();
        }
        Set contentIds = contents.stream().map(EntityObject::getId).collect(Collectors.toSet());
        return this.findNamedQueryStringParam("confluence.notifications_findNotificationsByContents", "contentIds", contentIds);
    }

    @Override
    public Notification findNotificationByUserAndLabel(User user, Label label) {
        if (user == null || label == null) {
            return null;
        }
        ConfluenceUser confluenceUser = HibernateNotificationDao.getConfluenceUser(user);
        return (Notification)this.getHibernateTemplate().execute(session -> {
            Query query = session.createQuery("from Notification n where n.receiver = :user and n.label.id = :labelId");
            query.setParameter("user", (Object)confluenceUser);
            query.setParameter("labelId", (Object)label.getId());
            List result = query.list();
            return result.size() == 0 ? null : result.get(0);
        });
    }

    @Override
    public List<Notification> findNotificationsByLabel(Label label) {
        if (label == null) {
            return Collections.emptyList();
        }
        return (List)this.getHibernateTemplate().execute(session -> {
            Query query = session.createQuery("from Notification n where n.label.id = :labelId");
            query.setParameter("labelId", (Object)label.getId());
            return query.list();
        });
    }

    @Override
    public Notification findNotificationByUserAndSpace(User user, String spaceKey) {
        ConfluenceUser confluenceUser = HibernateNotificationDao.getConfluenceUser(user);
        List results = this.findNamedQueryStringParams("confluence.notifications_findNotificationsByUserAndSpace", "user", confluenceUser, "spaceKey", (Object)spaceKey, HibernateObjectDao.Cacheability.CACHEABLE);
        return (Notification)this.findSingleObject(results);
    }

    @Override
    public Notification findNotificationByUserAndSpaceAndType(User user, Space space, ContentTypeEnum type) {
        ConfluenceUser confluenceUser = HibernateNotificationDao.getConfluenceUser(user);
        String spaceKey = space.getKey();
        List results = type == null ? this.findNamedQueryStringParams("confluence.notifications_findNotificationsByUserAndSpaceWithNoType", "user", confluenceUser, "spaceKey", (Object)spaceKey, HibernateObjectDao.Cacheability.CACHEABLE) : this.findNamedQueryStringParams("confluence.notifications_findNotificationsByUserAndSpaceAndType", "user", confluenceUser, "spaceKey", (Object)spaceKey, "type", (Object)type.getRepresentation(), HibernateObjectDao.Cacheability.CACHEABLE);
        return (Notification)this.findSingleObject(results);
    }

    @Override
    public Notification findDailyReportNotification(String username) {
        ConfluenceUser user = (ConfluenceUser)Preconditions.checkNotNull((Object)this.confluenceUserDao.findByUsername(username), (Object)("No such user [" + username + "]"));
        List results = this.findNamedQueryStringParam("confluence.notifications_findDailyReportNotificationByUser", "user", user, HibernateObjectDao.Cacheability.CACHEABLE);
        return (Notification)this.findSingleObject(results);
    }

    @Override
    public List<Notification> findAllDailyReportNotifications() {
        return this.findNamedQuery("confluence.notifications_findDailyReportNotifications", HibernateObjectDao.Cacheability.CACHEABLE);
    }

    @Override
    public Notification findGlobalBlogWatchForUser(User user) {
        ConfluenceUser confluenceUser = HibernateNotificationDao.getConfluenceUser(user);
        List results = this.findNamedQueryStringParams("confluence.notifications_findGlobalNonDigestNotificationsByUserAndType", "user", confluenceUser, "type", (Object)ContentTypeEnum.BLOG.getRepresentation(), HibernateObjectDao.Cacheability.CACHEABLE);
        return (Notification)this.findSingleObject(results);
    }

    @Override
    public Notification findNetworkNotificationByUser(User user) {
        ConfluenceUser confluenceUser = HibernateNotificationDao.getConfluenceUser(user);
        List results = this.findNamedQueryStringParam("confluence.notifications_findNetworkNotificationByUser", "user", confluenceUser, HibernateObjectDao.Cacheability.CACHEABLE);
        return (Notification)this.findSingleObject(results);
    }

    @Override
    public List<Notification> findSiteBlogNotifications() {
        return this.findNamedQueryStringParam("confluence.notifications_findGlobalNonDigestNotificationsByType", "type", ContentTypeEnum.BLOG.getRepresentation(), HibernateObjectDao.Cacheability.CACHEABLE);
    }

    @Override
    public List<Notification> findNotificationsByFollowing(User user) {
        ConfluenceUser confluenceUser = HibernateNotificationDao.getConfluenceUser(user);
        return this.findNamedQueryStringParam("confluence.notifications_findFollowNotificationsByFollowing", "user", confluenceUser, HibernateObjectDao.Cacheability.CACHEABLE);
    }

    @Override
    public Notification findNotificationById(long id) {
        List results = this.findNamedQueryStringParam("confluence.notifications_findById", "id", id);
        return (Notification)this.uniqueResult(results);
    }

    @Override
    public boolean isWatchingContent(@NonNull ConfluenceUser user, @NonNull ContentEntityObject content) {
        return this.findNotificationByUserAndContent(user, content) != null;
    }

    private static ConfluenceUser getConfluenceUser(User user) {
        return (ConfluenceUser)Preconditions.checkNotNull((Object)FindUserHelper.getUser(user), (Object)("No ConfluenceUser found for " + user));
    }

    @Override
    public Class<Notification> getPersistentClass() {
        return Notification.class;
    }
}

