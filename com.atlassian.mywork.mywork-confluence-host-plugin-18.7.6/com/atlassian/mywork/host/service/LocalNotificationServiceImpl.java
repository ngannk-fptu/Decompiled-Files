/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.Cache
 *  com.atlassian.cache.CacheFactory
 *  com.atlassian.cache.CacheManager
 *  com.atlassian.cache.CacheSettingsBuilder
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.mywork.event.notification.NotificationCreatedEvent
 *  com.atlassian.mywork.event.notification.NotificationStatusChangedEvent
 *  com.atlassian.mywork.event.notification.NotificationUpdatedEvent
 *  com.atlassian.mywork.model.Notification
 *  com.atlassian.mywork.model.NotificationBuilder
 *  com.atlassian.mywork.model.NotificationFilter
 *  com.atlassian.mywork.model.Status
 *  com.atlassian.mywork.model.Task
 *  com.atlassian.mywork.model.TaskBuilder
 *  com.atlassian.mywork.service.LocalNotificationService
 *  com.atlassian.mywork.service.PermissionException
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.atlassian.sal.api.user.UserKey
 *  com.google.common.base.MoreObjects
 *  com.google.common.util.concurrent.Futures
 *  org.apache.commons.lang3.StringUtils
 *  org.codehaus.jackson.node.ObjectNode
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.stereotype.Component
 */
package com.atlassian.mywork.host.service;

