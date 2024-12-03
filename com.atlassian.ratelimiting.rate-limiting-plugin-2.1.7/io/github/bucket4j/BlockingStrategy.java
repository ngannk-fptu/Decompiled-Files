/*
 * Decompiled with CFR 0.152.
 */
package io.github.bucket4j;

import java.util.concurrent.locks.LockSupport;

public interface BlockingStrategy {
    public static final BlockingStrategy PARKING = new BlockingStrategy(){

        @Override
        public void park(long nanosToPark) throws InterruptedException {
            long endNanos = System.nanoTime() + nanosToPark;
            long remainingParkNanos = nanosToPark;
            do {
                LockSupport.parkNanos(remainingParkNanos);
                long currentTimeNanos = System.nanoTime();
                remainingParkNanos = endNanos - currentTimeNanos;
                if (!Thread.interrupted()) continue;
                throw new InterruptedException();
            } while (remainingParkNanos > 0L);
        }
    };

    public void park(long var1) throws InterruptedException;
}

