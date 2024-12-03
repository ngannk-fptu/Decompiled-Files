/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.AsynchronousPreferred
 */
package com.atlassian.confluence.internal.index.event;

import com.atlassian.confluence.internal.index.lucene.snapshot.IndexSnapshotError;
import com.atlassian.event.api.AsynchronousPreferred;
import java.util.Objects;

@AsynchronousPreferred
public class IndexSnapshotCreationFailedEvent {
    private final IndexSnapshotError error;

    public IndexSnapshotCreationFailedEvent(IndexSnapshotError error) {
        this.error = Objects.requireNonNull(error);
    }

    public IndexSnapshotError getError() {
        return this.error;
    }
}

