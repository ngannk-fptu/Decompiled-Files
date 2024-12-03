/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.jcip.annotations.ThreadSafe
 */
package io.atlassian.util.concurrent;

import io.atlassian.util.concurrent.ReusableLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public class BooleanLatch
implements ReusableLatch {
    private final Sync sync = new Sync();

    @Override
    public final void release() {
        this.sync.release(0);
    }

    @Override
    public final void await() throws InterruptedException {
        this.sync.acquireInterruptibly(0);
    }

    @Override
    public final boolean await(long timeout, TimeUnit unit) throws InterruptedException {
        return this.sync.tryAcquireNanos(0, unit.toNanos(timeout));
    }

    private static class Sync
    extends AbstractQueuedSynchronizer {
        private static final long serialVersionUID = -3475411235403448115L;
        private static final int RELEASED = 0;
        private static final int UNAVAILABLE = -1;

        private Sync() {
            this.setState(-1);
        }

        @Override
        protected boolean tryAcquire(int ignore) {
            if (this.getState() != 0) {
                return false;
            }
            return this.compareAndSetState(0, -1);
        }

        @Override
        protected boolean tryRelease(int ignore) {
            int state = this.getState();
            if (state == -1) {
                this.setState(0);
            }
            return true;
        }
    }
}

