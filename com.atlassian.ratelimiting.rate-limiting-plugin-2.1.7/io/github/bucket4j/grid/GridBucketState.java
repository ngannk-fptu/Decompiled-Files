/*
 * Decompiled with CFR 0.152.
 */
package io.github.bucket4j.grid;

import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.BucketState;
import java.io.Serializable;

public class GridBucketState
implements Serializable {
    private static final long serialVersionUID = 1L;
    private BucketConfiguration configuration;
    private BucketState state;

    public GridBucketState(BucketConfiguration configuration, BucketState state) {
        this.configuration = configuration;
        this.state = state;
    }

    public GridBucketState deepCopy() {
        return new GridBucketState(this.configuration, this.state.copy());
    }

    public void refillAllBandwidth(long currentTimeNanos) {
        this.state.refillAllBandwidth(this.configuration.getBandwidths(), currentTimeNanos);
    }

    public long getAvailableTokens() {
        return this.state.getAvailableTokens(this.configuration.getBandwidths());
    }

    public void consume(long tokensToConsume) {
        this.state.consume(this.configuration.getBandwidths(), tokensToConsume);
    }

    public long calculateDelayNanosAfterWillBePossibleToConsume(long tokensToConsume, long currentTimeNanos) {
        return this.state.calculateDelayNanosAfterWillBePossibleToConsume(this.configuration.getBandwidths(), tokensToConsume, currentTimeNanos);
    }

    public void addTokens(long tokensToAdd) {
        this.state.addTokens(this.configuration.getBandwidths(), tokensToAdd);
    }

    public BucketState copyBucketState() {
        return this.state.copy();
    }

    public BucketConfiguration replaceConfigurationOrReturnPrevious(BucketConfiguration newConfiguration) {
        if (!this.configuration.isCompatible(newConfiguration)) {
            return this.configuration;
        }
        this.configuration = newConfiguration;
        return null;
    }

    public BucketConfiguration getConfiguration() {
        return this.configuration;
    }

    public BucketState getState() {
        return this.state;
    }
}

