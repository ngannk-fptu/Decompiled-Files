/*
 * Decompiled with CFR 0.152.
 */
package io.github.bucket4j;

import java.util.concurrent.locks.LockSupport;

public interface UninterruptibleBlockingStrategy {
    public static final UninterruptibleBlockingStrategy PARKING = new UninterruptibleBlockingStrategy(){

        @Override
        public void parkUninterruptibly(long nanosToPark) {
            long endNanos = System.nanoTime() + nanosToPark;
            long remainingParkNanos = nanosToPark;
            boolean interrupted = false;
            try {
                while (true) {
                    LockSupport.parkNanos(remainingParkNanos);
                    long currentTimeNanos = System.nanoTime();
                    remainingParkNanos = endNanos - currentTimeNanos;
                    if (remainingParkNanos <= 0L) {
                        return;
                    }
                    if (!Thread.interrupted()) continue;
                    interrupted = true;
                }
            }
            finally {
                if (interrupted) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    };

    public void parkUninterruptibly(long var1);
}

