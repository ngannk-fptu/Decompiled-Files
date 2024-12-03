/*
 * Decompiled with CFR 0.152.
 */
package io.github.bucket4j.grid;

import io.github.bucket4j.BucketState;
import io.github.bucket4j.grid.GridBucketState;
import io.github.bucket4j.grid.GridCommand;

public class CreateSnapshotCommand
implements GridCommand<BucketState> {
    private static final long serialVersionUID = 1L;

    @Override
    public BucketState execute(GridBucketState gridState, long currentTimeNanos) {
        return gridState.copyBucketState();
    }

    @Override
    public boolean isBucketStateModified() {
        return false;
    }
}

