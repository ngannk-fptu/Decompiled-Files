/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.internal;

import com.amazonaws.internal.CustomBackoffStrategy;

public class DynamoDBBackoffStrategy
extends CustomBackoffStrategy {
    public static final CustomBackoffStrategy DEFAULT = new DynamoDBBackoffStrategy();

    @Override
    public int getBackoffPeriod(int retries) {
        if (retries <= 0) {
            return 0;
        }
        int delay = 50 * (int)Math.pow(2.0, retries - 1);
        if (delay < 0) {
            delay = Integer.MAX_VALUE;
        }
        return delay;
    }
}

