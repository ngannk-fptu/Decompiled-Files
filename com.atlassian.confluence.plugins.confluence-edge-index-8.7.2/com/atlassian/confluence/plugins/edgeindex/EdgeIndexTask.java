/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.journal.JournalEntry
 *  com.atlassian.confluence.api.model.journal.JournalIdentifier
 *  com.atlassian.confluence.search.ConfluenceIndexTask
 */
package com.atlassian.confluence.plugins.edgeindex;

import com.atlassian.confluence.api.model.journal.JournalEntry;
import com.atlassian.confluence.api.model.journal.JournalIdentifier;
import com.atlassian.confluence.search.ConfluenceIndexTask;
import java.util.Optional;

public interface EdgeIndexTask
extends ConfluenceIndexTask {
    default public Optional<JournalEntry> convertToJournalEntry(JournalIdentifier journalIdentifier) {
        return Optional.empty();
    }
}

