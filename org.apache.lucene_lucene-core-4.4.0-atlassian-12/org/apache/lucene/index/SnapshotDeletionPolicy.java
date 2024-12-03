/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.index;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.lucene.index.IndexCommit;
import org.apache.lucene.index.IndexDeletionPolicy;
import org.apache.lucene.store.Directory;

public class SnapshotDeletionPolicy
extends IndexDeletionPolicy {
    protected Map<Long, Integer> refCounts = new HashMap<Long, Integer>();
    protected Map<Long, IndexCommit> indexCommits = new HashMap<Long, IndexCommit>();
    private IndexDeletionPolicy primary;
    protected IndexCommit lastCommit;
    private boolean initCalled;

    public SnapshotDeletionPolicy(IndexDeletionPolicy primary) {
        this.primary = primary;
    }

    @Override
    public synchronized void onCommit(List<? extends IndexCommit> commits) throws IOException {
        this.primary.onCommit(this.wrapCommits(commits));
        this.lastCommit = commits.get(commits.size() - 1);
    }

    @Override
    public synchronized void onInit(List<? extends IndexCommit> commits) throws IOException {
        this.initCalled = true;
        this.primary.onInit(this.wrapCommits(commits));
        for (IndexCommit indexCommit : commits) {
            if (!this.refCounts.containsKey(indexCommit.getGeneration())) continue;
            this.indexCommits.put(indexCommit.getGeneration(), indexCommit);
        }
        if (!commits.isEmpty()) {
            this.lastCommit = commits.get(commits.size() - 1);
        }
    }

    public synchronized void release(IndexCommit commit) throws IOException {
        long gen = commit.getGeneration();
        this.releaseGen(gen);
    }

    protected void releaseGen(long gen) throws IOException {
        if (!this.initCalled) {
            throw new IllegalStateException("this instance is not being used by IndexWriter; be sure to use the instance returned from writer.getConfig().getIndexDeletionPolicy()");
        }
        Integer refCount = this.refCounts.get(gen);
        if (refCount == null) {
            throw new IllegalArgumentException("commit gen=" + gen + " is not currently snapshotted");
        }
        int refCountInt = refCount;
        assert (refCountInt > 0);
        if (--refCountInt == 0) {
            this.refCounts.remove(gen);
            this.indexCommits.remove(gen);
        } else {
            this.refCounts.put(gen, refCountInt);
        }
    }

    protected synchronized void incRef(IndexCommit ic) {
        int refCountInt;
        long gen = ic.getGeneration();
        Integer refCount = this.refCounts.get(gen);
        if (refCount == null) {
            this.indexCommits.put(gen, this.lastCommit);
            refCountInt = 0;
        } else {
            refCountInt = refCount;
        }
        this.refCounts.put(gen, refCountInt + 1);
    }

    public synchronized IndexCommit snapshot() throws IOException {
        if (!this.initCalled) {
            throw new IllegalStateException("this instance is not being used by IndexWriter; be sure to use the instance returned from writer.getConfig().getIndexDeletionPolicy()");
        }
        if (this.lastCommit == null) {
            throw new IllegalStateException("No index commit to snapshot");
        }
        this.incRef(this.lastCommit);
        return this.lastCommit;
    }

    public synchronized List<IndexCommit> getSnapshots() {
        return new ArrayList<IndexCommit>(this.indexCommits.values());
    }

    public synchronized int getSnapshotCount() {
        int total = 0;
        for (Integer refCount : this.refCounts.values()) {
            total += refCount.intValue();
        }
        return total;
    }

    public synchronized IndexCommit getIndexCommit(long gen) {
        return this.indexCommits.get(gen);
    }

    @Override
    public synchronized IndexDeletionPolicy clone() {
        SnapshotDeletionPolicy other = (SnapshotDeletionPolicy)super.clone();
        other.primary = this.primary.clone();
        other.lastCommit = null;
        other.refCounts = new HashMap<Long, Integer>(this.refCounts);
        other.indexCommits = new HashMap<Long, IndexCommit>(this.indexCommits);
        return other;
    }

    private List<IndexCommit> wrapCommits(List<? extends IndexCommit> commits) {
        ArrayList<IndexCommit> wrappedCommits = new ArrayList<IndexCommit>(commits.size());
        for (IndexCommit indexCommit : commits) {
            wrappedCommits.add(new SnapshotCommitPoint(indexCommit));
        }
        return wrappedCommits;
    }

    private class SnapshotCommitPoint
    extends IndexCommit {
        protected IndexCommit cp;

        protected SnapshotCommitPoint(IndexCommit cp) {
            this.cp = cp;
        }

        public String toString() {
            return "SnapshotDeletionPolicy.SnapshotCommitPoint(" + this.cp + ")";
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void delete() {
            SnapshotDeletionPolicy snapshotDeletionPolicy = SnapshotDeletionPolicy.this;
            synchronized (snapshotDeletionPolicy) {
                if (!SnapshotDeletionPolicy.this.refCounts.containsKey(this.cp.getGeneration())) {
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
        public boolean isDeleted() {
            return this.cp.isDeleted();
        }

        @Override
        public int getSegmentCount() {
            return this.cp.getSegmentCount();
        }
    }
}

