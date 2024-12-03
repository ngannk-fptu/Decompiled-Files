/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.journal.JournalEntry
 *  com.atlassian.confluence.api.model.journal.JournalIdentifier
 */
package com.atlassian.confluence.search;

import com.atlassian.confluence.api.model.journal.JournalEntry;
import com.atlassian.confluence.api.model.journal.JournalIdentifier;
import java.util.Optional;

public interface ConvertibleToJournalEntry {
    public Optional<JournalEntry> convertToJournalEntry(JournalIdentifier var1);
}

