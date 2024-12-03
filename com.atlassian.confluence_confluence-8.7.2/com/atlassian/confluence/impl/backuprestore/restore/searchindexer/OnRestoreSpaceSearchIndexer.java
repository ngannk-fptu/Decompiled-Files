/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bonnie.Searchable
 *  org.apache.commons.lang3.time.StopWatch
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.backuprestore.restore.searchindexer;

import com.atlassian.bonnie.Searchable;
import com.atlassian.confluence.backuprestore.exception.BackupRestoreException;
import com.atlassian.confluence.core.Versioned;
import com.atlassian.confluence.impl.backuprestore.ParallelTasksExecutor;
import com.atlassian.confluence.impl.backuprestore.restore.domain.ImportedObjectV2;
import com.atlassian.confluence.impl.backuprestore.restore.searchindexer.OnRestoreSearchIndexer;
import com.atlassian.confluence.impl.backuprestore.restore.searchindexer.SearchIndexerAdapter;
import com.atlassian.confluence.impl.backuprestore.restore.stash.ImportedObjectsStash;
import com.atlassian.confluence.impl.backuprestore.restore.stash.ImportedObjectsStashFactory;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OnRestoreSpaceSearchIndexer
implements OnRestoreSearchIndexer {
    private static final Logger log = LoggerFactory.getLogger(OnRestoreSpaceSearchIndexer.class);
    private static final int BATCH_SIZE = Integer.getInteger("confluence.restore.indexing-batch-size", 500);
    private static final String STASH_NAME = "objects-to-reindex";
    private final SearchIndexerAdapter searchIndexerAdapter;
    private final ParallelTasksExecutor parallelTasksExecutor;
    private final ImportedObjectsStash stash;

    public OnRestoreSpaceSearchIndexer(SearchIndexerAdapter searchIndexerAdapter, ImportedObjectsStashFactory importedObjectsStashFactory, ParallelTasksExecutor parallelTasksExecutor) {
        this.searchIndexerAdapter = searchIndexerAdapter;
        this.parallelTasksExecutor = parallelTasksExecutor;
        this.stash = importedObjectsStashFactory.createStash(STASH_NAME);
    }

    @Override
    public void onObjectsPersisting(Collection<ImportedObjectV2> importedObjects) throws BackupRestoreException {
        Collection importedObjectsSuitableForIndexing = importedObjects.stream().filter(importedObject -> Searchable.class.isAssignableFrom(importedObject.getEntityClass())).filter(this::isCurrentVersion).collect(Collectors.toList());
        this.writeToStash(importedObjectsSuitableForIndexing);
    }

    @Override
    public void flush() throws BackupRestoreException {
        StopWatch stopWatch = StopWatch.createStarted();
        long numberOfObjectsToIndex = this.stash.getNumberOfWrittenObjects();
        log.debug("Async reindexing started. Number of elements to index: {}", (Object)numberOfObjectsToIndex);
        int batchNumber = 0;
        int flushedElementsNumber = 0;
        while (this.stash.hasMoreRecords()) {
            List<ImportedObjectV2> objects = this.stash.readObjects(BATCH_SIZE);
            this.searchIndexerAdapter.reindexObjectsAsync(this.parallelTasksExecutor, objects);
            log.trace("Reindexing batch N {} containing {} elements finished. Flushed {} elements so far, {} remaining.", new Object[]{batchNumber, numberOfObjectsToIndex, flushedElementsNumber += objects.size(), numberOfObjectsToIndex - (long)flushedElementsNumber});
        }
        log.debug("Add indexing tasks ({}) created. Duration: {}", (Object)numberOfObjectsToIndex, (Object)stopWatch);
    }

    private void writeToStash(Collection<ImportedObjectV2> importedObjectsSuitableForIndexing) throws BackupRestoreException {
        if (importedObjectsSuitableForIndexing.isEmpty()) {
            return;
        }
        log.trace("Writing {} objects to {} stash", (Object)importedObjectsSuitableForIndexing.size(), (Object)STASH_NAME);
        for (ImportedObjectV2 importedObject : importedObjectsSuitableForIndexing) {
            this.stash.add(importedObject);
        }
        log.trace("{} objects have been written to {} stash", (Object)importedObjectsSuitableForIndexing.size(), (Object)STASH_NAME);
    }

    private boolean isCurrentVersion(ImportedObjectV2 importedObject) {
        if (!Versioned.class.isAssignableFrom(importedObject.getEntityClass())) {
            return true;
        }
        return importedObject.getFieldValue("originalVersion") == null;
    }
}

