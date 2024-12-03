/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.Lists
 *  io.atlassian.util.concurrent.ThreadFactories
 *  org.apache.commons.lang3.time.StopWatch
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.transaction.PlatformTransactionManager
 *  org.springframework.transaction.TransactionDefinition
 *  org.springframework.transaction.interceptor.DefaultTransactionAttribute
 *  org.springframework.transaction.support.TransactionTemplate
 */
package com.atlassian.confluence.security.denormalisedpermissions.impl.content;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.security.denormalisedpermissions.DenormalisedPermissionServiceState;
import com.atlassian.confluence.security.denormalisedpermissions.impl.content.ContentToSidMappingCalculator;
import com.atlassian.confluence.security.denormalisedpermissions.impl.content.NextContentIdBatchGetter;
import com.atlassian.confluence.security.denormalisedpermissions.impl.content.dao.DenormalisedContentViewPermissionDao;
import com.atlassian.confluence.security.denormalisedpermissions.impl.content.dao.RealContentAndPermissionsDao;
import com.atlassian.confluence.security.denormalisedpermissions.impl.content.domain.DenormalisedContentViewPermission;
import com.atlassian.confluence.security.denormalisedpermissions.impl.content.domain.SimpleContent;
import com.atlassian.confluence.security.denormalisedpermissions.impl.user.DenormalisedSidManager;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import io.atlassian.util.concurrent.ThreadFactories;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;
import org.springframework.transaction.support.TransactionTemplate;

@Internal
public class DenormalisedContentPermissionsUpdater {
    private static final Logger log = LoggerFactory.getLogger(DenormalisedContentPermissionsUpdater.class);
    @VisibleForTesting
    public static final int DEFAULT_INITIAL_CONTENT_PROCESSING_LIMIT = 10000;
    private static final AtomicInteger INITIAL_CONTENT_PROCESSING_BATCH_SIZE = new AtomicInteger(Integer.getInteger("confluence.denormalised_content_permissions.initial_content_processing_batch_size", 10000));
    public static final int PAGE_PROCESSING_BATCH_SIZE = Integer.getInteger("confluence.denormalised_content_permissions.page_processing_batch_size", 1000);
    private final PlatformTransactionManager platformTransactionManager;
    private final DenormalisedContentViewPermissionDao denormalisedContentViewPermissionDao;
    private final RealContentAndPermissionsDao realContentAndPermissionsDao;
    private final ContentToSidMappingCalculator contentToSidMappingCalculator;
    private final ExecutorService executor = Executors.newCachedThreadPool(ThreadFactories.namedThreadFactory((String)this.getClass().getSimpleName()));

    @VisibleForTesting
    public static void setInitialContentProcessingBatchSize(int limit) {
        if (limit <= 0) {
            throw new IllegalArgumentException("Initial content processing batch size can't be negative or zero.");
        }
        INITIAL_CONTENT_PROCESSING_BATCH_SIZE.set(limit);
    }

    public DenormalisedContentPermissionsUpdater(PlatformTransactionManager platformTransactionManager, DenormalisedSidManager denormalisedSidManager, DenormalisedContentViewPermissionDao denormalisedContentViewPermissionDao, RealContentAndPermissionsDao realContentAndPermissionsDao) {
        this.platformTransactionManager = platformTransactionManager;
        this.denormalisedContentViewPermissionDao = denormalisedContentViewPermissionDao;
        this.realContentAndPermissionsDao = realContentAndPermissionsDao;
        this.contentToSidMappingCalculator = new ContentToSidMappingCalculator(realContentAndPermissionsDao, denormalisedSidManager);
    }

