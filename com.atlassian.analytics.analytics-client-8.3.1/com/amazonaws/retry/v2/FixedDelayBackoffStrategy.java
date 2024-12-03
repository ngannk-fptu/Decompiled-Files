/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.retry.v2;

import com.amazonaws.retry.v2.BackoffStrategy;
import com.amazonaws.retry.v2.RetryPolicyContext;
import com.amazonaws.util.ValidationUtils;

public class FixedDelayBackoffStrategy
implements BackoffStrategy {
    private final int fixedBackoff;

    public FixedDelayBackoffStrategy(int fixedBackoff) {
        this.fixedBackoff = ValidationUtils.assertIsPositive(fixedBackoff, "fixedBackoff");
    }

    @Override
    public long computeDelayBeforeNextRetry(RetryPolicyContext context) {
        return this.fixedBackoff;
    }
}

