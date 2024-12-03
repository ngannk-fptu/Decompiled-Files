/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.service.content.ContentService
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.notifications.NotificationUserService
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.google.common.collect.ListMultimap
 *  com.google.common.collect.Multimaps
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.tasklist.notification;

import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.service.content.ContentService;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.notifications.NotificationUserService;
import com.atlassian.confluence.plugins.tasklist.TaskModfication;
import com.atlassian.confluence.plugins.tasklist.notification.AbstractTaskNotificationContextFactory;
import com.atlassian.confluence.plugins.tasklist.notification.TaskRenderService;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimaps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EmailTaskNotificationContextFactory
extends AbstractTaskNotificationContextFactory {
    @Autowired
    public EmailTaskNotificationContextFactory(UserAccessor userAccessor, I18NBeanFactory beanFactory, LocaleManager localeManager, TaskRenderService taskRenderService, ContentService contentService, NotificationUserService notificationUserService) {
        super(userAccessor, beanFactory, localeManager, taskRenderService, contentService, notificationUserService);
    }

    @Override
    protected ListMultimap<TaskModfication.Operation, TaskModfication> renderTask(Iterable<TaskModfication> tasks, Content content, ConfluenceUser recipientUser) {
        return Multimaps.index(this.getTaskRenderService().renderTasksOnPage(tasks, content), this.byOperation());
    }
}

