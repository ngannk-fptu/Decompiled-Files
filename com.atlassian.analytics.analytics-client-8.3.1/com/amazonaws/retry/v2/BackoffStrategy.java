/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.retry.v2;

import com.amazonaws.retry.v2.RetryPolicyContext;

public interface BackoffStrategy {
    public long computeDelayBeforeNextRetry(RetryPolicyContext var1);
}

