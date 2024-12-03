/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityManager
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.plugins.tasklist.Task
 *  com.atlassian.confluence.plugins.tasklist.TaskStatus
 *  com.atlassian.confluence.plugins.tasklist.event.ConfluenceTaskCreateEvent
 *  com.atlassian.confluence.plugins.tasklist.event.ConfluenceTaskRemoveEvent
 *  com.atlassian.confluence.plugins.tasklist.event.ConfluenceTaskUpdateEvent
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.mywork.event.notification.PushNotificationEvent
 *  com.atlassian.mywork.model.Notification
 *  com.atlassian.mywork.model.NotificationBuilder
 *  com.atlassian.mywork.model.Status
 *  com.atlassian.mywork.model.TaskBuilder
 *  com.atlassian.mywork.service.NotificationService
 *  com.atlassian.mywork.service.TaskService
 *  com.atlassian.mywork.util.GlobalIdFactory
 *  com.atlassian.user.User
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  org.codehaus.jackson.node.JsonNodeFactory
 *  org.codehaus.jackson.node.ObjectNode
 */
package com.atlassian.mywork.providers.confluence;

import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.plugins.tasklist.Task;
import com.atlassian.confluence.plugins.tasklist.TaskStatus;
import com.atlassian.confluence.plugins.tasklist.event.ConfluenceTaskCreateEvent;
import com.atlassian.confluence.plugins.tasklist.event.ConfluenceTaskRemoveEvent;
import com.atlassian.confluence.plugins.tasklist.event.ConfluenceTaskUpdateEvent;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.mywork.event.notification.PushNotificationEvent;
import com.atlassian.mywork.model.Notification;
import com.atlassian.mywork.model.NotificationBuilder;
import com.atlassian.mywork.model.Status;
import com.atlassian.mywork.model.TaskBuilder;
import com.atlassian.mywork.providers.confluence.FieldHelper;
import com.atlassian.mywork.service.NotificationService;
import com.atlassian.mywork.service.TaskService;
import com.atlassian.mywork.util.GlobalIdFactory;
import com.atlassian.user.User;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;

public class ConfluenceTasksEventListener {
    private static final String ACTION_ASSIGN = "task.assign";
    private static final String ACTION_UPDATE = "task.update";
    private static final String ACTION_REMOVE = "task.remove";
    private static final String TASK_ENTITY = "inline-task";
    private final NotificationService notificationService;
    private final TaskService taskService;
    private final ContentEntityManager contentEntityManager;
    private final UserAccessor userAccessor;
    private final FieldHelper fieldHelper;
    private final EventPublisher eventPublisher;

    public ConfluenceTasksEventListener(NotificationService notificationService, TaskService taskService, ContentEntityManager contentEntityManager, UserAccessor userAccessor, FieldHelper fieldHelper, EventPublisher eventPublisher) {
        this.notificationService = notificationService;
        this.taskService = taskService;
        this.contentEntityManager = contentEntityManager;
        this.userAccessor = userAccessor;
        this.fieldHelper = fieldHelper;
        this.eventPublisher = eventPublisher;
    }

    @EventListener
    public void onTaskCreatedEvent(ConfluenceTaskCreateEvent event) throws Exception {
        Task task = event.getTask();
        ContentEntityObject content = this.contentEntityManager.getById(task.getContentId());
        User fromUser = event.getOriginatingUser();
        User assignee = this.userAccessor.getUser(task.getAssignee());
        if (assignee != null && task.getStatus() != TaskStatus.CHECKED) {
            this.taskService.createOrUpdate(assignee.getName(), this.buildTask(task, content).createTask());
            if (fromUser != null && !fromUser.getName().equals(assignee.getName())) {
                Notification notification = (Notification)this.notificationService.createOrUpdate(assignee.getName(), this.buildNotification(task, content, fromUser).title(fromUser.getFullName() + " assigned a task on " + content.getTitle()).description(task.getTitle()).action(ACTION_ASSIGN).createNotification()).get();
                this.eventPublisher.publish((Object)new PushNotificationEvent(Collections.singletonList(notification)));
            }
        } else if (assignee != null && task.getStatus() == TaskStatus.CHECKED) {
            this.taskService.createOrUpdate(assignee.getName(), this.buildTask(task, content).status(Status.DONE).createTask());
        }
    }

