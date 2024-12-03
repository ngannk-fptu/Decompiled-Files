/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.index;

import java.util.List;
import org.apache.lucene.index.IndexCommit;
import org.apache.lucene.index.IndexDeletionPolicy;

public final class KeepOnlyLastCommitDeletionPolicy
extends IndexDeletionPolicy {
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

