/*
 * Decompiled with CFR 0.152.
 */
package io.github.bucket4j.grid;

import io.github.bucket4j.grid.GridBucketState;
import io.github.bucket4j.grid.GridCommand;

public class GetAvailableTokensCommand
implements GridCommand<Long> {
    private static final long serialVersionUID = 1L;

    @Override
    public Long execute(GridBucketState state, long currentTimeNanos) {
        state.refillAllBandwidth(currentTimeNanos);
        return state.getAvailableTokens();
    }

    @Override
    public boolean isBucketStateModified() {
        return false;
    }
}

