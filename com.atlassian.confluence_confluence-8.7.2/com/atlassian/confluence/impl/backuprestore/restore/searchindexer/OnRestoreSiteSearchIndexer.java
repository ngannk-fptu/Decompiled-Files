/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.backuprestore.restore.searchindexer;

import com.atlassian.confluence.backuprestore.exception.BackupRestoreException;
import com.atlassian.confluence.impl.backuprestore.restore.domain.ImportedObjectV2;
import com.atlassian.confluence.impl.backuprestore.restore.searchindexer.OnRestoreSearchIndexer;
import com.atlassian.confluence.impl.backuprestore.restore.searchindexer.SearchIndexerAdapter;
import com.atlassian.confluence.impl.journal.JournalStateStore;
import java.util.Collection;

public class OnRestoreSiteSearchIndexer
implements OnRestoreSearchIndexer {
    private final SearchIndexerAdapter searchIndexerAdapter;
    private final JournalStateStore journalStateStore;
    private final JournalStateStore bandanaJournalStateStore;

    public OnRestoreSiteSearchIndexer(SearchIndexerAdapter searchIndexerAdapter, JournalStateStore journalStateStore, JournalStateStore bandanaJournalStateStore) {
        this.searchIndexerAdapter = searchIndexerAdapter;
        this.journalStateStore = journalStateStore;
        this.bandanaJournalStateStore = bandanaJournalStateStore;
        this.searchIndexerAdapter.unIndexAll();
    }

    @Override
    public void onObjectsPersisting(Collection<ImportedObjectV2> importedObjects) {
    }

    @Override
    public void flush() throws BackupRestoreException {
        this.journalStateStore.resetAllJournalStates();
        this.bandanaJournalStateStore.resetAllJournalStates();
        this.searchIndexerAdapter.reIndexAll();
    }
}