    public int updateAllContentPermissions(AtomicBoolean schedulingEnabled, Supplier<DenormalisedPermissionServiceState> contentServiceStateSupplier) throws ExecutionException, InterruptedException {
        StopWatch globalWatch = StopWatch.createStarted();
        log.info("Started updating all content permissions");
        NextContentIdBatchGetter nextPageListGetter = new NextContentIdBatchGetter(this.platformTransactionManager, this.realContentAndPermissionsDao, INITIAL_CONTENT_PROCESSING_BATCH_SIZE.get());
        int pageCounter = 0;
        int batchNumber = 0;
        Long minProcessedPageId = null;
        Long maxProcessedPageId = null;
        while (schedulingEnabled.get() && nextPageListGetter.hasNext()) {
            StopWatch watch = StopWatch.createStarted();
            DenormalisedPermissionServiceState currentContentServiceState = contentServiceStateSupplier.get();
            if (!DenormalisedPermissionServiceState.INITIALISING.equals((Object)currentContentServiceState)) {
                log.warn("Initialisation of the denormalised content service was interrupted. Current state: {}. Processed {} batches so far. Duration: {}", new Object[]{currentContentServiceState, pageCounter, watch});
                return pageCounter;
            }
            List<SimpleContent> nextPageBatch = this.executor.submit(nextPageListGetter).get();
            log.debug("Got next batch (N {}) with {} elements", (Object)batchNumber, (Object)nextPageBatch.size());
            if (nextPageBatch.isEmpty()) continue;
            this.updateDenormalisedPermissionsForOneBatch(nextPageBatch);
            log.debug("Batch number {}. Processed {} pages in {} ms. Overall time: {} ms, processed {} pages so far", new Object[]{batchNumber++, nextPageBatch.size(), watch.getTime(), globalWatch.getTime(), pageCounter += nextPageBatch.size()});
            Long fromId = maxProcessedPageId;
            Long toId = nextPageBatch.get(nextPageBatch.size() - 1).getId();
            this.removeOrphanSimpleContentObjects(nextPageBatch.stream().map(SimpleContent::getId).collect(Collectors.toSet()), fromId, toId);
            if (minProcessedPageId == null) {
                minProcessedPageId = nextPageBatch.get(0).getId();
            }
            maxProcessedPageId = nextPageBatch.get(nextPageBatch.size() - 1).getId();
        }
        if (minProcessedPageId == null && maxProcessedPageId == null) {
            this.removeOrphanSimpleContentObjects(Collections.emptySet(), null, null);
        } else {
            this.removeOrphanSimpleContentObjects(Collections.emptySet(), null, minProcessedPageId);
            this.removeOrphanSimpleContentObjects(Collections.emptySet(), maxProcessedPageId, null);
        }
        if (schedulingEnabled.get()) {
            log.info("All {} pages were processed successfully. Processing time: {}", (Object)pageCounter, (Object)globalWatch);
        } else {
            log.info("The synchronisation process was interrupted. {} pages were processed so far. Processing time: {}", (Object)pageCounter, (Object)globalWatch);
        }
        return pageCounter;
    }

    private void removeOrphanSimpleContentObjects(Set<Long> existingRealPageIdSet, Long fromId, Long toId) throws ExecutionException, InterruptedException {
        this.executor.submit(() -> {
            TransactionTemplate template = new TransactionTemplate(this.platformTransactionManager, (TransactionDefinition)new DefaultTransactionAttribute(3));
            template.execute(session -> {
                HashSet<Long> simplePageIdToRemove = new HashSet<Long>(this.denormalisedContentViewPermissionDao.getSimpleContentIdsInRange(fromId, toId));
                existingRealPageIdSet.forEach(simplePageIdToRemove::remove);
                if (!simplePageIdToRemove.isEmpty()) {
                    if (log.isDebugEnabled()) {
                        log.debug("Found {} orphaned simple content objects. They are going to be removed.", (Object)simplePageIdToRemove.size());
                        existingRealPageIdSet.forEach(id -> log.debug("An orphaned simple content with id {} will be removed.", id));
                    }
                    this.denormalisedContentViewPermissionDao.removeAllDenormalisedRecordsForPages(simplePageIdToRemove);
                    this.denormalisedContentViewPermissionDao.removeAllSimpleContentRecordsForPages(simplePageIdToRemove);
                }
                return null;
            });
            return null;
        }).get();
    }

