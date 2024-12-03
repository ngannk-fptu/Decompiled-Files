/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.backuprestore.JobScope
 */
package com.atlassian.confluence.impl.backuprestore.restore.searchindexer;

import com.atlassian.confluence.api.model.backuprestore.JobScope;
import com.atlassian.confluence.impl.backuprestore.ParallelTasksExecutor;
import com.atlassian.confluence.impl.backuprestore.restore.searchindexer.OnRestoreSearchIndexer;
import com.atlassian.confluence.impl.backuprestore.restore.searchindexer.OnRestoreSiteSearchIndexer;
import com.atlassian.confluence.impl.backuprestore.restore.searchindexer.OnRestoreSpaceSearchIndexer;
import com.atlassian.confluence.impl.backuprestore.restore.searchindexer.SearchIndexerAdapter;
import com.atlassian.confluence.impl.backuprestore.restore.stash.ImportedObjectsStashFactory;
import com.atlassian.confluence.impl.journal.JournalStateStore;

public class OnRestoreSearchIndexerFactory {
    private final SearchIndexerAdapter searchIndexerAdapter;
    private final ImportedObjectsStashFactory importedObjectsStashFactory;
    private final JournalStateStore journalStateStore;
    private final JournalStateStore bandanaJournalStateStore;

    public OnRestoreSearchIndexerFactory(SearchIndexerAdapter searchIndexerAdapter, ImportedObjectsStashFactory importedObjectsStashFactory, JournalStateStore journalStateStore, JournalStateStore bandanaJournalStateStore) {
        this.searchIndexerAdapter = searchIndexerAdapter;
        this.importedObjectsStashFactory = importedObjectsStashFactory;
        this.journalStateStore = journalStateStore;
        this.bandanaJournalStateStore = bandanaJournalStateStore;
    }

    public OnRestoreSearchIndexer createOnRestoreSearchIndexer(JobScope jobScope, ParallelTasksExecutor parallelTasksExecutor) {
        switch (jobScope) {
            case SITE: {
                return new OnRestoreSiteSearchIndexer(this.searchIndexerAdapter, this.journalStateStore, this.bandanaJournalStateStore);
            }
            case SPACE: {
                return new OnRestoreSpaceSearchIndexer(this.searchIndexerAdapter, this.importedObjectsStashFactory, parallelTasksExecutor);
            }
        }
        throw new IllegalArgumentException("Unable to create search indexer due to an unexpected job scope: " + jobScope);
    }
}

