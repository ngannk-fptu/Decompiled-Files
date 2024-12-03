/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.ContentQuery
 *  com.atlassian.confluence.content.CustomContentManager
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.content.render.xhtml.DefaultConversionContext
 *  com.atlassian.confluence.content.render.xhtml.model.inlinetask.InlineTaskListItem
 *  com.atlassian.confluence.core.BatchOperationManager
 *  com.atlassian.confluence.event.events.admin.AsyncImportFinishedEvent
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.dao.DataAccessException
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.tasklist.importexport;

import com.atlassian.confluence.content.ContentQuery;
import com.atlassian.confluence.content.CustomContentManager;
import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.content.render.xhtml.model.inlinetask.InlineTaskListItem;
import com.atlassian.confluence.core.BatchOperationManager;
import com.atlassian.confluence.event.events.admin.AsyncImportFinishedEvent;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.plugins.tasklist.Task;
import com.atlassian.confluence.plugins.tasklist.report.searchindex.indexmanagement.indexqueue.IndexTaskRegistrator;
import com.atlassian.confluence.plugins.tasklist.service.InlineTaskService;
import com.atlassian.confluence.plugins.tasklist.transformer.InlineTaskFinder;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.renderer.RenderContext;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

@Component
public class InlineTasksRebuildImportFinishedListener {
    private static final Logger log = LoggerFactory.getLogger(InlineTasksRebuildImportFinishedListener.class);
    private static final int pagesBatchSize = 100;
    private final InlineTaskFinder inlineTaskFinder;
    private final InlineTaskService inlineTaskService;
    private final SpaceManager spaceManager;
    private final PageManager pageManager;
    private final TransactionTemplate txTemplate;
    private final EventPublisher eventPublisher;
    private final CustomContentManager customContentManager;
    private final BatchOperationManager batchOperationManager;
    private final IndexTaskRegistrator indexTaskRegistrator;

    @Autowired
    public InlineTasksRebuildImportFinishedListener(InlineTaskFinder inlineTaskFinder, InlineTaskService inlineTaskService, SpaceManager spaceManager, PageManager pageManager, TransactionTemplate txTemplate, EventPublisher eventPublisher, CustomContentManager customContentManager, BatchOperationManager batchOperationManager, IndexTaskRegistrator indexTaskRegistrator) {
        this.inlineTaskFinder = Objects.requireNonNull(inlineTaskFinder, "null inlineTaskFinder");
        this.inlineTaskService = Objects.requireNonNull(inlineTaskService, "null inlineTaskService");
        this.spaceManager = Objects.requireNonNull(spaceManager, "null spaceManager");
        this.pageManager = Objects.requireNonNull(pageManager, "null pageManager");
        this.txTemplate = Objects.requireNonNull(txTemplate, "null txTemplate");
        this.eventPublisher = Objects.requireNonNull(eventPublisher, "null eventPublisher");
        this.customContentManager = Objects.requireNonNull(customContentManager, "null customContentManager");
        this.batchOperationManager = Objects.requireNonNull(batchOperationManager, "null batchOperationManager");
        this.indexTaskRegistrator = Objects.requireNonNull(indexTaskRegistrator, "null indexTaskRegistrator");
    }

    @PostConstruct
    public void setup() {
        this.eventPublisher.register((Object)this);
    }

    @PreDestroy
    public void teardown() {
        this.eventPublisher.unregister((Object)this);
    }

    final List<Long> getCurrentPageAndBlogPostIdsInSpace(String spaceKey) {
        return (List)this.txTemplate.execute(() -> {
            ArrayList pageBlogIdsList = new ArrayList();
            try {
                log.debug("Executing inline task rebuild process for space {}", (Object)spaceKey);
                Space space = this.spaceManager.getSpace(spaceKey);
                ContentQuery query = new ContentQuery("inlineTasks.findAllCurrentAbstractPageIdsHibernateQueryFactory", new Object[]{space.getId()});
                Iterator results = this.customContentManager.findByQuery(query, 0, Integer.MAX_VALUE);
                results.forEachRemaining(pageBlogIdsList::add);
                log.debug("Total {} pages and blogs to process. ", (Object)pageBlogIdsList.size());
            }
            catch (DataAccessException e) {
                log.warn("Error while recreating inline tasks (page and blog ids fetching failed). ", (Throwable)e);
            }
            return pageBlogIdsList;
        });
    }

    @EventListener
    public void onImportFinishedEvent(AsyncImportFinishedEvent event) {
        if (event.isSiteImport() || event.getImportContext() == null) {
            log.debug("Skipping execution, site import: {}, context null: {}", (Object)event.isSiteImport(), (Object)(event.getImportContext() == null ? 1 : 0));
            return;
        }
        String spaceKey = event.getImportContext().getSpaceKeyOfSpaceImport();
        List<Long> pageBlogIds = this.getCurrentPageAndBlogPostIdsInSpace(spaceKey);
        this.batchOperationManager.applyInChunks(pageBlogIds, 100, pageBlogIds.size(), ids -> (List)this.txTemplate.execute(() -> {
            try {
                this.pageManager.getAbstractPages((Iterable)ids).forEach(page -> this.processContent((AbstractPage)page, this.inlineTaskFinder));
            }
            catch (RuntimeException e) {
                log.warn("Error while recreating inline tasks (content processing failed). ", (Throwable)e);
            }
            return new ArrayList();
        }));
    }

    private void processContent(AbstractPage abstractPage, InlineTaskFinder finder) {
        long contentId = abstractPage.getId();
        Date pageLastModified = Optional.ofNullable(abstractPage.getLastModificationDate()).orElse(new Date());
        DefaultConversionContext conversionContext = new DefaultConversionContext((RenderContext)abstractPage.toPageContext());
        Map<Long, InlineTaskListItem> tasks = finder.findTasksInContent(contentId, abstractPage.getBodyAsString(), (ConversionContext)conversionContext);
        log.trace("Processing {} tasks found in: {}", (Object)tasks.keySet().size(), (Object)abstractPage.getTitle());
        for (InlineTaskListItem newInlineTask : tasks.values()) {
            Task task = finder.parseTask(newInlineTask, contentId, (ConversionContext)conversionContext);
            Task newTask = new Task.Builder(task).withCreateDate(pageLastModified).withCompleteDate(newInlineTask.isCompleted() ? pageLastModified : null).build();
            this.inlineTaskService.create(newTask);
        }
        if (!tasks.values().isEmpty()) {
            this.indexTaskRegistrator.requestToReindexAllInlineTasksOnPage(contentId);
        }
    }
}

