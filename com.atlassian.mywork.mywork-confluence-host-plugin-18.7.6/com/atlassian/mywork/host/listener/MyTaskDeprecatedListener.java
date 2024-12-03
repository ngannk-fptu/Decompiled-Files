/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.mywork.service.LocalTaskService
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.lifecycle.LifecycleAware
 *  org.springframework.stereotype.Component
 */
package com.atlassian.mywork.host.listener;

import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.mywork.host.event.MyTaskDeprecatedEvent;
import com.atlassian.mywork.host.notification.MailNotificationService;
import com.atlassian.mywork.service.LocalTaskService;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.lifecycle.LifecycleAware;
import org.springframework.stereotype.Component;

@ExportAsService(value={LifecycleAware.class})
@Component
public class MyTaskDeprecatedListener
implements LifecycleAware {
    private final EventPublisher eventPublisher;
    private final MailNotificationService mailNotificationService;
    private final LocalTaskService taskService;
    private final UserAccessor userAccessor;

    public MyTaskDeprecatedListener(EventPublisher eventPublisher, MailNotificationService mailNotificationService, LocalTaskService taskService, @ComponentImport UserAccessor userAccessor) {
        this.eventPublisher = eventPublisher;
        this.mailNotificationService = mailNotificationService;
        this.taskService = taskService;
        this.userAccessor = userAccessor;
    }

    @EventListener
    public void listen(MyTaskDeprecatedEvent event) {
        ConfluenceUser userByKey = this.userAccessor.getUserByKey(event.getUserKey());
        Iterable personalTasks = this.taskService.findAllTasksToMigrate(userByKey.getName());
        this.mailNotificationService.sendDeprecatedTaskEmail(userByKey, personalTasks);
    }

    public void onStart() {
        this.eventPublisher.register((Object)this);
    }

    public void onStop() {
        this.eventPublisher.unregister((Object)this);
    }
}

