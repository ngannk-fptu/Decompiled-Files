/*
 * Decompiled with CFR 0.152.
 */
package io.atlassian.fugue.retry;

public class BeforeRetryExponentialBackoffTask
implements Runnable {
    private long backoff;

    public BeforeRetryExponentialBackoffTask(long backoffMillis) {
        if (backoffMillis <= 0L) {
            throw new IllegalArgumentException("Backoff time must not be negative.");
        }
        this.backoff = backoffMillis;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(this.backoff);
            this.backoff *= 2L;
        }
        catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    long currentBackoff() {
        return this.backoff;
    }
}

