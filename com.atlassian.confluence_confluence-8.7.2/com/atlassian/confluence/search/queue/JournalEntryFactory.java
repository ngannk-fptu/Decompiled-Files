/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.journal.JournalEntry
 *  com.atlassian.confluence.api.model.journal.JournalIdentifier
 */
package com.atlassian.confluence.search.queue;

import com.atlassian.confluence.api.model.journal.JournalEntry;
import com.atlassian.confluence.api.model.journal.JournalIdentifier;
import com.atlassian.confluence.search.queue.JournalEntryType;
import java.util.Optional;

public class JournalEntryFactory {
    public static Optional<JournalEntry> createJournalEntry(JournalIdentifier journalId, JournalEntryType journalEntryType, String message) {
        return Optional.of(new JournalEntry(journalId, String.valueOf((Object)journalEntryType), message));
    }
}

