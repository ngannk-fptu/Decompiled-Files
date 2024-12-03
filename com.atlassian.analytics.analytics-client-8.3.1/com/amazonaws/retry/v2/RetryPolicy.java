/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.retry.v2;

import com.amazonaws.retry.v2.BackoffStrategy;
import com.amazonaws.retry.v2.RetryCondition;

public interface RetryPolicy
extends RetryCondition,
BackoffStrategy {
}

