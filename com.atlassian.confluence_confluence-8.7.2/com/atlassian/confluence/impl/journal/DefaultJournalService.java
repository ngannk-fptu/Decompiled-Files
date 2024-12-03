/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.journal.JournalEntry
 *  com.atlassian.confluence.api.model.journal.JournalIdentifier
 *  com.atlassian.confluence.api.service.exceptions.ServiceException
 *  com.atlassian.confluence.api.service.journal.EntryProcessorResult
 *  com.atlassian.confluence.api.service.journal.JournalService
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.Iterables
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.springframework.dao.DataAccessException
 */
package com.atlassian.confluence.impl.journal;

import com.atlassian.confluence.api.model.journal.JournalIdentifier;
import com.atlassian.confluence.api.service.exceptions.ServiceException;
import com.atlassian.confluence.api.service.journal.EntryProcessorResult;
import com.atlassian.confluence.api.service.journal.JournalService;
import com.atlassian.confluence.impl.journal.JournalEntry;
import com.atlassian.confluence.impl.journal.JournalManager;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import java.util.function.Function;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.dao.DataAccessException;

public class DefaultJournalService
implements JournalService {
    private final JournalManager journalManager;

    public DefaultJournalService(JournalManager journalManager) {
        this.journalManager = (JournalManager)Preconditions.checkNotNull((Object)journalManager);
    }

    public long enqueue(@NonNull com.atlassian.confluence.api.model.journal.JournalEntry entry) throws ServiceException {
        try {
            return this.journalManager.enqueue(DefaultJournalService.convert(entry));
        }
        catch (DataAccessException e) {
            throw new ServiceException("Failed to add entry to queue", (Throwable)e);
        }
    }

    public <V> V processNewEntries(@NonNull JournalIdentifier journalId, int maxEntries, @NonNull Function<Iterable<com.atlassian.confluence.api.model.journal.JournalEntry>, EntryProcessorResult<V>> entryProcessor) throws ServiceException {
        try {
            return this.journalManager.processNewEntries(journalId, maxEntries, DefaultJournalService.convert(entryProcessor));
        }
        catch (DataAccessException e) {
            throw new ServiceException("Failed to process entries", (Throwable)e);
        }
    }

    public void waitForRecentEntriesToBecomeVisible() throws InterruptedException {
        this.journalManager.waitForRecentEntriesToBecomeVisible();
    }

    public Iterable<com.atlassian.confluence.api.model.journal.JournalEntry> peek(@NonNull JournalIdentifier journalId, int maxEntries) throws ServiceException {
        try {
            return DefaultJournalService.convert(this.journalManager.peek(journalId, maxEntries));
        }
        catch (DataAccessException e) {
            throw new ServiceException("Failed to peek journal", (Throwable)e);
        }
    }

    public void reset(@NonNull JournalIdentifier journalId) throws ServiceException {
        try {
            this.journalManager.reset(journalId);
        }
        catch (DataAccessException e) {
            throw new ServiceException("Failed to reset journal", (Throwable)e);
        }
    }

    public int countEntries(@NonNull JournalIdentifier journalId) throws ServiceException {
        try {
            return this.journalManager.countEntries(journalId);
        }
        catch (DataAccessException e) {
            throw new ServiceException("Failed count entries", (Throwable)e);
        }
    }

    static JournalEntry convert(com.atlassian.confluence.api.model.journal.JournalEntry entry) {
        return new JournalEntry(entry.getJournalId(), entry.getType(), entry.getMessage());
    }

    private static <V> Function<Iterable<JournalEntry>, EntryProcessorResult<V>> convert(Function<Iterable<com.atlassian.confluence.api.model.journal.JournalEntry>, EntryProcessorResult<V>> entryProcessor) {
        return entries -> (EntryProcessorResult)entryProcessor.apply(DefaultJournalService.convert(entries));
    }

    private static Iterable<com.atlassian.confluence.api.model.journal.JournalEntry> convert(Iterable<JournalEntry> entries) {
        return Iterables.transform(entries, DefaultJournalService::convert);
    }

    static com.atlassian.confluence.api.model.journal.JournalEntry convert(JournalEntry entry) {
        return new com.atlassian.confluence.api.model.journal.JournalEntry(entry.getId(), entry.getJournalId(), entry.getCreationDate(), entry.getType(), entry.getMessage());
    }
}

