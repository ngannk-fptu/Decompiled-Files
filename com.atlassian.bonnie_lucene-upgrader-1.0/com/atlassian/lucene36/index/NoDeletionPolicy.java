/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.index;

import com.atlassian.lucene36.index.IndexCommit;
import com.atlassian.lucene36.index.IndexDeletionPolicy;
import java.io.IOException;
import java.util.List;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class NoDeletionPolicy
implements IndexDeletionPolicy {
    public static final IndexDeletionPolicy INSTANCE = new NoDeletionPolicy();

    private NoDeletionPolicy() {
    }

    @Override
    public void onCommit(List<? extends IndexCommit> commits) throws IOException {
    }

    @Override
    public void onInit(List<? extends IndexCommit> commits) throws IOException {
    }
}