    @EventListener
    public void onTaskRemovedEvent(ConfluenceTaskRemoveEvent event) {
        Task task = event.getTask();
        ContentEntityObject content = this.contentEntityManager.getById(task.getContentId());
        User fromUser = event.getOriginatingUser();
        User assignee = this.userAccessor.getUser(task.getAssignee());
        if (assignee != null) {
            this.taskService.delete(assignee.getName(), this.getTaskGlobalId(task));
            if (!fromUser.getName().equals(assignee.getName())) {
                this.notificationService.createOrUpdate(assignee.getName(), this.buildNotification(task, content, fromUser).title(fromUser.getFullName() + " removed a task on " + content.getTitle()).description(task.getTitle()).action(ACTION_REMOVE).createNotification());
            }
        }
    }

    @EventListener
    public void onTaskUpdatedEvent(ConfluenceTaskUpdateEvent event) {
        User oldAssignee;
        if (!(event.hasStatusChanged() || event.hasTitleChanged() || event.hasAssigneeChanged())) {
            return;
        }
        Task task = event.getTask();
        Task oldTask = event.getOldTask();
        ContentEntityObject content = this.contentEntityManager.getById(task.getContentId());
        User fromUser = event.getOriginatingUser();
        User assignee = this.userAccessor.getUser(task.getAssignee());
        User user = oldAssignee = event.hasAssigneeChanged() ? this.userAccessor.getUser(oldTask.getAssignee()) : assignee;
        if (event.hasTitleChanged()) {
            this.handleTaskRenaming(task, content, fromUser, oldAssignee, oldTask.getTitle());
        }
        if (event.hasAssigneeChanged()) {
            this.handleTaskReassignment(task, content, fromUser, assignee, oldAssignee);
        } else if (event.hasStatusChanged()) {
            if (task.getStatus() == TaskStatus.CHECKED) {
                this.handleTaskChecked(task, content, fromUser, assignee);
            } else {
                this.handleTaskUnchecked(task, content, fromUser, assignee);
            }
        }
    }

    private void handleTaskReassignment(Task task, ContentEntityObject content, User fromUser, User assignee, User oldAssignee) {
        ImmutableMap extraMetadata;
        if (oldAssignee != null) {
            this.taskService.delete(oldAssignee.getName(), this.getTaskGlobalId(task));
            if (!fromUser.getName().equals(oldAssignee.getName())) {
                extraMetadata = ImmutableMap.of((Object)"updateType", (Object)"unassign");
                this.notificationService.createOrUpdate(oldAssignee.getName(), this.buildNotification(task, content, fromUser, (Map<String, String>)extraMetadata).title(fromUser.getFullName() + " updated a task on " + content.getTitle()).description(task.getTitle()).action(ACTION_UPDATE).createNotification());
            }
        }
        if (assignee != null && task.getStatus() != TaskStatus.CHECKED) {
            this.taskService.createOrUpdate(assignee.getName(), this.buildTask(task, content).createTask());
            if (!fromUser.getName().equals(assignee.getName())) {
                extraMetadata = ImmutableMap.of((Object)"updateType", (Object)"reassign");
                this.notificationService.createOrUpdate(assignee.getName(), this.buildNotification(task, content, fromUser, (Map<String, String>)extraMetadata).title(fromUser.getFullName() + " updated a task on " + content.getTitle()).description(task.getTitle()).action(ACTION_UPDATE).createNotification());
            }
        } else if (assignee != null && task.getStatus() == TaskStatus.CHECKED) {
            this.taskService.createOrUpdate(assignee.getName(), this.buildTask(task, content).status(Status.DONE).createTask());
        }
    }

