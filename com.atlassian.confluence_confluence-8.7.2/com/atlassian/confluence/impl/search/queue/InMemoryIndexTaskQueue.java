/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.jcip.annotations.NotThreadSafe
 */
package com.atlassian.confluence.impl.search.queue;

import com.atlassian.confluence.search.IndexTask;
import com.atlassian.confluence.search.IndexTaskQueue;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import net.jcip.annotations.NotThreadSafe;

@NotThreadSafe
public class InMemoryIndexTaskQueue<T extends IndexTask>
implements IndexTaskQueue<T> {
    private LinkedList<T> queue = new LinkedList();

    @Override
    public synchronized int getSize() {
        return this.queue.size();
    }

    @Override
    public synchronized List<T> getQueuedEntries() {
        return Collections.unmodifiableList(this.queue);
    }

    @Override
    public synchronized List<T> getQueuedEntries(int limit) {
        LinkedList<IndexTask> taskList = new LinkedList<IndexTask>();
        int i = 0;
        for (IndexTask indexTask : this.queue) {
            taskList.add(indexTask);
            if (++i < limit) continue;
            break;
        }
        return Collections.unmodifiableList(taskList);
    }

    @Override
    public synchronized void enqueue(T task) {
        this.queue.add(task);
    }

    @Override
    public synchronized void enqueueAll(Collection<T> tasks) {
        this.queue.addAll(tasks);
    }

    @Override
    public synchronized List<T> flushQueue(int numberOfEntries) {
        List<T> result = this.getQueuedEntries(numberOfEntries);
        this.queue.removeAll(result);
        return result;
    }

    @Override
    public synchronized void reset() {
        this.queue = new LinkedList();
    }

    @Override
    public List<T> flushQueue() {
        List<T> result = this.getQueuedEntries();
        this.queue = new LinkedList();
        return result;
    }

    @Override
    public int flushAndExecute(Consumer<T> action) {
        List<T> indexTasks = this.flushQueue();
        for (IndexTask indexTask : indexTasks) {
            action.accept(indexTask);
        }
        return indexTasks.size();
    }

    @Override
    public int flushAndExecute(Consumer<T> action, int numberOfEntries) {
        List<T> indexTasks = this.flushQueue(numberOfEntries);
        for (IndexTask indexTask : indexTasks) {
            action.accept(indexTask);
        }
        return indexTasks.size();
    }

    @Override
    public int flushQueueWithActionOnIterableOfTasks(Consumer<Iterable<T>> actionOnIterableOfTasks, int numberOfTasks) {
        List<T> indexTasks = this.flushQueue(numberOfTasks);
        try {
            actionOnIterableOfTasks.accept(indexTasks);
            return indexTasks.size();
        }
        catch (RuntimeException e) {
            this.queue.addAll(indexTasks);
            throw e;
        }
    }
}

