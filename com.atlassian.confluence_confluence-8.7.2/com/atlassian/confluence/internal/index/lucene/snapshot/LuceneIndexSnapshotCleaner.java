/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.confluence.api.model.journal.JournalIdentifier
 *  com.atlassian.scheduler.JobRunner
 *  com.atlassian.scheduler.JobRunnerRequest
 *  com.atlassian.scheduler.JobRunnerResponse
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.internal.index.lucene.snapshot;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.confluence.api.model.journal.JournalIdentifier;
import com.atlassian.confluence.internal.index.lucene.LuceneReIndexer;
import com.atlassian.confluence.internal.index.lucene.snapshot.LuceneIndexSnapshot;
import com.atlassian.confluence.internal.index.lucene.snapshot.LuceneIndexSnapshotManager;
import com.atlassian.scheduler.JobRunner;
import com.atlassian.scheduler.JobRunnerRequest;
import com.atlassian.scheduler.JobRunnerResponse;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LuceneIndexSnapshotCleaner
implements JobRunner {
    private static final Logger log = LoggerFactory.getLogger(LuceneIndexSnapshotCleaner.class);
    private static final int INDEX_SNAPSHOT_RETAIN_SIZE = Integer.getInteger("index.snapshot.retain.count", 3);
    private final LuceneIndexSnapshotManager snapshotManager;

    public LuceneIndexSnapshotCleaner(LuceneIndexSnapshotManager snapshotManager) {
        this.snapshotManager = Objects.requireNonNull(snapshotManager);
    }

    @Nullable
    public JobRunnerResponse runJob(JobRunnerRequest jobRunnerRequest) {
        LuceneReIndexer.INDEXES_TO_SNAPSHOT.forEach(index -> this.cleanSnapshotsForJournal(index.getJournalIdentifier(), INDEX_SNAPSHOT_RETAIN_SIZE));
        return JobRunnerResponse.success();
    }

    @VisibleForTesting
    void cleanSnapshotsForJournal(JournalIdentifier journalIdentifier, int retainSize) {
        log.debug("Cleaning up index snapshots for journal {}", (Object)journalIdentifier);
        List<LuceneIndexSnapshot> snapshots = this.snapshotManager.findForJournal(journalIdentifier);
        while (snapshots.size() > retainSize) {
            LuceneIndexSnapshot oldestSnapshot = snapshots.get(snapshots.size() - 1);
            try {
                this.snapshotManager.delete(oldestSnapshot);
                log.debug("Deleted snapshot {}", (Object)oldestSnapshot);
            }
            catch (IOException e) {
                log.error("Error cleaning up index snapshot {}", (Object)oldestSnapshot, (Object)e);
                break;
            }
            snapshots = this.snapshotManager.findForJournal(journalIdentifier);
        }
    }
}

