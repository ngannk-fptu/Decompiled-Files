/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityManager
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.core.FormatSettingsManager
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.plugin.services.VelocityHelperService
 *  com.atlassian.confluence.security.PermissionHelper
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.user.PersonalInformationManager
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.confluence.util.HtmlUtil
 *  com.atlassian.confluence.util.i18n.I18NBean
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugin.webresource.WebResourceManager
 *  com.atlassian.renderer.WikiStyleRenderer
 *  com.atlassian.user.User
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.text.StringEscapeUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Qualifier
 */
package com.atlassian.confluence.extra.dynamictasklist2;

import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.FormatSettingsManager;
import com.atlassian.confluence.extra.dynamictasklist2.DateRenderer;
import com.atlassian.confluence.extra.dynamictasklist2.NameRenderer;
import com.atlassian.confluence.extra.dynamictasklist2.TaskListManager;
import com.atlassian.confluence.extra.dynamictasklist2.model.Sort;
import com.atlassian.confluence.extra.dynamictasklist2.model.Task;
import com.atlassian.confluence.extra.dynamictasklist2.model.TaskList;
import com.atlassian.confluence.extra.dynamictasklist2.model.TaskListConfig;
import com.atlassian.confluence.extra.dynamictasklist2.model.TaskListId;
import com.atlassian.confluence.extra.dynamictasklist2.util.TaskListUtil;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.plugin.services.VelocityHelperService;
import com.atlassian.confluence.security.PermissionHelper;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.user.PersonalInformationManager;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.util.HtmlUtil;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugin.webresource.WebResourceManager;
import com.atlassian.renderer.WikiStyleRenderer;
import com.atlassian.user.User;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;

