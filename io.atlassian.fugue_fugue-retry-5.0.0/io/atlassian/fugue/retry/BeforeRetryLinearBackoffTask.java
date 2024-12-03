/*
 * Decompiled with CFR 0.152.
 */
package io.atlassian.fugue.retry;

public class BeforeRetryLinearBackoffTask
implements Runnable {
    private final long backoff;

    public BeforeRetryLinearBackoffTask(long backoffMillis) {
        if (backoffMillis <= 0L) {
            throw new IllegalArgumentException("Backoff time must not be negative.");
        }
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

