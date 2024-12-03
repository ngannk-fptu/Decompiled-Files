/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.confluence.api.model.pagination.LimitedRequestImpl
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.transaction.PlatformTransactionManager
 *  org.springframework.transaction.support.TransactionTemplate
 */
package com.atlassian.confluence.internal.content.collab;

import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.api.model.pagination.LimitedRequestImpl;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.SpaceContentEntityObject;
import com.atlassian.confluence.internal.ContentEntityManagerInternal;
import com.atlassian.confluence.internal.content.collab.AttachmentRelatedContentReconciliationListener;
import com.atlassian.confluence.internal.content.collab.ContentReconciliationManager;
import com.atlassian.confluence.internal.content.collab.OwningContent;
import com.atlassian.confluence.internal.content.collab.ReconcileContentRegisterTask;
import com.atlassian.confluence.internal.persistence.ContentEntityObjectDaoInternal;
import com.atlassian.confluence.links.LinkManager;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

public class ReconcileReferringContentTask
implements ReconcileContentRegisterTask<OwningContent> {
    private static Logger logger = LoggerFactory.getLogger(ReconcileReferringContentTask.class);
    private final PlatformTransactionManager transactionManager;
    private final ContentEntityManagerInternal contentEntityManager;
    private final ContentReconciliationManager reconciliationManager;
    private final LinkManager linkManager;
    private final ContentEntityObjectDaoInternal contentEntityObjectDao;
    private final Set<Long> attachmentOwningContents;
    private final HashMap<Long, Boolean> shouldIncludeOwnContentMap;
    private static int processingBatchSize = Integer.getInteger("reconcile.referring.content.batch.size", 20);

    public ReconcileReferringContentTask(PlatformTransactionManager transactionManager, ContentEntityManagerInternal contentEntityManager, ContentReconciliationManager reconciliationManager, ContentEntityObjectDaoInternal contentEntityObjectDao, LinkManager linkManager) {
        this.transactionManager = transactionManager;
        this.contentEntityManager = contentEntityManager;
        this.reconciliationManager = reconciliationManager;
        this.contentEntityObjectDao = contentEntityObjectDao;
        this.linkManager = linkManager;
        this.attachmentOwningContents = new HashSet<Long>();
        this.shouldIncludeOwnContentMap = new HashMap();
    }

    @Override
    public void run() {
        AtomicInteger batchNumber = new AtomicInteger(0);
        HashSet uniqueReferringContents = new HashSet();
        TransactionTemplate transactionTemplate = new TransactionTemplate(this.transactionManager);
        transactionTemplate.setPropagationBehavior(3);
        transactionTemplate.execute(status -> {
            this.ofSubLists(new ArrayList<Long>(this.attachmentOwningContents), processingBatchSize).forEach(batchContents -> {
                logger.info("Process owning content for batch number {}", (Object)batchNumber.incrementAndGet());
                logger.debug("Processing a batch of owning content: {}", batchContents);
                List<ContentId> contentIdList = batchContents.stream().map(id -> ContentId.of((long)id)).collect(Collectors.toList());
                PageResponse<ContentEntityObject> response = this.contentEntityManager.getByIdsAndFilters(contentIdList, LimitedRequestImpl.create((int)batchContents.size()), contentEntityObject -> true);
                List owningCEOList = response.getResults();
                if (owningCEOList == null || owningCEOList.size() == 0) {
                    logger.debug("Could not load owning content from DB --> Skip this batch");
                }
                logger.debug("There are [{}] of owning CEO is loaded from DB", (Object)owningCEOList.size());
                List spaceContentEntityObjectList = owningCEOList.stream().filter(contentEntityObject -> contentEntityObject instanceof SpaceContentEntityObject).map(contentEntityObject -> (SpaceContentEntityObject)contentEntityObject).collect(Collectors.toList());
                Map<String, List<SpaceContentEntityObject>> groupCEOBySpaceKey = spaceContentEntityObjectList.stream().collect(Collectors.groupingBy(SpaceContentEntityObject::getSpaceKey));
                logger.debug("Space detail in current batch: {}", groupCEOBySpaceKey.keySet());
                groupCEOBySpaceKey.entrySet().stream().forEach(contentIdListGroupBySpace -> {
                    List<ContentEntityObject> tempCEOList = ((List)contentIdListGroupBySpace.getValue()).stream().map(item -> item).collect(Collectors.toList());
                    Collection<ContentEntityObject> referringContents = this.linkManager.getReferringContent((String)contentIdListGroupBySpace.getKey(), tempCEOList);
                    uniqueReferringContents.addAll(referringContents);
                });
                logger.info("Finishing process for owning content batch {}", (Object)batchNumber.get());
            });
            logger.debug("There are {} of distingue CEO potential to reconcile", (Object)uniqueReferringContents.size());
            this.ofSubLists(new ArrayList(uniqueReferringContents), processingBatchSize).forEach(batchContents -> {
                logger.info("Process reconcile content for batch number {}", (Object)batchNumber.incrementAndGet());
                logger.debug("Processing a batch of reconcile content: {}", batchContents);
                batchContents.stream().filter(reconcileContent -> Objects.nonNull(reconcileContent)).filter(reconcileContent -> this.getShouldIncludeOwnContent(reconcileContent.getId()) || !this.attachmentOwningContents.contains(reconcileContent.getId())).filter(reconcileContent -> reconcileContent.isCurrent()).forEach(reconcileContent -> {
                    AttachmentRelatedContentReconciliationListener.updateCEOWithAttachmentChange(reconcileContent, this.contentEntityObjectDao);
                    this.reconciliationManager.handleEditorOnlyContentUpdateBeforeSave((ContentEntityObject)reconcileContent, null);
                    this.reconciliationManager.handleEditorOnlyContentUpdateAfterSave((ContentEntityObject)reconcileContent, null, Optional.ofNullable(reconcileContent.getLastModificationDate()));
                    logger.debug("Reconcile for content {} is done", (Object)reconcileContent.getContentId());
                });
                logger.debug("There are {} of distingue CEO need to reconcile", (Object)uniqueReferringContents.size());
                logger.info("Finishing process for reconcile content batch {}", (Object)batchNumber.get());
            });
            return null;
        });
    }

    @Override
    public void registerReconcileContent(OwningContent owningContent) {
        if (owningContent == null) {
            return;
        }
        long contentId = owningContent.getContentId();
        boolean shouldIncludeOwnContent = owningContent.isShouldIncludeOwnContent();
        if (contentId <= 0L) {
            logger.warn("Content with ID [{}] is not valid. So we cannot register it for reconcile", (Object)contentId);
            return;
        }
        this.attachmentOwningContents.add(contentId);
        Boolean isPreviousIncluding = this.shouldIncludeOwnContentMap.get(contentId);
        if (isPreviousIncluding != null && isPreviousIncluding.booleanValue()) {
            logger.debug("Skip register reconcile content because it is already include own content");
            return;
        }
        this.shouldIncludeOwnContentMap.put(contentId, shouldIncludeOwnContent);
    }

    private boolean getShouldIncludeOwnContent(long contentId) {
        Boolean result = this.shouldIncludeOwnContentMap.get(contentId);
        if (result == null) {
            logger.warn("Could not find shouldIncludeOwnContentMap for Content ID {} but will reconcile anyway", (Object)contentId);
            return false;
        }
        if (result.booleanValue()) {
            logger.debug("It is {} to include content {} as it is a owning content", (Object)result, (Object)contentId);
        }
        return result;
    }

    private <T> Stream<List<T>> ofSubLists(List<T> source, int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("length = " + length);
        }
        int size = source.size();
        if (size == 0) {
            return Stream.empty();
        }
        int fullChunks = (size - 1) / length;
        return IntStream.range(0, fullChunks + 1).mapToObj(n -> source.subList(n * length, n == fullChunks ? size : (n + 1) * length));
    }
}

