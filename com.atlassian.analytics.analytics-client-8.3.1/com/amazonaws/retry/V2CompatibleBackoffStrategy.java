/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.retry;

import com.amazonaws.retry.RetryPolicy;
import com.amazonaws.retry.v2.BackoffStrategy;

public interface V2CompatibleBackoffStrategy
extends RetryPolicy.BackoffStrategy,
BackoffStrategy {
}

