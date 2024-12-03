/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.jcip.annotations.ThreadSafe
 */
package com.atlassian.util.concurrent;

import com.atlassian.util.concurrent.ReusableLatch;
import java.io.Serializable;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public class PhasedLatch
implements ReusableLatch {
    private static final PhaseComparator comparator = new PhaseComparator();
    private final Sync sync = new Sync();

    public void release() {
        this.sync.releaseShared(1);
    }

    public void await() throws InterruptedException {
        this.awaitPhase(this.getPhase());
    }

    public boolean await(long time, TimeUnit unit) throws InterruptedException {
        return this.sync.tryAcquireSharedNanos(this.getPhase(), unit.toNanos(time));
    }

    public void awaitPhase(int phase) throws InterruptedException {
        this.sync.acquireSharedInterruptibly(phase);
    }

    public boolean awaitPhase(int phase, long period, TimeUnit unit) throws InterruptedException {
        return this.sync.tryAcquireSharedNanos(phase, unit.toNanos(period));
    }

    public int getPhase() {
        return this.sync.getCurrentPhase();
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    static class PhaseComparator
    implements Comparator<Integer>,
    Serializable {
        private static final long serialVersionUID = -614957178717195674L;

        PhaseComparator() {
        }

        @Override
        public int compare(Integer current, Integer waitingFor) {
            return waitingFor - current;
        }

        boolean isPassed(int current, int waitingFor) {
            return this.compare(current, waitingFor) < 0;
        }
    }

    private static class Sync
    extends AbstractQueuedSynchronizer {
        private static final long serialVersionUID = -7753362916930221487L;

        private Sync() {
        }

        public int getCurrentPhase() {
            return this.getState();
        }

        protected int tryAcquireShared(int phase) {
            return comparator.isPassed(this.getState(), phase) ? 1 : -1;
        }

        protected boolean tryReleaseShared(int ignore) {
            int state;
            while (!this.compareAndSetState(state = this.getState(), state + 1)) {
            }
            return true;
        }
    }
}

