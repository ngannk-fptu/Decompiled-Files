/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.journal.JournalIdentifier
 *  com.atlassian.fugue.Option
 *  com.google.common.annotations.VisibleForTesting
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.springframework.transaction.annotation.Transactional
 */
package com.atlassian.confluence.impl.journal;

import com.atlassian.confluence.api.model.journal.JournalIdentifier;
import com.atlassian.confluence.impl.journal.JournalEntry;
import com.atlassian.fugue.Option;
import com.google.common.annotations.VisibleForTesting;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface JournalDao {
    public long enqueue(@NonNull JournalEntry var1);

    public void enqueue(@NonNull Collection<JournalEntry> var1);

    public List<JournalEntry> findEntries(@NonNull JournalIdentifier var1, long var2, long var4, int var6);

    @VisibleForTesting
    public Option<JournalEntry> findMostRecentEntryByMessage(@NonNull JournalIdentifier var1, String var2);

    public int removeEntriesOlderThan(@NonNull Date var1);

    public Option<JournalEntry> findLatestEntry(@NonNull JournalIdentifier var1, long var2);

    public Option<JournalEntry> findEarliestEntry();

    public JournalEntry findEntry(long var1);

    public int countEntries(@NonNull JournalIdentifier var1, long var2, long var4);

    public void updateEntry(JournalEntry var1);
}

