/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.bean.EntityObject
 *  com.google.common.util.concurrent.ThreadFactoryBuilder
 *  javax.annotation.PreDestroy
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.pages.ancestors;

import com.atlassian.confluence.pages.ancestors.AncestorsDao;
import com.atlassian.confluence.pages.ancestors.PageRepairWorker;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.spaces.SpaceStatus;
import com.atlassian.confluence.spaces.SpacesQuery;
import com.atlassian.core.bean.EntityObject;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import javax.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AncestorsRepairer {
    private static final Logger log = LoggerFactory.getLogger(AncestorsRepairer.class);
    private final Deque<ParentWithChildren> processingStack = new ArrayDeque<ParentWithChildren>();
    private final SpaceManager spaceManager;
    private final AncestorsDao ancestorsDao;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor(new ThreadFactoryBuilder().setNameFormat("ancestor-repair-%d").build());

    public AncestorsRepairer(SpaceManager spaceManager, AncestorsDao ancestorsDao) {
        this.spaceManager = spaceManager;
        this.ancestorsDao = ancestorsDao;
    }

    @PreDestroy
    public void destroy() {
        this.executorService.shutdownNow();
    }

    public void repairAncestors() throws InterruptedException, ExecutionException {
        PageRepairWorker pageRepairWorker = new PageRepairWorker(this.ancestorsDao);
        Future<?> future = this.executorService.submit(pageRepairWorker);
        try {
            long start = System.nanoTime();
            log.info("Repair process started");
            long brokenPagesCount = 0L;
            List spaces = this.spaceManager.getAllSpaces(SpacesQuery.newQuery().withSpaceStatus(SpaceStatus.CURRENT).build()).stream().map(EntityObject::getId).collect(Collectors.toList());
            for (Long spaceId : spaces) {
                List<Long> topLevelPages = this.ancestorsDao.getTopLevelPages(spaceId);
                long brokenPagesInSpaceCount = this.repairBrokenTopLevelPages(pageRepairWorker, topLevelPages);
                while (this.processingStack.size() > 0) {
                    brokenPagesInSpaceCount += (long)this.analyzeBrokenPages(pageRepairWorker);
                }
                if (brokenPagesInSpaceCount > 0L) {
                    log.info("Found " + brokenPagesInSpaceCount + " broken pages in space " + spaceId);
                }
                brokenPagesCount += brokenPagesInSpaceCount;
            }
            pageRepairWorker.noMoreBrokenPagesAreExpected();
            future.get();
            long durationSec = (System.nanoTime() - start) / 1000000000L;
            String averageSpaceProcessingTime = spaces.size() > 0 ? String.valueOf(durationSec / (long)spaces.size()) : "N/A";
            log.info("Ancestors have been repaired. Found and fixed {} broken pages. It took {} sec for {} spaces, average space processing time {} sec.", new Object[]{brokenPagesCount, durationSec, spaces.size(), averageSpaceProcessingTime});
        }
        catch (InterruptedException e) {
            log.warn("InterruptedException: " + e.getMessage());
            throw e;
        }
        catch (ExecutionException e) {
            log.warn("repairAncestors failed: " + e.getMessage(), (Throwable)e);
            throw e;
        }
        finally {
            pageRepairWorker.noMoreBrokenPagesAreExpected();
        }
    }

    private int repairBrokenTopLevelPages(PageRepairWorker pageRepairWorker, List<Long> pageIdList) throws InterruptedException {
        int numberOfBrokenPages = 0;
        Map<Long, List<Long>> ancestorsGroups = this.ancestorsDao.getAncestorsFromConfancestorsTable(pageIdList);
        for (Map.Entry<Long, List<Long>> entry : ancestorsGroups.entrySet()) {
            if (entry.getValue().size() <= 0) continue;
            ++numberOfBrokenPages;
            pageRepairWorker.addPageId(entry.getKey(), new ArrayList<Long>());
        }
        List emptyAncestors = Collections.emptyList();
        pageIdList.forEach(pageId -> this.processingStack.push(new ParentWithChildren((Long)pageId, emptyAncestors)));
        return numberOfBrokenPages;
    }

    private int analyzeBrokenPages(PageRepairWorker pageRepairWorker) throws InterruptedException {
        int brokenPageCounter = 0;
        Map<Long, List<Long>> processedPageIdsWithRealAncestors = this.getNextPagesForProcessing();
        ArrayList<Long> processedPageIds = new ArrayList<Long>(processedPageIdsWithRealAncestors.keySet());
        Map<Long, List<Long>> childrenGroupedByParent = this.ancestorsDao.getAllChildrenFromDB(processedPageIds);
        List<Long> fullPlainChildrenList = childrenGroupedByParent.entrySet().stream().map(Map.Entry::getValue).flatMap(Collection::stream).collect(Collectors.toList());
        Map<Long, List<Long>> confancestorsByChildren = this.ancestorsDao.getAncestorsFromConfancestorsTable(fullPlainChildrenList);
        for (Map.Entry<Long, List<Long>> parentWithChildren : childrenGroupedByParent.entrySet()) {
            Long parentId = parentWithChildren.getKey();
            ArrayList<Long> realAncestorsIds = new ArrayList<Long>((Collection)processedPageIdsWithRealAncestors.get(parentId));
            realAncestorsIds.add(parentId);
            for (Long childId : parentWithChildren.getValue()) {
                if (this.areAncestorsBroken(realAncestorsIds, confancestorsByChildren.get(childId))) {
                    ++brokenPageCounter;
                    pageRepairWorker.addPageId(childId, realAncestorsIds);
                }
                this.processingStack.push(new ParentWithChildren(childId, realAncestorsIds));
            }
        }
        return brokenPageCounter;
    }

    private Map<Long, List<Long>> getNextPagesForProcessing() {
        ParentWithChildren element;
        LinkedHashMap<Long, List<Long>> elements = new LinkedHashMap<Long, List<Long>>();
        while (elements.size() < AncestorsDao.IN_CLAUSE_LIMIT && (element = this.processingStack.poll()) != null) {
            elements.put(element.parent, element.children);
        }
        return elements;
    }

    private boolean areAncestorsBroken(List<Long> parentIds, List<Long> ancestors) {
        if (parentIds.size() != ancestors.size()) {
            return true;
        }
        for (int i = 0; i < parentIds.size(); ++i) {
            if (parentIds.get(i).equals(ancestors.get(i))) continue;
            return true;
        }
        return false;
    }

    private static class ParentWithChildren {
        final Long parent;
        final List<Long> children;

        ParentWithChildren(Long parent, List<Long> children) {
            this.parent = parent;
            this.children = children;
        }
    }
}

