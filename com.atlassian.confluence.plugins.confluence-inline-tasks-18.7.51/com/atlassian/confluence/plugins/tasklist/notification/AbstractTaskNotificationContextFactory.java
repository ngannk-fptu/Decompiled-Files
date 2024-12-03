/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansion
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.link.LinkType
 *  com.atlassian.confluence.api.service.content.ContentService
 *  com.atlassian.confluence.core.MaybeNot
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.notifications.Notification
 *  com.atlassian.confluence.notifications.NotificationUserService
 *  com.atlassian.confluence.notifications.RenderContextProviderTemplate
 *  com.atlassian.confluence.plugin.descriptor.mail.NotificationContext
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.confluence.util.i18n.I18NBean
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.fugue.Either
 *  com.atlassian.fugue.Iterables
 *  com.atlassian.fugue.Maybe
 *  com.atlassian.fugue.Option
 *  com.atlassian.plugin.notifications.api.medium.NotificationAddress
 *  com.atlassian.plugin.notifications.api.medium.ServerConfiguration
 *  com.atlassian.plugin.notifications.api.medium.recipient.RoleRecipient
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.user.User
 *  com.google.common.base.Function
 *  com.google.common.base.Predicate
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.ListMultimap
 *  javax.annotation.Nonnull
 */
package com.atlassian.confluence.plugins.tasklist.notification;

import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.link.LinkType;
import com.atlassian.confluence.api.service.content.ContentService;
import com.atlassian.confluence.core.MaybeNot;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.notifications.Notification;
import com.atlassian.confluence.notifications.NotificationUserService;
import com.atlassian.confluence.notifications.RenderContextProviderTemplate;
import com.atlassian.confluence.plugin.descriptor.mail.NotificationContext;
import com.atlassian.confluence.plugins.tasklist.TaskModfication;
import com.atlassian.confluence.plugins.tasklist.notification.TaskRenderService;
import com.atlassian.confluence.plugins.tasklist.notification.api.TaskPayload;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.fugue.Either;
import com.atlassian.fugue.Iterables;
import com.atlassian.fugue.Maybe;
import com.atlassian.fugue.Option;
import com.atlassian.plugin.notifications.api.medium.NotificationAddress;
import com.atlassian.plugin.notifications.api.medium.ServerConfiguration;
import com.atlassian.plugin.notifications.api.medium.recipient.RoleRecipient;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.user.User;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ListMultimap;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;

public abstract class AbstractTaskNotificationContextFactory
extends RenderContextProviderTemplate<TaskPayload> {
    private static final Function<TaskModfication, TaskModfication.Operation> TO_OPERATION_FROM_TASK = task -> task.getTaskOperation();
    private final UserAccessor userAccessor;
    private final I18NBeanFactory beanFactory;
    private final LocaleManager localeManager;
    private final TaskRenderService taskRenderService;
    private final ContentService contentService;
    private final NotificationUserService notificationUserService;

    public AbstractTaskNotificationContextFactory(UserAccessor userAccessor, I18NBeanFactory beanFactory, LocaleManager localeManager, TaskRenderService taskRenderService, ContentService contentService, NotificationUserService notificationUserService) {
        this.userAccessor = userAccessor;
        this.beanFactory = beanFactory;
        this.localeManager = localeManager;
        this.taskRenderService = taskRenderService;
        this.contentService = contentService;
        this.notificationUserService = notificationUserService;
    }

    protected Maybe<Map<String, Object>> checkedCreate(Notification<TaskPayload> simpleSendTaskPayloadNotification, ServerConfiguration configuration, Maybe<Either<NotificationAddress, RoleRecipient>> roleRecipient) {
        if (roleRecipient.isEmpty() || ((Either)roleRecipient.get()).isLeft()) {
            return MaybeNot.becauseOf((String)"This factory exposes content, thus recipient has to be provided in order to perform a VIEW permission check.", (Object[])new Object[0]);
        }
        RoleRecipient recipient = (RoleRecipient)((Either)roleRecipient.get()).right().get();
        TaskPayload payload = (TaskPayload)simpleSendTaskPayloadNotification.getPayload();
        ConfluenceUser recipientUser = this.userAccessor.getUserByKey(recipient.getUserKey());
        Option modifierKey = payload.getOriginatingUserKey().isDefined() ? Option.some((Object)new UserKey((String)payload.getOriginatingUserKey().get())) : Option.none();
        User modifier = this.notificationUserService.findUserForKey((User)recipientUser, (Maybe)modifierKey);
        I18NBean i18NBean = this.beanFactory.getI18NBean(this.localeManager.getLocale((User)recipientUser));
        NotificationContext context = new NotificationContext();
        Iterable<TaskModfication> tasks = payload.getTasks().get(recipient.getUserKey());
        Option maybeContent = this.contentService.find(new Expansion[0]).withId(payload.getContentId()).fetchOne();
        if (!maybeContent.isDefined()) {
            return MaybeNot.becauseOf((String)String.format("Could not read content for content id %d", payload.getContentId().asLong()), (Object[])new Object[0]);
        }
        Content content = (Content)maybeContent.get();
        ListMultimap<TaskModfication.Operation, TaskModfication> categorisedTasks = this.renderTask(tasks, content, recipientUser);
        context.put("subjectKey", (Object)("inline-tasks-update.mail.subject." + (com.google.common.collect.Iterables.isEmpty((Iterable)com.google.common.collect.Iterables.filter(tasks, AbstractTaskNotificationContextFactory.with(TaskModfication.Operation.ASSIGNED))) ? "updated" : "assigned")));
        context.put("subjectFullName", (Object)(modifier != null ? modifier.getFullName() : i18NBean.getText("anonymous.name")));
        context.put("modifier", (Object)modifier);
        context.put("categorizedTasks", this.getTaskMapWithStringKeys(categorisedTasks.asMap()));
        context.put("numberOfTasks", (Object)com.google.common.collect.Iterables.size(tasks));
        context.put("content", (Object)content);
        context.put("contentLink", content.getLinks().get(LinkType.WEB_UI));
        if (categorisedTasks.keys().size() > 1) {
            context.put("headerActionString", (Object)"tasks.mail.templates.multiple.actions");
        } else {
            context.put("headerActionString", (Object)((TaskModfication.Operation)((Object)Iterables.first((Iterable)categorisedTasks.keySet()).get())).getI18nKey());
        }
        return Option.some((Object)context.getMap());
    }

    private Map<String, Collection<TaskModfication>> getTaskMapWithStringKeys(Map<TaskModfication.Operation, Collection<TaskModfication>> categorisedTasks) {
        HashMap<String, Collection<TaskModfication>> categorisedTasksMap = new HashMap<String, Collection<TaskModfication>>();
        for (TaskModfication.Operation operation : categorisedTasks.keySet()) {
            categorisedTasksMap.put(operation.name(), categorisedTasks.get((Object)operation));
        }
        return categorisedTasksMap;
    }

    protected abstract ListMultimap<TaskModfication.Operation, TaskModfication> renderTask(Iterable<TaskModfication> var1, Content var2, ConfluenceUser var3);

    protected TaskRenderService getTaskRenderService() {
        return this.taskRenderService;
    }

    protected Function<TaskModfication, TaskModfication.Operation> byOperation() {
        return TO_OPERATION_FROM_TASK;
    }

    private static Predicate<TaskModfication> with(@Nonnull TaskModfication.Operation operation) {
        return taskUpdate -> taskUpdate.getTaskOperation() == operation;
    }
}