    public Set<Long> updateContentViewPermissions(Set<Long> pageIds, long deadline) {
        List<SimpleContent> simplePagesFromContentTable = this.realContentAndPermissionsDao.getSimpleContentList(pageIds);
        Set<Long> processedPageIdSet = this.updateDenormalisedContentPermissions(simplePagesFromContentTable, deadline);
        if (this.ifDeadlineMissed(deadline)) {
            return processedPageIdSet;
        }
        processedPageIdSet.addAll(this.processPhysicallyRemovedContent(pageIds, simplePagesFromContentTable));
        return processedPageIdSet;
    }

    private Collection<? extends Long> processPhysicallyRemovedContent(Set<Long> pageIdsToProcess, List<SimpleContent> simplePagesFromContentTable) {
        if (simplePagesFromContentTable.size() == pageIdsToProcess.size()) {
            return Collections.emptySet();
        }
        HashSet<Long> pageIdsToRemove = new HashSet<Long>();
        Set existingPageIdsFromContentTable = simplePagesFromContentTable.stream().map(SimpleContent::getId).collect(Collectors.toSet());
        for (Long pageIdToProcess : pageIdsToProcess) {
            if (existingPageIdsFromContentTable.contains(pageIdToProcess)) continue;
            pageIdsToRemove.add(pageIdToProcess);
        }
        this.denormalisedContentViewPermissionDao.removeAllDenormalisedRecordsForPages(pageIdsToRemove);
        this.denormalisedContentViewPermissionDao.removeAllSimpleContentRecordsForPages(pageIdsToRemove);
        return pageIdsToProcess;
    }

    private void updateDenormalisedPermissionsForOneBatch(List<SimpleContent> pageList) throws ExecutionException, InterruptedException {
        List pagePartitionList = Lists.partition(pageList, (int)PAGE_PROCESSING_BATCH_SIZE);
        for (List pageBatch : pagePartitionList) {
            this.executor.submit(() -> {
                this.updateDenormalisedContentPermissions(pageBatch, null);
                return null;
            }).get();
        }
    }

    private Set<Long> updateDenormalisedContentPermissions(List<SimpleContent> pagesFromContentTable, Long deadline) {
        StopWatch stopWatch = StopWatch.createStarted();
        Set pageIdSet = pagesFromContentTable.stream().map(SimpleContent::getId).collect(Collectors.toSet());
        TransactionTemplate template = new TransactionTemplate(this.platformTransactionManager, (TransactionDefinition)new DefaultTransactionAttribute(3));
        Map directTargetContentToSidList = (Map)template.execute(session -> this.contentToSidMappingCalculator.getRequiredSidsForPages(pageIdSet));
        HashSet<Long> processedContentIds = new HashSet<Long>();
        template.execute(session -> {
            Map<Long, Set<Long>> existingDenormalisedContentToSidList = this.denormalisedContentViewPermissionDao.getAllExistingSidsForPages(pageIdSet);
            ArrayList<DenormalisedContentViewPermission> permissionsToAdd = new ArrayList<DenormalisedContentViewPermission>();
            for (SimpleContent pageFromContentTable : pagesFromContentTable) {
                Long pageId = pageFromContentTable.getId();
                Set sidsForThePageWeNeedToHave = (Set)directTargetContentToSidList.get(pageId);
                Set<Long> existingSidsForThePage = existingDenormalisedContentToSidList.get(pageId);
                Set<Long> sidsToRemove = this.calculateSidsToRemove(existingSidsForThePage, sidsForThePageWeNeedToHave);
                if (sidsToRemove.size() > 0) {
                    this.denormalisedContentViewPermissionDao.removeRecords(pageId, sidsToRemove);
                }
                permissionsToAdd.addAll(this.calculateSidsToAdd(pageId, existingSidsForThePage, sidsForThePageWeNeedToHave));
                processedContentIds.add(pageFromContentTable.getId());
                if (processedContentIds.size() >= pagesFromContentTable.size() || !this.ifDeadlineMissed(deadline)) continue;
                log.debug("Due to a timeout only {} pagesFromContentTable (of {}) were updated. Note that the rest of the records could be updated by another node.", (Object)processedContentIds.size(), (Object)pagesFromContentTable.size());
                break;
            }
            this.denormalisedContentViewPermissionDao.add(permissionsToAdd);
            List<SimpleContent> processedPagesFromContentTable = pagesFromContentTable.stream().filter(page -> processedContentIds.contains(page.getId())).collect(Collectors.toList());
            this.updateChangedSimplePages(processedPagesFromContentTable);
            return null;
        });
        log.trace("Processed {} records. Took {} ms", (Object)pagesFromContentTable.size(), (Object)stopWatch.getTime());
        return processedContentIds;
    }

