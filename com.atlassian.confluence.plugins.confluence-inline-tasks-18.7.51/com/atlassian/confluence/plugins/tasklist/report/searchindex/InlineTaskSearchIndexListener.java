/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.event.events.admin.ReIndexRequestEvent
 *  com.atlassian.confluence.event.events.content.blogpost.BlogPostRestoreEvent
 *  com.atlassian.confluence.event.events.content.blogpost.BlogPostTrashedEvent
 *  com.atlassian.confluence.event.events.content.page.PageRestoreEvent
 *  com.atlassian.confluence.event.events.content.page.PageTrashedEvent
 *  com.atlassian.confluence.event.events.label.LabelAddEvent
 *  com.atlassian.confluence.event.events.label.LabelRemoveEvent
 *  com.atlassian.confluence.event.events.security.ContentPermissionEvent
 *  com.atlassian.confluence.event.events.user.UserRemoveCompletedEvent
 *  com.atlassian.confluence.labels.Labelable
 *  com.atlassian.crowd.event.user.UserRenamedEvent
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.tasklist.report.searchindex;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.event.events.admin.ReIndexRequestEvent;
import com.atlassian.confluence.event.events.content.blogpost.BlogPostRestoreEvent;
import com.atlassian.confluence.event.events.content.blogpost.BlogPostTrashedEvent;
import com.atlassian.confluence.event.events.content.page.PageRestoreEvent;
import com.atlassian.confluence.event.events.content.page.PageTrashedEvent;
import com.atlassian.confluence.event.events.label.LabelAddEvent;
import com.atlassian.confluence.event.events.label.LabelRemoveEvent;
import com.atlassian.confluence.event.events.security.ContentPermissionEvent;
import com.atlassian.confluence.event.events.user.UserRemoveCompletedEvent;
import com.atlassian.confluence.labels.Labelable;
import com.atlassian.confluence.plugins.tasklist.Task;
import com.atlassian.confluence.plugins.tasklist.ao.dao.InlineTaskDao;
import com.atlassian.confluence.plugins.tasklist.event.AbstractConfluenceTaskEvent;
import com.atlassian.confluence.plugins.tasklist.event.ConfluenceTaskV2CreateEvent;
import com.atlassian.confluence.plugins.tasklist.event.ConfluenceTaskV2RemoveEvent;
import com.atlassian.confluence.plugins.tasklist.event.ConfluenceTaskV2UpdateEvent;
import com.atlassian.confluence.plugins.tasklist.report.searchindex.indexmanagement.indexqueue.IndexTaskRegistrator;
import com.atlassian.crowd.event.user.UserRenamedEvent;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class InlineTaskSearchIndexListener {
    private static final Logger log = LoggerFactory.getLogger(InlineTaskSearchIndexListener.class);
    private final EventPublisher eventPublisher;
    private final IndexTaskRegistrator indexTaskRegistrator;
    private final InlineTaskDao inlineTaskDao;

    public InlineTaskSearchIndexListener(EventPublisher eventPublisher, IndexTaskRegistrator indexTaskRegistrator, InlineTaskDao inlineTaskDao) {
        this.eventPublisher = eventPublisher;
        this.indexTaskRegistrator = indexTaskRegistrator;
        this.inlineTaskDao = inlineTaskDao;
    }

    @PostConstruct
    public void setup() {
        this.eventPublisher.register((Object)this);
    }

    @PreDestroy
    public void teardown() {
        this.eventPublisher.unregister((Object)this);
    }

    @EventListener
    public void onReIndexRequestEvent(ReIndexRequestEvent reIndexRequestEvent) {
        log.info("ReIndexRequestEvent received, so all inline tasks will be re-indexed.");
        this.indexTaskRegistrator.requestToReindexAllInlineTasks();
    }

    @EventListener
    public void onPageTrash(PageTrashedEvent pageTrashedEvent) {
        this.indexTaskRegistrator.requestToRemoveAllTasksOnThePage(pageTrashedEvent.getPage().getId());
    }

    @EventListener
    public void onBlogPostTrash(BlogPostTrashedEvent event) {
        this.indexTaskRegistrator.requestToRemoveAllTasksOnThePage(event.getBlogPost().getId());
    }

    @EventListener
    public void onPageRestore(PageRestoreEvent event) {
        this.indexTaskRegistrator.requestToReindexAllInlineTasksOnPage(event.getPage().getId());
    }

    @EventListener
    public void onBlogPostRestore(BlogPostRestoreEvent event) {
        this.indexTaskRegistrator.requestToReindexAllInlineTasksOnPage(event.getBlogPost().getId());
    }

    @EventListener
    public void onTaskRemove(ConfluenceTaskV2RemoveEvent event) {
        log.debug("Received ConfluenceTaskV2RemoveEvent event with the task {}", (Object)event.getTask());
        Long globalId = this.getGlobalIdFromEvent(event);
        if (globalId != null) {
            this.indexTaskRegistrator.requestToRemoveTask(globalId);
        } else {
            log.warn("Unable to find the task in the database: {}", (Object)event.getTask());
        }
    }

    @EventListener
    public void onTaskCreate(ConfluenceTaskV2CreateEvent event) {
        log.debug("Received ConfluenceTaskV2CreateEvent event with the task {}", (Object)event.getTask());
        Long taskGlobalId = this.getGlobalIdFromEvent(event);
        if (taskGlobalId != null) {
            this.indexTaskRegistrator.requestToReindexInlineTask(taskGlobalId);
        } else {
            log.warn("Unable to find the task in the database: {}", (Object)event.getTask());
        }
    }

    @EventListener
    public void onTaskUpdate(ConfluenceTaskV2UpdateEvent event) {
        log.debug("Received ConfluenceTaskV2UpdateEvent event with the task {}", (Object)event.getTask());
        Long taskGlobalId = this.getGlobalIdFromEvent(event);
        if (taskGlobalId != null) {
            this.indexTaskRegistrator.requestToReindexTask(taskGlobalId);
        } else {
            log.warn("Unable to find the task in the database: {}", (Object)event.getTask());
        }
    }

    private Long getGlobalIdFromEvent(AbstractConfluenceTaskEvent event) {
        long globalId = event.getTask().getGlobalId();
        if (globalId != 0L) {
            return globalId;
        }
        Task task = this.inlineTaskDao.find(event.getTask().getContentId(), event.getTask().getId());
        return task != null ? Long.valueOf(task.getGlobalId()) : null;
    }

    @EventListener
    public void onUserRename(UserRenamedEvent event) {
    }

    @EventListener
    public void onUserRemove(UserRemoveCompletedEvent event) {
    }

    @EventListener
    public void onContentPermissionsChange(ContentPermissionEvent contentPermissionEvent) {
        log.debug("Received ContentPermissionEvent event with content id {}", (Object)contentPermissionEvent.getContent().getContentId());
        this.indexTaskRegistrator.requestToReindexAllInlineTasksOnPageIncludingAllDescendants(contentPermissionEvent.getContent().getId());
    }

    @EventListener
    public void onLabelAddedToContent(LabelAddEvent event) {
        this.reindexLabelledObject(event.getLabelled());
    }

    @EventListener
    public void onLabelRemovedFromContent(LabelRemoveEvent event) {
        this.reindexLabelledObject(event.getLabelled());
    }

    private void reindexLabelledObject(Labelable labelled) {
        if (!(labelled instanceof ContentEntityObject)) {
            return;
        }
        ContentEntityObject ceo = (ContentEntityObject)labelled;
        this.indexTaskRegistrator.requestToReindexAllInlineTasksOnPage(ceo.getId());
    }
}

