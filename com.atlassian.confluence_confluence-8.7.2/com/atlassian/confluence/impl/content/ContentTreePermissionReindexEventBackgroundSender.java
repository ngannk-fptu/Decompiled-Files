/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.confluence.api.model.Depth
 *  com.atlassian.confluence.api.model.pagination.LimitedRequestImpl
 *  com.atlassian.core.bean.EntityObject
 *  com.atlassian.event.api.EventPublisher
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.content;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.confluence.api.model.Depth;
import com.atlassian.confluence.api.model.pagination.LimitedRequestImpl;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.persistence.hibernate.CacheMode;
import com.atlassian.confluence.core.persistence.hibernate.SessionCacheModeThreadLocal;
import com.atlassian.confluence.event.events.permission.ContentTreePermissionReindexEvent;
import com.atlassian.confluence.impl.backgroundjob.BackgroundJobProcessor;
import com.atlassian.confluence.impl.backgroundjob.BackgroundJobResponse;
import com.atlassian.confluence.internal.pages.persistence.PageDaoInternal;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.util.Cleanup;
import com.atlassian.core.bean.EntityObject;
import com.atlassian.event.api.EventPublisher;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ContentTreePermissionReindexEventBackgroundSender
implements BackgroundJobProcessor {
    private static final Logger log = LoggerFactory.getLogger(ContentTreePermissionReindexEventBackgroundSender.class);
    @VisibleForTesting
    public static final String PAGE_IDS_TO_PROCESS = "pageIdsToProcess";
    public static final int PAGE_BATCH_SIZE = Integer.getInteger("confluence.page-tree-permissions-updater.batch-size", 20);
    private final PageDaoInternal pageDao;
    private final AttachmentManager attachmentManager;
    private final EventPublisher eventPublisher;

    public ContentTreePermissionReindexEventBackgroundSender(PageDaoInternal pageDao, AttachmentManager attachmentManager, EventPublisher eventPublisher) {
        this.pageDao = pageDao;
        this.attachmentManager = attachmentManager;
        this.eventPublisher = eventPublisher;
    }

    public static Map<String, Object> createParametersForContentEntityObject(ContentEntityObject page) {
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(PAGE_IDS_TO_PROCESS, Collections.singleton(page.getId()));
        return parameters;
    }

    @Override
    public BackgroundJobResponse process(Long jobId, Map<String, Object> parameters, long recommendedTimeout) {
        Map<String, Object> newParameters;
        long deadline = System.currentTimeMillis() + recommendedTimeout;
        log.debug("Processing started for job {}.", (Object)jobId);
        Map<String, Object> currentParameters = new HashMap<String, Object>(parameters);
        do {
            newParameters = this.processNextBatchOfPages(currentParameters, jobId);
            currentParameters = newParameters;
            if (!((Collection)newParameters.get(PAGE_IDS_TO_PROCESS)).isEmpty()) continue;
            log.debug("Processing completely finished for jobId {}.", (Object)jobId);
            return BackgroundJobResponse.markJobAsFinished();
        } while (System.currentTimeMillis() <= deadline);
        log.debug("Processing partially finished for jobId {} because of the deadline. Additional {} ms spent after the deadline", (Object)jobId, (Object)(System.currentTimeMillis() - deadline));
        return BackgroundJobResponse.scheduleNextRunNow(newParameters);
    }

    private Map<String, Object> processNextBatchOfPages(Map<String, Object> parameters, long jobId) {
        try (Cleanup cleanup = SessionCacheModeThreadLocal.temporarilySetCacheMode(CacheMode.IGNORE);){
            Collection<Long> allPageIdsToProcess = this.getAllPageIdsToProcess((Collection)parameters.get(PAGE_IDS_TO_PROCESS));
            log.debug("Processing next iteration for jobId {}. Ids to process: {}", (Object)jobId, (Object)allPageIdsToProcess.size());
            if (allPageIdsToProcess.isEmpty()) {
                log.warn("Unexpected: performNextIteration got an empty list of pages");
                Map<String, Object> map = parameters;
                return map;
            }
            Collection<Long> pageIdsToProcess = this.getPortionOfPageIds(allPageIdsToProcess);
            List<Page> pagesToProcess = pageIdsToProcess.stream().map(this.pageDao::getById).filter(Objects::nonNull).collect(Collectors.toList());
            List<Attachment> attachments = this.attachmentManager.getLatestVersionsOfAttachmentsForMultipleCeos(pagesToProcess);
            attachments.forEach(this::reindexContentAndComments);
            pagesToProcess.forEach(this::reindexContentAndComments);
            ArrayList<Long> nextPageIdsToProcess = new ArrayList<Long>();
            pagesToProcess.forEach(p -> {
                List children = this.pageDao.getAllChildren((Page)p, LimitedRequestImpl.create((int)0x7FFFFFFE), Depth.ROOT).getResults();
                nextPageIdsToProcess.addAll(children.stream().map(EntityObject::getId).collect(Collectors.toList()));
            });
            HashMap<String, Object> newParameters = new HashMap<String, Object>();
            nextPageIdsToProcess.addAll(allPageIdsToProcess);
            nextPageIdsToProcess.removeAll(pageIdsToProcess);
            newParameters.put(PAGE_IDS_TO_PROCESS, nextPageIdsToProcess);
            HashMap<String, Object> hashMap = newParameters;
            return hashMap;
        }
    }

    private Collection<Long> getPortionOfPageIds(Collection<Long> allPageIdsToProcess) {
        return allPageIdsToProcess.stream().limit(PAGE_BATCH_SIZE).collect(Collectors.toList());
    }

    private Collection<Long> getAllPageIdsToProcess(Collection<Object> pageIds) {
        return pageIds.stream().map(this::convertToLong).collect(Collectors.toList());
    }

    private Long convertToLong(Object pageId) {
        return pageId instanceof Integer ? Long.valueOf(((Integer)pageId).intValue()) : (Long)pageId;
    }

    private void reindexContentAndComments(ContentEntityObject content) {
        this.sendContentTreePermissionReindexEvent(content);
        content.getComments().forEach(this::sendContentTreePermissionReindexEvent);
    }

    private void sendContentTreePermissionReindexEvent(ContentEntityObject content) {
        this.eventPublisher.publish((Object)new ContentTreePermissionReindexEvent((Object)this, content));
    }
}

