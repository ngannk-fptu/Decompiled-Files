/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.jetty.util.NanoTime
 *  org.eclipse.jetty.util.component.Destroyable
 *  org.eclipse.jetty.util.thread.Scheduler
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.eclipse.jetty.io;

import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.eclipse.jetty.io.CyclicTimeout;
import org.eclipse.jetty.util.NanoTime;
import org.eclipse.jetty.util.component.Destroyable;
import org.eclipse.jetty.util.thread.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class CyclicTimeouts<T extends Expirable>
implements Destroyable {
    private static final Logger LOG = LoggerFactory.getLogger(CyclicTimeouts.class);
    private final AtomicLong earliestNanoTime = new AtomicLong(Long.MAX_VALUE);
    private final CyclicTimeout cyclicTimeout;

    public CyclicTimeouts(Scheduler scheduler) {
        this.cyclicTimeout = new Timeouts(scheduler);
    }

    protected abstract Iterator<T> iterator();

    protected abstract boolean onExpired(T var1);

    private void onTimeoutExpired() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Timeouts check for {}", (Object)this);
        }
        long now = NanoTime.now();
        long earliest = Long.MAX_VALUE;
        this.earliestNanoTime.set(earliest);
        Iterator<T> iterator = this.iterator();
        if (iterator == null) {
            return;
        }
        while (iterator.hasNext()) {
            Expirable expirable = (Expirable)iterator.next();
            long expiresAt = expirable.getExpireNanoTime();
            if (expiresAt == Long.MAX_VALUE) {
                if (!LOG.isDebugEnabled()) continue;
                LOG.debug("Entity {} does not expire for {}", (Object)expirable, (Object)this);
                continue;
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("Entity {} expires in {} ms for {}", new Object[]{expirable, NanoTime.millisElapsed((long)now, (long)expiresAt), this});
            }
            if (NanoTime.isBeforeOrSame((long)expiresAt, (long)now)) {
                boolean remove = this.onExpired(expirable);
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Entity {} expired, remove={} for {}", new Object[]{expirable, remove, this});
                }
                if (!remove) continue;
                iterator.remove();
                continue;
            }
            earliest = Math.min(earliest, NanoTime.elapsed((long)now, (long)expiresAt));
        }
        if (earliest < Long.MAX_VALUE) {
            this.schedule(now + earliest);
        }
    }

    public void schedule(T expirable) {
        long expiresAt = expirable.getExpireNanoTime();
        if (expiresAt < Long.MAX_VALUE) {
            this.schedule(expiresAt);
        }
    }

    private void schedule(long expiresAt) {
        long prevEarliest = this.earliestNanoTime.getAndUpdate(t -> NanoTime.isBefore((long)t, (long)expiresAt) ? t : expiresAt);
        long expires = expiresAt;
        while (NanoTime.isBefore((long)expires, (long)prevEarliest)) {
            long delay = Math.max(0L, NanoTime.until((long)expires));
            if (LOG.isDebugEnabled()) {
                LOG.debug("Scheduling timeout in {} ms for {}", (Object)TimeUnit.NANOSECONDS.toMillis(delay), (Object)this);
            }
            this.schedule(this.cyclicTimeout, delay, TimeUnit.NANOSECONDS);
            prevEarliest = expires;
            expires = this.earliestNanoTime.get();
        }
    }

    public void destroy() {
        this.cyclicTimeout.destroy();
    }

    boolean schedule(CyclicTimeout cyclicTimeout, long delay, TimeUnit unit) {
        return cyclicTimeout.schedule(delay, unit);
    }

    private class Timeouts
    extends CyclicTimeout {
        private Timeouts(Scheduler scheduler) {
            super(scheduler);
        }

        @Override
        public void onTimeoutExpired() {
            CyclicTimeouts.this.onTimeoutExpired();
        }
    }

    public static interface Expirable {
        public long getExpireNanoTime();
    }
}

