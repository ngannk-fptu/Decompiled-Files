/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.internal.index.lucene.snapshot;

import com.atlassian.confluence.internal.index.lucene.snapshot.IndexSnapshotError;
import java.util.Objects;

public class LuceneIndexSnapshotException
extends RuntimeException {
    private final IndexSnapshotError error;

    public LuceneIndexSnapshotException(String message) {
        this(message, IndexSnapshotError.UNKNOWN, null);
    }

    public LuceneIndexSnapshotException(String message, IndexSnapshotError error) {
        this(message, error, null);
    }

    public LuceneIndexSnapshotException(String message, Throwable cause) {
        this(message, IndexSnapshotError.UNKNOWN, cause);
    }

    public LuceneIndexSnapshotException(String message, IndexSnapshotError error, Throwable cause) {
        super(message, cause);
        this.error = Objects.requireNonNull(error);
    }

    public IndexSnapshotError getError() {
        return this.error;
    }
}

