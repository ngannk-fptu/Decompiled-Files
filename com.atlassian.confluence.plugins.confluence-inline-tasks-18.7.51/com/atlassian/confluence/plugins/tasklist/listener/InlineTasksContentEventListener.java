/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.content.render.xhtml.DefaultConversionContext
 *  com.atlassian.confluence.content.render.xhtml.model.inlinetask.InlineTaskListItem
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.event.events.content.blogpost.BlogPostCreateEvent
 *  com.atlassian.confluence.event.events.content.blogpost.BlogPostRemoveEvent
 *  com.atlassian.confluence.event.events.content.blogpost.BlogPostUpdateEvent
 *  com.atlassian.confluence.event.events.content.page.PageCreateEvent
 *  com.atlassian.confluence.event.events.content.page.PageRemoveEvent
 *  com.atlassian.confluence.event.events.content.page.PageUpdateEvent
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.pages.BlogPost
 *  com.atlassian.confluence.pages.PageUpdateTrigger
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.security.access.AccessStatus
 *  com.atlassian.confluence.security.access.ConfluenceAccessManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.user.User
 *  com.google.common.collect.ArrayListMultimap
 *  com.google.common.collect.ListMultimap
 *  com.google.common.collect.Multimaps
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.tasklist.listener;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.content.render.xhtml.model.inlinetask.InlineTaskListItem;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.event.events.content.blogpost.BlogPostCreateEvent;
import com.atlassian.confluence.event.events.content.blogpost.BlogPostRemoveEvent;
import com.atlassian.confluence.event.events.content.blogpost.BlogPostUpdateEvent;
import com.atlassian.confluence.event.events.content.page.PageCreateEvent;
import com.atlassian.confluence.event.events.content.page.PageRemoveEvent;
import com.atlassian.confluence.event.events.content.page.PageUpdateEvent;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.pages.PageUpdateTrigger;
import com.atlassian.confluence.plugins.tasklist.Task;
import com.atlassian.confluence.plugins.tasklist.TaskModfication;
import com.atlassian.confluence.plugins.tasklist.TaskStatus;
import com.atlassian.confluence.plugins.tasklist.event.AbstractConfluenceTaskEvent;
import com.atlassian.confluence.plugins.tasklist.event.ConfluenceTaskCreateEvent;
import com.atlassian.confluence.plugins.tasklist.event.ConfluenceTaskRemoveEvent;
import com.atlassian.confluence.plugins.tasklist.event.ConfluenceTaskUpdateEvent;
import com.atlassian.confluence.plugins.tasklist.event.ConfluenceTaskV2CreateEvent;
import com.atlassian.confluence.plugins.tasklist.event.ConfluenceTaskV2RemoveEvent;
import com.atlassian.confluence.plugins.tasklist.event.ConfluenceTaskV2UpdateEvent;
import com.atlassian.confluence.plugins.tasklist.event.SendTaskEmailEvent;
import com.atlassian.confluence.plugins.tasklist.service.InlineTaskService;
import com.atlassian.confluence.plugins.tasklist.transformer.InlineTaskFinder;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.security.access.AccessStatus;
import com.atlassian.confluence.security.access.ConfluenceAccessManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.renderer.RenderContext;
import com.atlassian.user.User;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimaps;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class InlineTasksContentEventListener {
    private final EventPublisher eventPublisher;
    private final InlineTaskFinder finder;
    private final PermissionManager permissionManager;
    private final UserAccessor userAccessor;
    private final ConfluenceAccessManager confluenceAccessManager;
    private final InlineTaskService inlineTaskService;

    @Autowired
    public InlineTasksContentEventListener(EventPublisher eventPublisher, InlineTaskFinder finder, PermissionManager permissionManager, UserAccessor userAccessor, ConfluenceAccessManager confluenceAccessManager, InlineTaskService inlineTaskService) {
        this.eventPublisher = eventPublisher;
        this.finder = finder;
        this.permissionManager = permissionManager;
        this.userAccessor = userAccessor;
        this.confluenceAccessManager = confluenceAccessManager;
        this.inlineTaskService = inlineTaskService;
    }

    @PostConstruct
    public final void setup() {
        this.eventPublisher.register((Object)this);
    }

    @PreDestroy
    public final void teardown() {
        this.eventPublisher.unregister((Object)this);
    }

    void processContent(ConfluenceUser user, ContentEntityObject content, ContentEntityObject originalContent, PageUpdateTrigger updateTrigger, boolean suppressNotifications) {
        if (content == null && originalContent == null) {
            throw new IllegalArgumentException("An event was called with an null ContentEntityObject.");
        }
        long contentId = content != null ? content.getId() : originalContent.getId();
        Map tasks = content != null ? this.finder.findTasksInContent(contentId, content.getBodyAsString(), (ConversionContext)new DefaultConversionContext((RenderContext)content.toPageContext())) : Collections.emptyMap();
        Map previousTasks = originalContent != null ? this.finder.findTasksInContent(contentId, originalContent.getBodyAsString(), (ConversionContext)new DefaultConversionContext((RenderContext)originalContent.toPageContext())) : Collections.emptyMap();
        ArrayList<InlineTaskListItem> newInlineTasks = new ArrayList<InlineTaskListItem>();
        ArrayList<InlineTaskListItem> updatedInlineTasks = new ArrayList<InlineTaskListItem>();
        ArrayList<InlineTaskListItem> previousInlineTasks = new ArrayList<InlineTaskListItem>();
        for (InlineTaskListItem inlineTaskItem : tasks.values()) {
            long taskId = Long.parseLong(inlineTaskItem.getId());
            InlineTaskListItem previousTask = (InlineTaskListItem)previousTasks.get(taskId);
            if (previousTask == null) {
                newInlineTasks.add(inlineTaskItem);
                continue;
            }
            previousTasks.remove(taskId);
            if (inlineTaskItem.equals((Object)previousTask)) continue;
            previousInlineTasks.add(previousTask);
            updatedInlineTasks.add(inlineTaskItem);
        }
        int taskCount = Math.max(tasks.size(), previousTasks.size());
        ArrayListMultimap allEvents = ArrayListMultimap.create((int)taskCount, (int)taskCount);
        ArrayListMultimap allTaskV2Events = ArrayListMultimap.create((int)taskCount, (int)taskCount);
        String creatorName = user == null ? null : user.getName();
        Date currentDate = new Date();
        for (InlineTaskListItem newInlineTask : newInlineTasks) {
            Task task = this.finder.parseTask(newInlineTask, contentId, (ConversionContext)new DefaultConversionContext((RenderContext)content.toPageContext()));
            Task.Builder builder = new Task.Builder(task).withCreator(creatorName).withCreateDate(currentDate);
            if (newInlineTask.isCompleted()) {
                builder.withCompleteUser(creatorName).withCompleteDate(currentDate);
            }
            Task newTask = builder.build();
            Task savedTask = this.inlineTaskService.create(newTask);
            allEvents.put((Object)task.getAssignee(), (Object)new ConfluenceTaskCreateEvent(content, (User)user, savedTask));
            allTaskV2Events.put((Object)task.getAssignee(), (Object)new ConfluenceTaskV2CreateEvent(content, (User)user, savedTask));
        }
        for (int index = 0; index < updatedInlineTasks.size(); ++index) {
            InlineTaskListItem previousInlineTask = (InlineTaskListItem)previousInlineTasks.get(index);
            Task previousTask = this.finder.parseTask(previousInlineTask, contentId, (ConversionContext)new DefaultConversionContext((RenderContext)originalContent.toPageContext()));
            InlineTaskListItem updatedInlineTask = (InlineTaskListItem)updatedInlineTasks.get(index);
            Task updatedTask = this.finder.parseTask(updatedInlineTask, contentId, (ConversionContext)new DefaultConversionContext((RenderContext)content.toPageContext()));
            allEvents.put((Object)updatedTask.getAssignee(), (Object)new ConfluenceTaskUpdateEvent(content, (User)user, updatedTask, previousTask, updateTrigger));
            allTaskV2Events.put((Object)updatedTask.getAssignee(), (Object)new ConfluenceTaskV2UpdateEvent(content, (User)user, updatedTask, previousTask));
        }
        for (InlineTaskListItem deletedInlineTask : previousTasks.values()) {
            Task deletedTask = this.finder.parseTask(deletedInlineTask, contentId, (ConversionContext)new DefaultConversionContext((RenderContext)originalContent.toPageContext()));
            allEvents.put((Object)deletedTask.getAssignee(), (Object)new ConfluenceTaskRemoveEvent(originalContent, (User)user, deletedTask));
            allTaskV2Events.put((Object)deletedTask.getAssignee(), (Object)new ConfluenceTaskV2RemoveEvent(originalContent, (User)user, deletedTask));
        }
        for (AbstractConfluenceTaskEvent event : allTaskV2Events.values()) {
            this.eventPublisher.publish((Object)event);
        }
        ListMultimap allEventsFiltered = Multimaps.filterKeys((ListMultimap)allEvents, recipient -> {
            ConfluenceUser recipientUser = this.userAccessor.getUserByName(recipient);
            return this.permissionManager.hasPermission((User)recipientUser, Permission.VIEW, (Object)(content != null ? content : originalContent)) && this.isValidRecipient(recipientUser);
        });
        if (allEventsFiltered.isEmpty()) {
            return;
        }
        allEventsFiltered.values().forEach(arg_0 -> ((EventPublisher)this.eventPublisher).publish(arg_0));
        ArrayListMultimap taskModificationListMultimap = ArrayListMultimap.create((int)allEventsFiltered.size(), (int)allEventsFiltered.size());
        for (Map.Entry entry : allEventsFiltered.entries()) {
            InlineTasksContentEventListener.addModificationFromEvent((AbstractConfluenceTaskEvent)((Object)entry.getValue()), (ListMultimap<String, TaskModfication>)taskModificationListMultimap);
        }
        if (!taskModificationListMultimap.isEmpty()) {
            this.eventPublisher.publish((Object)new SendTaskEmailEvent(this, content != null ? content : originalContent, suppressNotifications && PageUpdateTrigger.LINK_REFACTORING.equals((Object)updateTrigger), (ListMultimap<String, TaskModfication>)taskModificationListMultimap));
        }
    }

    private boolean isValidRecipient(ConfluenceUser user) {
        AccessStatus accessStatus = this.confluenceAccessManager.getUserAccessStatus((User)user);
        return accessStatus.hasLicensedAccess();
    }

    private static void addModificationFromEvent(AbstractConfluenceTaskEvent event, ListMultimap<String, TaskModfication> modifications) {
        if (event instanceof ConfluenceTaskCreateEvent) {
            modifications.put((Object)event.getTask().getAssignee(), (Object)new TaskModfication(event.getTask(), TaskModfication.Operation.ASSIGNED));
        } else if (event instanceof ConfluenceTaskRemoveEvent) {
            modifications.put((Object)event.getTask().getAssignee(), (Object)new TaskModfication(event.getTask(), TaskModfication.Operation.DELETED));
        } else if (event instanceof ConfluenceTaskUpdateEvent) {
            ConfluenceTaskUpdateEvent updateEvent = (ConfluenceTaskUpdateEvent)event;
            Task t = event.getTask();
            if (updateEvent.hasAssigneeChanged()) {
                modifications.put((Object)updateEvent.getOldTask().getAssignee(), (Object)new TaskModfication(event.getTask(), TaskModfication.Operation.UNASSIGNED));
                modifications.put((Object)updateEvent.getTask().getAssignee(), (Object)new TaskModfication(event.getTask(), TaskModfication.Operation.ASSIGNED));
            } else if (updateEvent.hasStatusChanged()) {
                TaskModfication.Operation stat = t.getStatus().equals((Object)TaskStatus.CHECKED) ? TaskModfication.Operation.COMPLETE : TaskModfication.Operation.IN_COMPLETE;
                modifications.put((Object)event.getTask().getAssignee(), (Object)new TaskModfication(event.getTask(), stat));
            } else if (updateEvent.hasTitleChanged()) {
                modifications.put((Object)updateEvent.getTask().getAssignee(), (Object)new TaskModfication(event.getTask(), TaskModfication.Operation.REWORDED));
            }
        }
    }

    @EventListener
    public void pageCreated(PageCreateEvent event) {
        this.processContent(this.getEditorUser(), (ContentEntityObject)event.getPage(), null, event.getUpdateTrigger(), event.isSuppressNotifications());
    }

    @EventListener
    public void pageUpdated(PageUpdateEvent event) {
        AbstractPage originalPage = event.getOriginalPage();
        if (originalPage != null) {
            this.processContent(this.getEditorUser(), (ContentEntityObject)event.getPage(), (ContentEntityObject)originalPage, event.getUpdateTrigger(), event.isSuppressNotifications());
        }
    }

    @EventListener
    public void pageDeleted(PageRemoveEvent event) {
        this.processContent(this.getEditorUser(), null, (ContentEntityObject)event.getPage(), null, event.isSuppressNotifications());
    }

    @EventListener
    public void blogPostCreated(BlogPostCreateEvent event) {
        this.processContent(this.getEditorUser(), (ContentEntityObject)event.getBlogPost(), null, null, event.isSuppressNotifications());
    }

    @EventListener
    public void blogPostUpdated(BlogPostUpdateEvent event) {
        BlogPost originalBlog = event.getOriginalBlogPost();
        if (originalBlog != null) {
            this.processContent(this.getEditorUser(), (ContentEntityObject)event.getBlogPost(), (ContentEntityObject)originalBlog, event.getUpdateTrigger(), event.isSuppressNotifications());
        }
    }

    @EventListener
    public void blogPostDeleted(BlogPostRemoveEvent event) {
        this.processContent(this.getEditorUser(), null, (ContentEntityObject)event.getBlogPost(), null, event.isSuppressNotifications());
    }

    private ConfluenceUser getEditorUser() {
        return AuthenticatedUserThreadLocal.get();
    }
}

