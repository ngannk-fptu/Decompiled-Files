/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.retry.v2;

import com.amazonaws.retry.v2.BackoffStrategy;
import com.amazonaws.retry.v2.RetryCondition;
import com.amazonaws.retry.v2.RetryPolicy;
import com.amazonaws.retry.v2.RetryPolicyContext;
import com.amazonaws.util.ValidationUtils;

public class SimpleRetryPolicy
implements RetryPolicy {
    private final RetryCondition retryCondition;
    private final BackoffStrategy backoffStrategy;

    public SimpleRetryPolicy(RetryCondition retryCondition, BackoffStrategy backoffStrategy) {
        this.retryCondition = ValidationUtils.assertNotNull(retryCondition, "retryCondition");
        this.backoffStrategy = ValidationUtils.assertNotNull(backoffStrategy, "backoffStrategy");
    }

    @Override
    public long computeDelayBeforeNextRetry(RetryPolicyContext context) {
        return this.backoffStrategy.computeDelayBeforeNextRetry(context);
    }

    @Override
    public boolean shouldRetry(RetryPolicyContext context) {
        return this.retryCondition.shouldRetry(context);
    }
}

