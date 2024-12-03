/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.lifecycle.events.ApplicationStoppingEvent
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  com.atlassian.sal.api.lifecycle.LifecycleAware
 *  com.atlassian.sal.api.scheduling.PluginScheduler
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.google.common.collect.ImmutableMap
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.stereotype.Component
 */
package com.atlassian.mywork.host.batch;

import com.atlassian.config.lifecycle.events.ApplicationStoppingEvent;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.mywork.host.batch.NotificationCleanerTask;
import com.atlassian.mywork.host.batch.TaskCleanerTask;
import com.atlassian.mywork.host.batch.UserCleanerTask;
import com.atlassian.mywork.host.dao.NotificationDao;
import com.atlassian.mywork.host.dao.TaskDao;
import com.atlassian.mywork.host.dao.UserDao;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.sal.api.lifecycle.LifecycleAware;
import com.atlassian.sal.api.scheduling.PluginScheduler;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.google.common.collect.ImmutableMap;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

@ExportAsService(value={LifecycleAware.class})
@Component
public class CleanerScheduler
implements LifecycleAware,
InitializingBean {
    private static final String TASK_CLEANER_NAME = CleanerScheduler.class.getName() + ":job";
    private static final String NOTIFICATION_CLEANER_NAME = NotificationCleanerTask.class.getName() + ":job";
    private static final String USER_CLEANER_NAME = UserCleanerTask.class.getName() + ":job";
    private final AtomicReference<PluginScheduler> pluginScheduler = new AtomicReference();
    private final TaskDao taskDao;
    private final NotificationDao notificationDao;
    private final UserDao userDao;
    private final TransactionTemplate transactionTemplate;
    private final EventPublisher eventPublisher;

    public CleanerScheduler(PluginScheduler pluginScheduler, TaskDao taskDao, NotificationDao notificationDao, UserDao userDao, TransactionTemplate transactionTemplate, EventPublisher eventPublisher) {
        this.pluginScheduler.set(pluginScheduler);
        this.taskDao = taskDao;
        this.notificationDao = notificationDao;
        this.userDao = userDao;
        this.transactionTemplate = transactionTemplate;
        this.eventPublisher = eventPublisher;
    }

    public void afterPropertiesSet() {
        this.eventPublisher.register((Object)this);
    }

    public void onStart() {
        this.pluginScheduler.get().scheduleJob(TASK_CLEANER_NAME, TaskCleanerTask.class, (Map)ImmutableMap.of((Object)TaskDao.class.getName(), (Object)this.taskDao, (Object)TransactionTemplate.class.getName(), (Object)this.transactionTemplate), new Date(System.currentTimeMillis() + TimeUnit.MILLISECONDS.convert(60L, TimeUnit.SECONDS)), TimeUnit.MILLISECONDS.convert(1L, TimeUnit.DAYS));
        this.pluginScheduler.get().scheduleJob(NOTIFICATION_CLEANER_NAME, NotificationCleanerTask.class, (Map)ImmutableMap.of((Object)NotificationDao.class.getName(), (Object)this.notificationDao, (Object)TransactionTemplate.class.getName(), (Object)this.transactionTemplate), new Date(System.currentTimeMillis() + TimeUnit.MILLISECONDS.convert(120L, TimeUnit.SECONDS)), TimeUnit.MILLISECONDS.convert(1L, TimeUnit.DAYS));
        this.pluginScheduler.get().scheduleJob(USER_CLEANER_NAME, UserCleanerTask.class, (Map)ImmutableMap.of((Object)UserDao.class.getName(), (Object)this.userDao, (Object)TransactionTemplate.class.getName(), (Object)this.transactionTemplate), new Date(System.currentTimeMillis() + TimeUnit.MILLISECONDS.convert(12L, TimeUnit.HOURS)), TimeUnit.MILLISECONDS.convert(1L, TimeUnit.DAYS));
    }

    @EventListener
    public void onEvent(ApplicationStoppingEvent stoppingEvent) {
        this.pluginScheduler.set(null);
    }

    public void onStop() {
        this.eventPublisher.unregister((Object)this);
        this.unscheduleJob(TASK_CLEANER_NAME);
        this.unscheduleJob(NOTIFICATION_CLEANER_NAME);
    }

    private void unscheduleJob(String jobName) {
        PluginScheduler scheduler = this.pluginScheduler.get();
        if (scheduler == null) {
            return;
        }
        try {
            scheduler.unscheduleJob(jobName);
        }
        catch (IllegalArgumentException illegalArgumentException) {
            // empty catch block
        }
    }
}