public class TaskListServlet
extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(TaskListServlet.class);
    private final UserAccessor userAccessor;
    private final PermissionHelper permissionHelper;
    private final LocaleManager localeManager;
    private final I18NBeanFactory i18NBeanFactory;
    private final TaskListManager defaultTaskListManager;
    private final ContentEntityManager contentEntityManager;
    private final WikiStyleRenderer wikiStyleRenderer;
    private final FormatSettingsManager formatSettingsManager;
    private final WebResourceManager webResourceManager;
    private final VelocityHelperService velocityHelperService;
    private Map<String, Class<? extends Action>> actionHandlers = new HashMap<String, Class<? extends Action>>();

    public TaskListServlet(@ComponentImport UserAccessor userAccessor, @ComponentImport PermissionManager permissionManager, @ComponentImport PersonalInformationManager personalInformationManager, @ComponentImport LocaleManager localeManager, @ComponentImport I18NBeanFactory i18NBeanFactory, TaskListManager defaultTaskListManager, @ComponentImport @Qualifier(value="contentEntityManager") ContentEntityManager contentEntityManager, @ComponentImport WikiStyleRenderer wikiStyleRenderer, @ComponentImport FormatSettingsManager formatSettingsManager, @ComponentImport WebResourceManager webResourceManager, @ComponentImport VelocityHelperService velocityHelperService, @ComponentImport PageManager pageManager) {
        this.userAccessor = userAccessor;
        this.permissionHelper = new PermissionHelper(permissionManager, personalInformationManager, pageManager);
        this.localeManager = localeManager;
        this.i18NBeanFactory = i18NBeanFactory;
        this.defaultTaskListManager = defaultTaskListManager;
        this.contentEntityManager = contentEntityManager;
        this.wikiStyleRenderer = wikiStyleRenderer;
        this.formatSettingsManager = formatSettingsManager;
        this.webResourceManager = webResourceManager;
        this.velocityHelperService = velocityHelperService;
        this.actionHandlers.put("toggleTaskStatus", ToggleTaskStatusAction.class);
        this.actionHandlers.put("copyTasks", CopyTasksAction.class);
        this.actionHandlers.put("reorderTasks", ReorderTasksAction.class);
        this.actionHandlers.put("addTask", AddTaskAction.class);
        this.actionHandlers.put("editTask", EditTaskAction.class);
        this.actionHandlers.put("removeTask", RemoveTaskAction.class);
        this.actionHandlers.put("markAllIncomplete", MarkAllIncompleteAction.class);
        this.actionHandlers.put("toggleTaskLock", ToggleTaskLockAction.class);
        this.actionHandlers.put("changeTaskPriority", ChangeTaskPriorityAction.class);
        this.actionHandlers.put("sortByAssignee", SortByAssigneeAction.class);
        this.actionHandlers.put("sortByPriority", SortByPriorityAction.class);
        this.actionHandlers.put("sortByName", SortByNameAction.class);
        this.actionHandlers.put("sortByDate", SortByDateAction.class);
        this.actionHandlers.put("sortByCompleted", SortByCompletedAction.class);
        this.actionHandlers.put("sortByNone", SortByNoneAction.class);
        this.actionHandlers.put("reassignTask", ReassignTaskAction.class);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Action action;
        String actionName = request.getParameter("action");
        Class<? extends Action> actionClass = this.actionHandlers.get(actionName);
        if (actionClass == null) {
            this.sendResponse(response, 400, this.error("Action must be specified"));
            return;
        }
        try {
            Constructor<? extends Action> ctor = actionClass.getConstructor(TaskListServlet.class, HttpServletRequest.class, HttpServletResponse.class);
            action = ctor.newInstance(new Object[]{this, request, response});
        }
        catch (Exception e) {
            log.error("Failed to instantiate action handler", (Throwable)e);
            this.sendResponse(response, 500, this.error("Failed to instantiate action handler"));
            return;
        }
        if (action.isValid()) {
            action.execute();
        }
    }

    private String message(String message) {
        return "<div class=\"dtl-message\"><div>" + message + "</div></div>";
    }

    private String error(String message) {
        return this.errors(Collections.singletonList(message));
    }

    private String errors(List<String> errors) {
        StringBuilder sb = new StringBuilder("<div class=\"dtl-error\">");
        for (String error : errors) {
            sb.append("<div>");
            sb.append(error);
            sb.append("</div>");
        }
        sb.append("</div>");
        return sb.toString();
    }

    private void sendResponse(HttpServletResponse response, int responseCode, String message) {
        this.sendResponse(response, responseCode, message, "text/xml");
    }

    private void sendResponse(HttpServletResponse response, int responseCode, String message, String contentType) {
        response.setStatus(responseCode);
        response.setContentType(contentType);
        try {
            if (!StringUtils.isEmpty((CharSequence)message)) {
                response.getWriter().write(message);
            }
        }
        catch (IOException e) {
            throw new RuntimeException("Could not write error message", e);
        }
    }

    private ContentEntityObject getEntity(long entityId) {
        return this.contentEntityManager.getById(entityId);
    }

    private TaskList getTaskList(ContentEntityObject contentObject, String listId) {
        return this.defaultTaskListManager.getTaskList(contentObject, new TaskListId(listId));
    }

    private abstract class TaskListEditingAction
    extends Action {
        public TaskListEditingAction(HttpServletRequest request, HttpServletResponse response) {
            super(request, response);
            if (!this.hasEditPermission()) {
                TaskListServlet.this.sendResponse(response, 401, TaskListServlet.this.error(this.getText("edit.not.permitted.description")));
                this.invalid();
            }
        }
    }

    private abstract class TaskEditingAction
    extends TaskAction {
        public TaskEditingAction(HttpServletRequest request, HttpServletResponse response) {
            super(request, response);
            if (!this.hasEditPermission()) {
                TaskListServlet.this.sendResponse(response, 401, TaskListServlet.this.error(this.getText("edit.not.permitted.description")));
                this.invalid();
            }
        }
    }

    private abstract class TaskAction
    extends Action {
        private Task task;

        public TaskAction(HttpServletRequest request, HttpServletResponse response) {
            super(request, response);
            String taskId = request.getParameter("taskId");
            if (taskId == null) {
                TaskListServlet.this.sendResponse(response, 400, TaskListServlet.this.error(this.getText("invalid.or.missing.task")));
                this.invalid();
                return;
            }
            this.task = this.getTaskList().getTask(taskId);
            if (this.task == null) {
                TaskListServlet.this.sendResponse(response, 400, TaskListServlet.this.error(this.getText("invalid.or.missing.task")));
                this.invalid();
            }
        }

        public Task getTask() {
            return this.task;
        }
    }

    private abstract class Action {
        final HttpServletRequest request;
        final HttpServletResponse response;
        private NameRenderer nameRenderer;
        private DateRenderer dateRenderer;
        private Locale userLocale = null;
        private ContentEntityObject contentObject;
        private TaskList list;
        private boolean valid = true;

        public Action(HttpServletRequest request, HttpServletResponse response) {
            this.request = request;
            this.response = response;
            try {
                long entityId = Long.valueOf(request.getParameter("entityId"));
                this.contentObject = TaskListServlet.this.getEntity(entityId);
                if (this.contentObject == null) {
                    TaskListServlet.this.sendResponse(response, 400, TaskListServlet.this.error("Unknown content ID."));
                    this.invalid();
                    return;
                }
            }
            catch (NumberFormatException e) {
                TaskListServlet.this.sendResponse(response, 400, TaskListServlet.this.error("Content ID is not a long."));
                this.invalid();
                return;
            }
            String listId = request.getParameter("listId");
            if (listId == null) {
                TaskListServlet.this.sendResponse(response, 400, TaskListServlet.this.error("Task list ID is not present."));
                this.invalid();
                return;
            }
            this.list = TaskListServlet.this.getTaskList(this.contentObject, listId);
            if (this.list == null) {
                TaskListServlet.this.sendResponse(response, 400, TaskListServlet.this.error(this.getText("invalid.or.missing.tasklist")));
                this.invalid();
            }
        }

        public boolean hasEditPermission() {
            return TaskListServlet.this.permissionHelper.canEdit(this.getRemoteUser(), (Object)this.getContentObject());
        }

        abstract void execute() throws IOException;

        public final boolean isValid() {
            return this.valid;
        }

        protected final void invalid() {
            this.valid = false;
        }

        public ContentEntityObject getContentObject() {
            return this.contentObject;
        }

        public TaskList getTaskList() {
            return this.list;
        }

        public User getRemoteUser() {
            User remoteUser = null;
            if (this.request.getRemoteUser() != null) {
                remoteUser = this.getUser(this.request.getRemoteUser());
            }
            return remoteUser;
        }

        public User getUser(String username) {
            return TaskListServlet.this.userAccessor.getUserByName(username);
        }

        public Locale getLocale() {
            if (this.userLocale == null) {
                this.userLocale = TaskListServlet.this.localeManager.getLocale(this.getRemoteUser());
            }
            return this.userLocale;
        }

        public I18NBean getI18n() {
            return TaskListServlet.this.i18NBeanFactory.getI18NBean(this.getLocale());
        }

        public String getText(String key) {
            return this.getI18n().getText(key);
        }

        public String getText(String key, String[] args) {
            return this.getI18n().getText(key, (Object[])args);
        }

        public NameRenderer getNameRenderer() {
            if (this.nameRenderer == null) {
                this.nameRenderer = new NameRenderer(TaskListServlet.this.wikiStyleRenderer, this.getContentObject().toPageContext());
            }
            return this.nameRenderer;
        }

        public DateRenderer getDateRenderer() {
            if (this.dateRenderer == null) {
                this.dateRenderer = new DateRenderer(TaskListServlet.this.userAccessor, TaskListServlet.this.formatSettingsManager, TaskListServlet.this.localeManager);
            }
            return this.dateRenderer;
        }

        protected Map<String, Object> createVelocityContext() {
            HashMap<String, Object> context = new HashMap<String, Object>();
            context.put("action", TaskListServlet.this.i18NBeanFactory.getI18NBean());
            context.put("content", this.getContentObject());
            context.put("tasklist", this.getTaskList());
            context.put("nameRenderer", this.getNameRenderer());
            context.put("dateRenderer", this.getDateRenderer());
            context.put("remoteUser", this.getRemoteUser());
            context.put("permissionHelper", TaskListServlet.this.permissionHelper);
            context.put("webResourceManager", TaskListServlet.this.webResourceManager);
            context.put("htmlUtil", new HtmlUtil());
            context.put("escapeUtils", new StringEscapeUtils());
            context.put("random", new Random());
            return context;
        }
    }

    private class ReorderTasksAction
    extends TaskEditingAction {
        private TaskList toList;
        private int index;

        public ReorderTasksAction(HttpServletRequest request, HttpServletResponse response) {
            super(request, response);
            String toListId = request.getParameter("toListId");
            if (toListId == null) {
                TaskListServlet.this.sendResponse(response, 400, TaskListServlet.this.error("To list id is not present"));
                this.invalid();
                return;
            }
            this.toList = TaskListServlet.this.getTaskList(this.getDestinationContentEntity(), toListId);
            if (this.toList == null) {
                TaskListServlet.this.sendResponse(response, 400, TaskListServlet.this.error("To task list not found"));
                this.invalid();
                return;
            }
            if (request.getParameter("index") == null) {
                TaskListServlet.this.sendResponse(response, 400, TaskListServlet.this.error("To list id is not present"));
                this.invalid();
                return;
            }
            try {
                this.index = Integer.valueOf(request.getParameter("index"));
            }
            catch (NumberFormatException e) {
                TaskListServlet.this.sendResponse(response, 400, TaskListServlet.this.error("Index is not a number"));
                this.invalid();
            }
        }

        protected ContentEntityObject getDestinationContentEntity() {
            long destinationContentId;
            String destinationContentIdString = this.request.getParameter("destinationEntityId");
            if (StringUtils.isNotBlank((CharSequence)destinationContentIdString) && StringUtils.isNumeric((CharSequence)destinationContentIdString) && (destinationContentId = Long.parseLong(destinationContentIdString)) != this.getContentObject().getId()) {
                return TaskListServlet.this.getEntity(destinationContentId);
            }
            return this.getContentObject();
        }

        public TaskList getToList() {
            return this.toList;
        }

        public int getIndex() {
            return this.index;
        }

        @Override
        public void execute() {
            TaskList fromList = this.getTaskList();
            if (fromList.getId().equals(this.toList.getId()) && this.getContentObject().getId() == this.getDestinationContentEntity().getId()) {
                this.toList.setTaskIndex(this.getTask().getId(), this.index);
                TaskListServlet.this.defaultTaskListManager.saveTaskList(this.getContentObject(), this.toList, this.getText("task.moved.in.list", new String[]{this.toList.getName()}));
            } else {
                fromList.removeTask(this.getTask().getId());
                TaskListServlet.this.defaultTaskListManager.saveTaskList(this.getContentObject(), fromList, this.getText("task.removed.from.list", new String[]{fromList.getName()}));
                this.toList.insertTask(this.index, this.getTask());
                TaskListServlet.this.defaultTaskListManager.saveTaskList(this.getDestinationContentEntity(), this.toList, this.getText("task.added.to.list", new String[]{this.toList.getName()}));
            }
            Map<String, Object> context = this.createVelocityContext();
            context.put("task", this.getTask());
            context.put("editable", Boolean.TRUE);
            TaskListServlet.this.sendResponse(this.response, 200, TaskListServlet.this.velocityHelperService.getRenderedTemplate("templates/extra/dynamictasklist2/task-moved.vm", context));
        }
    }

    private class CopyTasksAction
    extends ReorderTasksAction {
        public CopyTasksAction(HttpServletRequest request, HttpServletResponse response) {
            super(request, response);
        }

        private String getIdOfTaskAfterSourceTask() {
            Task t;
            int sourceTaskIndex;
            Task sourceTask = this.getTask();
            TaskList sourceList = this.getTaskList();
            int sourceListSize = sourceList.getTasks().size();
            for (sourceTaskIndex = 0; sourceTaskIndex < sourceListSize && (t = sourceList.getTasks().get(sourceTaskIndex)) != sourceTask; ++sourceTaskIndex) {
            }
            return sourceTaskIndex < sourceListSize - 1 ? sourceList.getTasks().get(sourceTaskIndex + 1).getId() : null;
        }

        @Override
        public void execute() {
            Task task = this.getTask();
            Task sourceTask = new Task(task);
            TaskList toList = this.getToList();
            toList.insertTask(this.getIndex(), task);
            TaskListServlet.this.defaultTaskListManager.saveTaskList(this.getDestinationContentEntity(), toList, this.getText("task.added.to.list", new String[]{toList.getName()}));
            Map<String, Object> context = this.createVelocityContext();
            context.put("task", sourceTask);
            context.put("editable", Boolean.TRUE);
            context.put("idOfTaskAfterSourceTask", this.getIdOfTaskAfterSourceTask());
            context.put("newTaskId", task.getId());
            TaskListServlet.this.sendResponse(this.response, 200, TaskListServlet.this.velocityHelperService.getRenderedTemplate("templates/extra/dynamictasklist2/task-copied.vm", context));
        }
    }

    private class ToggleTaskStatusAction
    extends TaskEditingAction {
        public ToggleTaskStatusAction(HttpServletRequest request, HttpServletResponse response) {
            super(request, response);
        }

        @Override
        public void execute() {
            long completedDate = 0L;
            String assignee = this.getRemoteUser() != null ? this.getRemoteUser().getName() : "";
            boolean completed = this.getTask().isCompleted();
            if (completed) {
                completed = false;
            } else {
                completed = true;
                completedDate = Calendar.getInstance().getTimeInMillis();
                TaskListConfig config = this.getTaskList().getConfig();
                if (config.getAutoLockOnComplete() && config.getEnableLocking()) {
                    this.getTask().setLocked(true);
                }
            }
            this.getTask().setCompleted(completed);
            this.getTask().setCompletedDate(completedDate);
            this.getTask().setAssignee(assignee);
            TaskListServlet.this.defaultTaskListManager.saveTaskList(this.getContentObject(), this.getTaskList(), this.getText("task.status.changed.in.list", new String[]{this.getTaskList().getName()}));
            Map<String, Object> context = this.createVelocityContext();
            context.put("task", this.getTask());
            TaskListServlet.this.sendResponse(this.response, 200, TaskListServlet.this.velocityHelperService.getRenderedTemplate("templates/extra/dynamictasklist2/task-completed.vm", context));
        }
    }

    private class AddTaskAction
    extends TaskListEditingAction {
        public AddTaskAction(HttpServletRequest request, HttpServletResponse response) {
            super(request, response);
        }

        private String getNewTask() {
            return this.request.getParameter("newTask");
        }

        @Override
        void execute() {
            TaskList list = this.getTaskList();
            if (StringUtils.isEmpty((CharSequence)this.getNewTask())) {
                TaskListServlet.this.sendResponse(this.response, 400, TaskListServlet.this.error(this.getText("task.name.is.required")));
                return;
            }
            Task task = new Task(this.getNewTask(), list);
            task.setCreatedDate(Calendar.getInstance().getTimeInMillis());
            User user = this.getRemoteUser();
            String assignee = user != null ? user.getName() : "";
            task.setAssignee(assignee);
            list.addTask(task);
            TaskListServlet.this.defaultTaskListManager.saveTaskList(this.getContentObject(), list, this.getText("task.added.to.list", new String[]{list.getName()}));
            Map<String, Object> context = this.createVelocityContext();
            context.put("task", task);
            context.put("editable", Boolean.TRUE);
            context.put("adgEnabled", TaskListUtil.isAdgEnabled());
            TaskListServlet.this.sendResponse(this.response, 200, TaskListServlet.this.velocityHelperService.getRenderedTemplate("templates/extra/dynamictasklist2/task-added.vm", context));
        }
    }

    private class EditTaskAction
    extends TaskEditingAction {
        public EditTaskAction(HttpServletRequest request, HttpServletResponse response) {
            super(request, response);
        }

        private String getNewTask() {
            return this.request.getParameter("newTask");
        }

        @Override
        void execute() {
            TaskList list = this.getTaskList();
            Task task = this.getTask();
            if (StringUtils.isEmpty((CharSequence)this.getNewTask())) {
                TaskListServlet.this.sendResponse(this.response, 400, TaskListServlet.this.error(this.getText("task.name.is.required")));
                return;
            }
            if (task.getName().equals(this.getNewTask())) {
                TaskListServlet.this.sendResponse(this.response, 200, TaskListServlet.this.message("No change"));
                return;
            }
            task.setName(this.getNewTask(), list);
            list.sort();
            TaskListServlet.this.defaultTaskListManager.saveTaskList(this.getContentObject(), list, this.getText("task.edited.in.list", new String[]{list.getName()}));
            Map<String, Object> context = this.createVelocityContext();
            context.put("task", task);
            TaskListServlet.this.sendResponse(this.response, 200, TaskListServlet.this.velocityHelperService.getRenderedTemplate("templates/extra/dynamictasklist2/task-edited.vm", context));
        }
    }

    private class RemoveTaskAction
    extends TaskEditingAction {
        public RemoveTaskAction(HttpServletRequest request, HttpServletResponse response) {
            super(request, response);
        }

        @Override
        void execute() {
            TaskList list = this.getTaskList();
            list.removeTask(this.getTask().getId());
            TaskListServlet.this.defaultTaskListManager.saveTaskList(this.getContentObject(), list, this.getText("task.removed.from.list", new String[]{list.getName()}));
            TaskListServlet.this.sendResponse(this.response, 200, TaskListServlet.this.message("Updated task"));
        }
    }

    private class MarkAllIncompleteAction
    extends TaskListEditingAction {
        public MarkAllIncompleteAction(HttpServletRequest request, HttpServletResponse response) {
            super(request, response);
        }

        @Override
        void execute() {
            TaskList list = this.getTaskList();
            for (Task task : list.getTasks()) {
                task.setCompleted(false);
                task.setCompletedDate(0L);
            }
            TaskListServlet.this.defaultTaskListManager.saveTaskList(this.getContentObject(), list, this.getText("marked.all.tasks.incomplete.in.list", new String[]{list.getName()}));
            TaskListServlet.this.sendResponse(this.response, 200, TaskListServlet.this.message("All tasks unchecked"));
        }
    }

    private class ToggleTaskLockAction
    extends TaskEditingAction {
        public ToggleTaskLockAction(HttpServletRequest request, HttpServletResponse response) {
            super(request, response);
        }

        @Override
        void execute() {
            this.getTask().setLocked(!this.getTask().isLocked());
            TaskListServlet.this.defaultTaskListManager.saveTaskList(this.getContentObject(), this.getTaskList(), this.getText("task.lock.change.in.list", new String[]{this.getTaskList().getName()}));
            TaskListServlet.this.sendResponse(this.response, 200, TaskListServlet.this.message("Task lock changed"));
        }
    }

    private class ChangeTaskPriorityAction
    extends TaskEditingAction {
        private Task.Priority priority;

        public ChangeTaskPriorityAction(HttpServletRequest request, HttpServletResponse response) {
            super(request, response);
            if (StringUtils.isEmpty((CharSequence)request.getParameter("priority"))) {
                TaskListServlet.this.sendResponse(response, 400, TaskListServlet.this.error(this.getText("task.priority.is.required")));
                this.invalid();
                return;
            }
            try {
                this.priority = Task.Priority.from(request.getParameter("priority"));
            }
            catch (IllegalArgumentException e) {
                TaskListServlet.this.sendResponse(response, 400, TaskListServlet.this.error(this.getText("task.priority.is.invalid")));
                this.invalid();
            }
        }

        @Override
        void execute() {
            this.getTask().setPriority(this.priority);
            this.getTaskList().sort();
            TaskListServlet.this.defaultTaskListManager.saveTaskList(this.getContentObject(), this.getTaskList(), this.getText("task.priority.changed.in.list", new String[]{this.getTaskList().getName()}));
            TaskListServlet.this.sendResponse(this.response, 200, TaskListServlet.this.message("Task priority changed"));
        }
    }

    private class SortByPriorityAction
    extends TaskListEditingAction {
        public SortByPriorityAction(HttpServletRequest request, HttpServletResponse response) {
            super(request, response);
        }

        @Override
        void execute() {
            TaskList list = this.getTaskList();
            list.sortByPriority();
            TaskListServlet.this.defaultTaskListManager.saveTaskList(this.getContentObject(), list, this.getText("task.list.sorted", new String[]{list.getName()}));
            TaskListServlet.this.sendResponse(this.response, 200, TaskListServlet.this.message("Task list sorted"));
        }
    }

    private class SortByNameAction
    extends TaskListEditingAction {
        public SortByNameAction(HttpServletRequest request, HttpServletResponse response) {
            super(request, response);
        }

        @Override
        void execute() {
            TaskList list = this.getTaskList();
            list.sortByName();
            TaskListServlet.this.defaultTaskListManager.saveTaskList(this.getContentObject(), list, this.getText("task.list.sorted", new String[]{list.getName()}));
            TaskListServlet.this.sendResponse(this.response, 200, TaskListServlet.this.message("Task list sorted"));
        }
    }

    private class SortByAssigneeAction
    extends TaskListEditingAction {
        public SortByAssigneeAction(HttpServletRequest request, HttpServletResponse response) {
            super(request, response);
        }

        @Override
        void execute() {
            TaskList list = this.getTaskList();
            list.sortByAssignee();
            TaskListServlet.this.defaultTaskListManager.saveTaskList(this.getContentObject(), list, this.getText("task.list.sorted", new String[]{list.getName()}));
            TaskListServlet.this.sendResponse(this.response, 200, TaskListServlet.this.message("Task list sorted"));
        }
    }

    private class SortByDateAction
    extends TaskListEditingAction {
        public SortByDateAction(HttpServletRequest request, HttpServletResponse response) {
            super(request, response);
        }

        @Override
        void execute() {
            TaskList list = this.getTaskList();
            list.sortByDate();
            TaskListServlet.this.defaultTaskListManager.saveTaskList(this.getContentObject(), list, this.getText("task.list.sorted", new String[]{list.getName()}));
            TaskListServlet.this.sendResponse(this.response, 200, TaskListServlet.this.message("Task list sorted"));
        }
    }

    private class SortByCompletedAction
    extends TaskListEditingAction {
        public SortByCompletedAction(HttpServletRequest request, HttpServletResponse response) {
            super(request, response);
        }

        @Override
        void execute() {
            TaskList list = this.getTaskList();
            list.sortByComplete();
            TaskListServlet.this.defaultTaskListManager.saveTaskList(this.getContentObject(), list, this.getText("task.list.sorted", new String[]{list.getName()}));
            TaskListServlet.this.sendResponse(this.response, 200, TaskListServlet.this.message("Task list sorted"));
        }
    }

    private class SortByNoneAction
    extends TaskListEditingAction {
        public SortByNoneAction(HttpServletRequest request, HttpServletResponse response) {
            super(request, response);
        }

        @Override
        void execute() {
            TaskList list = this.getTaskList();
            list.getConfig().setSort(Sort.NONE);
            TaskListServlet.this.defaultTaskListManager.saveTaskList(this.getContentObject(), list, this.getText("task.list.sorted", new String[]{list.getName()}));
            TaskListServlet.this.sendResponse(this.response, 200, TaskListServlet.this.message("Task list sorted"));
        }
    }

    private class ReassignTaskAction
    extends TaskEditingAction {
        private String assignees;
        private List<String> errors;

        public ReassignTaskAction(HttpServletRequest request, HttpServletResponse response) {
            super(request, response);
            this.errors = new ArrayList<String>();
            this.assignees = request.getParameter("assignee");
            this.assignees = (this.assignees != null ? this.assignees : "").replaceAll("\\||(?<!\\\\)\\\\\\|", "");
            if (!this.isValidAssigneeList()) {
                TaskListServlet.this.sendResponse(response, 400, TaskListServlet.this.errors(this.errors));
                this.invalid();
            }
        }

        @Override
        void execute() {
            this.getTask().setAssignee(this.assignees);
            this.getTaskList().sort();
            TaskListServlet.this.defaultTaskListManager.saveTaskList(this.getContentObject(), this.getTaskList(), this.getText("task.reassigned.in.list", new String[]{this.getTaskList().getName()}));
            TaskListServlet.this.sendResponse(this.response, 200, TaskListServlet.this.message("Task reassigned"));
        }

        private boolean isValidAssigneeList() {
            String[] assigneeList;
            if (StringUtils.isEmpty((CharSequence)this.assignees)) {
                return true;
            }
            boolean valid = true;
            for (String anAssigneeList : assigneeList = this.assignees.split(",")) {
                String assignee = anAssigneeList.trim();
                if (TaskListServlet.this.userAccessor.getUserByName(assignee) != null || TaskListServlet.this.userAccessor.getGroup(assignee) != null) continue;
                this.errors.add(this.getText("invalid.assignee", new String[]{assignee}));
                valid = false;
            }
            return valid;
        }
    }
}

