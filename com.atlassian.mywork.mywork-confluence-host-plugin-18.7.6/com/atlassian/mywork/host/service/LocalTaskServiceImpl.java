/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.fugue.Pair
 *  com.atlassian.mywork.event.task.TaskCreatedEvent
 *  com.atlassian.mywork.event.task.TaskDeletedEvent
 *  com.atlassian.mywork.event.task.TaskMovedEvent
 *  com.atlassian.mywork.event.task.TaskUpdatedEvent
 *  com.atlassian.mywork.model.Status
 *  com.atlassian.mywork.model.Task
 *  com.atlassian.mywork.model.TaskBuilder
 *  com.atlassian.mywork.service.LocalTaskService
 *  com.atlassian.mywork.service.PermissionException
 *  com.google.common.base.Function
 *  com.google.common.collect.Lists
 *  com.google.common.util.concurrent.Futures
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.lang3.Validate
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.stereotype.Component
 */
package com.atlassian.mywork.host.service;

import com.atlassian.event.api.EventPublisher;
import com.atlassian.fugue.Pair;
import com.atlassian.mywork.event.task.TaskCreatedEvent;
import com.atlassian.mywork.event.task.TaskDeletedEvent;
import com.atlassian.mywork.event.task.TaskMovedEvent;
import com.atlassian.mywork.event.task.TaskUpdatedEvent;
import com.atlassian.mywork.host.dao.NotificationDao;
import com.atlassian.mywork.host.dao.TaskDao;
import com.atlassian.mywork.host.dao.UserDao;
import com.atlassian.mywork.host.service.ApplicationLinkIdService;
import com.atlassian.mywork.host.service.TaskOrder;
import com.atlassian.mywork.model.Status;
import com.atlassian.mywork.model.Task;
import com.atlassian.mywork.model.TaskBuilder;
import com.atlassian.mywork.service.LocalTaskService;
import com.atlassian.mywork.service.PermissionException;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.Futures;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class LocalTaskServiceImpl
implements LocalTaskService {
    private static final Logger log = LoggerFactory.getLogger(LocalTaskServiceImpl.class);
    private final TaskDao taskDao;
    private final NotificationDao notificationDao;
    private final UserDao userDao;
    private final EventPublisher eventPublisher;
    private final ApplicationLinkIdService applicationLinkHelper;

    public LocalTaskServiceImpl(TaskDao taskDao, NotificationDao notificationDao, UserDao userDao, EventPublisher eventPublisher, ApplicationLinkIdService applicationLinkHelper) {
        this.taskDao = taskDao;
        this.notificationDao = notificationDao;
        this.userDao = userDao;
        this.eventPublisher = eventPublisher;
        this.applicationLinkHelper = applicationLinkHelper;
    }

    public Iterable<Task> findAll(String user) {
        Iterable<Task> tasks = this.userDao.getTaskOrdering(user).order(this.taskDao.findAll(user));
        return Lists.reverse((List)Lists.newArrayList(tasks));
    }

    public boolean hasTasksToMigrate(String username) {
        return this.taskDao.hasTasksToMigrate(username);
    }

    public Iterable<Task> findAllTasksToMigrate(String username) {
        return this.taskDao.findAllTasksToMigrate(username);
    }

    public Iterable<Task> findAllTasksByType(String user, String type) {
        Iterable<Task> tasks = this.userDao.getTaskOrdering(user).order(this.taskDao.findAllTasksByEntity(user, type));
        return Lists.reverse((List)Lists.newArrayList(tasks));
    }

    public Task get(long id) {
        return this.taskDao.get(id);
    }

    public Task find(String username, String globalId) {
        return this.taskDao.find(username, globalId);
    }

    public Future<Task> markComplete(String username, String globalId) {
        return this.updateByGlobalId(username, globalId, (Function<Task, Task>)((Function)task -> new TaskBuilder(task).status(Status.DONE).createTask()));
    }

    public Future<Task> markIncomplete(String username, String globalId) {
        return this.updateByGlobalId(username, globalId, (Function<Task, Task>)((Function)task -> new TaskBuilder(task).status(Status.TODO).createTask()));
    }

    public Future<Task> setTitle(String username, String globalId, String title) {
        return this.updateByGlobalId(username, globalId, (Function<Task, Task>)((Function)task -> new TaskBuilder(task).title(title).createTask()));
    }

    private Future<Task> updateByGlobalId(String username, String globalId, Function<Task, Task> updateFunction) {
        return this.createOrUpdate(username, (Task)updateFunction.apply((Object)this.find(username, globalId)));
    }

    public Future<Task> createOrUpdate(String username, Task task) {
        return Futures.immediateFuture((Object)this.createOrUpdateInternal(username, task));
    }

    public Future<List<Task>> createOrUpdate(String username, List<Task> tasks) {
        ArrayList<Task> createdTasks = new ArrayList<Task>(tasks.size());
        for (Task task : tasks) {
            createdTasks.add(this.createOrUpdateInternal(username, task));
        }
        return Futures.immediateFuture(createdTasks);
    }

    private Task createOrUpdateInternal(String username, Task task) {
        Validate.notEmpty((CharSequence)StringUtils.trimToEmpty((String)task.getTitle()), (String)"Task title must not be left blank", (Object[])new Object[0]);
        Task checkedTask = this.setUsernameAndAppLink(username, task);
        Pair<Boolean, Task> createdResult = this.taskDao.createOrUpdate(checkedTask);
        boolean isCreate = (Boolean)createdResult.left();
        Task createdTask = (Task)createdResult.right();
        if (isCreate) {
            this.userDao.setTaskOrdering(username, this.getTaskOrder(username).moveBefore(createdTask.getId(), null));
        }
        this.notificationDao.setStatusByGlobalId(username, createdTask.getGlobalId(), createdTask.getStatus());
        if (isCreate) {
            log.debug("Created new task \"{}\"", (Object)createdTask);
            this.eventPublisher.publish((Object)new TaskCreatedEvent(createdTask));
        } else {
            log.debug("Updated existing task \"{}\"", (Object)createdTask);
            this.eventPublisher.publish((Object)new TaskUpdatedEvent(createdTask, checkedTask));
        }
        return createdTask;
    }

    public Task update(String username, Task task) {
        Task oldTask = this.taskDao.get(task.getId());
        if (!username.equals(oldTask.getUser())) {
            throw new PermissionException("Cannot update task not owned by the current user");
        }
        Task checkedTask = this.setUsernameAndAppLink(username, task);
        Task updatedTask = this.taskDao.update(checkedTask);
        if (oldTask.getStatus() != updatedTask.getStatus()) {
            this.notificationDao.setStatusByGlobalId(username, updatedTask.getGlobalId(), updatedTask.getStatus());
        }
        this.eventPublisher.publish((Object)new TaskUpdatedEvent(oldTask, updatedTask));
        return updatedTask;
    }

    public Task updateNotes(String username, long taskId, String notes) {
        Task oldTask = this.taskDao.get(taskId);
        if (!username.equals(oldTask.getUser())) {
            throw new PermissionException("Cannot update task not owned by the current user");
        }
        Task updatedTask = this.taskDao.updateNotes(taskId, notes);
        this.eventPublisher.publish((Object)new TaskUpdatedEvent(oldTask, updatedTask));
        return updatedTask;
    }

    private Task setUsernameAndAppLink(String username, Task task) {
        return this.applicationLinkHelper.checkAndUpdate(new TaskBuilder(task).user(username)).createTask();
    }

    public void delete(String username, long id) {
        this.postDelete(username, this.taskDao.delete(username, id));
    }

    public void delete(String username, String globalId) {
        this.postDelete(username, this.taskDao.delete(username, globalId));
    }

    private void postDelete(String username, Task deletedTask) {
        if (deletedTask != null) {
            this.notificationDao.setStatusByGlobalId(username, deletedTask.getGlobalId(), null);
            this.eventPublisher.publish((Object)new TaskDeletedEvent(deletedTask));
        }
    }

    public void moveBefore(String username, long sourceId, Long targetId) {
        this.userDao.setTaskOrdering(username, this.getTaskOrder(username).moveAfter(sourceId, targetId));
        this.eventPublisher.publish((Object)new TaskMovedEvent(this.taskDao.get(sourceId), targetId));
    }

    private TaskOrder getTaskOrder(String username) {
        TaskOrder order = this.userDao.getTaskOrdering(username);
        return order.update(this.taskDao.findAll(username));
    }
}

