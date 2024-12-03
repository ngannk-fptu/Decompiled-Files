/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.retry.v2;

import com.amazonaws.retry.v2.RetryPolicyContext;

public interface RetryCondition {
    public boolean shouldRetry(RetryPolicyContext var1);
}

