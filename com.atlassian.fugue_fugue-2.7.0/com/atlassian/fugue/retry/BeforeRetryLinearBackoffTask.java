/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.atlassian.fugue.retry;

import com.google.common.base.Preconditions;

public class BeforeRetryLinearBackoffTask
implements Runnable {
    private final long backoff;

    public BeforeRetryLinearBackoffTask(long backoffMillis) {
        Preconditions.checkArgument((backoffMillis > 0L ? 1 : 0) != 0, (Object)"Backoff time must not be negative.");
        this.backoff = backoffMillis;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(this.backoff);
        }
        catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    long currentBackoff() {
        return this.backoff;
    }
}

