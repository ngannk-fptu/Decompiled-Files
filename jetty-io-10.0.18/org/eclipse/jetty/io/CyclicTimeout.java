/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.jetty.util.NanoTime
 *  org.eclipse.jetty.util.component.Destroyable
 *  org.eclipse.jetty.util.thread.Scheduler
 *  org.eclipse.jetty.util.thread.Scheduler$Task
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.eclipse.jetty.io;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import org.eclipse.jetty.util.NanoTime;
import org.eclipse.jetty.util.component.Destroyable;
import org.eclipse.jetty.util.thread.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class CyclicTimeout
implements Destroyable {
    private static final Logger LOG = LoggerFactory.getLogger(CyclicTimeout.class);
    private static final Timeout NOT_SET = new Timeout(Long.MAX_VALUE, null);
    private static final Scheduler.Task DESTROYED = () -> false;
    private final Scheduler _scheduler;
    private final AtomicReference<Timeout> _timeout = new AtomicReference<Timeout>(NOT_SET);

    public CyclicTimeout(Scheduler scheduler) {
        this._scheduler = scheduler;
    }

    public Scheduler getScheduler() {
        return this._scheduler;
    }

    public boolean schedule(long delay, TimeUnit units) {
        boolean result;
        Wakeup wakeup;
        Timeout timeout;
        long now = NanoTime.now();
        long newTimeoutAt = now + units.toNanos(delay);
        Wakeup newWakeup = null;
        do {
            timeout = this._timeout.get();
            result = timeout._at != Long.MAX_VALUE;
            wakeup = timeout._wakeup;
            if (wakeup != null && !NanoTime.isBefore((long)newTimeoutAt, (long)wakeup._at)) continue;
            wakeup = newWakeup = new Wakeup(newTimeoutAt, wakeup);
        } while (!this._timeout.compareAndSet(timeout, new Timeout(newTimeoutAt, wakeup)));
        if (LOG.isDebugEnabled()) {
            LOG.debug("Installed timeout in {} ms, {} wake up in {} ms", new Object[]{units.toMillis(delay), newWakeup != null ? "new" : "existing", NanoTime.millisElapsed((long)now, (long)wakeup._at)});
        }
        if (newWakeup != null) {
            newWakeup.schedule(now);
        }
        return result;
    }

    public boolean cancel() {
        boolean result;
        Wakeup wakeup;
        Timeout newTimeout;
        Timeout timeout;
        do {
            timeout = this._timeout.get();
            result = timeout._at != Long.MAX_VALUE;
        } while (!this._timeout.compareAndSet(timeout, newTimeout = (wakeup = timeout._wakeup) == null ? NOT_SET : new Timeout(Long.MAX_VALUE, wakeup)));
        return result;
    }

    public abstract void onTimeoutExpired();

    public void destroy() {
        Wakeup wakeup;
        Timeout timeout = this._timeout.getAndSet(NOT_SET);
        Wakeup wakeup2 = wakeup = timeout == null ? null : timeout._wakeup;
        while (wakeup != null) {
            wakeup.destroy();
            wakeup = wakeup._next;
        }
    }

    private static class Timeout {
        private final long _at;
        private final Wakeup _wakeup;

        private Timeout(long timeoutAt, Wakeup wakeup) {
            this._at = timeoutAt;
            this._wakeup = wakeup;
        }

        public String toString() {
            return String.format("%s@%x:%dms,%s", this.getClass().getSimpleName(), this.hashCode(), NanoTime.millisUntil((long)this._at), this._wakeup);
        }
    }

    private class Wakeup
    implements Runnable {
        private final AtomicReference<Scheduler.Task> _task = new AtomicReference();
        private final long _at;
        private final Wakeup _next;

        private Wakeup(long wakeupAt, Wakeup next) {
            this._at = wakeupAt;
            this._next = next;
        }

        private void schedule(long now) {
            this._task.compareAndSet(null, CyclicTimeout.this._scheduler.schedule((Runnable)this, NanoTime.elapsed((long)now, (long)this._at), TimeUnit.NANOSECONDS));
        }

        private void destroy() {
            Scheduler.Task task = this._task.getAndSet(DESTROYED);
            if (task != null) {
                task.cancel();
            }
        }

        @Override
        public void run() {
            Timeout newTimeout;
            Timeout timeout;
            long now = NanoTime.now();
            Wakeup newWakeup = null;
            boolean hasExpired = false;
            do {
                timeout = CyclicTimeout.this._timeout.get();
                Wakeup wakeup = timeout._wakeup;
                while (wakeup != null && wakeup != this) {
                    wakeup = wakeup._next;
                }
                if (wakeup == null) {
                    return;
                }
                wakeup = wakeup._next;
                if (NanoTime.isBeforeOrSame((long)timeout._at, (long)now)) {
                    hasExpired = true;
                    newTimeout = wakeup == null ? NOT_SET : new Timeout(Long.MAX_VALUE, wakeup);
                    continue;
                }
                if (timeout._at != Long.MAX_VALUE) {
                    if (wakeup == null || NanoTime.isBefore((long)timeout._at, (long)wakeup._at)) {
                        wakeup = newWakeup = new Wakeup(timeout._at, wakeup);
                    }
                    newTimeout = new Timeout(timeout._at, wakeup);
                    continue;
                }
                Timeout timeout2 = newTimeout = wakeup == null ? NOT_SET : new Timeout(Long.MAX_VALUE, wakeup);
            } while (!CyclicTimeout.this._timeout.compareAndSet(timeout, newTimeout));
            if (newWakeup != null) {
                newWakeup.schedule(now);
            }
            if (hasExpired) {
                CyclicTimeout.this.onTimeoutExpired();
            }
        }

        public String toString() {
            return String.format("%s@%x:%dms->%s", this.getClass().getSimpleName(), this.hashCode(), this._at == Long.MAX_VALUE ? this._at : NanoTime.millisUntil((long)this._at), this._next);
        }
    }
}

