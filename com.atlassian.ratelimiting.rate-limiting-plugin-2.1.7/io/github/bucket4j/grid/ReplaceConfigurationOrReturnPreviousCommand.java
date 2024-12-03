/*
 * Decompiled with CFR 0.152.
 */
package io.github.bucket4j.grid;

import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.grid.GridBucketState;
import io.github.bucket4j.grid.GridCommand;

public class ReplaceConfigurationOrReturnPreviousCommand
implements GridCommand<BucketConfiguration> {
    private static final long serialVersionUID = 8183759647555953907L;
    private BucketConfiguration newConfiguration;
    private boolean replaced;

    public ReplaceConfigurationOrReturnPreviousCommand(BucketConfiguration newConfiguration) {
        this.newConfiguration = newConfiguration;
    }

    @Override
    public BucketConfiguration execute(GridBucketState state, long currentTimeNanos) {
        state.refillAllBandwidth(currentTimeNanos);
        BucketConfiguration previousConfiguration = state.replaceConfigurationOrReturnPrevious(this.newConfiguration);
        if (previousConfiguration != null) {
            return previousConfiguration;
        }
        this.replaced = true;
        return null;
    }

    @Override
    public boolean isBucketStateModified() {
        return this.replaced;
    }
}

