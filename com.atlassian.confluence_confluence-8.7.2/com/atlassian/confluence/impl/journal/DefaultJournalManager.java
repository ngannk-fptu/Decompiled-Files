/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.journal.JournalIdentifier
 *  com.atlassian.confluence.api.service.exceptions.ServiceException
 *  com.atlassian.confluence.api.service.journal.EntryProcessorResult
 *  com.atlassian.fugue.Option
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.Iterables
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.dao.DataAccessException
 */
package com.atlassian.confluence.impl.journal;

import com.atlassian.confluence.api.model.journal.JournalIdentifier;
import com.atlassian.confluence.api.service.exceptions.ServiceException;
import com.atlassian.confluence.api.service.journal.EntryProcessorResult;
import com.atlassian.confluence.impl.journal.JournalDao;
import com.atlassian.confluence.impl.journal.JournalEntry;
import com.atlassian.confluence.impl.journal.JournalManager;
import com.atlassian.confluence.impl.journal.JournalStateStore;
import com.atlassian.confluence.test.JournalManagerBackdoor;
import com.atlassian.fugue.Option;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;

public class DefaultJournalManager
implements JournalManager,
JournalManagerBackdoor {
    private static final Integer JOURNAL_MAX_TRY_TIMES = Integer.getInteger("journal.max.try.times", 3);
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultJournalManager.class);
    private final JournalDao journalDao;
    private final JournalStateStore journalStateStore;
    private long ignoreWithinMillis;

    public DefaultJournalManager(JournalDao journalDao, JournalStateStore journalStateStore, long ignoreWithinMillis) {
        this.journalDao = (JournalDao)Preconditions.checkNotNull((Object)journalDao);
        this.journalStateStore = (JournalStateStore)Preconditions.checkNotNull((Object)journalStateStore);
        this.ignoreWithinMillis = ignoreWithinMillis;
    }

    @Override
    public long enqueue(@NonNull JournalEntry entry) {
        Preconditions.checkArgument((entry.getId() == 0L ? 1 : 0) != 0, (Object)"Cannot enqueue a JournalEntry if it already has an ID");
        JournalIdentifier journalId = entry.getJournalId();
        long mostRecentId = this.journalStateStore.getMostRecentId(journalId);
        long newEntryId = this.journalDao.enqueue(entry);
        LOGGER.debug("Enqueued JournalEntry: {}", (Object)entry);
        if (newEntryId <= mostRecentId) {
            LOGGER.warn("Newly enqueued entry in journal [{}] has an ID [{}] that should have been higher than the journal state store's most-recent-id [{}]. it is likely that this node's journal state store is corrupt.", new Object[]{journalId.getJournalName(), newEntryId, mostRecentId});
        }
        return newEntryId;
    }

    @Override
    public void enqueue(@NonNull Collection<JournalEntry> entries) throws ServiceException {
        entries.stream().filter(entry -> entry.getId() != 0L).findAny().ifPresent(entry -> {
            throw new IllegalArgumentException("Cannot enqueue a JournalEntry if it already has an ID");
        });
        this.journalDao.enqueue(entries);
    }

    @Override
    public Iterable<JournalEntry> peek(@NonNull JournalIdentifier journalId, int maxEntries) {
        long mostRecentId = this.journalStateStore.getMostRecentId(journalId);
        return this.journalDao.findEntries(journalId, mostRecentId, 0L, maxEntries);
    }

    @Override
    public void reset(@NonNull JournalIdentifier journalId) {
        this.journalDao.findLatestEntry(journalId, 0L).map(JournalEntry::getId).foreach(entryId -> this.journalStateStore.setMostRecentId(journalId, (long)entryId));
    }

    @Override
    public int countEntries(@NonNull JournalIdentifier journalId) {
        long mostRecentId = this.journalStateStore.getMostRecentId(journalId);
        return this.journalDao.countEntries(journalId, mostRecentId, 0L);
    }

    @Override
    public <V> V processNewEntries(@NonNull JournalIdentifier journalId, int maxEntries, @NonNull Function<Iterable<JournalEntry>, @NonNull EntryProcessorResult<V>> entryProcessor) throws DataAccessException {
        long mostRecentId = this.journalStateStore.getMostRecentId(journalId);
        List<JournalEntry> entries = this.journalDao.findEntries(journalId, mostRecentId, this.ignoreWithinMillis, maxEntries);
        EntryProcessorResult<V> result = entryProcessor.apply(entries);
        this.updateMostRecentId(journalId, entries, result);
        return (V)result.getResult();
    }

    @Override
    public void waitForRecentEntriesToBecomeVisible() throws InterruptedException {
        long sleepTime = this.ignoreWithinMillis + 100L;
        LOGGER.debug("Sleeping for {} ms in order to make recent entries visible", (Object)sleepTime);
        Thread.sleep(sleepTime);
    }

    @Override
    public long getIgnoreWithinMillis() {
        return this.ignoreWithinMillis;
    }

    @Override
    @VisibleForTesting
    public void setIgnoreWithinMillis(long ignoreWithinMillis) {
        this.ignoreWithinMillis = ignoreWithinMillis;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private void updateMostRecentId(JournalIdentifier journalId, List<JournalEntry> entries, EntryProcessorResult<?> result) {
        long mostRecentId;
        List entryIds = entries.stream().map(entry -> entry.getId()).collect(Collectors.toList());
        if (entries.isEmpty()) {
            return;
        }
        if (result.getLastSuccessfulId() != null) {
            if (!entryIds.contains(result.getLastSuccessfulId())) throw new IllegalArgumentException("lastSuccessfulId did not match any JournalEntries");
            mostRecentId = result.getLastSuccessfulId();
        } else if (result.getFailedEntryId() != null) {
            int failureIndex = entryIds.indexOf(result.getFailedEntryId());
            if (failureIndex == -1) {
                throw new IllegalArgumentException("failedEntryId did not match any JournalEntries");
            }
            JournalEntry failedEntry = entries.get(failureIndex);
            if (failedEntry.getTriedTimes() >= JOURNAL_MAX_TRY_TIMES - 1) {
                mostRecentId = failedEntry.getId();
            } else {
                failedEntry.setTriedTimes(failedEntry.getTriedTimes() + 1);
                this.journalDao.updateEntry(failedEntry);
                if (failureIndex == 0) {
                    return;
                }
                mostRecentId = entries.get(failureIndex - 1).getId();
            }
        } else {
            mostRecentId = ((JournalEntry)Iterables.getLast(entries)).getId();
        }
        this.journalStateStore.setMostRecentId(journalId, mostRecentId);
    }

    @Override
    public Optional<JournalEntry> getMostRecentId(@NonNull JournalIdentifier journalIdentifier) throws DataAccessException {
        Option<JournalEntry> entry = this.journalDao.findLatestEntry(journalIdentifier, this.getIgnoreWithinMillis());
        return entry.isEmpty() ? Optional.empty() : Optional.of((JournalEntry)entry.get());
    }

    @Override
    public void setMostRecentId(@NonNull JournalIdentifier journalIdentifier, long id) throws DataAccessException {
        this.journalStateStore.setMostRecentId(journalIdentifier, id);
    }

    @Override
    public void setMostRecentId(@NonNull JournalEntry journalEntry) throws DataAccessException {
        this.setMostRecentId(journalEntry.getJournalId(), journalEntry.getId());
    }
}

