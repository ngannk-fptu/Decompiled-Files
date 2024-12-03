/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.journal.JournalIdentifier
 *  com.google.common.base.Preconditions
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.upgrade.upgradetask.splitindex;

import com.atlassian.confluence.api.model.journal.JournalIdentifier;
import com.atlassian.confluence.cluster.ClusterManager;
import com.atlassian.confluence.impl.journal.JournalDao;
import com.atlassian.confluence.impl.journal.JournalEntry;
import com.atlassian.confluence.internal.index.lucene.snapshot.LuceneIndexSnapshot;
import com.atlassian.confluence.internal.index.lucene.snapshot.LuceneIndexSnapshotManager;
import com.atlassian.confluence.upgrade.upgradetask.splitindex.IndexInfo;
import com.atlassian.confluence.upgrade.upgradetask.splitindex.SplitIndexException;
import com.atlassian.confluence.upgrade.upgradetask.splitindex.SplitIndexFileHelper;
import com.google.common.base.Preconditions;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SplitIndexSnapshotManager {
    private static final Logger log = LoggerFactory.getLogger(SplitIndexSnapshotManager.class);
    private final LuceneIndexSnapshotManager snapshotManager;
    private final JournalDao journalDao;
    private final ClusterManager clusterManager;
    private final JournalIdentifier contentIdentifier;
    private final JournalIdentifier changeIdentifier;

    public SplitIndexSnapshotManager(LuceneIndexSnapshotManager snapshotManager, JournalDao journalDao, ClusterManager clusterManager, JournalIdentifier contentIdentifier, JournalIdentifier changeIdentifier) {
        this.snapshotManager = snapshotManager;
        this.journalDao = journalDao;
        this.clusterManager = clusterManager;
        this.contentIdentifier = contentIdentifier;
        this.changeIdentifier = changeIdentifier;
    }

    public boolean needsSnapshotRestoration(File contentDirectory) {
        File changeDirectory = new File(contentDirectory, "change");
        boolean contentIndexIsBlank = !contentDirectory.exists() || this.indexIsEmpty(contentDirectory, "content");
        boolean changeIndexIsBlank = !changeDirectory.exists() || this.indexIsEmpty(changeDirectory, "change");
        boolean needSnapshotRestoration = contentIndexIsBlank && changeIndexIsBlank;
        log.info("ContentIndexIsBlank={}, ChangeIndexIsBlank={}, NeedSnapshotRestoration={}", new Object[]{contentIndexIsBlank, changeIndexIsBlank, needSnapshotRestoration});
        return needSnapshotRestoration;
    }

    private boolean indexIsEmpty(File directory, String name) {
        IndexInfo info = new SplitIndexFileHelper(directory, name).refreshIndexInfo();
        return !info.isValid() || !info.hasDocuments();
    }

    public boolean hasSuitableContentSnapshot() {
        return this.hasSuitableSnapshot(this.contentIdentifier, "content");
    }

    public boolean hasSuitableChangeSnapshot() {
        return this.hasSuitableSnapshot(this.changeIdentifier, "change");
    }

    private boolean hasSuitableSnapshot(JournalIdentifier identifier, String name) {
        Optional snapshot = this.snapshotManager.findForJournal(identifier).stream().findFirst();
        if (!snapshot.isPresent()) {
            log.info("No {} snapshot has been found in shared home", (Object)name);
            return false;
        }
        Optional<Long> earliestJournalEntry = this.earliestJournalEntryId();
        log.info("Latest {} snapshot={}, Earliest journalId={}", new Object[]{name, snapshot, earliestJournalEntry});
        return earliestJournalEntry.isPresent() && ((LuceneIndexSnapshot)snapshot.get()).getJournalEntryId() >= earliestJournalEntry.get();
    }

    private Optional<Long> earliestJournalEntryId() {
        return Optional.ofNullable((Long)this.journalDao.findEarliestEntry().map(JournalEntry::getId).getOrNull());
    }

    public long estimatedUnpackedLatestSnapshotSizeBytes() {
        long contentSnapshotSize = this.snapshotManager.findForJournal(this.contentIdentifier).stream().findFirst().flatMap(this.snapshotManager::getFile).map(this::unpackedFileSize).orElseThrow(() -> new SplitIndexException("Unable to calculate the size of snapshot archive"));
        long changeSnapshotSize = this.snapshotManager.findForJournal(this.changeIdentifier).stream().findFirst().flatMap(this.snapshotManager::getFile).map(this::unpackedFileSize).orElse(0L);
        log.info("UnpackedContentSnapshotSize={}, UnpackedChangeSnapshotSize={}", (Object)contentSnapshotSize, (Object)changeSnapshotSize);
        return contentSnapshotSize + changeSnapshotSize;
    }

    public long unpackedFileSize(Path path) {
        try {
            return SplitIndexFileHelper.getTotalZipEntriesSize(path.toFile());
        }
        catch (IOException e) {
            log.error("Unable to get the size of a snapshot archive");
            throw new RuntimeException(e);
        }
    }

    public void restoreLatestContentSnapshot() {
        Preconditions.checkState((boolean)this.hasSuitableContentSnapshot());
        Optional contentSnapshot = this.snapshotManager.findForJournal(this.contentIdentifier).stream().findFirst();
        if (contentSnapshot.isPresent()) {
            log.info("Starting to restore latest content snapshot: {}", contentSnapshot.get());
            this.snapshotManager.restore((LuceneIndexSnapshot)contentSnapshot.get());
            log.info("Latest content snapshot has been restored");
        }
    }

    public void restoreLatestChangeSnapshot() {
        Preconditions.checkState((boolean)this.hasSuitableChangeSnapshot());
        Optional changeSnapshot = this.snapshotManager.findForJournal(this.changeIdentifier).stream().findFirst();
        if (changeSnapshot.isPresent()) {
            log.info("Starting to restore latest change snapshot: {}", changeSnapshot.get());
            this.snapshotManager.restore((LuceneIndexSnapshot)changeSnapshot.get());
            log.info("Latest change snapshot has been restored");
        }
    }

    public boolean shouldTakeNewSnapshot(File contentDirectory) {
        if (!this.clusterManager.isClustered()) {
            log.info("Won't create a snapshot as it's not DC");
            return false;
        }
        boolean alreadyHasBothSnapshots = this.hasSuitableContentSnapshot() && this.hasSuitableChangeSnapshot();
        log.info("Already has both snapshots: {}", (Object)alreadyHasBothSnapshots);
        if (alreadyHasBothSnapshots) {
            log.info("Won't create new snapshot, as it has them already.");
            return false;
        }
        File changeDirectory = new File(contentDirectory, "change");
        boolean contentIndexIsBlank = !contentDirectory.exists() || this.indexIsEmpty(contentDirectory, "content");
        boolean changeIndexIsBlank = !changeDirectory.exists() || this.indexIsEmpty(changeDirectory, "change");
        boolean needsNewSnapshot = !contentIndexIsBlank || !changeIndexIsBlank;
        log.info("ContentIndexIsBlank={}, ChangeIndexIsBlank={}, NeedsNewSnapshot={}", new Object[]{contentIndexIsBlank, changeIndexIsBlank, needsNewSnapshot});
        return needsNewSnapshot;
    }

    public void takeNewSnapshot() {
        log.info("Taking new content snapshot");
        LuceneIndexSnapshot contentSnapshot = this.snapshotManager.create(this.contentIdentifier);
        log.info("New content snapshot has been taken: {}", (Object)contentSnapshot);
        log.info("Taking new change snapshot");
        LuceneIndexSnapshot changeSnapshot = this.snapshotManager.create(this.changeIdentifier);
        log.info("New change snapshot has been taken: {}", (Object)changeSnapshot);
    }
}

