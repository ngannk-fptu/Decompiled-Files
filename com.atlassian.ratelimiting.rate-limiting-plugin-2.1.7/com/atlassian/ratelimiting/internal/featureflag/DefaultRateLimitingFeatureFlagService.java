/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.atlassian.util.concurrent.ResettableLazyReference
 */
package com.atlassian.ratelimiting.internal.featureflag;

import com.atlassian.ratelimiting.featureflag.RateLimitingFeatureFlagService;
import io.atlassian.util.concurrent.ResettableLazyReference;

public abstract class DefaultRateLimitingFeatureFlagService
implements RateLimitingFeatureFlagService {
    private final ResettableLazyReference<Boolean> dryRunFeatureFlag = new ResettableLazyReference<Boolean>(){

        protected Boolean create() {
            return DefaultRateLimitingFeatureFlagService.this.getDryRunEnabled();
        }
    };

    public abstract boolean getDryRunEnabled();

    @Override
    public boolean isDryRunEnabled() {
        return (Boolean)this.dryRunFeatureFlag.get();
    }

    public void resetDryRunFeatureFlag() {
        this.dryRunFeatureFlag.reset();
    }
}

