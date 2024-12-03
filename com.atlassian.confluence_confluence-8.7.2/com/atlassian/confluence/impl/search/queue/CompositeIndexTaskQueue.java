/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.search.queue;

import com.atlassian.confluence.search.ConfluenceIndexTask;
import com.atlassian.confluence.search.IndexTask;
import com.atlassian.confluence.search.IndexTaskQueue;
import com.atlassian.confluence.search.v2.lucene.SearchIndex;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;

@Deprecated
public class CompositeIndexTaskQueue<T extends IndexTask>
implements IndexTaskQueue<T> {
    private final Map<SearchIndex, IndexTaskQueue<T>> taskQueueBySearchIndex;
    private final IndexTaskQueue<T> fallbackQueue;

    public CompositeIndexTaskQueue(Map<SearchIndex, IndexTaskQueue<T>> taskQueueBySearchIndex, IndexTaskQueue<T> fallbackQueue) {
        this.taskQueueBySearchIndex = taskQueueBySearchIndex;
        this.fallbackQueue = fallbackQueue;
    }

    @Override
    public int getSize() {
        return this.taskQueueBySearchIndex.values().stream().mapToInt(IndexTaskQueue::getSize).sum();
    }

    @Override
    public List<T> getQueuedEntries() {
        return this.taskQueueBySearchIndex.values().stream().map(IndexTaskQueue::getQueuedEntries).flatMap(Collection::stream).collect(Collectors.toList());
    }

    @Override
    public List<T> getQueuedEntries(int limit) {
        return this.taskQueueBySearchIndex.values().stream().map(q -> q.getQueuedEntries(limit)).flatMap(Collection::stream).limit(limit).collect(Collectors.toList());
    }

    @Override
    public void enqueue(T task) {
        IndexTaskQueue<T> targetQueue = Optional.of(task).filter(ConfluenceIndexTask.class::isInstance).map(ConfluenceIndexTask.class::cast).map(ConfluenceIndexTask::getSearchIndex).map(this.taskQueueBySearchIndex::get).orElse(this.fallbackQueue);
        targetQueue.enqueue(task);
    }

    @Override
    public void enqueueAll(Collection<T> tasks) {
        tasks.forEach(this::enqueue);
    }

    @Override
    public List<T> flushQueue(int numberOfEntries) {
        if (numberOfEntries >= this.taskQueueBySearchIndex.size()) {
            return this.taskQueueBySearchIndex.values().stream().map(queue -> queue.flushQueue(numberOfEntries / this.taskQueueBySearchIndex.size())).flatMap(Collection::stream).collect(Collectors.toList());
        }
        ArrayList<T> result = new ArrayList<T>();
        for (IndexTaskQueue<T> queue2 : this.taskQueueBySearchIndex.values()) {
            if (result.size() == numberOfEntries) break;
            result.addAll(queue2.flushQueue(1));
        }
        return result;
    }

    @Override
    public List<T> flushQueue() {
        return this.taskQueueBySearchIndex.values().stream().map(IndexTaskQueue::flushQueue).flatMap(Collection::stream).collect(Collectors.toList());
    }

    @Override
    public int flushAndExecute(Consumer<T> actionOnTask) {
        return this.taskQueueBySearchIndex.values().stream().mapToInt(queue -> queue.flushAndExecute(actionOnTask)).sum();
    }

    @Override
    public int flushAndExecute(Consumer<T> actionOnTask, int numberOfEntries) {
        if (numberOfEntries >= this.taskQueueBySearchIndex.size()) {
            return this.taskQueueBySearchIndex.values().stream().mapToInt(queue -> queue.flushAndExecute(actionOnTask, numberOfEntries / this.taskQueueBySearchIndex.size())).sum();
        }
        return this.flushIncrementally(numberOfEntries, taskQueue -> taskQueue.flushAndExecute(actionOnTask, 1));
    }

    @Override
    public void reset() {
        this.taskQueueBySearchIndex.values().forEach(IndexTaskQueue::reset);
    }

    @Override
    public int flushQueueWithActionOnIterableOfTasks(Consumer<Iterable<T>> actionOnIterableOfTasks, int numberOfTasks) {
        if (numberOfTasks >= this.taskQueueBySearchIndex.size()) {
            return this.taskQueueBySearchIndex.values().stream().mapToInt(queue -> queue.flushQueueWithActionOnIterableOfTasks(actionOnIterableOfTasks, numberOfTasks / this.taskQueueBySearchIndex.size())).sum();
        }
        return this.flushIncrementally(numberOfTasks, taskQueue -> taskQueue.flushQueueWithActionOnIterableOfTasks(actionOnIterableOfTasks, 1));
    }

    private int flushIncrementally(int numEntries, ToIntFunction<IndexTaskQueue<T>> flushFunction) {
        int result = 0;
        for (IndexTaskQueue<T> queue : this.taskQueueBySearchIndex.values()) {
            if (result == numEntries) break;
            result += flushFunction.applyAsInt(queue);
        }
        return result;
    }
}

