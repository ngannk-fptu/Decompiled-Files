/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.index;

import com.atlassian.lucene36.index.IndexCommit;
import com.atlassian.lucene36.index.IndexDeletionPolicy;
import java.util.List;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class KeepOnlyLastCommitDeletionPolicy
implements IndexDeletionPolicy {
    @Override
    public void onInit(List<? extends IndexCommit> commits) {
        this.onCommit(commits);
    }

    @Override
    public void onCommit(List<? extends IndexCommit> commits) {
        int size = commits.size();
        for (int i = 0; i < size - 1; ++i) {
            commits.get(i).delete();
        }
    }
}

