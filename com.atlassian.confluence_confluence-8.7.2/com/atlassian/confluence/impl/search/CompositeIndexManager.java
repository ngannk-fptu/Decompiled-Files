/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.springframework.transaction.annotation.Propagation
 *  org.springframework.transaction.annotation.Transactional
 */
package com.atlassian.confluence.impl.search;

import com.atlassian.confluence.internal.index.lucene.FullReindexManager;
import com.atlassian.confluence.internal.search.IncrementalIndexManager;
import com.atlassian.confluence.search.ConfluenceIndexTask;
import com.atlassian.confluence.search.FlushStatistics;
import com.atlassian.confluence.search.IndexManager;
import com.atlassian.confluence.search.IndexTaskQueue;
import com.atlassian.confluence.search.ReIndexOption;
import com.atlassian.confluence.search.ReIndexTask;
import com.atlassian.confluence.search.v2.SearchQuery;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public class CompositeIndexManager
implements IndexManager {
    private final IndexTaskQueue<ConfluenceIndexTask> compositeTaskQueue;
    private final List<IncrementalIndexManager> incrementalManagers;
    private final FullReindexManager fullReindexManager;

    public CompositeIndexManager(List<IncrementalIndexManager> incrementalManagers, IndexTaskQueue<ConfluenceIndexTask> compositeTaskQueue, FullReindexManager fullReindexManager) {
        this.incrementalManagers = incrementalManagers;
        this.compositeTaskQueue = compositeTaskQueue;
        this.fullReindexManager = fullReindexManager;
    }

    @Override
    public boolean isFlushing() {
        return this.incrementalManagers.stream().anyMatch(IncrementalIndexManager::isFlushing);
    }

    @Override
    @Transactional(readOnly=true, propagation=Propagation.REQUIRED)
    public boolean flushQueue(IndexManager.IndexQueueFlushMode flushMode) {
        List flushResults = this.incrementalManagers.stream().map(indexManager -> indexManager.flushQueue(flushMode)).collect(Collectors.toList());
        return flushResults.stream().allMatch(x -> x);
    }

    @Override
    public ReIndexTask reIndex() {
        return this.fullReindexManager.reIndex();
    }

    @Override
    public ReIndexTask reIndex(EnumSet<ReIndexOption> options) {
        return this.fullReindexManager.reIndex(options);
    }

    @Override
    public ReIndexTask reIndex(EnumSet<ReIndexOption> options, SearchQuery searchQuery) {
        return this.fullReindexManager.reIndex(options, searchQuery);
    }

    @Override
    public ReIndexTask reIndex(EnumSet<ReIndexOption> options, @NonNull List<String> spaceKeys) {
        return this.fullReindexManager.reIndex(options, spaceKeys);
    }

    @Override
    public void unIndexAll() {
        this.fullReindexManager.unIndexAll();
    }

    @Override
    public ReIndexTask getLastReindexingTask() {
        return this.fullReindexManager.getLastReindexingTask();
    }

    @Override
    public boolean isReIndexing() {
        return this.fullReindexManager.isReIndexing();
    }

    @Override
    public void resetIndexQueue() {
        this.incrementalManagers.forEach(IncrementalIndexManager::resetIndexQueue);
    }

    @Override
    @Deprecated
    public IndexTaskQueue<ConfluenceIndexTask> getTaskQueue() {
        return this.compositeTaskQueue;
    }

    @Override
    public FlushStatistics getLastNonEmptyFlushStats() {
        return this.incrementalManagers.stream().map(IncrementalIndexManager::getLastNonEmptyFlushStats).filter(Objects::nonNull).collect(new FlushStatisticsCollector());
    }

    @Override
    public int getQueueSize() {
        return this.compositeTaskQueue.getSize();
    }

    @Override
    @Transactional(propagation=Propagation.REQUIRED)
    public void addTask(ConfluenceIndexTask task) {
        this.compositeTaskQueue.enqueue(task);
    }

    private static class FlushStatisticsCollector
    implements Collector<FlushStatistics, FlushStatistics, FlushStatistics> {
        private final Supplier<FlushStatistics> supplier = FlushStatistics::new;
        private final BinaryOperator<FlushStatistics> combiner = (a, b) -> {
            a.setQueueSize(a.getQueueSize() + b.getQueueSize());
            a.setRecreated(a.wasRecreated() || b.wasRecreated());
            if (a.getStarted() == null || b.getStarted() != null && b.getStarted().before(a.getStarted())) {
                a.setStarted(b.getStarted());
            }
            if (a.getFinished() == null || b.getFinished() != null && b.getFinished().after(a.getFinished())) {
                a.setFinished(b.getFinished());
            }
            return a;
        };
        private final BiConsumer<FlushStatistics, FlushStatistics> accumulator = this.combiner::apply;
        private final UnaryOperator<FlushStatistics> finisher = t -> t;
        private final Set<Collector.Characteristics> characteristics = Collections.unmodifiableSet(EnumSet.of(Collector.Characteristics.IDENTITY_FINISH));

        private FlushStatisticsCollector() {
        }

        @Override
        public Supplier<FlushStatistics> supplier() {
            return this.supplier;
        }

        @Override
        public BiConsumer<FlushStatistics, FlushStatistics> accumulator() {
            return this.accumulator;
        }

        @Override
        public BinaryOperator<FlushStatistics> combiner() {
            return this.combiner;
        }

        @Override
        public Function<FlushStatistics, FlushStatistics> finisher() {
            return this.finisher;
        }

        @Override
        public Set<Collector.Characteristics> characteristics() {
            return this.characteristics;
        }
    }
}