    private void handleTaskRenaming(Task task, ContentEntityObject content, User fromUser, User assignee, String oldTitle) {
        if (assignee != null) {
            String assigneeName = assignee.getName();
            this.taskService.setTitle(assigneeName, this.getTaskGlobalId(task), task.getTitle());
            if (!fromUser.getName().equals(assigneeName)) {
                ImmutableMap extraMetadata = ImmutableMap.of((Object)"updateType", (Object)"rename", (Object)"oldTaskTitle", (Object)oldTitle);
                this.notificationService.createOrUpdate(assigneeName, this.buildNotification(task, content, fromUser, (Map<String, String>)extraMetadata).title(fromUser.getFullName() + " updated a task on " + content.getTitle()).description(task.getTitle()).action(ACTION_UPDATE).createNotification());
            }
        }
    }

    private void handleTaskChecked(Task task, ContentEntityObject content, User fromUser, User assignee) {
        if (assignee == null) {
            return;
        }
        this.taskService.markComplete(assignee.getName(), this.getTaskGlobalId(task));
        if (!fromUser.getName().equals(assignee.getName())) {
            ImmutableMap extraMetadata = ImmutableMap.of((Object)"updateType", (Object)"complete");
            this.notificationService.createOrUpdate(assignee.getName(), this.buildNotification(task, content, fromUser, (Map<String, String>)extraMetadata).title(fromUser.getFullName() + " updated a task on " + content.getTitle()).description(task.getTitle()).action(ACTION_UPDATE).createNotification());
        }
    }

    private void handleTaskUnchecked(Task task, ContentEntityObject content, User fromUser, User assignee) {
        if (assignee == null) {
            return;
        }
        this.taskService.markIncomplete(assignee.getName(), this.getTaskGlobalId(task));
        if (!fromUser.getName().equals(assignee.getName())) {
            ImmutableMap extraMetadata = ImmutableMap.of((Object)"updateType", (Object)"uncomplete");
            this.notificationService.createOrUpdate(assignee.getName(), this.buildNotification(task, content, fromUser, (Map<String, String>)extraMetadata).title(fromUser.getFullName() + " updated a task on " + content.getTitle()).description(task.getTitle()).action(ACTION_UPDATE).createNotification());
        }
    }

    private NotificationBuilder buildNotification(Task task, ContentEntityObject content, User fromUser) {
        return this.buildNotification(task, content, fromUser, (Map<String, String>)ImmutableMap.of());
    }

    private NotificationBuilder buildNotification(Task task, ContentEntityObject content, User fromUser, Map<String, String> extraMetadata) {
        ObjectNode metadata = JsonNodeFactory.instance.objectNode();
        metadata.put("user", fromUser.getFullName());
        metadata.put("username", fromUser.getName());
        metadata.put("taskId", task.getId());
        metadata.put("pageId", content.getId());
        metadata.put("contentVersion", content.getVersion());
        for (Map.Entry<String, String> entry : extraMetadata.entrySet()) {
            metadata.put(entry.getKey(), entry.getValue());
        }
        return this.fieldHelper.buildNotification(content, fromUser).groupingId(this.fieldHelper.createGlobalId(FieldHelper.getContentType(content), content.getId())).globalId(this.getTaskGlobalId(task)).metadata(metadata);
    }

    private TaskBuilder buildTask(Task task, ContentEntityObject content) {
        ObjectNode metadata = JsonNodeFactory.instance.objectNode();
        metadata.put("contentId", task.getContentId());
        metadata.put("taskId", task.getId());
        return new TaskBuilder().applicationLinkId(this.fieldHelper.getHostId()).user(task.getAssignee()).globalId(this.getTaskGlobalId(task)).application(FieldHelper.APP_CONFLUENCE).entity(TASK_ENTITY).metadata(metadata).status(Status.TODO).url(content.getUrlPath()).title(task.getTitle()).itemTitle(content.getTitle());
    }

    private String getTaskGlobalId(Task task) {
        ArrayList keys = Lists.newArrayList((Object[])new String[]{"appId", "contentId", "taskId"});
        HashMap params = Maps.newHashMap();
        params.put("appId", this.fieldHelper.getHostId());
        params.put("contentId", Long.toString(task.getContentId()));
        params.put("taskId", Long.toString(task.getId()));
        return GlobalIdFactory.encode((List)keys, (Map)params);
    }
}

