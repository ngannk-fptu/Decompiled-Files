/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.index;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import org.apache.lucene.store.Directory;

public abstract class IndexCommit
implements Comparable<IndexCommit> {
    public abstract String getSegmentsFileName();

    public abstract Collection<String> getFileNames() throws IOException;

    public abstract Directory getDirectory();

    public abstract void delete();

    public abstract boolean isDeleted();

    public abstract int getSegmentCount();

    protected IndexCommit() {
    }

    public boolean equals(Object other) {
        if (other instanceof IndexCommit) {
            IndexCommit otherCommit = (IndexCommit)other;
            return otherCommit.getDirectory() == this.getDirectory() && otherCommit.getGeneration() == this.getGeneration();
        }
        return false;
    }

    public int hashCode() {
        return this.getDirectory().hashCode() + Long.valueOf(this.getGeneration()).hashCode();
    }

    public abstract long getGeneration();

    public abstract Map<String, String> getUserData() throws IOException;

    @Override
    public int compareTo(IndexCommit commit) {
        long comgen;
        if (this.getDirectory() != commit.getDirectory()) {
            throw new UnsupportedOperationException("cannot compare IndexCommits from different Directory instances");
        }
        long gen = this.getGeneration();
        if (gen < (comgen = commit.getGeneration())) {
            return -1;
        }
        if (gen > comgen) {
            return 1;
        }
        return 0;
    }
}

