/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.confluence.api.model.journal.JournalEntry
 *  com.atlassian.confluence.api.model.journal.JournalIdentifier
 *  com.atlassian.confluence.api.service.journal.EntryProcessorResult
 *  com.atlassian.confluence.api.service.journal.JournalService
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Iterators
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.internal.search.queue;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.api.model.journal.JournalEntry;
import com.atlassian.confluence.api.model.journal.JournalIdentifier;
import com.atlassian.confluence.api.service.journal.EntryProcessorResult;
import com.atlassian.confluence.api.service.journal.JournalService;
import com.atlassian.confluence.core.persistence.AnyTypeDao;
import com.atlassian.confluence.internal.search.queue.JournalIndexTaskQueue;
import com.atlassian.confluence.search.IndexFlushRequester;
import com.atlassian.confluence.search.IndexTask;
import com.atlassian.confluence.search.IndexTaskQueue;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterators;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Internal
public abstract class AbstractJournalIndexTaskQueue<T extends IndexTask>
implements IndexTaskQueue<T> {
    @Deprecated
    @Internal
    public static final JournalIdentifier CONTENT_JOURNAL_ID = new JournalIdentifier("main_index");
    @Deprecated
    @Internal
    public static final JournalIdentifier CHANGE_JOURNAL_ID = new JournalIdentifier("change_index");
    private static final Logger log = LoggerFactory.getLogger(JournalIndexTaskQueue.class);
    private static final int BATCH_SIZE = 1000;
    protected final JournalService journalService;
    protected final AnyTypeDao anyTypeDao;
    protected final IndexFlushRequester indexFlushRequester;
    protected final JournalIdentifier journalIdentifier;

    public AbstractJournalIndexTaskQueue(JournalService journalService, AnyTypeDao anyTypeDao, IndexFlushRequester indexFlushRequester, JournalIdentifier journalIdentifier) {
        this.journalService = Objects.requireNonNull(journalService);
        this.anyTypeDao = Objects.requireNonNull(anyTypeDao);
        this.indexFlushRequester = Objects.requireNonNull(indexFlushRequester);
        this.journalIdentifier = journalIdentifier;
    }

    protected abstract T toTask(JournalEntry var1);

    protected abstract Optional<JournalEntry> toEntry(T var1);

    @Override
    public int getSize() {
        return this.journalService.countEntries(this.journalIdentifier);
    }

    @Override
    public List<T> getQueuedEntries() {
        return ImmutableList.copyOf((Iterator)Iterators.transform(this.journalService.peek(this.journalIdentifier, Integer.MAX_VALUE).iterator(), this::toTask));
    }

    @Override
    public List<T> getQueuedEntries(int limit) {
        return ImmutableList.copyOf((Iterator)Iterators.transform(this.journalService.peek(this.journalIdentifier, limit).iterator(), this::toTask));
    }

    @Override
    public void enqueue(T task) {
        this.toEntry(task).ifPresent(entry -> {
            this.journalService.enqueue(entry);
            this.indexFlushRequester.requestFlush();
        });
    }

    @Override
    public void enqueueAll(Collection<T> tasks) {
        for (IndexTask task : tasks) {
            this.enqueue(task);
        }
    }

    @Override
    @Deprecated
    public List<T> flushQueue(int numberOfEntries) {
        ArrayList tasks = new ArrayList();
        this.flushAndExecute(tasks::add, numberOfEntries);
        return tasks;
    }

    @Override
    @Deprecated
    public List<T> flushQueue() {
        ArrayList tasks = new ArrayList();
        this.flushAndExecute(tasks::add);
        return tasks;
    }

    @Override
    public int flushAndExecute(Consumer<T> action) {
        try {
            this.journalService.waitForRecentEntriesToBecomeVisible();
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
        int timeoutTimeMins = Integer.getInteger("confluence.index.manager.lock.timeout", 5);
        int totalCount = 0;
        int expectedTotal = this.journalService.countEntries(this.journalIdentifier);
        int entriesSeen = 0;
        long startTime = System.currentTimeMillis();
        long tooSlowTime = startTime + TimeUnit.MINUTES.toMillis(timeoutTimeMins);
        do {
            totalCount += this.flushAndExecute(action, 1000);
            entriesSeen += 1000;
            long batchEndTime = System.currentTimeMillis();
            if (batchEndTime > tooSlowTime) {
                log.warn("Flushed {} tasks out of {}  ({} ms over lock timeout)", new Object[]{totalCount, expectedTotal, batchEndTime - tooSlowTime});
                continue;
            }
            log.debug("Flushed {} tasks", (Object)totalCount);
        } while (entriesSeen < expectedTotal);
        return totalCount;
    }

    @Override
    public int flushAndExecute(Consumer<T> action, int numberOfEntries) {
        return (Integer)this.journalService.processNewEntries(this.journalIdentifier, numberOfEntries, entries -> {
            int count = 0;
            for (JournalEntry entry : entries) {
                try {
                    action.accept(this.toTask(entry));
                    ++count;
                }
                catch (RuntimeException e) {
                    log.warn("Failed to process index task for entry '" + entry + "': " + e.getMessage(), (Throwable)e);
                }
            }
            return EntryProcessorResult.success((Object)count);
        });
    }

    @Override
    public int flushQueueWithActionOnIterableOfTasks(Consumer<Iterable<T>> actionOnIterableOfTasks, int numberOfTasks) {
        return (Integer)this.journalService.processNewEntries(this.journalIdentifier, numberOfTasks, entries -> {
            LinkedList<T> tasks = new LinkedList<T>();
            for (JournalEntry entry : entries) {
                try {
                    tasks.add(this.toTask(entry));
                }
                catch (RuntimeException e) {
                    log.debug("Error converting entry to task", (Throwable)e);
                }
            }
            actionOnIterableOfTasks.accept(tasks);
            return EntryProcessorResult.success((Object)tasks.size());
        });
    }

    @Override
    public void reset() {
        this.journalService.reset(this.journalIdentifier);
    }

    public JournalIdentifier getJournalIdentifier() {
        return this.journalIdentifier;
    }
}

