/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.retry.v2;

import com.amazonaws.retry.v2.RetryCondition;
import com.amazonaws.retry.v2.RetryPolicyContext;
import com.amazonaws.util.ValidationUtils;

public class MaxNumberOfRetriesCondition
implements RetryCondition {
    private final int maxNumberOfRetries;

    public MaxNumberOfRetriesCondition(int maxNumberOfRetries) {
        this.maxNumberOfRetries = ValidationUtils.assertIsPositive(maxNumberOfRetries, "maxNumberOfRetries");
    }

    @Override
    public boolean shouldRetry(RetryPolicyContext context) {
        return context.retriesAttempted() < this.maxNumberOfRetries;
    }
}

