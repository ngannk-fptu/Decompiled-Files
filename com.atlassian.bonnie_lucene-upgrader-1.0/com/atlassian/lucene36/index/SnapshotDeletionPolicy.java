/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.index;

import com.atlassian.lucene36.index.IndexCommit;
import com.atlassian.lucene36.index.IndexDeletionPolicy;
import com.atlassian.lucene36.store.Directory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class SnapshotDeletionPolicy
implements IndexDeletionPolicy {
    private Map<String, SnapshotInfo> idToSnapshot = new HashMap<String, SnapshotInfo>();
    private Map<String, Set<String>> segmentsFileToIDs = new HashMap<String, Set<String>>();
    private IndexDeletionPolicy primary;
    protected IndexCommit lastCommit;

    public SnapshotDeletionPolicy(IndexDeletionPolicy primary) {
        this.primary = primary;
    }

    public SnapshotDeletionPolicy(IndexDeletionPolicy primary, Map<String, String> snapshotsInfo) {
        this(primary);
        if (snapshotsInfo != null) {
            for (Map.Entry<String, String> e : snapshotsInfo.entrySet()) {
                this.registerSnapshotInfo(e.getKey(), e.getValue(), null);
            }
        }
    }

    protected void checkSnapshotted(String id) {
        if (this.isSnapshotted(id)) {
            throw new IllegalStateException("Snapshot ID " + id + " is already used - must be unique");
        }
    }

    protected void registerSnapshotInfo(String id, String segment, IndexCommit commit) {
        this.idToSnapshot.put(id, new SnapshotInfo(id, segment, commit));
        Set<String> ids = this.segmentsFileToIDs.get(segment);
        if (ids == null) {
            ids = new HashSet<String>();
            this.segmentsFileToIDs.put(segment, ids);
        }
        ids.add(id);
    }

    protected List<IndexCommit> wrapCommits(List<? extends IndexCommit> commits) {
        ArrayList<IndexCommit> wrappedCommits = new ArrayList<IndexCommit>(commits.size());
        for (IndexCommit indexCommit : commits) {
            wrappedCommits.add(new SnapshotCommitPoint(indexCommit));
        }
        return wrappedCommits;
    }

    public synchronized IndexCommit getSnapshot(String id) {
        SnapshotInfo snapshotInfo = this.idToSnapshot.get(id);
        if (snapshotInfo == null) {
            throw new IllegalStateException("No snapshot exists by ID: " + id);
        }
        return snapshotInfo.commit;
    }

    public synchronized Map<String, String> getSnapshots() {
        HashMap<String, String> snapshots = new HashMap<String, String>();
        for (Map.Entry<String, SnapshotInfo> e : this.idToSnapshot.entrySet()) {
            snapshots.put(e.getKey(), e.getValue().segmentsFileName);
        }
        return snapshots;
    }

    public boolean isSnapshotted(String id) {
        return this.idToSnapshot.containsKey(id);
    }

    @Override
    public synchronized void onCommit(List<? extends IndexCommit> commits) throws IOException {
        this.primary.onCommit(this.wrapCommits(commits));
        this.lastCommit = commits.get(commits.size() - 1);
    }

    @Override
    public synchronized void onInit(List<? extends IndexCommit> commits) throws IOException {
        this.primary.onInit(this.wrapCommits(commits));
        this.lastCommit = commits.get(commits.size() - 1);
        for (IndexCommit indexCommit : commits) {
            Set<String> ids = this.segmentsFileToIDs.get(indexCommit.getSegmentsFileName());
            if (ids == null) continue;
            for (String id : ids) {
                this.idToSnapshot.get((Object)id).commit = indexCommit;
            }
        }
        ArrayList<String> idsToRemove = null;
        for (Map.Entry<String, SnapshotInfo> e : this.idToSnapshot.entrySet()) {
            if (e.getValue().commit != null) continue;
            if (idsToRemove == null) {
                idsToRemove = new ArrayList<String>();
            }
            idsToRemove.add(e.getKey());
        }
        if (idsToRemove != null) {
            for (String id : idsToRemove) {
                SnapshotInfo info = this.idToSnapshot.remove(id);
                this.segmentsFileToIDs.remove(info.segmentsFileName);
            }
        }
    }

    public synchronized void release(String id) throws IOException {
        SnapshotInfo info = this.idToSnapshot.remove(id);
        if (info == null) {
            throw new IllegalStateException("Snapshot doesn't exist: " + id);
        }
        Set<String> ids = this.segmentsFileToIDs.get(info.segmentsFileName);
        if (ids != null) {
            ids.remove(id);
            if (ids.size() == 0) {
                this.segmentsFileToIDs.remove(info.segmentsFileName);
            }
        }
    }

    public synchronized IndexCommit snapshot(String id) throws IOException {
        if (this.lastCommit == null) {
            throw new IllegalStateException("No index commit to snapshot");
        }
        this.checkSnapshotted(id);
        this.registerSnapshotInfo(id, this.lastCommit.getSegmentsFileName(), this.lastCommit);
        return this.lastCommit;
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    protected class SnapshotCommitPoint
    extends IndexCommit {
        protected IndexCommit cp;

        protected SnapshotCommitPoint(IndexCommit cp) {
            this.cp = cp;
        }

        public String toString() {
            return "SnapshotDeletionPolicy.SnapshotCommitPoint(" + this.cp + ")";
        }

        protected boolean shouldDelete(String segmentsFileName) {
            return !SnapshotDeletionPolicy.this.segmentsFileToIDs.containsKey(segmentsFileName);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void delete() {
            SnapshotDeletionPolicy snapshotDeletionPolicy = SnapshotDeletionPolicy.this;
            synchronized (snapshotDeletionPolicy) {
                if (this.shouldDelete(this.getSegmentsFileName())) {
                    this.cp.delete();
                }
            }
        }

        @Override
        public Directory getDirectory() {
            return this.cp.getDirectory();
        }

        @Override
        public Collection<String> getFileNames() throws IOException {
            return this.cp.getFileNames();
        }

        @Override
        public long getGeneration() {
            return this.cp.getGeneration();
        }

        @Override
        public String getSegmentsFileName() {
            return this.cp.getSegmentsFileName();
        }

        @Override
        public Map<String, String> getUserData() throws IOException {
            return this.cp.getUserData();
        }

        @Override
        public long getVersion() {
            return this.cp.getVersion();
        }

        @Override
        public boolean isDeleted() {
            return this.cp.isDeleted();
        }

        @Override
        public int getSegmentCount() {
            return this.cp.getSegmentCount();
        }
    }

    private static class SnapshotInfo {
        String id;
        String segmentsFileName;
        IndexCommit commit;

        public SnapshotInfo(String id, String segmentsFileName, IndexCommit commit) {
            this.id = id;
            this.segmentsFileName = segmentsFileName;
            this.commit = commit;
        }

        public String toString() {
            return this.id + " : " + this.segmentsFileName;
        }
    }
}

