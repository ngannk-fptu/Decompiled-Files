/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.event.events.user.UserRemoveEvent
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  com.atlassian.sal.api.lifecycle.LifecycleAware
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.stereotype.Component
 */
package com.atlassian.mywork.host.listener;

import com.atlassian.confluence.event.events.user.UserRemoveEvent;
import com.atlassian.confluence.usercompatibility.UserCompatibilityHelper;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.mywork.host.dao.NotificationDao;
import com.atlassian.mywork.host.dao.TaskDao;
import com.atlassian.mywork.host.dao.UserDao;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.sal.api.lifecycle.LifecycleAware;
import com.atlassian.sal.usercompatibility.UserKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@ExportAsService(value={LifecycleAware.class})
@Component
public class UserDeletedListener
implements LifecycleAware {
    private static final Logger LOG = LoggerFactory.getLogger(UserDeletedListener.class);
    private final EventPublisher eventPublisher;
    private final NotificationDao notificationDao;
    private final TaskDao taskDao;
    private final UserDao userDao;

    public UserDeletedListener(EventPublisher eventPublisher, NotificationDao notificationDao, TaskDao taskDao, UserDao userDao) {
        this.eventPublisher = eventPublisher;
        this.notificationDao = notificationDao;
        this.taskDao = taskDao;
        this.userDao = userDao;
    }

    @EventListener
    public void userDeleted(UserRemoveEvent event) {
        UserKey userKey = UserCompatibilityHelper.getKeyForUser(event.getUser());
        if (userKey != null) {
            int notifications = this.notificationDao.deleteAll(userKey);
            int tasks = this.taskDao.deleteAll(userKey);
            this.userDao.delete(userKey);
            LOG.info("Removed {} notifications and {} tasks for deleted user {}", new Object[]{notifications, tasks, event.getUser().getName()});
        } else {
            LOG.debug("Cannot delete workbox data, no userkey retrieved for  {}", (Object)event.getUser());
        }
    }

    public void onStart() {
        this.eventPublisher.register((Object)this);
    }

    public void onStop() {
        this.eventPublisher.unregister((Object)this);
    }
}