import com.atlassian.cache.Cache;
import com.atlassian.cache.CacheFactory;
import com.atlassian.cache.CacheManager;
import com.atlassian.cache.CacheSettingsBuilder;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.usercompatibility.UserCompatibilityHelper;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.mywork.event.notification.NotificationCreatedEvent;
import com.atlassian.mywork.event.notification.NotificationStatusChangedEvent;
import com.atlassian.mywork.event.notification.NotificationUpdatedEvent;
import com.atlassian.mywork.host.dao.NotificationDao;
import com.atlassian.mywork.host.dao.TaskDao;
import com.atlassian.mywork.host.dao.UserDao;
import com.atlassian.mywork.host.event.BeforeCountNewNotificationsEvent;
import com.atlassian.mywork.host.service.ApplicationLinkIdService;
import com.atlassian.mywork.host.service.HTMLServiceImpl;
import com.atlassian.mywork.model.Notification;
import com.atlassian.mywork.model.NotificationBuilder;
import com.atlassian.mywork.model.NotificationFilter;
import com.atlassian.mywork.model.Status;
import com.atlassian.mywork.model.Task;
import com.atlassian.mywork.model.TaskBuilder;
import com.atlassian.mywork.service.LocalNotificationService;
import com.atlassian.mywork.service.PermissionException;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.sal.api.user.UserKey;
import com.google.common.base.MoreObjects;
import com.google.common.util.concurrent.Futures;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class LocalNotificationServiceImpl
implements LocalNotificationService {
    private static final Logger log = LoggerFactory.getLogger(LocalNotificationServiceImpl.class);
    private static final String CACHE_KEY_COUNT = LocalNotificationService.class.getName() + ".count";
    private static final int MAX_COUNT = 10;
    private final NotificationDao notificationDao;
    private final TaskDao taskDao;
    private final UserDao userDao;
    private final EventPublisher eventPublisher;
    private final HTMLServiceImpl htmlService;
    private final Cache<String, Integer> cache;
    private final TransactionTemplate transactionTemplate;
    private final ApplicationLinkIdService applicationLinkHelper;

    public LocalNotificationServiceImpl(NotificationDao notificationDao, TaskDao taskDao, UserDao userDao, EventPublisher eventPublisher, HTMLServiceImpl htmlService, @ComponentImport CacheManager cacheFactory, TransactionTemplate transactionTemplate, ApplicationLinkIdService applicationLinkHelper) {
        this.notificationDao = notificationDao;
        this.taskDao = taskDao;
        this.userDao = userDao;
        this.eventPublisher = eventPublisher;
        this.htmlService = htmlService;
        this.transactionTemplate = transactionTemplate;
        this.applicationLinkHelper = applicationLinkHelper;
        this.cache = LocalNotificationServiceImpl.getCache((CacheFactory)cacheFactory);
    }

    private static Cache<String, Integer> getCache(CacheFactory cacheFactory) {
        return cacheFactory.getCache(CACHE_KEY_COUNT, null, new CacheSettingsBuilder().replicateViaInvalidation().build());
    }

    public Iterable<Notification> findAll(String user) {
        return this.notificationDao.findAll(user);
    }

    public Iterable<Notification> findAllWithCurrentUser(String appId, List<String> actions, Date after) {
        String userKey = this.getCurrentUserKey();
        if (StringUtils.isBlank((CharSequence)userKey)) {
            return Collections.emptyList();
        }
        return this.notificationDao.findAll(userKey, appId, actions, after);
    }

    public List<Notification> findAllWithCurrentUser(boolean onlyGetDirectedAction, int start, int limit) {
        String userKey = this.getCurrentUserKey();
        if (StringUtils.isBlank((CharSequence)userKey)) {
            return Collections.emptyList();
        }
        return this.notificationDao.findAll(userKey, onlyGetDirectedAction, start, limit);
    }

    public List<Notification> findAllWithCurrentUser(NotificationFilter filter, int start, int limit) {
        if (StringUtils.isNotBlank((CharSequence)filter.getUserKey()) && filter.getUserKey().equals(this.getCurrentUserKey())) {
            return this.notificationDao.findAll(filter, start, limit);
        }
        return Collections.emptyList();
    }

    public Iterable<Notification> findAllUnread(String user) {
        return this.notificationDao.findAllUnread(user);
    }

    public Iterable<Notification> findAllUnread(String username, String applicationLinkId, String application) {
        return this.notificationDao.findAllUnread(username, applicationLinkId, application);
    }

    public Iterable<Notification> findAllAfter(String username, long after, long before, int limit) {
        return this.notificationDao.findAllAfter(username, after, before, limit);
    }

    public Notification find(String username, long notificationId) {
        Notification notification = this.notificationDao.get(notificationId);
        if (!username.equals(notification.getUser())) {
            throw new PermissionException("Cannot retrieve notification not owned by the current user");
        }
        return notification;
    }

    public Iterable<Notification> find(String username, String globalId) {
        return this.notificationDao.findByGlobalId(username, globalId);
    }

    public int count(String username, String globalId) {
        return this.notificationDao.countByGlobalId(username, globalId);
    }

    public void delete(String username, long notificationId) {
        Notification notification = this.notificationDao.get(notificationId);
        if (notification != null) {
            if (!username.equals(notification.getUser())) {
                throw new PermissionException("Cannot retrieve notification not owned by the current user");
            }
            this.notificationDao.delete(notificationId);
            this.cache.remove((Object)username);
        }
    }

    public void deleteByGlobalId(String username, String globalId) {
        for (Notification notification : this.notificationDao.findByGlobalId(username, globalId)) {
            this.delete(username, notification.getId());
        }
    }

    public void deleteByGlobalId(String globalId) {
        for (Notification n : this.notificationDao.deleteByGlobalId(globalId)) {
            this.cache.remove((Object)n.getUser());
        }
    }

    public Future<Notification> createOrUpdate(String username, Notification notification) {
        boolean create = notification.getId() == 0L;
        Notification checkedNotification = this.applicationLinkHelper.checkAndUpdate(new NotificationBuilder(notification)).user(username).description(this.htmlService.clean(notification.getDescription())).status(create ? this.getTaskStatus(username, notification) : notification.getStatus()).createNotification();
        Notification createdNotification = this.notificationDao.create(checkedNotification);
        log.debug("Created new notification \"{}\"", (Object)createdNotification);
        if (create) {
            this.eventPublisher.publish((Object)new NotificationCreatedEvent(createdNotification));
        } else {
            this.eventPublisher.publish((Object)new NotificationUpdatedEvent(checkedNotification, createdNotification));
        }
        this.cache.remove((Object)username);
        return Futures.immediateFuture((Object)createdNotification);
    }

    public Status getTaskStatus(String username, Notification notification) {
        Task task;
        if (!StringUtils.isEmpty((CharSequence)notification.getGlobalId()) && (task = this.taskDao.find(username, notification.getGlobalId())) != null) {
            return task.getStatus();
        }
        return notification.getStatus();
    }

    public Task setStatus(String username, long notificationId, Status newStatus) {
        Notification oldNotification = this.notificationDao.get(notificationId);
        Status oldStatus = oldNotification.getStatus();
        if (!username.equals(oldNotification.getUser())) {
            throw new PermissionException("Cannot update notification not owned by the current user");
        }
        Task task = this.taskDao.find(username, oldNotification.getGlobalId());
        if (oldStatus == newStatus) {
            return task;
        }
        this.notificationDao.setStatusByGlobalId(username, oldNotification.getGlobalId(), newStatus);
        Notification notification = new NotificationBuilder(oldNotification).status(newStatus).createNotification();
        if (newStatus == Status.TODO) {
            if (task != null) {
                this.taskDao.update(new TaskBuilder(task).globalId(null).createTask());
            }
            task = (Task)this.taskDao.createOrUpdate(new TaskBuilder().applicationLinkId(notification.getApplicationLinkId()).application(notification.getApplication()).entity(notification.getEntity()).user(username).notes(this.htmlService.clean(this.getDescription(notification))).status(newStatus).iconUrl(notification.getItem().getIconUrl()).url(notification.getItem().getUrl()).title((String)StringUtils.defaultIfEmpty((CharSequence)notification.getItem().getTitle(), (CharSequence)notification.getTitle())).globalId(notification.getGlobalId()).itemTitle(notification.getItem().getTitle()).createTask()).right();
        } else if (task != null) {
            this.taskDao.update(new TaskBuilder(task).status(Status.DONE).createTask());
        }
        this.eventPublisher.publish((Object)new NotificationStatusChangedEvent(notification, task, oldStatus));
        return task;
    }

    @Deprecated
    private String getDescription(Notification notification) {
        if ("com.atlassian.mywork.providers.confluence".equals(notification.getApplication()) && "share".equals(notification.getAction())) {
            return ((String)MoreObjects.firstNonNull((Object)notification.getDescription(), (Object)"")).replaceAll("<br /><br />Also shared with.*", "");
        }
        return "";
    }

    public void setLastRead(String username, Long notificationId) {
        this.userDao.setLastReadNotificationId(username, notificationId);
        this.notificationDao.markAllRead(username, notificationId);
        this.cache.remove((Object)username);
    }

    public void setRead(String username, List<Long> notificationIds) {
        if (!notificationIds.isEmpty()) {
            this.notificationDao.setRead(username, notificationIds);
            this.cache.remove((Object)username);
        }
    }

    public void setRead(UserKey userKey, String globalId, String action, ObjectNode condition) {
        List<Long> readIds = this.notificationDao.setRead(userKey, globalId, action, condition);
        if (!readIds.isEmpty()) {
            this.cache.remove((Object)UserCompatibilityHelper.getUserForKey(userKey.getStringValue()).getName());
        }
    }

    public void updateMetadata(String username, String globalId, ObjectNode condition, ObjectNode metadata) {
        if (StringUtils.isBlank((CharSequence)globalId)) {
            return;
        }
        this.notificationDao.updateMetadata(username, globalId, condition, metadata);
    }

    public int getCount(String username) {
        return (Integer)this.transactionTemplate.execute(() -> this._getCount(username));
    }

    public void invalidateCachedCounts() {
        this.cache.removeAll();
    }

    public void deleteWithCurrentUser(NotificationFilter filter) {
        if (StringUtils.isNotBlank((CharSequence)filter.getUserKey()) && filter.getUserKey().equals(this.getCurrentUserKey())) {
            this.notificationDao.delete(filter);
        }
    }

    public void setReadWithCurrentUser(NotificationFilter filter) {
        if (StringUtils.isNotBlank((CharSequence)filter.getUserKey()) && filter.getUserKey().equals(this.getCurrentUserKey())) {
            this.notificationDao.setRead(filter);
        }
    }

    private String getCurrentUserKey() {
        if (AuthenticatedUserThreadLocal.isAnonymousUser()) {
            return null;
        }
        return AuthenticatedUserThreadLocal.get().getKey().getStringValue();
    }

    private Integer _getCount(String username) {
        return (Integer)this.cache.get((Object)username, () -> this.loadCount(username));
    }

    private int loadCount(String username) {
        this.eventPublisher.publish((Object)new BeforeCountNewNotificationsEvent(username));
        return this.notificationDao.countAllUnreadAfterOnlyIdsAction(username, this.userDao.getLastReadNotificationId(username), 10);
    }
}

