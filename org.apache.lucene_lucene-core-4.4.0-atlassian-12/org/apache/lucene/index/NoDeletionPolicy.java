/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.index;

import java.util.List;
import org.apache.lucene.index.IndexCommit;
import org.apache.lucene.index.IndexDeletionPolicy;

public final class NoDeletionPolicy
extends IndexDeletionPolicy {
    public static final IndexDeletionPolicy INSTANCE = new NoDeletionPolicy();

    private NoDeletionPolicy() {
    }

    @Override
    public void onCommit(List<? extends IndexCommit> commits) {
    }

    @Override
    public void onInit(List<? extends IndexCommit> commits) {
    }

    @Override
    public IndexDeletionPolicy clone() {
        return this;
    }
}

