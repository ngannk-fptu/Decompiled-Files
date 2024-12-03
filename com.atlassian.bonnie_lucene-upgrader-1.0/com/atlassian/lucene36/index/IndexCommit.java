/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.index;

import com.atlassian.lucene36.store.Directory;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class IndexCommit
implements Comparable<IndexCommit> {
    public abstract String getSegmentsFileName();

    public abstract Collection<String> getFileNames() throws IOException;

    public abstract Directory getDirectory();

    public abstract void delete();

    public abstract boolean isDeleted();

    public abstract int getSegmentCount();

    public boolean equals(Object other) {
        if (other instanceof IndexCommit) {
            IndexCommit otherCommit = (IndexCommit)other;
            return otherCommit.getDirectory().equals(this.getDirectory()) && otherCommit.getVersion() == this.getVersion();
        }
        return false;
    }

    public int hashCode() {
        return (int)((long)this.getDirectory().hashCode() + this.getVersion());
    }

    @Deprecated
    public abstract long getVersion();

    public abstract long getGeneration();

    @Deprecated
    public long getTimestamp() throws IOException {
        return this.getDirectory().fileModified(this.getSegmentsFileName());
    }

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

