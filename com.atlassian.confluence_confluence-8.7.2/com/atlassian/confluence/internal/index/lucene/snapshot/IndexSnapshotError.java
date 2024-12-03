/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.internal.index.lucene.snapshot;

import com.atlassian.confluence.index.status.ReIndexError;

public enum IndexSnapshotError {
    UNKNOWN_INDEX(ReIndexError.UNKNOWN),
    SNAPSHOT_NOT_EXIST(ReIndexError.UNKNOWN),
    NOT_WRITABLE_LOCAL_HOME(ReIndexError.NOT_WRITABLE_LOCAL_HOME),
    NOT_WRITABLE_SHARED_HOME(ReIndexError.NOT_WRITABLE_SHARED_HOME),
    NOT_ENOUGH_DISK_SPACE_LOCAL_HOME(ReIndexError.NOT_ENOUGH_DISK_SPACE_LOCAL_HOME),
    NOT_ENOUGH_DISK_SPACE_SHARED_HOME(ReIndexError.NOT_ENOUGH_DISK_SPACE_SHARED_HOME),
    UNKNOWN(ReIndexError.UNKNOWN),
    UNAVAILABLE(ReIndexError.UNAVAILABLE);

    private final ReIndexError error;

    private IndexSnapshotError(ReIndexError error) {
        this.error = error;
    }

    public ReIndexError toReIndexError() {
        return this.error;
    }
}

