/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 *  com.google.common.base.Function
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.api.service.journal;

import com.atlassian.annotations.PublicApi;
import com.atlassian.confluence.api.model.journal.JournalEntry;
import com.atlassian.confluence.api.model.journal.JournalIdentifier;
import com.atlassian.confluence.api.service.exceptions.ServiceException;
import com.atlassian.confluence.api.service.journal.EntryProcessorResult;
import com.google.common.base.Function;
import org.checkerframework.checker.nullness.qual.NonNull;

@PublicApi
public interface JournalService {
    public long enqueue(@NonNull JournalEntry var1) throws ServiceException;

    @Deprecated
    default public <V> V processEntries(@NonNull JournalIdentifier journalId, int maxEntries, @NonNull Function<Iterable<JournalEntry>, EntryProcessorResult<V>> entryProcessor) throws ServiceException {
        return this.processNewEntries(journalId, maxEntries, (java.util.function.Function<Iterable<JournalEntry>, EntryProcessorResult<V>>)entryProcessor);
    }

    public <V> V processNewEntries(@NonNull JournalIdentifier var1, int var2, @NonNull java.util.function.Function<Iterable<JournalEntry>, EntryProcessorResult<V>> var3) throws ServiceException;

    public void waitForRecentEntriesToBecomeVisible() throws InterruptedException;

    public Iterable<JournalEntry> peek(@NonNull JournalIdentifier var1, int var2) throws ServiceException;

    public void reset(@NonNull JournalIdentifier var1) throws ServiceException;

    public int countEntries(@NonNull JournalIdentifier var1) throws ServiceException;
}