    private int updateChangedSimplePages(List<SimpleContent> pageListFromContentTable) {
        List<SimpleContent> existingDenormalisedContentList = this.denormalisedContentViewPermissionDao.getDenormalisedContentList(pageListFromContentTable.stream().map(SimpleContent::getId).collect(Collectors.toSet()));
        Map existingDenormalisedContentMap = existingDenormalisedContentList.stream().collect(Collectors.toMap(SimpleContent::getId, Function.identity()));
        int changedPageCounter = 0;
        for (SimpleContent pageFromContentTable : pageListFromContentTable) {
            SimpleContent changedContent = this.updateSimlePageIfRequired((SimpleContent)existingDenormalisedContentMap.get(pageFromContentTable.getId()), pageFromContentTable);
            if (changedContent == null) continue;
            this.denormalisedContentViewPermissionDao.saveSimpleContent(changedContent);
            ++changedPageCounter;
        }
        return changedPageCounter;
    }

    private SimpleContent updateSimlePageIfRequired(SimpleContent simpleContent, SimpleContent pageFromContentTable) {
        if (simpleContent == null) {
            simpleContent = new SimpleContent();
            simpleContent.setId(pageFromContentTable.getId());
            simpleContent.mergeFieldsFrom(pageFromContentTable);
            return simpleContent;
        }
        if (!pageFromContentTable.equals(simpleContent)) {
            simpleContent.mergeFieldsFrom(pageFromContentTable);
            return simpleContent;
        }
        return null;
    }

    private List<DenormalisedContentViewPermission> calculateSidsToAdd(long pageId, Set<Long> existingDenormalisedSidIdsForPage, Set<Long> requiredSidIdsForPage) {
        if (requiredSidIdsForPage == null || requiredSidIdsForPage.size() == 0) {
            if (existingDenormalisedSidIdsForPage == null || !existingDenormalisedSidIdsForPage.contains(-1L)) {
                return Collections.singletonList(new DenormalisedContentViewPermission(pageId, -1L));
            }
            return Collections.emptyList();
        }
        ArrayList<DenormalisedContentViewPermission> permissionsToAdd = new ArrayList<DenormalisedContentViewPermission>();
        requiredSidIdsForPage.stream().filter(sid -> existingDenormalisedSidIdsForPage == null || !existingDenormalisedSidIdsForPage.contains(sid)).forEach(sid -> permissionsToAdd.add(new DenormalisedContentViewPermission(pageId, (long)sid)));
        return permissionsToAdd;
    }

    private Set<Long> calculateSidsToRemove(Set<Long> existingDenormalisedSidIdsForPage, Set<Long> requiredSidIdsForPage) {
        if (existingDenormalisedSidIdsForPage == null) {
            return Collections.emptySet();
        }
        if (requiredSidIdsForPage == null || requiredSidIdsForPage.size() == 0) {
            return existingDenormalisedSidIdsForPage.stream().filter(sid -> -1L != sid).collect(Collectors.toSet());
        }
        return existingDenormalisedSidIdsForPage.stream().filter(sid -> !requiredSidIdsForPage.contains(sid)).collect(Collectors.toSet());
    }

    private boolean ifDeadlineMissed(Long deadline) {
        return deadline != null && System.currentTimeMillis() > deadline;
    }
}

