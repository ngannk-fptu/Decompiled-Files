/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.journal.JournalIdentifier
 */
package com.atlassian.confluence.internal.index.lucene.snapshot;

import com.atlassian.confluence.api.model.journal.JournalIdentifier;
import java.util.Objects;

public class LuceneIndexSnapshot {
    private final JournalIdentifier journalIdentifier;
    private final long journalEntryId;

    public LuceneIndexSnapshot(JournalIdentifier journalIdentifier, long journalEntryId) {
        this.journalIdentifier = Objects.requireNonNull(journalIdentifier);
        this.journalEntryId = journalEntryId;
    }

    public JournalIdentifier getJournalIdentifier() {
        return this.journalIdentifier;
    }

    public long getJournalEntryId() {
        return this.journalEntryId;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        LuceneIndexSnapshot snapshot = (LuceneIndexSnapshot)o;
        return this.journalIdentifier.equals((Object)snapshot.journalIdentifier) && this.journalEntryId == snapshot.journalEntryId;
    }

    public int hashCode() {
        return Objects.hash(this.journalIdentifier, this.journalEntryId);
    }

    public String toString() {
        return String.format("IndexSnapshot[JournalId=%s, JournalEntryId=%d]", this.getJournalIdentifier().getJournalName(), this.getJournalEntryId());
    }
}

