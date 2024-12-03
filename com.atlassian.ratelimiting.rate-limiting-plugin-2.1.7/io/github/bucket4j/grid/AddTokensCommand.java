/*
 * Decompiled with CFR 0.152.
 */
package io.github.bucket4j.grid;

import io.github.bucket4j.Nothing;
import io.github.bucket4j.grid.GridBucketState;
import io.github.bucket4j.grid.GridCommand;

public class AddTokensCommand
implements GridCommand<Nothing> {
    private static final long serialVersionUID = 1L;
    private long tokensToAdd;

    public AddTokensCommand(long tokensToAdd) {
        this.tokensToAdd = tokensToAdd;
    }

    @Override
    public Nothing execute(GridBucketState state, long currentTimeNanos) {
        state.refillAllBandwidth(currentTimeNanos);
        state.addTokens(this.tokensToAdd);
        return Nothing.INSTANCE;
    }

    @Override
    public boolean isBucketStateModified() {
        return true;
    }
}

