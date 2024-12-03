/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  bucket.core.persistence.hibernate.HibernateHandle
 *  com.atlassian.annotations.Internal
 *  com.atlassian.confluence.api.model.journal.JournalEntry
 *  com.atlassian.confluence.api.model.journal.JournalIdentifier
 *  com.atlassian.confluence.api.service.journal.JournalService
 *  com.atlassian.event.api.EventPublisher
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.Lists
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.internal.index.lucene;

import bucket.core.persistence.hibernate.HibernateHandle;
import com.atlassian.annotations.Internal;
import com.atlassian.confluence.api.model.journal.JournalIdentifier;
import com.atlassian.confluence.api.service.journal.JournalService;
import com.atlassian.confluence.cluster.ClusterManager;
import com.atlassian.confluence.cluster.ReIndexingScopeThreadLocal;
import com.atlassian.confluence.core.persistence.SearchableDao;
import com.atlassian.confluence.impl.journal.JournalEntry;
import com.atlassian.confluence.impl.journal.JournalManager;
import com.atlassian.confluence.impl.system.SystemMaintenanceTaskQueue;
import com.atlassian.confluence.impl.system.task.CreateIndexSnapshotMaintenanceTask;
import com.atlassian.confluence.index.ReIndexSpec;
import com.atlassian.confluence.internal.index.BatchIndexer;
import com.atlassian.confluence.internal.index.EventPublishingReindexProgress;
import com.atlassian.confluence.internal.index.Index;
import com.atlassian.confluence.internal.index.ReIndexer;
import com.atlassian.confluence.internal.index.ReindexProgress;
import com.atlassian.confluence.internal.index.lucene.BatchIndexerFactory;
import com.atlassian.confluence.internal.search.LuceneIndependent;
import com.atlassian.confluence.search.ReIndexOption;
import com.atlassian.confluence.search.SearchPlatformConfig;
import com.atlassian.confluence.search.v2.BatchUpdateAction;
import com.atlassian.confluence.search.v2.SearchFieldNames;
import com.atlassian.confluence.search.v2.SearchIndexAccessor;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.query.BooleanQuery;
import com.atlassian.confluence.util.Progress;
import com.atlassian.event.api.EventPublisher;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import java.text.ParseException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@LuceneIndependent
@Internal
public class LuceneReIndexer
implements ReIndexer {
    protected static final Set<Index> INDEXES_TO_SET_EARLIEST_ENTRY = EnumSet.of(Index.MAIN_INDEX, Index.EDGE_INDEX, Index.CHANGE_INDEX);
    public static final Set<Index> INDEXES_TO_SNAPSHOT = EnumSet.of(Index.MAIN_INDEX, Index.CHANGE_INDEX);
    public static final String KEY_REINDEX_PARTITION_SIZE_MAX = "reindex.partition.size.max";
    public static final int DEFAULT_REINDEX_PARTITION_SIZE_MAX = 100000;
    private static final Logger log = LoggerFactory.getLogger(LuceneReIndexer.class);
    private final SearchIndexAccessor contentIndexAccessor;
    private final SearchIndexAccessor changeIndexAccessor;
    private final SearchableDao searchableDao;
    private final EventPublisher eventPublisher;
    private final ClusterManager clusterManager;
    private final SystemMaintenanceTaskQueue systemMaintenanceTaskQueue;
    private final BatchIndexerFactory batchIndexerFactory;
    private final SearchPlatformConfig searchPlatformConfig;
    private final JournalManager journalManager;
    private final JournalService journalService;

    public LuceneReIndexer(SearchableDao searchableDao, SearchIndexAccessor contentIndexAccessor, SearchIndexAccessor changeIndexAccessor, EventPublisher eventPublisher, ClusterManager clusterManager, SystemMaintenanceTaskQueue systemMaintenanceTaskQueue, BatchIndexerFactory batchIndexerFactory, SearchPlatformConfig searchPlatformConfig, JournalManager journalManager, JournalService journalService) {
        this.searchableDao = Objects.requireNonNull(searchableDao);
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
        this.contentIndexAccessor = Objects.requireNonNull(contentIndexAccessor);
        this.changeIndexAccessor = Objects.requireNonNull(changeIndexAccessor);
        this.clusterManager = Objects.requireNonNull(clusterManager);
        this.systemMaintenanceTaskQueue = Objects.requireNonNull(systemMaintenanceTaskQueue);
        this.batchIndexerFactory = Objects.requireNonNull(batchIndexerFactory);
        this.searchPlatformConfig = Objects.requireNonNull(searchPlatformConfig);
        this.journalManager = Objects.requireNonNull(journalManager);
        this.journalService = Objects.requireNonNull(journalService);
    }

    @Override
    public void reIndex(EnumSet<ReIndexOption> maybeOptions, Progress progress) {
        this.reIndex(maybeOptions, this.reindexProgress(progress));
    }

    private void reIndex(EnumSet<ReIndexOption> maybeOptions, ReindexProgress progress) {
        progress.reindexStarted(maybeOptions, Collections.emptyList());
        EnumSet<ReIndexOption> options = maybeOptions == null ? ReIndexOption.fullReindex() : maybeOptions;
        options.forEach(option -> {
            progress.reindexStageStarted((ReIndexOption)((Object)option));
            this.reIndex((ReIndexOption)((Object)option), progress);
            progress.reindexStageFinished((ReIndexOption)((Object)option));
        });
        progress.reindexFinished(Collections.emptyList());
        if (this.needToPropagateSnapshot(options)) {
            this.systemMaintenanceTaskQueue.enqueue(new CreateIndexSnapshotMaintenanceTask(Objects.requireNonNull(this.clusterManager.getThisNodeInformation()).getAnonymizedNodeIdentifier(), INDEXES_TO_SNAPSHOT));
        }
    }

    private boolean needToPropagateSnapshot(EnumSet<ReIndexOption> options) {
        return ReIndexOption.isFullReindex(options) && !this.searchPlatformConfig.isSharedIndex() && this.clusterManager.isClustered() && ReIndexingScopeThreadLocal.currentScope() == ReIndexingScopeThreadLocal.ReIndexingScope.CLUSTER_WIDE;
    }

    @Override
    public void reIndex(EnumSet<ReIndexOption> maybeOptions, @NonNull SearchQuery searchQuery, Progress progress) {
        this.reIndex(maybeOptions, searchQuery, this.reindexProgress(progress));
    }

    @Override
    public void reIndex(EnumSet<ReIndexOption> options, List<String> spaceKeys, Progress progress) {
        this.reIndex(options, spaceKeys, this.reindexProgress(progress));
    }

    private void reIndex(EnumSet<ReIndexOption> maybeOptions, List<String> spaceKeys, ReindexProgress progress) {
        progress.reindexStarted(maybeOptions, spaceKeys);
        spaceKeys.forEach(spaceKey -> this.reIndex(maybeOptions, (ReIndexOption option) -> this.reIndex((ReIndexOption)((Object)((Object)option)), (String)spaceKey, progress), progress));
        progress.reindexFinished(spaceKeys);
    }

    private void reIndex(EnumSet<ReIndexOption> maybeOptions, SearchQuery searchQuery, ReindexProgress progress) {
        progress.reindexStarted(maybeOptions, Collections.emptyList());
        this.reIndex(maybeOptions, (ReIndexOption option) -> this.reIndex((ReIndexOption)((Object)option), searchQuery, progress), progress);
        progress.reindexFinished(Collections.emptyList());
    }

    private void reIndex(EnumSet<ReIndexOption> maybeOptions, Consumer<ReIndexOption> consumer, ReindexProgress progress) {
        EnumSet<ReIndexOption> options = maybeOptions == null ? ReIndexOption.fullReindex() : maybeOptions;
        options.forEach(option -> {
            progress.reindexStageStarted((ReIndexOption)((Object)option));
            consumer.accept((ReIndexOption)((Object)option));
            progress.reindexStageFinished((ReIndexOption)((Object)option));
        });
    }

    private void reIndex(ReIndexOption option, String spaceKey, ReindexProgress progress) {
        log.info("Indexing of space {} starting for stage {}", (Object)spaceKey, (Object)option);
        this.reIndex(new ReIndexSpec(option.getDeleteQuery(Optional.of(spaceKey)), option.getThreadCount(), option.getHandles(this.searchableDao, Optional.ofNullable(spaceKey)), option.name(), false), progress);
        log.info("Indexing of space {} completed for stage {}", (Object)spaceKey, (Object)option);
    }

    private void reIndex(ReIndexOption option, SearchQuery searchQuery, ReindexProgress progress) {
        log.info("Indexing starting for stage {}", (Object)option);
        LinkedHashSet handles = new LinkedHashSet();
        this.contentIndexAccessor.scan(searchQuery, Collections.singleton(SearchFieldNames.HANDLE), fieldValues -> {
            String[] values = (String[])fieldValues.get(SearchFieldNames.HANDLE);
            if (values.length > 0) {
                try {
                    com.atlassian.confluence.core.persistence.hibernate.HibernateHandle handle = new com.atlassian.confluence.core.persistence.hibernate.HibernateHandle(values[0]);
                    if (option.getClassFilter().test(handle.getClassName())) {
                        handles.add(handle);
                    }
                }
                catch (ParseException ignored) {
                    log.error("Error when parsing handle field value {}", (Object)values[0]);
                }
            }
        });
        SearchQuery deleteQuery = (SearchQuery)BooleanQuery.builder().addMust(searchQuery).addMust(option.getDeleteQuery()).build();
        this.reIndex(new ReIndexSpec(deleteQuery, option.getThreadCount(), new ArrayList<com.atlassian.confluence.core.persistence.hibernate.HibernateHandle>(handles), option.name()), progress);
        log.info("Indexing completed for stage {}", (Object)option);
    }

    private void reIndex(ReIndexOption option, ReindexProgress progress) {
        log.info("Indexing starting for stage {}", (Object)option);
        ReIndexSpec reIndexSpec = new ReIndexSpec(option.getDeleteQuery(), option.getThreadCount(), option.getHandles(this.searchableDao), option.name());
        this.reIndex(reIndexSpec, progress);
        log.info("Indexing completed for stage {}", (Object)option);
    }

    @VisibleForTesting
    int getMaxPartitionSize(int concurrencyLevel) {
        int partitionSize = Integer.getInteger(KEY_REINDEX_PARTITION_SIZE_MAX, 100000);
        if (partitionSize < concurrencyLevel) {
            log.warn("provided value {} for {} is less than concurrencyLevel {}, resetting to {}", new Object[]{partitionSize, KEY_REINDEX_PARTITION_SIZE_MAX, concurrencyLevel, concurrencyLevel});
            return concurrencyLevel;
        }
        return partitionSize;
    }

    @Override
    public void reIndex(ReIndexSpec spec, Progress progress) {
        this.reIndex(spec, this.reindexProgress(progress));
    }

    private void reIndex(ReIndexSpec spec, ReindexProgress progress) {
        int concurrencyLevel = spec.getConcurrencyLevel();
        int maxPartitionSize = this.getMaxPartitionSize(concurrencyLevel);
        Deque groups = spec.getHandles().stream().collect(Collectors.groupingBy(HibernateHandle::getClassName)).values().stream().flatMap(group -> Lists.partition((List)group, (int)maxPartitionSize).stream().map(ArrayList::new)).collect(Collectors.toCollection(ArrayDeque::new));
        try {
            spec.getHandles().clear();
        }
        catch (UnsupportedOperationException e) {
            log.warn("unable to clear list of {}", spec.getHandles().getClass());
        }
        log.info("full reindex partitions for {} and concurrency level {}, total {} groups", new Object[]{spec.getName(), concurrencyLevel, groups.size()});
        BatchUpdateAction batchUpdateAction = () -> this.changeIndexAccessor.withBatchUpdate(() -> this.reIndex(spec, progress, groups));
        this.contentIndexAccessor.withBatchUpdate(batchUpdateAction);
    }

    private void reIndex(ReIndexSpec spec, ReindexProgress progress, Deque<List<com.atlassian.confluence.core.persistence.hibernate.HibernateHandle>> groups) {
        String specName = spec.getName();
        SearchQuery deleteQuery = spec.getDeleteQuery();
        this.makeSureJournalExistsInDB();
        List<JournalEntry> latestJournalEntries = this.getJournalsLatestEntries();
        this.contentIndexAccessor.execute(contentIndexWriter -> this.changeIndexAccessor.execute(changesIndexWriter -> {
            log.info("full reindex starting for {}, deleting documents from index", (Object)specName);
            if (spec.shouldOptimize()) {
                log.info("Pre-optimizing indices...");
                contentIndexWriter.preOptimize();
                changesIndexWriter.preOptimize();
            }
            contentIndexWriter.delete(deleteQuery);
            changesIndexWriter.delete(deleteQuery);
            log.info("full reindex documents deleted for {}, starting full reindex", (Object)specName);
            int numGroups = groups.size();
            AtomicInteger count = new AtomicInteger(0);
            while (!groups.isEmpty()) {
                List handles = (List)groups.pop();
                BatchIndexer concurrentIndexer = this.batchIndexerFactory.createConcurrentIndexer(spec, contentIndexWriter, changesIndexWriter);
                concurrentIndexer.index(handles, progress);
                log.info("full reindex group {}/{} completed for {}, {}% complete", new Object[]{count.incrementAndGet(), numGroups, specName, progress.getPercentComplete()});
            }
            log.info("full reindex completed for {}, {}% complete, start cleaning up files", (Object)specName, (Object)progress.getPercentComplete());
            if (spec.shouldOptimize()) {
                log.info("Post-optimizing indices...");
                contentIndexWriter.postOptimize();
                changesIndexWriter.postOptimize();
            }
            log.info("full reindex cleanup completed for {}", (Object)specName);
        }));
        this.setJournalsLatestEntries(latestJournalEntries);
    }

    @VisibleForTesting
    ReindexProgress reindexProgress(Progress progress) {
        return new EventPublishingReindexProgress(this.eventPublisher, progress);
    }

    @VisibleForTesting
    List<JournalEntry> getJournalsLatestEntries() {
        return INDEXES_TO_SET_EARLIEST_ENTRY.stream().map(index -> this.getMostRecentId(index.getJournalIdentifier())).filter(Optional::isPresent).map(Optional::get).collect(Collectors.toCollection(ArrayList::new));
    }

    private Optional<JournalEntry> getMostRecentId(JournalIdentifier journalIdentifier) {
        try {
            Optional<JournalEntry> entry = this.journalManager.getMostRecentId(journalIdentifier);
            if (entry.isPresent()) {
                log.info("the latest entry for {} is {}", (Object)journalIdentifier.getJournalName(), (Object)entry.get().getId());
            } else {
                log.info("{} has no entries", (Object)journalIdentifier.getJournalName());
            }
            return entry;
        }
        catch (Exception e) {
            log.error("unable to get the latest entry for {}", (Object)journalIdentifier.getJournalName());
            return Optional.empty();
        }
    }

    @VisibleForTesting
    boolean setJournalsLatestEntries(List<JournalEntry> entries) {
        AtomicBoolean result = new AtomicBoolean(true);
        entries.forEach(entry -> {
            JournalIdentifier journalIdentifier = entry.getJournalId();
            long entryId = entry.getId();
            try {
                this.journalManager.setMostRecentId(journalIdentifier, entryId);
                log.info("{} storage was set to the latest entry {}", (Object)journalIdentifier.getJournalName(), (Object)entryId);
            }
            catch (Exception e) {
                result.set(false);
                log.error("unable to set {} storage to the latest journal entry", (Object)journalIdentifier.getJournalName());
            }
        });
        return result.get();
    }

    private void makeSureJournalExistsInDB() {
        INDEXES_TO_SNAPSHOT.stream().forEach(index -> this.journalService.enqueue(new com.atlassian.confluence.api.model.journal.JournalEntry(index.getJournalIdentifier(), "NO_OP", "Placeholder journal only, safe to ignore.")));
    }
}

