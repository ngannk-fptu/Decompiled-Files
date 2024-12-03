/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.journal.JournalIdentifier
 *  com.atlassian.confluence.api.service.exceptions.ServiceException
 *  com.atlassian.confluence.api.service.journal.EntryProcessorResult
 *  com.google.common.base.Function
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.springframework.dao.DataAccessException
 */
package com.atlassian.confluence.impl.journal;

import com.atlassian.confluence.api.model.journal.JournalIdentifier;
import com.atlassian.confluence.api.service.exceptions.ServiceException;
import com.atlassian.confluence.api.service.journal.EntryProcessorResult;
import com.atlassian.confluence.impl.journal.JournalEntry;
import com.google.common.base.Function;
import java.util.Collection;
import java.util.Optional;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.dao.DataAccessException;

public interface JournalManager {
    public long enqueue(@NonNull JournalEntry var1) throws DataAccessException;

    public void enqueue(@NonNull Collection<JournalEntry> var1) throws ServiceException;

    @Deprecated
    default public <V> V processEntries(@NonNull JournalIdentifier journalId, int maxEntries, @NonNull Function<Iterable<JournalEntry>, @NonNull EntryProcessorResult<V>> entryProcessor) throws DataAccessException {
        return this.processNewEntries(journalId, maxEntries, (java.util.function.Function<Iterable<JournalEntry>, EntryProcessorResult<V>>)entryProcessor);
    }

    public <V> V processNewEntries(@NonNull JournalIdentifier var1, int var2, @NonNull java.util.function.Function<Iterable<JournalEntry>, @NonNull EntryProcessorResult<V>> var3) throws DataAccessException;

    public void waitForRecentEntriesToBecomeVisible() throws InterruptedException;

    public Iterable<JournalEntry> peek(@NonNull JournalIdentifier var1, int var2);

    public void reset(@NonNull JournalIdentifier var1);

    public int countEntries(@NonNull JournalIdentifier var1);

    public long getIgnoreWithinMillis();

    public Optional<JournalEntry> getMostRecentId(@NonNull JournalIdentifier var1) throws DataAccessException;

    public void setMostRecentId(@NonNull JournalIdentifier var1, long var2) throws DataAccessException;

    public void setMostRecentId(@NonNull JournalEntry var1);
}

