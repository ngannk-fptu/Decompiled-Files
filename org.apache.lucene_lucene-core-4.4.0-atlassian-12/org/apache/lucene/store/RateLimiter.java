/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.store;

import org.apache.lucene.util.ThreadInterruptedException;

public abstract class RateLimiter {
    public abstract void setMbPerSec(double var1);

    public abstract double getMbPerSec();

    public abstract long pause(long var1);

    public static class SimpleRateLimiter
    extends RateLimiter {
        private volatile double mbPerSec;
        private volatile double nsPerByte;
        private volatile long lastNS;

        public SimpleRateLimiter(double mbPerSec) {
            this.setMbPerSec(mbPerSec);
        }

        @Override
        public void setMbPerSec(double mbPerSec) {
            this.mbPerSec = mbPerSec;
            this.nsPerByte = 1.0E9 / (1048576.0 * mbPerSec);
        }

        @Override
        public double getMbPerSec() {
            return this.mbPerSec;
        }

        @Override
        public long pause(long bytes) {
            long pauseNS;
            if (bytes == 1L) {
                return 0L;
            }
            long targetNS = this.lastNS += (long)((double)bytes * this.nsPerByte);
            long startNS = System.nanoTime();
            long curNS = startNS;
            if (this.lastNS < curNS) {
                this.lastNS = curNS;
            }
            while ((pauseNS = targetNS - curNS) > 0L) {
                try {
                    Thread.sleep((int)(pauseNS / 1000000L), (int)(pauseNS % 1000000L));
                }
                catch (InterruptedException ie) {
                    throw new ThreadInterruptedException(ie);
                }
                curNS = System.nanoTime();
            }
            return curNS - startNS;
        }
    }
}

